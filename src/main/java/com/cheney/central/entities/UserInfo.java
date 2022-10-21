package com.cheney.central.entities;

import lombok.Data;
import java.util.List;

@Data
public class UserInfo {
    public Object id;
    public Object getId() {
		return id;
	}
	public void setId(Object id) {
		this.id = id;
	}
	public boolean isLockoutEnabled() {
		return lockoutEnabled;
	}
	public void setLockoutEnabled(boolean lockoutEnabled) {
		this.lockoutEnabled = lockoutEnabled;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isEmailConfirmed() {
		return emailConfirmed;
	}
	public void setEmailConfirmed(boolean emailConfirmed) {
		this.emailConfirmed = emailConfirmed;
	}
	public boolean isAllowOrderEntry() {
		return allowOrderEntry;
	}
	public void setAllowOrderEntry(boolean allowOrderEntry) {
		this.allowOrderEntry = allowOrderEntry;
	}
	public boolean isAllowPricing() {
		return allowPricing;
	}
	public void setAllowPricing(boolean allowPricing) {
		this.allowPricing = allowPricing;
	}
	public boolean isAllowItemBook() {
		return allowItemBook;
	}
	public void setAllowItemBook(boolean allowItemBook) {
		this.allowItemBook = allowItemBook;
	}
	public boolean isAllowLodging() {
		return allowLodging;
	}
	public void setAllowLodging(boolean allowLodging) {
		this.allowLodging = allowLodging;
	}
	public boolean isAllowMenuCosting() {
		return allowMenuCosting;
	}
	public void setAllowMenuCosting(boolean allowMenuCosting) {
		this.allowMenuCosting = allowMenuCosting;
	}
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	public List<Object> getRoles() {
		return roles;
	}
	public void setRoles(List<Object> roles) {
		this.roles = roles;
	}
	public boolean lockoutEnabled;
    public String email;
    public boolean emailConfirmed;
    public boolean allowOrderEntry;
    public boolean allowPricing;
    public boolean allowItemBook;
    public boolean allowLodging;
    public boolean allowMenuCosting;
    public List<Tag> tags;
    public List<Object> roles;
}
