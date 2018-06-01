package uk.gov.hmcts.ccd;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultView;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultViewItem;
import uk.gov.hmcts.ccd.types.FakeCCDImplementation;

import static org.assertj.core.api.Assertions.assertThat;

public class PluginTests {

    CoreCaseService service;

    @Before
    public void setup() {
        FakeCCDImplementation impl = new FakeCCDImplementation();
        service = new CoreCaseService(new CCDAppConfig(), impl);
    }

    @Test
    public void searchesCases() {
        SearchResultView view = service.search(Maps.newHashMap());

        assertThat(view.getSearchResultViewColumns().length).isEqualTo(3);
        SearchResultViewItem[] items = view.getSearchResultViewItems();
        assertThat(items.length).isEqualTo(2);
        assertThat(items[0].getCaseFields().get("defendantName").asText()).isEqualTo("Defendant");
    }
}
