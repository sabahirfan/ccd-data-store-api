package uk.gov.hmcts.ccd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import uk.gov.hmcts.ccd.definition.CaseEventField;
import uk.gov.hmcts.ccd.definition.CaseListField;
import uk.gov.hmcts.ccd.definition.CaseSearchableField;
import uk.gov.hmcts.ccd.definition.CaseViewField;
import uk.gov.hmcts.ccd.definition.CaseViewTabs;
import uk.gov.hmcts.ccd.definition.ComplexType;
import uk.gov.hmcts.ccd.definition.FieldLabel;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewTab;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;
import uk.gov.hmcts.ccd.domain.model.search.Field;
import uk.gov.hmcts.ccd.domain.model.search.SearchInput;
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

    public static List<CaseField> getCaseListFields(Class c) {
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
                    result.addAll(getCaseListFields(field.getType()));
                }
            }
        }

        return result;
    }

    /**
     * Get a view model for the case list consisting of a flattened
     * map of all the fields the list needs.
     */
    public static Map<String, Object> getCaseListViewModel(Object c) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            for (java.lang.reflect.Field field : c.getClass().getDeclaredFields()) {
                CaseListField cf = field.getAnnotation(CaseListField.class);
                field.setAccessible(true);
                if (cf != null) {
                    result.put(field.getName(), field.get(c));
                } else {
                    if (field.getAnnotation(ComplexType.class) != null) {
                        result.putAll(getCaseListViewModel(field.get(c)));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static List<CaseField> getCaseViewFieldForEvent(
        Class eventClass
    ) {
        return Arrays.stream(eventClass.getDeclaredFields())
            .map(f -> {
                CaseEventField annotation = f.getAnnotation(CaseEventField.class);
                if (annotation != null) {
                    CaseField cvf = new CaseField();
                    cvf.setId(f.getName());
                    cvf.setFieldType(getFieldType(f));
                    cvf.setOrder(annotation.order());
                    cvf.setLabel(annotation.label());
                    cvf.setDisplayContext("OPTIONAL");
                    cvf.setSecurityLabel("PUBLIC");

                    return cvf;
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

    public static List<SearchInput> generateSearchInputs(Class c) {
        List<SearchInput> result = Lists.newArrayList();

        for (java.lang.reflect.Field field : c.getDeclaredFields()) {
            CaseSearchableField annotation = field.getAnnotation(CaseSearchableField.class);
            if (annotation != null) {
                SearchInput searchInput = new SearchInput(
                        new Field(
                                field.getName()
                                , getFieldType(field)
                        ),
                        annotation.label(),
                        annotation.order()
                );

                result.add(searchInput);
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
                    CaseField[] fields = caseViewTab.getFields();
                    if (fields == null) {
                        fields = new CaseField[1];
                    }
                    CaseField caseViewField = getCaseViewField(c,declaredField);
                    if (null == caseViewField) {
                        continue;
                    }

                    caseViewField.setOrder(i + 1);
                    caseViewField.setLabel(cf.label());
                    caseViewField.setId(cf.label());

                    fields[fields.length - 1] = caseViewField;
                    caseViewTab.setFields(fields);
                    caseViewTabs.put(tab, caseViewTab);
                }
            }
        }

        CaseViewTab[] caseViewTabsArr = new CaseViewTab[1];
        return caseViewTabs.values().toArray(caseViewTabsArr);
    }

    private static CaseField
    getCaseViewField(
            ICase c,
            java.lang.reflect.Field declaredField
    ) {
        String t = getFieldType(declaredField).getType();
        if (t.equals("Complex")) {
            try {
                declaredField.setAccessible(true);
                return mapComplexType(declaredField.get(c));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (t.equals("Unknown")) {
            return null;
        }
        CaseField caseViewField = new CaseField();
        caseViewField.setFieldType(getFieldType(declaredField));
        ObjectMapper m = new ObjectMapper();
        try {
            declaredField.setAccessible(true);
            caseViewField.setValue(m.valueToTree(declaredField.get(c)));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return caseViewField;
    }

    public static String determineFieldType(Class c) {
        switch (c.getSimpleName()) {
            case "String":
                return "Text";
            case "Integer":
            case "Long":
                return "Number";
            case "LocalDate":
            case "LocalDateTime":
            case "Date":
                return "Date";
        }
        return "Unknown";
    }


    public static FieldType getFieldType(Class c) {
        FieldType type = new FieldType();
        String typeId = determineFieldType(c);
        type.setId(typeId);
        type.setType(typeId);
        return type;
    }

    private static FieldType getFieldType(java.lang.reflect.Field field) {
        FieldType type = getFieldType(field.getType());
        if (field.getAnnotation(ComplexType.class) != null) {
            type.setType("Complex");
        }
        return type;
    }

    public static CaseField mapComplexType(Object instance) {
        CaseField result = new CaseField();
        FieldType type = new FieldType();
        type.setType("Complex");
        result.setFieldType(type);
        List<CaseField> complexFields = Lists.newArrayList();
        type.setComplexFields(complexFields);
        for (java.lang.reflect.Field field : instance.getClass().getDeclaredFields()) {
            FieldType fieldType = getFieldType(field);
            if (fieldType.getType().equals("Unknown")) {
                continue;
            }
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(instance);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            CaseField f = new CaseField();
            FieldLabel label = field.getAnnotation(FieldLabel.class);
            if (null != label) {
                f.setLabel(label.value());
            }
            f.setFieldType(fieldType);
            f.setId(field.getName());
            if (fieldType.getType().equals("Complex")) {
                CaseField complex = mapComplexType(value);

                FieldType cType = new FieldType();
                cType.setId(field.getName());
                cType.setType("Complex");
                cType.setComplexFields(complex.getFieldType().getComplexFields());
                f.setFieldType(cType);
            } else {
                f.setValue(new ObjectMapper().valueToTree(value));
            }
            complexFields.add(f);
        }

        return result;
    }
}
