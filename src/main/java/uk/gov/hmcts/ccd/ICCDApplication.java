package uk.gov.hmcts.ccd;

import uk.gov.hmcts.ccd.domain.model.definition.CaseType;

import java.util.List;

public interface ICCDApplication {
    List<CaseType> getCaseTypes();
}
