package uk.gov.hmcts.ccd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccd.domain.model.definition.*;
import uk.gov.hmcts.ccd.domain.model.search.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
@Service
public class CoreCaseService {
    private final ICCDApplication application;
    private final CCDAppConfig config;

    public CoreCaseService(CCDAppConfig config, ICCDApplication application) {
        this.config = config;
        this.application = application;
    }

    public CaseType getCaseType() {
        CaseType result = new CaseType();
        result.setId(config.getCaseTypeId());
        result.setName(config.getCaseTypeId());
        result.setDescription(config.getCaseTypeId());
        result.setCaseFields(FieldGenerator.generateFields(application.getCaseClass()));
        List<CaseState> states = Lists.newArrayList();
        application.getStates().stream().forEach(x -> states.add(createState(x.toString())));
        List<CaseEvent> events = Lists.newArrayList();
        application.getEvents().stream().forEach(x -> events.add(createEvent(x.toString())));

        result.setStates(states);
        result.setEvents(events);
        return result;
    }

    private CaseEvent createEvent(String s) {
        CaseEvent event = new CaseEvent();
        event.setId(s);
        event.setName(s);
        return event;
    }

    private CaseState createState(String name) {
        CaseState result = new CaseState();
        result.setId(name);
        result.setName(name);
        return result;
    }

    public WorkbasketInput[] getWorkBasketInputs() {
        return FieldGenerator.generateFields(application.getCaseClass()).stream().map(x -> {
            WorkbasketInput i = new WorkbasketInput();
            Field field = new Field();
            field.setType(x.getFieldType());
            field.setId(x.getId());
            i.setField(field);
            return i;
        }).toArray(WorkbasketInput[]::new);
    }

    public SearchResultView search() {
        SearchResultViewColumn[] columns = FieldGenerator.generateFields(application.getCaseClass()).stream().map(x ->
                new SearchResultViewColumn(x.getId(), x.getFieldType(), x.getLabel(), 1)
        ).toArray(SearchResultViewColumn[]::new);

        ObjectMapper mapper = new ObjectMapper();
        List<ICase> cases = application.getCases();
        SearchResultViewItem[] items = cases.stream().map(x -> {
           return new SearchResultViewItem(x.getCaseId(), mapper.valueToTree(x));
        }).toArray(SearchResultViewItem[]::new);
        return new SearchResultView(columns, items);
    }

    public void onCaseCreated(JsonNode node) throws JsonProcessingException {
        ICase c = (ICase) new ObjectMapper().treeToValue(node, application.getCaseClass());
        application.saveCase(c);
    }
}
