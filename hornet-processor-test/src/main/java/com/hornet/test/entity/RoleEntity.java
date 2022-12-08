package com.hornet.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.client.HttpClientErrorException;

@Getter
@Setter
@Entity
public class RoleEntity {
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;


}
