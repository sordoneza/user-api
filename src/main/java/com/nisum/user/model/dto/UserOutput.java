package com.nisum.user.model.dto;

import com.nisum.user.model.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserOutput {
    private UUID id;
    private String name;
    private String email;
    private Date created;
    private Date modified;
    private Date lastLogin;
    private String token;
    private Boolean active;

    public static UserOutput toUserOutput(User user) {
        UserOutput output = new UserOutput();

        output.setId(user.getId());
        output.setName(user.getName());
        output.setEmail(user.getEmail());
        output.setCreated(user.getCreated());
        output.setModified(user.getLastModified());
        output.setLastLogin(user.getLastLogin());
        output.setToken(user.getToken());
        output.setActive(user.getActive());

        return output;
    }
}
