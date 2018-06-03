package uk.gov.hmcts.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.hmcts.ccd.definition.ICaseView;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;

import java.util.List;

public class ViewGenerator {

    private static ObjectMapper mapper = new ObjectMapper();

    public static <T extends ICase> List<CaseField> generate(ICaseView<T> view, T model) {
        throw new RuntimeException();
    }

    public static CaseField convert(Object value) {
        CaseField result = new CaseField();
        result.setFieldType(ReflectionUtils.getFieldType(value.getClass()));
        result.setValue(mapper.valueToTree(value));
        return result;
    }
}
