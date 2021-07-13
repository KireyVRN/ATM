package ru.kireev.ATM.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        //httpSecurity.httpBasic().disable();

        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/","/h2-console/**","/static/**").permitAll()
                .and().headers().frameOptions().disable();

        

    }






}

//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    private final UserService userService;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        http
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/", "/authorization/newUser", "/h2-console/**","/static/**").permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .formLogin().loginPage("/authorization/login").permitAll()
//                .defaultSuccessUrl("/people")
//                .and().headers().frameOptions().disable()
//                .and()
//                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/authorization/logout", "POST"))
//                .invalidateHttpSession(true)
//                .clearAuthentication(true)
//                .deleteCookies("JSESSIONID")
//                .logoutSuccessUrl("/");
//    }
//
//    @Bean
//    protected DaoAuthenticationProvider daoAuthenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
//        daoAuthenticationProvider.setUserDetailsService(userService);
//        return daoAuthenticationProvider;
//    }
//
//}
