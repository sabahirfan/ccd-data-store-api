package uk.gov.hmcts.ccd.endpoint.std;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.ccd.domain.model.std.UserId;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3451", allowCredentials = "true")
@RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "/", description = "Case access API")
public class CaseAccessEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(CaseAccessEndpoint.class);

    @RequestMapping(
        value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/ids",
        method = RequestMethod.GET,
        consumes = MediaType.ALL_VALUE
    )
    @ApiOperation(value = "Get case ids", notes = "Retrieve case ids for given users ids")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "List of cases ids found"),
        @ApiResponse(code = 400, message = "Invalid case ID")
    })
    public List<String> findCaseIdsGivenUserIdHasAccessTo(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final Integer uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "User id searching for", required = true)
        @RequestParam(value = "userId") final String idSearchingFor
    ) {
        LOG.debug("Finding cases user: {} has access to", idSearchingFor);
        throw new RuntimeException("not implemented");
    }

    @RequestMapping(
        value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/{cid}/users",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(value = "Grant access to case")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Grant successful"),
        @ApiResponse(code = 400, message = "Invalid case ID")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    public void grantAccessToCase(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final String uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Case ID", required = true)
        @PathVariable("cid") final String caseId,
        @RequestBody final UserId id
    ) {
        LOG.debug("Granting access to case: {}, for user: {}", caseId, id);
        throw new RuntimeException("not implemented");
    }

    @RequestMapping(
        value = "/caseworkers/{uid}/jurisdictions/{jid}/case-types/{ctid}/cases/{cid}/users/{idToDelete}",
        method = RequestMethod.DELETE,
        consumes = MediaType.ALL_VALUE
    )
    @ApiOperation(value = "Revoke access to case")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Access revoked"),
        @ApiResponse(code = 400, message = "Invalid case ID")
    })
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void revokeAccessToCase(
        @ApiParam(value = "Idam user ID", required = true)
        @PathVariable("uid") final Integer uid,
        @ApiParam(value = "Jurisdiction ID", required = true)
        @PathVariable("jid") final String jurisdictionId,
        @ApiParam(value = "Case type ID", required = true)
        @PathVariable("ctid") final String caseTypeId,
        @ApiParam(value = "Case ID", required = true)
        @PathVariable("cid") final String caseId,
        @ApiParam(value = "Id to delete", required = true)
        @PathVariable("idToDelete") final String idToDelete
    ) {
        LOG.debug("Revoking access to case: {}, for user: {}", caseId, idToDelete, idToDelete);
        throw new RuntimeException("not implemented");
    }
}
