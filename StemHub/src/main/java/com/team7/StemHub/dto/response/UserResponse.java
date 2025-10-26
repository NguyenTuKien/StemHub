package com.team7.StemHub.dto.response;

import com.team7.StemHub.model.User;

import java.util.UUID;

public class UserResponse {
    private UUID userId;
    private String username;
    private String fullname;
    private String email;
    private String role;

    public UserResponse(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.fullname = user.getFullname();
        this.email = user.getEmail();
        this.role = user.getRole().toString();
    }
}
