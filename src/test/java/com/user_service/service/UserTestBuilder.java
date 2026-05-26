package com.user_service.service;

import com.user_service.entity.User;
import com.user_service.enums.UserRole;
import com.user_service.enums.UserStatus;

public class UserTestBuilder {

    private Long id = 0L;

    private String username = "testusername";

    private String password = "tetspassword";

    private String firstname = "testfirstname";

    private String lastname = "testlastname";

    private String email = "testemail@gmail.com";

    private UserRole role = UserRole.USER;

    private UserStatus status = UserStatus.ACTIVE;

    public UserTestBuilder withId(Long id) {

        this.id = id;

        return this;
    }

    public UserTestBuilder withUsername(String username) {

        this.username = username;

        return this;
    }

    public UserTestBuilder withPassword(String password) {

        this.password = password;

        return this;
    }

    public UserTestBuilder withFirstname(String firstname) {

        this.firstname = firstname;

        return this;
    }

    public UserTestBuilder withLastname(String lastname) {

        this.lastname = lastname;

        return this;
    }

    public UserTestBuilder withEmail(String email) {

        this.email = email;

        return this;
    }

    public UserTestBuilder withRole(UserRole role) {

        this.role = role;

        return this;
    }

    public UserTestBuilder withStatus(UserStatus status) {

        this.status = status;

        return this;
    }

    public User build(){

        return new User(
                id,
                username,
                password,
                firstname,
                lastname,
                email,
                role,
                status
        );
    }


}
