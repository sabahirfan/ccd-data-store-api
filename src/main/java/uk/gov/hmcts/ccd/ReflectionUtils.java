package uk.gov.hmcts.ccd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import uk.gov.hmcts.ccd.definition.CaseEventFields;
import uk.gov.hmcts.ccd.definition.CaseListField;
import uk.gov.hmcts.ccd.definition.CaseSearchableField;
import uk.gov.hmcts.ccd.definition.CaseViewField;
import uk.gov.hmcts.ccd.definition.CaseViewTabs;
import uk.gov.hmcts.ccd.definition.ComplexType;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewTab;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;
import uk.gov.hmcts.ccd.domain.model.search.Field;
import uk.gov.hmcts.ccd.domain.model.search.WorkbasketInput;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReflectionUtils {
    private static ImmutableMap<String, String> typeMap = ImmutableMap.of(
        "String", "Text"
    );

    public static List<CaseField> generateFields(Class c) {
        List<CaseField> result = Lists.newArrayList();
        for (java.lang.reflect.Field field : c.getDeclaredFields()) {
            CaseListField cf = field.getAnnotation(CaseListField.class);
            if (cf != null) {
                CaseField caseField = new CaseField();
                caseField.setId(field.getName());
                FieldType type = getFieldType(field);
                caseField.setFieldType(type);
                caseField.setLabel(cf.label());
                result.add(caseField);
            } else {
                if (field.getAnnotation(ComplexType.class) != null) {
                    result.addAll(generateFields(field.getType()));
                }
            }
        }

        return result;
    }

    public static Map<String, Object> getCaseView(Object c) {

        Map<String, Object> result = Maps.newHashMap();
        try {
        for (java.lang.reflect.Field field : c.getClass().getDeclaredFields()) {
            CaseListField cf = field.getAnnotation(CaseListField.class);
            field.setAccessible(true);
            if (cf != null) {
                    result.put(field.getName(), field.get(c));
            } else {
                if (field.getAnnotation(ComplexType.class) != null) {
                    result.putAll(getCaseView(field.get(c)));
                }
            }
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static List<uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField> getCaseViewFieldForEvent(
        Class caseClass,
        String eventId
    ) {
        return Arrays.stream(caseClass.getDeclaredFields())
            .map(f -> {
                CaseEventFields annotation = f.getAnnotation(CaseEventFields.class);
                if (annotation != null) {
                    return Arrays.stream(annotation.value())
                        .filter(e -> e.event().equals(eventId))
                        .map(e -> {
                            uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField cvf = new uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField();
                            cvf.setId(e.label());
                            cvf.setFieldType(getFieldType(f));
                            cvf.setOrder(e.order());
                            cvf.setLabel(e.label());
                            cvf.setDisplayContext("OPTIONAL");
                            cvf.setSecurityLabel("PUBLIC");

                            return cvf;
                        })
                        .findFirst()
                        .orElse(null);
                } else {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static List<WorkbasketInput> generateWorkbasketInputs(Class c) {
        List<WorkbasketInput> result = Lists.newArrayList();

        for (java.lang.reflect.Field field : c.getDeclaredFields()) {
            CaseSearchableField annotation = field.getAnnotation(CaseSearchableField.class);
            if (annotation != null) {
                WorkbasketInput workbasketInput = new WorkbasketInput();

                workbasketInput.setLabel(annotation.label());
                workbasketInput.setOrder(annotation.order());
                workbasketInput.setField(
                    new Field(
                        field.getName(),
                        getFieldType(field)
                    )
                );

                result.add(workbasketInput);
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

    public static CaseViewTab[] generateCaseViewTabs(ICase c) {
        Class caseClass = c.getClass();
        Map<String, CaseViewTab> caseViewTabs = new HashMap<>();
        CaseViewTabs annotation = (CaseViewTabs) caseClass.getAnnotation(CaseViewTabs.class);
        String[] caseTabs = annotation.value();
        for (int i = 0; i < caseTabs.length; i++) {
            String tab = caseTabs[i];
            CaseViewTab caseViewTab = new CaseViewTab();
            caseViewTab.setOrder(i + 1);
            caseViewTab.setId(tab);
            caseViewTab.setLabel(tab);
            caseViewTabs.put(tab, caseViewTab);
        }

        java.lang.reflect.Field[] declaredFields = caseClass.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            java.lang.reflect.Field declaredField = declaredFields[i];
            CaseViewField cf = declaredField.getAnnotation(CaseViewField.class);
            if (cf != null) {
                for (String tab : cf.tab()) {
                    CaseViewTab caseViewTab = caseViewTabs.get(tab);
                    uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField[] fields = caseViewTab.getFields();
                    if (fields == null) {
                        fields = new uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField[1];
                    }
                    uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField caseViewField = getCaseViewField(c, i, declaredField, cf);

                    fields[fields.length - 1] = caseViewField;
                    caseViewTab.setFields(fields);
                    caseViewTabs.put(tab, caseViewTab);
                }
            }
        }

        CaseViewTab[] caseViewTabsArr = new CaseViewTab[1];
        return caseViewTabs.values().toArray(caseViewTabsArr);
    }

    private static uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField
    getCaseViewField(
            ICase c, int i,
            java.lang.reflect.Field declaredField,
            CaseViewField cf
    ) {
        uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField caseViewField = new uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField();
        caseViewField.setId(cf.label());
        FieldType fieldType = new FieldType();
        fieldType.setType(determineFieldType(declaredField));
        fieldType.setId("foo");
        caseViewField.setFieldType(fieldType);
        caseViewField.setOrder(i + 1);
        caseViewField.setLabel(cf.label());
        ObjectMapper m = new ObjectMapper();
        try {
            declaredField.setAccessible(true);
            caseViewField.setValue(m.valueToTree(declaredField.get(c)));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return caseViewField;
    }

    private static String determineFieldType(java.lang.reflect.Field declaredField) {
        switch (declaredField.getType().getSimpleName()) {
            case "String":
                return "Text";
            case "Integer":
            case "Long":
                return "Number";
        }
        throw new RuntimeException("unsupported type " + declaredField.getType());
    }


    private static FieldType getFieldType(java.lang.reflect.Field field) {
        FieldType type = new FieldType();
        String typeId = typeMap.get(field.getType().getSimpleName());
        type.setId(typeId);
        type.setType(typeId);
        return type;
    }
}
