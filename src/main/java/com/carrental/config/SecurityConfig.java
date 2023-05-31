package com.carrental.config;

import com.carrental.models.enums.AuthorityType;
import com.carrental.models.repository.UserRepository;
import com.carrental.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
@EnableJpaAuditing
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/resources/**", "/css/**", "/js/**", "/img/**", "/layouts/**", "/webjars/**",
                        "/health", "/metrics", "/trace", "/info", "/login", "/registration", "/", "/car/list", "/car/{id}/details", "/email/**",

                        "/admin/e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855/",

                        "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**").permitAll()
                .mvcMatchers("/car/add", "/car/{id}/edit", "/car/{id}/delete")
                .access(String.format("hasAnyAuthority('%s', '%s')",
                        AuthorityType.ROLE_ADMIN.toString(),
                        AuthorityType.ROLE_OWNER.toString()))
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();

        http.sessionManagement()
                .maximumSessions(10)
                .expiredUrl("/login")
                .and()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionUrl("/login");
    }
}

