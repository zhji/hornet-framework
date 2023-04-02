package com.hornet.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
public class UserEntity {

    @Id
    private Long id;

    @NotNull
    @Pattern(regexp = "111")
    private String name;



}
