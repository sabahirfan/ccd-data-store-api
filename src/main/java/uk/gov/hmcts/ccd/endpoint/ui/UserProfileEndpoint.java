package uk.gov.hmcts.ccd.endpoint.ui;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.ccd.domain.model.aggregated.*;

@RestController
@CrossOrigin(origins = "http://localhost:3451", allowCredentials = "true")
public class UserProfileEndpoint {
    private static final String BEARER = "Bearer ";


    @RequestMapping(value = "/data/caseworkers/{uid}/profile", method = RequestMethod.GET)
    @ApiOperation(value = "Get default setting for user")
    @ApiResponse(code = 200, message = "User default settings")
    public UserProfile getUserProfile(@RequestHeader(value = AUTHORIZATION, defaultValue = "fake") final String authHeader) {
        UserProfile profile = new UserProfile();
        JurisdictionDisplayProperties prop = new JurisdictionDisplayProperties();
        prop.setId("RHUBARB");
        prop.setName("RHUBARB");
        prop.setDescription("Some description");
        JurisdictionDisplayProperties[] jurs = new JurisdictionDisplayProperties[] {
                prop
        };
        profile.setJurisdictions(jurs);
        User user = new User();
        profile.setUser(user);
        IDAMProperties idam = new IDAMProperties();
        idam.setId("1");
        idam.setRoles(new String[] { "case-worker"});
        idam.setEmail("a@b.com");
        idam.setForename("John");
        idam.setForename("Jones");
        user.setIdamProperties(idam);
        profile.setChannels(new String[] {"channel1"});
        DefaultSettings settings = new DefaultSettings();
        WorkbasketDefault basket = new WorkbasketDefault();
        basket.setJurisdictionId("RHUBARB");
        basket.setCaseTypeId("RHUBARB");
        basket.setStateId("created");
        settings.setWorkbasketDefault(basket);
        profile.setDefaultSettings(settings);
        return profile;
        // TODO
//        final String userToken = authHeader.substring(BEARER.length());
//        return getUserProfileOperation.execute(userToken);
    }
}
