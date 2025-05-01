package com.sip.requests;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private List<String> roles;

    private Boolean enabled;

    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

	public RegisterRequest(@NotBlank @Size(min = 3, max = 20) String username, @NotBlank @Email String email,
			@NotBlank @Size(min = 6, max = 40) String password, List<String> roles, Boolean enabled) {
		super();
		this.username = username;
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.enabled = enabled;
	}

	public RegisterRequest() {
		super();
	}
    
    
}