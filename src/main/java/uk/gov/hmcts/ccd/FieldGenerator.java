package uk.gov.hmcts.ccd;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
        for (Field field : c.getDeclaredFields()) {
            CaseListField cf = field.getAnnotation(CaseListField.class);
            if (cf != null) {
                CaseField caseField = new CaseField();
                caseField.setId(field.getName());
                FieldType type = new FieldType();
                String typeId = typeMap.get(field.getType().getSimpleName());
                type.setId(typeId);
                type.setType(typeId);
                caseField.setFieldType(type);
                caseField.setLabel(cf.label());
                result.add(caseField);
            }
        }
        return result;
    }
}
