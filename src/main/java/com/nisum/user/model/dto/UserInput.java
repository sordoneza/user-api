package com.nisum.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@AllArgsConstructor
public class UserInput {

    @NotNull()
    @NotEmpty()
    private String name;

    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    @NotNull()
    @NotEmpty()
    @Column(unique = true)
    private String email;

    @NotNull()
    @NotEmpty()
    private String password;

    private Collection<PhoneDto> phones;
}
