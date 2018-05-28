package uk.gov.hmcts.ccd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import uk.gov.hmcts.reform.auth.checker.core.RequestAuthorizer;
import uk.gov.hmcts.reform.auth.checker.core.service.Service;
import uk.gov.hmcts.reform.auth.checker.core.user.User;
import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.AuthCheckerServiceAndUserFilter;

import javax.inject.Inject;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthCheckerServiceAndUserFilter authCheckerFilter;

    @Inject
    public SecurityConfiguration(final RequestAuthorizer<User> userRequestAuthorizer,
                                 final RequestAuthorizer<Service> serviceRequestAuthorizer,
                                 final AuthenticationManager authenticationManager) {
        this.authCheckerFilter = new AuthCheckerServiceAndUserFilter(serviceRequestAuthorizer, userRequestAuthorizer);
        this.authCheckerFilter.setAuthenticationManager(authenticationManager);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/swagger-ui.html",
                                   "/webjars/springfox-swagger-ui/**",
                                   "/swagger-resources/**",
                                   "/v2/**",
                                   "/health",
                                   "/status/health",
                                   "/");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // TODO - Rework Auth.
        http.csrf().disable();

//        // Don't erase user credentials as this is needed for the user profile
//        final ProviderManager authenticationManager = (ProviderManager) authenticationManager();
//        authenticationManager.setEraseCredentialsAfterAuthentication(false);
//        authCheckerFilter.setAuthenticationManager(authenticationManager());
//
//        http
//            .addFilter(authCheckerFilter)
//            .sessionManagement().sessionCreationPolicy(STATELESS).and()
//            .csrf().disable()
//            .formLogin().disable()
//            .logout().disable()
//            .authorizeRequests()
//            .anyRequest()
//            .authenticated();
    }
}
