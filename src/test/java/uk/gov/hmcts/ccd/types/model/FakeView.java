package uk.gov.hmcts.ccd.types.model;

import com.google.common.collect.Lists;
import uk.gov.hmcts.ccd.definition.BaseCaseView;
import uk.gov.hmcts.ccd.definition.ICaseView;

import java.util.Map;

public class FakeView extends BaseCaseView<FakeCase> {
    public String getTab() {
        return "tab2";
    }

    @Override
    protected void onRender(FakeCase theCase) {
        render(null);
        render(theCase.getCaseId());
        render(theCase.getParty());
    }
}
