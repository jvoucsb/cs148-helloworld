package com.github.jvogit.springreactnextjs.config;

import com.github.jvogit.springreactnextjs.filter.JwtTokenFilter;
import com.github.jvogit.springreactnextjs.model.JwtUserDetails;
import com.github.jvogit.springreactnextjs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final JwtTokenFilter jwtTokenFilter;

    @Autowired
    public SecurityConfig(UserService userService, JwtTokenFilter jwtTokenFilter) {
        this.userService = userService;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(input ->
                userService.getUserByUsernameOrEmail(input)
                        .map(JwtUserDetails::from)
                        .orElseThrow(() -> new UsernameNotFoundException("Username not found " + input))
        );
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers("/refresh_token").permitAll()
                .antMatchers("/graphql", "/graphiql").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }
}
