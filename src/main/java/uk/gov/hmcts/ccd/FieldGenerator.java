package uk.gov.hmcts.ccd;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class FieldGenerator {
    private static ImmutableMap<String, String> typeMap = ImmutableMap.of(
            "String", "Text"
    );
    public static List<CaseField> generateFields(Class c) {
        List<CaseField> result = Lists.newArrayList();
        for (Method method : c.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) &&
                    method.getName().startsWith("get")) {
                Annotation a = method.getAnnotation(CaseListField.class);
                Annotation[] ans = method.getAnnotations();
                if (a != null) {
                    CaseField field = new CaseField();
                    String id = method.getName().replace("get", "");
                    id = Character.toLowerCase(id.charAt(0)) + id.substring(1);
                    field.setId(id);
                    FieldType type = new FieldType();
                    String typeId = typeMap.get(method.getReturnType().getSimpleName());
                    type.setId(id);
                    type.setType(typeId);
                    field.setFieldType(type);
                    field.setLabel("A label");
                    result.add(field);
                }
            }
        }
        return result;
    }
}
