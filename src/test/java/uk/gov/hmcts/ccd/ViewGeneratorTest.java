package uk.gov.hmcts.ccd;

import org.junit.Test;
import uk.gov.hmcts.ccd.definition.ICaseView;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.types.Address;
import uk.gov.hmcts.ccd.types.model.FakeCase;
import uk.gov.hmcts.ccd.types.model.FakeView;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewGeneratorTest {
    @Test
    public void extractsFields() {
        FakeView view = new FakeView();
        FakeCase fakeCase = new FakeCase("A", "B");
        List<Object> fields = view.render(fakeCase);
        assertThat(fields.size()).isEqualTo(2);
    }

    @Test
    public void convertsString() {
        CaseField field = ViewGenerator.convert("hello");
        assertThat(field.getFieldType().getType()).isEqualTo("Text");
        assertThat(field.getValue().isTextual()).isTrue();
    }

    @Test
    public void convertsComplex() {
        CaseField field = ViewGenerator.convert(new Address());
        assertThat(field.getFieldType().getType()).isEqualTo("Complex");
        assertThat(field.getFieldType().getComplexFields().get(0).getValue().toString()).contains("test line 1");
    }
}
