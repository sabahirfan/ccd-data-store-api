package uk.gov.hmcts.ccd.data.user;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.ccd.ApplicationParams;
import uk.gov.hmcts.ccd.AuthCheckerConfiguration;
import uk.gov.hmcts.ccd.data.SecurityUtils;
import uk.gov.hmcts.ccd.data.casedetails.SecurityClassification;
import uk.gov.hmcts.ccd.data.definition.CachedCaseDefinitionRepository;
import uk.gov.hmcts.ccd.data.definition.CaseDefinitionRepository;
import uk.gov.hmcts.ccd.domain.model.aggregated.*;
import uk.gov.hmcts.ccd.domain.model.definition.CaseType;
import uk.gov.hmcts.ccd.domain.model.definition.Jurisdiction;
import uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException;
import uk.gov.hmcts.ccd.endpoint.exceptions.ServiceException;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.ServiceAndUserDetails;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

@Named
@Qualifier(DefaultUserRepository.QUALIFIER)
@Singleton
public class DefaultUserRepository implements UserRepository {

    public static final String QUALIFIER = "default";

    private static final String RELEVANT_ROLES = "caseworker-%s";
    private final ApplicationParams applicationParams;
    private final CaseDefinitionRepository caseDefinitionRepository;
    private final SecurityUtils securityUtils;
    private final RestTemplate restTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUserRepository.class);

    @Inject
    public DefaultUserRepository(final ApplicationParams applicationParams,
                                 @Qualifier(CachedCaseDefinitionRepository.QUALIFIER) final CaseDefinitionRepository caseDefinitionRepository,
                                 final SecurityUtils securityUtils,
                                 final RestTemplate restTemplate) {
        this.applicationParams = applicationParams;
        this.caseDefinitionRepository = caseDefinitionRepository;
        this.securityUtils = securityUtils;
        this.restTemplate = restTemplate;
    }

    @Override
    public UserProfile getUserSettings() {
        final IDAMProperties idamProperties = getUserDetails();
        final String userId = idamProperties.getEmail();
        final UserProfile userProfile = new UserProfile();
        final UserDefault userDefault = getUserDefaultSettings(userId);
        final JurisdictionDisplayProperties[] jurisdictions = userDefault.getJurisdictions()
            .stream()
            .map(j -> {
                final JurisdictionDisplayProperties jurisdiction = new JurisdictionDisplayProperties();
                jurisdiction.setId(j.getId());
                // TODO https://tools.hmcts.net/jira/browse/RDM-1433
                Optional<Jurisdiction> caseTypeJurisdiction = caseDefinitionRepository.getCaseTypesForJurisdiction(jurisdiction.getId())
                    .stream()
                    .findAny()
                    .map(CaseType::getJurisdiction);
                if (caseTypeJurisdiction.isPresent()) {
                    jurisdiction.setName(caseTypeJurisdiction.get().getName());
                    jurisdiction.setDescription(caseTypeJurisdiction.get().getDescription());
                }
                return jurisdiction;
            })
            .toArray(JurisdictionDisplayProperties[]::new);

        if (jurisdictions.length == 0)
            throw new BadRequestException("No Case Type's found for the Jurisdictions associated with User " + userId);
        userProfile.setJurisdictions(jurisdictions);
        userProfile.getUser().setIdamProperties(idamProperties);

        final WorkbasketDefault workbasketDefault = new WorkbasketDefault();
        workbasketDefault.setJurisdictionId(userDefault.getWorkBasketDefaultJurisdiction());
        workbasketDefault.setCaseTypeId(userDefault.getWorkBasketDefaultCaseType());
        workbasketDefault.setStateId(userDefault.getWorkBasketDefaultState());
        userProfile.getDefaultSettings().setWorkbasketDefault(workbasketDefault);
        return userProfile;
    }

    @Override
    public IDAMProperties getUserDetails() {
        final HttpEntity requestEntity = new HttpEntity(securityUtils.userAuthorizationHeaders());
        return restTemplate.exchange(applicationParams.idamUserProfileURL(), HttpMethod.GET, requestEntity, IDAMProperties.class).getBody();
    }

    @Override
    public Set<String> getUserRoles() {
        final ServiceAndUserDetails serviceAndUser = (ServiceAndUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return serviceAndUser.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<SecurityClassification> getUserClassifications(String jurisdictionId) {
        Set<String> roles = this.getUserRoles();

        return roles.stream()
                    .filter(role -> filterRole(jurisdictionId, role))
                    .map(caseDefinitionRepository::getUserRoleClassifications)
                    .filter(Objects::nonNull)
                    .filter(userRole -> Objects.nonNull(userRole.getSecurityClassification()))
                    .map(userRole -> SecurityClassification.valueOf(userRole.getSecurityClassification()))
                    .collect(Collectors.toSet());
    }

    /**
     * Gets user profile default settings.
     * @param userId user id
     * @return UserDefault
     */
    private UserDefault getUserDefaultSettings(final String userId) {
        try {
            final HttpEntity requestEntity = new HttpEntity(securityUtils.authorizationHeaders());
            final Map<String, String> queryParams = new HashMap<>();
            queryParams.put("uid", userId);
            return restTemplate.exchange(applicationParams.userDefaultSettingsURL(),
                HttpMethod.GET, requestEntity, UserDefault.class, queryParams).getBody();
        } catch (HttpStatusCodeException e) {
            LOG.error("Failed to retrieve user profile", e);
            final List<String> headerMessages = e.getResponseHeaders().get("Message");
            final String message = headerMessages != null ? headerMessages.get(0) : e.getMessage();
            if (message != null)
                throw new BadRequestException(message);
            throw new ServiceException("Problem getting user default settings for " + userId);
        }

    }

    private boolean filterRole(final String jurisdictionId, final String role) {
        return startsWithIgnoreCase(role, String.format(RELEVANT_ROLES, jurisdictionId))
            || ArrayUtils.contains(AuthCheckerConfiguration.getCitizenRoles(), role);
    }

}
