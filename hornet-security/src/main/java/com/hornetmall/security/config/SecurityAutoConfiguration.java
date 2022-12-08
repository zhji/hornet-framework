package com.hornetmall.security.config;

import com.hornetmall.security.TokenProvider;
import com.hornetmall.security.filter.JWTFilter;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@RequiredArgsConstructor
@Import(SecurityProperties.class)
public class SecurityAutoConfiguration {




    @Bean
    @ConditionalOnMissingBean
    public JWSSigner jwsSigner(SecurityProperties securityProperties) throws KeyLengthException {
        return new MACSigner(securityProperties.getJwt().getSignerKey());
    }


    @Bean
    @ConditionalOnMissingBean
    public JWSVerifier jwsVerifier(SecurityProperties securityProperties) throws JOSEException {
        return new MACVerifier(securityProperties.getJwt().getSignerKey());
    }



    @Bean
    public TokenProvider tokenProvider(SecurityProperties securityProperties, JWSSigner signer, JWSVerifier verifier){
        return new TokenProvider(securityProperties,signer,verifier);
    }


    @Bean
    public JWTFilter jwtFilter(TokenProvider tokenProvider){
        return new JWTFilter(tokenProvider);
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TokenProvider tokenProvider, SecurityProperties securityProperties) throws Exception {
        // @formatter:off
        http
                .csrf()
                .disable()
                .exceptionHandling()
                .and()
                .headers()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                .and()
                .permissionsPolicy().policy("camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()")
                .and()
                .frameOptions().sameOrigin()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                . authorizeHttpRequests()

                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()


                .requestMatchers("/**/*.{js,html}").permitAll()
                .requestMatchers("/i18n/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/test/**").permitAll()
                .requestMatchers(securityProperties.getPermitAll().toArray(new String[securityProperties.getPermitAll().size()])).permitAll()
                .requestMatchers(securityProperties.getAuthenticated().toArray(new String[securityProperties.getAuthenticated().size()])).authenticated()
                .requestMatchers("/management/health").permitAll()
                .requestMatchers("/management/health/**").permitAll()
                .requestMatchers("/management/info").permitAll()
                .requestMatchers("/management/prometheus").permitAll()
                .and()

                .apply(securityConfigurerAdapter(tokenProvider));

        return http.build();
        // @formatter:on
    }


    private JWTConfigurer securityConfigurerAdapter(TokenProvider tokenProvider) {
        return new JWTConfigurer(tokenProvider);
    }
}
