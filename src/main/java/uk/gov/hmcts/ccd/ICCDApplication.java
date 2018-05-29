package uk.gov.hmcts.ccd;

import com.google.common.collect.ImmutableSet;
import uk.gov.hmcts.ccd.data.casedetails.search.MetaData;
import uk.gov.hmcts.ccd.domain.model.definition.CaseEvent;
import uk.gov.hmcts.ccd.domain.model.definition.CaseState;
import uk.gov.hmcts.ccd.domain.model.definition.CaseType;
import uk.gov.hmcts.ccd.domain.model.definition.Jurisdiction;
import uk.gov.hmcts.ccd.domain.model.search.SearchResultView;

import java.util.List;
import java.util.Map;

public interface ICCDApplication<T extends ICase> {
    Class<T> getCaseClass();
    List<CaseState> getStates();
    List<T> getCases();
    void saveCase(T c);

    List<CaseEvent> getEvents();
}
