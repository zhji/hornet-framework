package com.hornetmall.security;

import com.hornetmall.framework.exception.UnauthorizedException;
import com.hornetmall.security.config.SecurityProperties;
import com.hornetmall.security.userdetails.SecurityUser;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.client.HttpClientErrorException;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auths";
    private final SecurityProperties securityProperties;
    private final JWSSigner signer;
    private final JWSVerifier verifier;

    public JWTToken createToken(Authentication authentication) throws JOSEException {

        LocalDateTime now=LocalDateTime.now();
        LocalDateTime expiredTime = now.plusSeconds(securityProperties.getJwt().getExpiredInSeconds());


        JWTClaimsSet.Builder setBuilder=new JWTClaimsSet.Builder();
        setBuilder
                .claim(AUTHORITIES_KEY,authentication.getAuthorities().stream().distinct().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .issueTime(Date.from(now.atZone(ZoneOffset.systemDefault()).toInstant()))
                .expirationTime(Date.from(expiredTime.atZone(ZoneOffset.systemDefault()).toInstant()))
                .subject(authentication.getPrincipal().toString());

        SignedJWT jwt=new SignedJWT(new JWSHeader(JWSAlgorithm.HS256),setBuilder.build());

        jwt.sign(signer);
        String token = jwt.serialize();

        return new JWTToken().setToken(token).setIssueTime(Date.from(now.atZone(ZoneOffset.systemDefault()).toInstant()).getTime()).setExpiredIn(securityProperties.getJwt().getExpiredInSeconds());
    }



    public Authentication getAuthentication(String token) {
        try{
            SignedJWT jwt = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();

            if (!jwt.verify(verifier)) {
                throw new UnauthorizedException("Token校验失败");
            }

            Collection< GrantedAuthority> authorities = Arrays
                    .stream(jwt.getJWTClaimsSet().getClaims().get(AUTHORITIES_KEY).toString().split(","))
                    .filter(auth -> !auth.trim().isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            SecurityUser principal = SecurityUser.builder()
                    .enabled(true)
                    .id(Long.parseLong(claimsSet.getSubject()))
                    .authorities(authorities)
//                    .profile(claimsSet.getClaim(PROFILE_KEY).toString())
//                    .profileId(Long.parseLong(claimsSet.getClaim(PROFILE_ID_KEY).toString()))
                    .build();

            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        }catch (Exception e){
            throw new UnauthorizedException("Token验证失败");
        }
    }


    @Data
    @Accessors(chain = true)
    public static class JWTToken{
        private String token;
        private Long issueTime;
        private Long expiredIn;
    }
}
