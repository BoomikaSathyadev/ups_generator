package com.kalai.ups.dto;

import com.kalai.ups.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter @NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private User.Role role;
    private User.Status status;
}
