package uk.gov.hmcts.ccd.definition;

import uk.gov.hmcts.ccd.ICase;

import java.util.List;
import java.util.Map;

public interface ICaseView<T extends ICase> {
    String getTab();
    void render(ICaseRenderer renderer, T theCase);
}
