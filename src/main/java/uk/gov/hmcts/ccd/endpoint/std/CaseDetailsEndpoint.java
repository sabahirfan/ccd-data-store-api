package uk.gov.hmcts.ccd.endpoint.std;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.ccd.AppInsights;
import uk.gov.hmcts.ccd.CoreCaseService;
import uk.gov.hmcts.ccd.data.casedetails.SecurityClassification;
import uk.gov.hmcts.ccd.data.casedetails.search.FieldMapSanitizeOperation;
import uk.gov.hmcts.ccd.data.casedetails.search.MetaData;
import uk.gov.hmcts.ccd.data.casedetails.search.PaginatedSearchMetadata;
import uk.gov.hmcts.ccd.domain.model.callbacks.StartEventTrigger;
import uk.gov.hmcts.ccd.domain.model.definition.CaseDetails;
import uk.gov.hmcts.ccd.domain.model.definition.Document;
import uk.gov.hmcts.ccd.domain.model.std.CaseDataContent;
import uk.gov.hmcts.ccd.domain.service.createcase.CreateCaseOperation;
import uk.gov.hmcts.ccd.domain.service.createevent.CreateEventOperation;
import uk.gov.hmcts.ccd.domain.service.getcase.CaseNotFoundException;
import uk.gov.hmcts.ccd.domain.service.getcase.GetCaseOperation;
import uk.gov.hmcts.ccd.domain.service.startevent.StartEventOperation;
import uk.gov.hmcts.ccd.domain.service.validate.ValidateCaseFieldsOperation;
import uk.gov.hmcts.ccd.endpoint.exceptions.BadRequestException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = "/",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "/", description = "Standard case API")
public class CaseDetailsEndpoint {
    private final AppInsights appInsights;
    private final FieldMapSanitizeOperation fieldMapSanitizeOperation;
    private final ValidateCaseFieldsOperation validateCaseFieldsOperation;
    private final CoreCaseService service;

    @Autowired
    public CaseDetailsEndpoint(

                               final FieldMapSanitizeOperation fieldMapSanitizeOperation,
                               final ValidateCaseFieldsOperation validateCaseFieldsOperation,
                               final AppInsights appinsights,
                               final CoreCaseService service) {
        this.fieldMapSanitizeOperation = fieldMapSanitizeOperation;
        this.validateCaseFieldsOperation = validateCaseFieldsOperation;
        this.appInsights = appinsights;
        this.service = service;
    }


    @RequestMapping(value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/{cid}", method = RequestMethod.GET)
    @ApiOperation(value = "Get case", notes = "Retrieve an existing case with its state and data")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Case found for the given ID"),
        @ApiResponse(code = 400, message = "Invalid case ID"),
        @ApiResponse(code = 404, message = "No case found for the given ID")
    })
    public CaseDetails findCaseDetailsForCaseworker(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final Integer uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Case ID", required = true)
        @PathVariable("cid") final String caseId) {

        throw new RuntimeException("not implemented");
    }


    @RequestMapping(value = "/citizens/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/{cid}", method = RequestMethod.GET)
    @ApiOperation(value = "Get case", notes = "Retrieve an existing case with its state and data")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Case found for the given ID"),
        @ApiResponse(code = 400, message = "Invalid case ID"),
        @ApiResponse(code = 404, message = "No case found for the given ID")
    })
    public CaseDetails findCaseDetailsForCitizen(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final Integer uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Case ID", required = true)
        @PathVariable("cid") final String caseId) {

        throw new RuntimeException("not implemented");
    }


    @RequestMapping(value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/{cid}/event-triggers/{etid}/token", method = RequestMethod.GET)
    @ApiOperation(value = "Start event creation as Case worker", notes = "Start the event creation process for an existing case. Triggers `AboutToStart` callback.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Event creation process started"),
        @ApiResponse(code = 404, message = "No case found for the given ID"),
        @ApiResponse(code = 422, message = "Process could not be started")
    })
    public StartEventTrigger startEventForCaseworker(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final Integer uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Case ID", required = true)
        @PathVariable("cid") final String caseId,
        @ApiParam(value = "Event ID", required = true)
        @PathVariable("etid") final String eventTriggerId,
        @ApiParam(value = "Should `AboutToStart` callback warnings be ignored")
        @RequestParam(value = "ignore-warning", required = false) final Boolean ignoreWarning) {

        throw new RuntimeException("not implemented");
    }


    @RequestMapping(value = "/citizens/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/{cid}/event-triggers/{etid}/token", method = RequestMethod.GET)
    @ApiOperation(value = "Start event creation as Citizen", notes = "Start the event creation process for an existing case. Triggers `AboutToStart` callback.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Event creation process started"),
        @ApiResponse(code = 404, message = "No case found for the given ID"),
        @ApiResponse(code = 422, message = "Process could not be started")
    })
    public StartEventTrigger startEventForCitizen(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final Integer uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Case ID", required = true)
        @PathVariable("cid") final String caseId,
        @ApiParam(value = "Event ID", required = true)
        @PathVariable("etid") final String eventTriggerId,
        @ApiParam(value = "Should `AboutToStart` callback warnings be ignored")
        @RequestParam(value = "ignore-warning", required = false) final Boolean ignoreWarning) {

        throw new RuntimeException("not implemented");
    }


    @RequestMapping(value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/event-triggers/{etid}/token", method = RequestMethod.GET)
    @ApiOperation(value = "Start case creation as Case worker", notes = "Start the case creation process for a new case. Triggers `AboutToStart` callback.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Case creation process started"),
        @ApiResponse(code = 422, message = "Process could not be started")
    })
    public StartEventTrigger startCaseForCaseworker(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final Integer uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Event ID", required = true)
        @PathVariable("etid") final String eventTriggerId,
        @ApiParam(value = "Should `AboutToStart` callback warnings be ignored")
        @RequestParam(value = "ignore-warning", required = false) final Boolean ignoreWarning) {

        throw new RuntimeException("not implemented");
    }


    @RequestMapping(value = "/citizens/{uid}/jurisdictions/{jid}/case-types/{ctid}/event-triggers/{etid}/token", method = RequestMethod.GET)
    @ApiOperation(value = "Start case creation as Citizen", notes = "Start the case creation process for a new case. Triggers `AboutToStart` callback.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Case creation process started"),
        @ApiResponse(code = 422, message = "Process could not be started")
    })
    public StartEventTrigger startCaseForCitizen(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final Integer uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Event ID", required = true)
        @PathVariable("etid") final String eventTriggerId,
        @ApiParam(value = "Should `AboutToStart` callback warnings be ignored")
        @RequestParam(value = "ignore-warning", required = false) final Boolean ignoreWarning) {

        throw new RuntimeException("not implemented");
    }

    @RequestMapping(value = "/data/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(
        value = "Submit case creation as Case worker",
        notes = "Complete the case creation process. This requires a valid event token to be provided, as generated by `startCaseForCaseworker`."
    )
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Case created"),
        @ApiResponse(code = 422, message = "Case submission failed"),
        @ApiResponse(code = 409, message = "Case reference not unique")
    })
    public CaseDetails saveCaseDetailsForCaseWorker(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final String uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Should `AboutToSubmit` callback warnings be ignored")
        @RequestParam(value = "ignore-warning", required = false) final Boolean ignoreWarning,
        @RequestBody final CaseDataContent content) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.valueToTree(content.getData());
        service.onCaseCreated(node);
        return new CaseDetails();
    }

    @RequestMapping(value = "/citizens/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(
        value = "Submit case creation as Citizen",
        notes = "Complete the case creation process. This requires a valid event token to be provided, as generated by `startCaseForCitizen`."
    )
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Case created"),
        @ApiResponse(code = 422, message = "Case submission failed"),
        @ApiResponse(code = 409, message = "Case reference not unique")
    })
    public CaseDetails saveCaseDetailsForCitizen(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final String uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Should `AboutToSubmit` callback warnings be ignored")
        @RequestParam(value = "ignore-warning", required = false) final Boolean ignoreWarning,
        @RequestBody final CaseDataContent content) {

        throw new RuntimeException("not implemented");
    }

    @RequestMapping(value = "/data/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/validate", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Validate a set of fields as Case worker",
        notes = "Validate the case data entered during the case/event creation process."
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Validation OK"),
        @ApiResponse(code = 422, message = "Field validation failed"),
        @ApiResponse(code = 409, message = "Case reference not unique")
    })
    public Map<String, JsonNode> validateCaseDetailsForCaseWorker(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final String uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @RequestBody final CaseDataContent content) throws JsonProcessingException {

        return content.getData();
    }

    @RequestMapping(value = "/citizens/{uid}/jurisdictions/{jid}/case-types/{ctid}/validate", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        value = "Validate a set of fields as Citizen",
        notes = "Validate the case data entered during the case/event creation process."
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Validation OK"),
        @ApiResponse(code = 422, message = "Field validation failed"),
        @ApiResponse(code = 409, message = "Case reference not unique")
    })
    public Map<String, JsonNode> validateCaseDetailsForCitizen(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final String uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @RequestBody final CaseDataContent content) {
        return validateCaseFieldsOperation.validateCaseDetails(jurisdictionId, caseTypeId, content.getEvent(), content.getData());
    }

    @RequestMapping(value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/{cid}/events", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(
        value = "Submit event creation as Case worker",
        notes = "Complete the event creation process. This requires a valid event token to be provided, as generated by `startEventForCaseworker`."
    )
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Case event created"),
        @ApiResponse(code = 409, message = "Case altered outside of transaction"),
        @ApiResponse(code = 422, message = "Event submission failed")
    })
    public CaseDetails createCaseEventForCaseWorker(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final String uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Case ID", required = true)
        @PathVariable("cid") final String caseId,
        @RequestBody final CaseDataContent content) {
        throw new RuntimeException("not implemented");
    }


    @RequestMapping(value = "/citizens/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/{cid}/events", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(
        value = "Submit event creation as Citizen",
        notes = "Complete the event creation process. This requires a valid event token to be provided, as generated by `startEventForCitizen`."
    )
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Case event created"),
        @ApiResponse(code = 409, message = "Case altered outside of transaction"),
        @ApiResponse(code = 422, message = "Event submission failed")
    })
    public CaseDetails createCaseEventForCitizen(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final String uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Case ID", required = true)
        @PathVariable("cid") final String caseId,
        @RequestBody final CaseDataContent content) {
        throw new RuntimeException("not implemented");
    }


    @RequestMapping(value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/{cid}/documents", method = RequestMethod.GET)
    @ApiOperation(value = "Get a list of printable documents for the given case type ")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Documents list for the given case type and id")
    })
    public List<Document> getDocumentsForEvent(
        @PathVariable("jid") String jid,
        @PathVariable("ctid") String ctid,
        @PathVariable("cid") String cid) {
        throw new RuntimeException("not implemented");
    }


    @RequestMapping(value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases", method = RequestMethod.GET)
    @ApiOperation(value = "Get case data for a given case type")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of case data for the given search criteria")})
    public List<CaseDetails> searchCasesForCaseWorkers(@PathVariable("jid") final String jurisdictionId,
                                                      @PathVariable("ctid") final String caseTypeId,
                                                      @RequestParam Map<String, String> queryParameters) {
        return searchCases(jurisdictionId, caseTypeId, queryParameters);
    }


    @RequestMapping(value = "/citizens/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases", method = RequestMethod.GET)
    @ApiOperation(value = "Get case data for a given case type")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of case data for the given search criteria")})
    public List<CaseDetails> searchCasesForCitizens(@PathVariable("jid") final String jurisdictionId,
                                                    @PathVariable("ctid") final String caseTypeId,
                                                    @RequestParam Map<String, String> queryParameters) {
        return searchCases(jurisdictionId, caseTypeId, queryParameters);
    }

    private List<CaseDetails> searchCases(final String jurisdictionId,
                                          final String caseTypeId,
                                          final Map<String, String> queryParameters) {

        throw new RuntimeException("not implemented");
    }


    @RequestMapping(value = "/data/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/pagination_metadata", method = RequestMethod.GET)
    @ApiOperation(value = "Get the pagination metadata for a case data search")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pagination metadata for the given search criteria")})
    public PaginatedSearchMetadata searchCasesMetadataForCaseworkers(@PathVariable("jid") final String jurisdictionId,
                                                                  @PathVariable("ctid") final String caseTypeId,
                                                                  @RequestParam Map<String, String> queryParameters) {
        PaginatedSearchMetadata metadata = new PaginatedSearchMetadata();
        metadata.setTotalPagesCount(1);
        // todo: pass actual search criteria
        metadata.setTotalResultsCount(service.search(null).getSearchResultViewItems().length);
        return metadata;
    }


    @RequestMapping(value = "/citizens/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/pagination_metadata", method = RequestMethod.GET)
    @ApiOperation(value = "Get the pagination metadata for a case data search")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pagination metadata for the given search criteria")})
    public PaginatedSearchMetadata searchCasesMetadataForCitizens(@PathVariable("jid") final String jurisdictionId,
                                                                  @PathVariable("ctid") final String caseTypeId,
                                                                  @RequestParam Map<String, String> queryParameters) {
        return searchMetadata(jurisdictionId, caseTypeId, queryParameters);
    }

    private PaginatedSearchMetadata searchMetadata(final String jurisdictionId,
                                                   final String caseTypeId,
                                                   final Map<String, String> queryParameters) {
        throw new RuntimeException("not implemented");
    }



    private void validateMetadataSearchParameters(Map<String, String> queryParameters) {
        List<String> metadataParams = queryParameters.keySet().stream().filter(p -> !FieldMapSanitizeOperation.isCaseFieldParameter(p)).collect(toList());
        if (!MetaData.unknownMetadata(metadataParams).isEmpty()) {
            throw new BadRequestException(String.format("unknown metadata search parameters: %s",
                    String.join((","), MetaData.unknownMetadata(metadataParams))));
        }
        param(queryParameters, MetaData.SECURITY_CLASSIFICATION_PARAM).ifPresent(sc -> {
            if(!EnumUtils.isValidEnum(SecurityClassification.class, sc.toUpperCase())) {
                throw new BadRequestException(String.format("unknown security classification '%s'", sc));
            }
        });
    }

    private Optional<String> param(Map<String, String> queryParameters, String param) {
        return Optional.ofNullable(queryParameters.get(param));
    }

    private MetaData createMetadata(String jurisdictionId, String caseTypeId, Map<String, String> queryParameters) {

        validateMetadataSearchParameters(queryParameters);

        final MetaData metadata = new MetaData(caseTypeId, jurisdictionId);
        metadata.setState(param(queryParameters, MetaData.STATE_PARAM));
        metadata.setCaseReference(param(queryParameters, MetaData.CASE_REFERENCE_PARAM));
        metadata.setCreatedDate(param(queryParameters, MetaData.CREATED_DATE_PARAM));
        metadata.setLastModified(param(queryParameters, MetaData.LAST_MODIFIED_PARAM));
        metadata.setSecurityClassification(param(queryParameters, MetaData.SECURITY_CLASSIFICATION_PARAM));
        metadata.setPage(param(queryParameters, MetaData.PAGE_PARAM));
        return metadata;
    }
}
