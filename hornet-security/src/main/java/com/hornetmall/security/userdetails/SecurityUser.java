package com.hornetmall.security.userdetails;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@Accessors(chain = true)
@Builder
public class SecurityUser implements UserDetails {


    private final Long id;
    private final String username;
    private String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final String profile;
    private final Long profileId;


}
