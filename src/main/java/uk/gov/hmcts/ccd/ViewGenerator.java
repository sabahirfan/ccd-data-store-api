package uk.gov.hmcts.ccd;

import uk.gov.hmcts.ccd.definition.ICaseView;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;

import java.util.List;

public class ViewGenerator {

    public static <T extends ICase> List<CaseField> generate(ICaseView<T> view, T model) {
        throw new RuntimeException();
    }
}
