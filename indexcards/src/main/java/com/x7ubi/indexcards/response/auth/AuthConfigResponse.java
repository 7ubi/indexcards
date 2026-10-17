package com.x7ubi.indexcards.response.auth;

public class AuthConfigResponse {

    private String authority;

    private String clientId;

    public AuthConfigResponse() {}

    public AuthConfigResponse(String authority, String clientId) {
        this.authority = authority;
        this.clientId = clientId;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
