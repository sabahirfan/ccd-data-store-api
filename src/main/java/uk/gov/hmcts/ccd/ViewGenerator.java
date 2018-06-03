package uk.gov.hmcts.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import uk.gov.hmcts.ccd.definition.ICaseView;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;

import java.util.List;

public class ViewGenerator {

    private static ObjectMapper mapper = new ObjectMapper();
    private static ImmutableSet PRIMITIVES = ImmutableSet.of(
        "Text",
        "Date",
        "Number"
    );

    public static <T extends ICase> List<CaseField> generate(ICaseView<T> view, T model) {
        throw new RuntimeException();
    }

    public static CaseField convert(Object value) {
        if (PRIMITIVES.contains(ReflectionUtils.determineFieldType(value.getClass()))) {
            CaseField result = new CaseField();
            result.setFieldType(ReflectionUtils.getFieldType(value.getClass()));
            result.setValue(mapper.valueToTree(value));
            return result;
        }
        return ReflectionUtils.mapComplexType(value);
    }
}
