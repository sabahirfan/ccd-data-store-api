package uk.gov.hmcts.ccd.data.definition;

import uk.gov.hmcts.ccd.domain.model.definition.CaseType;
import uk.gov.hmcts.ccd.domain.model.definition.FieldType;
import uk.gov.hmcts.ccd.domain.model.definition.UserRole;

import java.util.List;

public interface CaseDefinitionRepository {
    List<CaseType> getCaseTypesForJurisdiction(String jurisdictionId);

    CaseType getCaseType(String caseTypeId);

    CaseType getCaseType(int version, String caseTypeId);

    List<FieldType> getBaseTypes();

    UserRole getUserRoleClassifications(String userRole);

    CaseTypeDefinitionVersion getLatestVersion(String caseTypeId);
}
