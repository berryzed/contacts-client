package com.daou.contacts.client.config;

import com.daou.contacts.client.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String LOGIN_URL = "/login";
    public static final String JOIN_URL = "/join";
    public static final String LOGOUT_URL = "/logout";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";
    private static final String PARAM_REMEMBER = "remember-me";
    private static final int TOKEN_VALIDITY_TIME = 604800;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    /**
     * Spring Security Principal missing from 4xx responses
     */
    @Bean
    public FilterRegistrationBean<Filter> getSpringSecurityFilterChainBindedToError(
            @Qualifier("springSecurityFilterChain") Filter springSecurityFilterChain) {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(springSecurityFilterChain);
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationHandler();
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationHandler();
    }

    /**
     * 유저 DB의 DataSource와 Query 및 Password Encoder 설정.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

    /**
     * Spring Security에서 인증받지 않아도 되는 리소스 URL 패턴을 지정해 줍니다.
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/statics/**");
    }

    /**
     * Spring Security에 의해 인증받아야 할 URL 또는 패턴을 지정해 줍니다.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers(LOGIN_URL, JOIN_URL).permitAll()
                .antMatchers("/error/**", "/test/**").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable()
                .formLogin().loginPage(LOGIN_URL)
                .loginProcessingUrl("/login/process")
                .successHandler(customAuthenticationSuccessHandler())
                .failureHandler(customAuthenticationFailureHandler())
                .usernameParameter(PARAM_USERNAME).passwordParameter(PARAM_PASSWORD)
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL)).addLogoutHandler(new CustomLogoutHandler()).logoutSuccessUrl(LOGIN_URL)
//                .and().exceptionHandling().authenticationEntryPoint(ajaxAwareAuthenticationEntryPoint()).accessDeniedHandler(customAccessDeniedHandler())
//                .and().rememberMe().key(customConfig.getRememberKey()).rememberMeParameter(PARAM_REMEMBER).tokenValiditySeconds(TOKEN_VALIDITY_TIME)
                .and().sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(false).expiredUrl(LOGIN_URL)
                .and().invalidSessionUrl("/login?invalidsession");
    }
}
