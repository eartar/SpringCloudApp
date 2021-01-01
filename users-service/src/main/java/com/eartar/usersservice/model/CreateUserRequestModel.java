package com.eartar.usersservice.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUserRequestModel {

    @NotNull(message = "Firstname cannot be empty")
    @Size(min = 2, message = "First name cannot be less then 2 characters")
    private String firstName;

    @NotNull(message = "LastName cannot be empty")
    @Size(min = 2, message = "First name cannot be less then 2 characters")
    private String lastName;

    @NotNull(message = "Password cannot be empty")
    @Size(min = 6, max=16, message = "Password must be between 8 and 16 characters")
    private String password;

    @NotNull(message = "Email cannot be empty")
    @Email
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
