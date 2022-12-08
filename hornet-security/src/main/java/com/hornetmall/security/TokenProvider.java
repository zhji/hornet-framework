package com.hornetmall.security;

import com.hornetmall.security.config.SecurityProperties;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.HttpClientErrorException;

import java.text.ParseException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TokenProvider {

    private final SecurityProperties securityProperties;
    private final JWSSigner signer;
    private final JWSVerifier verifier;

    public String createToken(Authentication authentication) throws JOSEException {

        JWTClaimsSet.Builder setBuilder=new JWTClaimsSet.Builder();
        setBuilder
                .audience(authentication.getAuthorities().stream().distinct().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .subject(authentication.getPrincipal().toString());
        SignedJWT jwt=new SignedJWT(new JWSHeader(JWSAlgorithm.HS256),setBuilder.build());

        jwt.sign(signer);
        return jwt.serialize();
    }



    public Authentication parseToken(String token) {
        SignedJWT jwt = null;
        try {
            jwt = SignedJWT.parse(token);

        } catch (ParseException e) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN,"token.parse.error");
        }
        try {
            jwt.verify(verifier);
        } catch (JOSEException e) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN,"token.verify.error");
        }

        try {
            JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();

            return new UsernamePasswordAuthenticationToken(jwtClaimsSet.getSubject(),"",
                    jwtClaimsSet.getAudience().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        } catch (ParseException e) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN,"token.parse.claims.error");
        }

    }
}
