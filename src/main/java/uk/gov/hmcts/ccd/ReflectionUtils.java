package uk.gov.hmcts.ccd;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import uk.gov.hmcts.ccd.definition.CaseListField;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;

import java.lang.reflect.*;
import java.util.List;

public class ReflectionUtils {
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

    public static Class getCaseType(Class c) {
        Type[] params = c.getGenericInterfaces();
        for (Type param : params) {
            if (param instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) param;
                if (p.getRawType().equals(ICCDApplication.class)) {
                    return (Class) p.getActualTypeArguments()[0];
                }
            }
        }
        throw new RuntimeException();
    }

    public static ImmutableSet<? extends Enum> extractStates(Class<? extends ICase> rhubarbCaseClass) {
        try {
            Method m = rhubarbCaseClass.getMethod("getState");
            return (ImmutableSet<? extends Enum>) ImmutableSet.copyOf(m.getReturnType().getEnumConstants());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
