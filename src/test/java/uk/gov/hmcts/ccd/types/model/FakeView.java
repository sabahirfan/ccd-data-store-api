package uk.gov.hmcts.ccd.types.model;

import uk.gov.hmcts.ccd.definition.FieldRenderer;
import uk.gov.hmcts.ccd.definition.ICaseView;

import java.util.List;
import java.util.Set;

public class FakeView implements ICaseView<FakeCase> {
    public String getTab() {
        return "tab2";
    }

    public void render(FakeCase theCase, List<Object> fields) {
        fields.add(theCase.getCaseId());
        fields.add(theCase.getState());
    }
}
