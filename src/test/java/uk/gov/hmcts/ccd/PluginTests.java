package uk.gov.hmcts.ccd;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ccd.definition.ICaseView;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseView;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultView;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultViewItem;
import uk.gov.hmcts.ccd.types.model.FakeCCDImplementation;
import uk.gov.hmcts.ccd.types.model.FakeView;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PluginTests {

    CoreCaseService service;

    @Before
    public void setup() {
        FakeCCDImplementation impl = new FakeCCDImplementation();
        List<ICaseView> views = Lists.newArrayList(new FakeView());
        service = new CoreCaseService(new CCDAppConfig(), impl, views);
    }

    @Test
    public void searchesCases() {
        SearchResultView view = service.search(Maps.newHashMap());

        assertThat(view.getSearchResultViewColumns().length).isEqualTo(3);
        SearchResultViewItem[] items = view.getSearchResultViewItems();
        assertThat(items.length).isEqualTo(2);
        assertThat(items[0].getCaseFields().get("defendantName").asText()).isEqualTo("Defendant");
    }

    @Test
    public void rendersTabs() {
        CaseView view = service.getCaseView("CMC", "foo", FakeCCDImplementation.fakeCase.getCaseId());
        assertThat(view.getTabs().length).isEqualTo(1);
    }
}
