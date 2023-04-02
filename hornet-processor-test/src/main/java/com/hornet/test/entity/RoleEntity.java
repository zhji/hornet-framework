package com.hornet.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.client.HttpClientErrorException;

@Getter
@Setter
@Entity
public class RoleEntity {
    @Id
    private Long id;
    @NotNull
    @Pattern(regexp = "111")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;


}
