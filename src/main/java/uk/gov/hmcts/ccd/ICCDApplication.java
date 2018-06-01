package uk.gov.hmcts.ccd;

import com.google.common.collect.ImmutableSet;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewTrigger;
import uk.gov.hmcts.ccd.domain.model.aggregated.ProfileCaseState;
import uk.gov.hmcts.ccd.domain.model.std.CaseDataContent;

import java.util.List;
import java.util.Map;

public interface ICCDApplication<T extends ICase> {

    List<T> getCases(Map<String, String> searchCriteria);

    String saveCase(T c);

    T getCase(String id);

    ImmutableSet<String> getEvents();

    List<CaseViewTrigger> getTriggers(String caseId);

    void handleTrigger(String caseID, CaseDataContent caseDetails);

    ProfileCaseState getCaseState(String caseId);
}
