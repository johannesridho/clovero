package clovero.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(2)
public class ActuatorSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${security.user.name}")
    private String username;

    @Value("${security.user.password}")
    private String password;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .antMatcher("/actuator/**")

            .authorizeRequests()
                .antMatchers("/actuator/**").hasRole("ACTUATOR")
            .and()
            .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
        authManagerBuilder.inMemoryAuthentication()
            .withUser(username).password(password).roles("ACTUATOR");
    }
}
