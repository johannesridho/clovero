package clovero.dashboard.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
public class DashboardWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/dashboard/**")

                .authorizeRequests()
                    .antMatchers("/dashboard/**").authenticated()
                .and()

                .formLogin()
                    .loginPage("/dashboard/login")
                    .usernameParameter("username").passwordParameter("password")
                    .failureUrl("/dashboard/login?error")
                    .defaultSuccessUrl("/dashboard")
                    .permitAll()
                .and()

                .logout()
                    .logoutUrl("/dashboard/logout")
                    .logoutSuccessUrl("/dashboard/login?logout")
                    .permitAll();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .passwordEncoder(passwordEncoder)
                .dataSource(dataSource)
                .usersByUsernameQuery("select username, password, enabled from admin where username=?")
                .authoritiesByUsernameQuery("select username, 'ROLE_USER' from admin where username=?");
    }
}
