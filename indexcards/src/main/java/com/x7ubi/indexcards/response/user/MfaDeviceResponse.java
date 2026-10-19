package com.x7ubi.indexcards.response.user;

public class MfaDeviceResponse {

    /**
     * {@code totp}, {@code webauthn} or {@code email}.
     */
    private String type;

    private String id;

    private String name;

    private String created;

    private String lastUsed;

    public MfaDeviceResponse() {}

    public MfaDeviceResponse(String type, String id, String name, String created, String lastUsed) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.created = created;
        this.lastUsed = lastUsed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(String lastUsed) {
        this.lastUsed = lastUsed;
    }
}
