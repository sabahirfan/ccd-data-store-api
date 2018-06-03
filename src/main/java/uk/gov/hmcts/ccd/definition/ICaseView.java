package uk.gov.hmcts.ccd.definition;

import uk.gov.hmcts.ccd.ICase;

import java.util.List;

public interface ICaseView<T extends ICase> {
    String getTab();
    List<Object> render(T theCase);
}
