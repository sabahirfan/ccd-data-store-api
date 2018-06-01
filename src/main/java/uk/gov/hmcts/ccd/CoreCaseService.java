package uk.gov.hmcts.ccd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseEventTrigger;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseView;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewJurisdiction;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewTrigger;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewType;
import uk.gov.hmcts.ccd.domain.model.definition.CaseDetails;
import uk.gov.hmcts.ccd.domain.model.definition.CaseEvent;
import uk.gov.hmcts.ccd.domain.model.definition.CaseState;
import uk.gov.hmcts.ccd.domain.model.definition.CaseType;
import uk.gov.hmcts.ccd.domain.model.definition.WizardPage;
import uk.gov.hmcts.ccd.domain.model.definition.WizardPageField;
import uk.gov.hmcts.ccd.domain.model.search.SearchInput;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultView;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultViewColumn;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultViewItem;
import uk.gov.hmcts.ccd.domain.model.search.WorkbasketInput;
import uk.gov.hmcts.ccd.domain.model.std.CaseDataContent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Configuration
@Service
@SuppressWarnings("unchecked")
public class CoreCaseService {
    private final ICCDApplication application;
    private final CCDAppConfig config;
    private final Class caseClass;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    public CoreCaseService(CCDAppConfig config, ICCDApplication application) {
        this.config = config;
        this.application = application;
        this.caseClass = ReflectionUtils.getCaseType(application.getClass());
    }

    public CaseType getCaseType() {
        CaseType result = new CaseType();
        result.setId(config.getCaseTypeId());
        result.setName(config.getCaseTypeId());
        result.setDescription(config.getCaseTypeId());
        result.setCaseFields(ReflectionUtils.generateFields(caseClass));

        List<CaseState> states = Lists.newArrayList();
        ReflectionUtils.extractStates(caseClass).stream().forEach(x -> states.add(createState(x.toString())));

        result.setStates(states);
        result.setEvents(getCaseEvents());
        return result;
    }

    private List<CaseEvent> getCaseEvents() {
        List<CaseEvent> events = Lists.newArrayList();
        application.getEvents().forEach(x -> events.add(createEvent(x.toString())));
        return events;
    }

    private CaseViewEvent[] getCaseViewEvents() {
        Set<String> events = application.getEvents();
        List<CaseViewEvent> collect = events
                .stream()
                .map(this::createCaseViewEvent)
                .collect(toList());

        CaseViewEvent[] caseViewEvents = new CaseViewEvent[collect.size()];
        return collect.toArray(caseViewEvents);
    }


    public CaseView getCaseView(String jurisdictionId, String caseTypeId, String caseId) {
        CaseView caseView = new CaseView();
        caseView.setCaseId(caseId);
        caseView.setTabs(ReflectionUtils.generateCaseViewTabs(application.getCase(caseId)));
        caseView.setChannels(getChannels());
        caseView.setTriggers(getTriggers(caseId));

        caseView.setState(application.getCaseState(caseId));
        CaseViewType caseType = new CaseViewType();
        CaseViewJurisdiction jurisdiction = new CaseViewJurisdiction();
        jurisdiction.setId(jurisdictionId);
        jurisdiction.setName(jurisdictionId);
        jurisdiction.setDescription(jurisdictionId);
        caseType.setJurisdiction(jurisdiction);
        caseType.setDescription(jurisdictionId);
        caseType.setId(caseTypeId);
        caseView.setCaseType(caseType);
        caseView.setEvents(getCaseViewEvents());

        return caseView;
    }

    private CaseViewTrigger[] getTriggers(String caseId) {
        return (CaseViewTrigger[]) application.getTriggers(caseId).toArray();
    }

    private String[] getChannels() {
        String[] strings = new String[1];
        strings[0] = "channel1";
        return strings;
    }

    private CaseEvent createEvent(String s) {
        CaseEvent event = new CaseEvent();
        event.setId(s);
        event.setName(s);
        return event;
    }

    private CaseViewEvent createCaseViewEvent(String s) {
        CaseViewEvent event = new CaseViewEvent();
        event.setId(new Random().nextLong());
        event.setComment("Hardcoded TODO");
        event.setEventName(s);
        event.setStateId("Hardcoded TODO");
        event.setStateName("Hardcoded TODO");
        event.setSummary(s);
        event.setTimestamp(LocalDateTime.now());
        event.setUserFirstName("First");
        event.setUserLastName("Last");
        event.setEventId("Hardcoded TODO");
        event.setUserId(new Random().nextLong());
        return event;
    }

    private CaseState createState(String name) {
        CaseState result = new CaseState();
        result.setId(name);
        result.setName(name);
        return result;
    }

    public WorkbasketInput[] getWorkBasketInputs() {
        return ReflectionUtils.generateWorkbasketInputs(caseClass).toArray(new WorkbasketInput[0]);
    }

    public SearchResultView search(Map<String, String> criteria) {
        SearchResultViewColumn[] columns = ReflectionUtils.generateFields(caseClass).stream().map(x ->
                new SearchResultViewColumn(x.getId(), x.getFieldType(), x.getLabel(), 1)
        ).toArray(SearchResultViewColumn[]::new);

        List<ICase> cases = application.getCases(criteria);
        SearchResultViewItem[] items = cases.stream().map(x -> {
            return new SearchResultViewItem(x.getCaseId(), objectMapper.valueToTree(ReflectionUtils.getCaseView(x)));
        }).toArray(SearchResultViewItem[]::new);
        return new SearchResultView(columns, items);
    }

    public SearchInput[] searchInputs() {
        return ReflectionUtils.generateSearchInputs(caseClass).toArray(new SearchInput[0]);
    }

    public String onCaseCreated(JsonNode node) throws JsonProcessingException {
        ICase c = (ICase) objectMapper.treeToValue(node, caseClass);
        return application.saveCase(c);
    }

    public CaseEventTrigger getCaseEventTrigger(String caseId, String eventTriggerId) {

        List<CaseViewField> fields = ReflectionUtils.getCaseViewFieldForEvent(caseClass, eventTriggerId);

        CaseEventTrigger caseEventTrigger = new CaseEventTrigger();
        caseEventTrigger.setCaseFields(fields);
        caseEventTrigger.setCaseId(caseId);
        caseEventTrigger.setId(eventTriggerId);
        caseEventTrigger.setDescription("blah");
        caseEventTrigger.setName(eventTriggerId);
        caseEventTrigger.setEventToken("hi");
        caseEventTrigger.setWizardPages(
            singletonList(
                new WizardPage(
                    UUID.randomUUID().toString(),
                    null,
                    null,
                    fields.stream()
                        .map(f -> new WizardPageField(
                            f.getId(),
                            null,
                            null
                        ))
                        .collect(toList()),
                    null
                )
            ));

        return caseEventTrigger;
    }

    public void handleTrigger(String caseID, CaseDataContent caseDetails) {
        application.handleTrigger(caseID, caseDetails);
    }
}
