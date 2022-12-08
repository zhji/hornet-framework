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

@Configuration
@RequiredArgsConstructor
@Import(SecurityProperties.class)
public class SecurityConfiguration {




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
}
