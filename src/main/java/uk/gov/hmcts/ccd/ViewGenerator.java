package uk.gov.hmcts.ccd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import uk.gov.hmcts.ccd.definition.ICaseView;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @SneakyThrows
    public static <T extends ICase> List<ICaseView<T>> getViews(String packageName) {
        BeanDefinitionRegistry bdr = new SimpleBeanDefinitionRegistry();
        ClassPathBeanDefinitionScanner s = new ClassPathBeanDefinitionScanner(bdr);

        TypeFilter tf = new AssignableTypeFilter(ICaseView.class);
        s.setIncludeAnnotationConfig(false);
        s.resetFilters(false);
        s.addIncludeFilter(tf);
        s.scan("uk.gov.hmcts.ccd");
        String[] beans = bdr.getBeanDefinitionNames();
        List<ICaseView<T>> result = Lists.newArrayList();
        for (String bean : beans) {
            Class<T> c = (Class<T>) Class.forName(bdr.getBeanDefinition(bean).getBeanClassName());
            Constructor<?> ctor = c.getConstructor();
            ICaseView<T> view = (ICaseView<T>) ctor.newInstance();
            result.add(view);
        }

        return result;
    }
}
