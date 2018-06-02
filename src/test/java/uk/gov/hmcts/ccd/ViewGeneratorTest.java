package uk.gov.hmcts.ccd;

import org.assertj.core.util.Lists;
import org.junit.Test;
import uk.gov.hmcts.ccd.types.model.FakeCase;
import uk.gov.hmcts.ccd.types.model.FakeView;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewGeneratorTest {
    @Test
    public void extractsFields() {
        FakeView view = new FakeView();
        FakeCase fakeCase = new FakeCase("A", "B");
        List<Object> fields = Lists.newArrayList();
        view.render(fakeCase, fields);
        assertThat(fields.size()).isEqualTo(2);
    }
}
