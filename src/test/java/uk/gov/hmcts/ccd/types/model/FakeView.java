package uk.gov.hmcts.ccd.types.model;

import com.google.common.collect.Lists;
import uk.gov.hmcts.ccd.definition.FieldRenderer;
import uk.gov.hmcts.ccd.definition.ICaseView;

import java.util.List;
import java.util.Set;

public class FakeView implements ICaseView<FakeCase> {
    public String getTab() {
        return "tab2";
    }

    public List<Object> render(FakeCase theCase) {
        return Lists.newArrayList(
            theCase.getCaseId(),
            theCase.getParty()
        );
    }
}
