package uk.gov.hmcts.ccd;

import com.google.common.collect.Lists;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class FieldGenerator {
    public static List<CaseField> generateFields(Class c) {
        List<CaseField> result = Lists.newArrayList();
        for (Method method : c.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                CaseField field = new CaseField();
                String id = method.getName().replace("get", "");
                field.setId(id);
                FieldType type = new FieldType();
                type.setType(method.getReturnType().getSimpleName().toLowerCase());
                field.setFieldType(type);
                result.add(field);
            }
        }
        return result;
    }
}
