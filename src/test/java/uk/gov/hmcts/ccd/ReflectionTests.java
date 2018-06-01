package uk.gov.hmcts.ccd;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewTab;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.types.*;
import uk.gov.hmcts.ccd.types.nested.WithSubClass;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ReflectionTests {


    @Test
    public void generatesCaseField() {
        List<CaseField> result = ReflectionUtils.getCaseListFields(TwoFields.class);
        assertThat(result.size()).isEqualTo(1);
        CaseField first = result.get(0);
        assertThat(first.getFieldType().getType()).isEqualTo("Text");
        assertThat(first.getId()).isEqualTo("fooBar");
        assertThat(first.getLabel()).isEqualTo("Foo Bar");
    }

    @Test
    public void extractsSubClassFields() {
        List<CaseField> result = ReflectionUtils.getCaseListFields(WithSubClass.class);
        assertThat(result.size()).isEqualTo(2);
        CaseField first = result.get(0);
        assertThat(first.getFieldType().getType()).isEqualTo("Text");
        assertThat(first.getId()).isEqualTo("fooBar");
        assertThat(first.getLabel()).isEqualTo("Foo Bar");

        CaseField second = result.get(1);
        assertThat(second.getFieldType().getType()).isEqualTo("Text");
        assertThat(second.getId()).isEqualTo("line1");
        assertThat(second.getLabel()).isEqualTo("Nested field");
    }

    @Test
    public void getsView() {
        WithSubClass w = new WithSubClass();
        Map<String, Object> result = ReflectionUtils.getCaseListViewModel(w);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isEqualTo(ImmutableMap.of(
            "fooBar", "foo",
            "line1", "test line 1"
        ));
    }

    @Test
    public void extractsCaseStates() {
        ImmutableSet<? extends Enum> states = ReflectionUtils.extractStates(FakeCase.class);
        assertThat(states).isEqualTo(ImmutableSet.copyOf(
            FakeState.values()
        ));
    }

    @Test
    public void mapsComplextype() {
        CaseViewField field = ReflectionUtils.mapComplexType(new TestAddress());
        assertThat(field.getFieldType().getType()).isEqualTo("Complex");
        assertThat(field.getFieldType().getComplexFields().size()).isGreaterThanOrEqualTo(3);
        assertThat(field.getFieldType().getComplexFields().get(0).getLabel()).isEqualTo("test");
    }
    @Test
    public void mapsNestedComplexType() {
        CaseViewField field = ReflectionUtils.mapComplexType(new Party());
        assertThat(field.getFieldType().getType()).isEqualTo("Complex");
        assertThat(field.getFieldType().getComplexFields().size()).isGreaterThanOrEqualTo(2);
        assertThat(field.getFieldType().getComplexFields().get(1).getFieldType().getType()).isEqualTo("Complex");
    }

    @Test
    public void extractsCaseType() {
        assertThat(ReflectionUtils.getCaseType(FakeCCDImplementation.class)).isEqualTo(FakeCase.class);
    }

    @Test
    public void extractsDate() {
        List<CaseField> fields = ReflectionUtils.getCaseListFields(HasDate.class);
        assertThat(fields.size()).isEqualTo(3);
        assertThat(fields.get(0).getFieldType().getType()).isEqualTo("Date");
        assertThat(fields.get(1).getFieldType().getType()).isEqualTo("Date");
        assertThat(fields.get(2).getFieldType().getType()).isEqualTo("Date");
    }

    @Test
    public void testRhubarbTabView() {
        CaseViewTab[] result = ReflectionUtils.generateCaseViewTabs(new FakeCase("defendantName", "prosecutorName"));
        assertThat(result.length).isEqualTo(3);
        CaseViewTab addressTab = result[0];
        assertThat(addressTab.getFields().length).isEqualTo(1);
        CaseViewField vf = addressTab.getFields()[0];
        assertThat(vf.getFieldType().getComplexFields().size()).isEqualTo(2);
    }

}
