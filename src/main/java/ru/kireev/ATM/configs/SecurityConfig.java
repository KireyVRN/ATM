package ru.kireev.ATM.configs;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.kireev.ATM.services.CardService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CardService cardService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/static/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll()
                //.defaultSuccessUrl("/client")
                .successHandler(authenticationSuccessHandler()).failureHandler(authenticationFailureHandler())
                .and().headers().frameOptions().disable()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                .addLogoutHandler(logoutHandler())
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/logoutSuccess").permitAll();

    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    protected DaoAuthenticationProvider daoAuthenticationProvider() {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(cardService);
        return daoAuthenticationProvider;

    }

    @Bean
    protected AuthenticationSuccessHandler authenticationSuccessHandler() {

        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {

                httpServletResponse.sendRedirect(httpServletResponse.encodeURL("/client"));
                LOGGER.info(String.format("Login - card %s", authentication.getName()));

            }
        };
    }

    @Bean
    protected AuthenticationFailureHandler authenticationFailureHandler() {

        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {

                httpServletResponse.sendRedirect(httpServletResponse.encodeURL("/login"));
                LOGGER.info(String.format("Unsuccessful attempt to log in with next data -> card: %s, PIN: %s", httpServletRequest.getParameter("username"), httpServletRequest.getParameter("password")));

            }
        };
    }

    protected LogoutHandler logoutHandler() {

        return new LogoutHandler() {
            @Override
            public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {

                LOGGER.info(String.format("Logout - card %s", authentication.getName()));

            }
        };
    }

}



