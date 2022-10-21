package com.cheney.central.entities;

import com.cheney.central.config.Constants;

import lombok.Data;

@Data
public class Credential {
    public Credential(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
        this.setClient_id(Constants.client_id);
        this.setGrant_type(Constants.grant_type);
    }

    public String getGrant_type() {
		return grant_type;
	}
	public void setGrant_type(String grant_type) {
		this.grant_type = grant_type;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String grant_type;
    public String username;
    public String password;
    public String client_id;
}
