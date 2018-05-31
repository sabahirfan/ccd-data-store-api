package uk.gov.hmcts.ccd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseView;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewJurisdiction;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewTab;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewTrigger;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewType;
import uk.gov.hmcts.ccd.domain.model.aggregated.ProfileCaseState;
import uk.gov.hmcts.ccd.domain.model.definition.CaseEvent;
import uk.gov.hmcts.ccd.domain.model.definition.CaseState;
import uk.gov.hmcts.ccd.domain.model.definition.CaseType;
import uk.gov.hmcts.ccd.domain.model.search.Field;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultView;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultViewColumn;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultViewItem;
import uk.gov.hmcts.ccd.domain.model.search.WorkbasketInput;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;

@Configuration
@Service
@SuppressWarnings("unchecked")
public class CoreCaseService {
    private final ICCDApplication application;
    private final CCDAppConfig config;
    private final Class caseClass;

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
                .collect(Collectors.toList());

        CaseViewEvent[] caseViewEvents = new CaseViewEvent[collect.size()];
        return collect.toArray(caseViewEvents);
    }


    public CaseView getCaseView(String jurisdictionId, String caseTypeId, String caseId) {
        CaseView caseView = new CaseView();
        caseView.setCaseId(caseId);
        caseView.setTabs(getCaseViewTabs());
        caseView.setChannels(getChannels());
        caseView.setTriggers(getTriggers());
        ProfileCaseState state = new ProfileCaseState();
        state.setId("open");
        state.setName("Open");
        state.setDescription("Open");
        caseView.setState(state);
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

    private CaseViewTrigger[] getTriggers() {
        CaseViewTrigger[] caseViewTriggers = new CaseViewTrigger[1];
        CaseViewTrigger caseViewTrigger = new CaseViewTrigger();
        caseViewTrigger.setId("MoreTimeRequestPaper");
        caseViewTrigger.setName("More time request paper");
        caseViewTrigger.setOrder(1);

        caseViewTriggers[0] = caseViewTrigger;
        return caseViewTriggers;
    }

    private String[] getChannels() {
        String[] strings = new String[1];
        strings[0] = "channel1";
        return strings;
    }

    private CaseViewTab[] getCaseViewTabs() {
        CaseViewTab caseViewTab1 = createCaseViewTab(1, "ApplicantTab", "Applicant");
        CaseViewTab caseViewTab2 = createCaseViewTab(2, "ProsecutorTab", "Prosecutor");
        CaseViewTab[] caseViewTabs = new CaseViewTab[2];
        caseViewTabs[0] = caseViewTab1;
        caseViewTabs[1] = caseViewTab2;
        return caseViewTabs;
    }

    private CaseViewTab createCaseViewTab(int order, String id, String label) {
        CaseViewTab caseViewTab = new CaseViewTab();
        caseViewTab.setOrder(order);
        caseViewTab.setId(id);
        caseViewTab.setLabel(label);
        return caseViewTab;
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
        return ReflectionUtils.generateFields(caseClass).stream().map(x -> {
            WorkbasketInput i = new WorkbasketInput();
            Field field = new Field();
            field.setType(x.getFieldType());
            field.setId(x.getId());
            i.setField(field);
            i.setLabel(x.getLabel());
            return i;
        }).toArray(WorkbasketInput[]::new);
    }

    public SearchResultView search(Map<String, String> criteria) {
        SearchResultViewColumn[] columns = ReflectionUtils.generateFields(caseClass).stream().map(x ->
                new SearchResultViewColumn(x.getId(), x.getFieldType(), x.getLabel(), 1)
        ).toArray(SearchResultViewColumn[]::new);

        ObjectMapper mapper = new ObjectMapper();
        List<ICase> cases = application.getCases(criteria);
        SearchResultViewItem[] items = cases.stream().map(x -> {
            return new SearchResultViewItem(x.getCaseId(), mapper.valueToTree(x));
        }).toArray(SearchResultViewItem[]::new);
        return new SearchResultView(columns, items);
    }

    public void onCaseCreated(JsonNode node) throws JsonProcessingException {
        ICase c = (ICase) new ObjectMapper().treeToValue(node, caseClass);
        application.saveCase(c);
    }
}
