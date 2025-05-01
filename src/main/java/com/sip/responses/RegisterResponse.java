package com.sip.responses;

import java.util.List;

public class RegisterResponse {
	
	    private Long id;
	    private String username;
	    private String email;
	    private List<String> roles;
	    private Boolean enabled ;
	    private String message;

	    public RegisterResponse() {
	    }

	    public RegisterResponse(Long id, String username, String email, List<String> roles,Boolean enabled , String message ) {
	        this.id = id;
	        this.username = username;
	        this.email = email;
	        this.roles = roles;
	        this.enabled = enabled ;
	        this.message = message;
	    }

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

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

	    public List<String> getRoles() {
	        return roles;
	    }

	    public void setRoles(List<String> roles) {
	        this.roles = roles;
	    }

	    public String getMessage() {
	        return message;
	    }

	    public void setMessage(String message) {
	        this.message = message;
	    }

		public Boolean getEnabled() {
			return enabled;
		}

		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}
		
		
	    
}
