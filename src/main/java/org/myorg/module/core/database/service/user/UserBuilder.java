package org.myorg.module.core.database.service.user;

import org.myorg.modules.utils.DomainObjectBuilder;

public class UserBuilder extends DomainObjectBuilder {

    private BuilderField<String> username = new BuilderField<>();
    private BuilderField<String> passwordHash = new BuilderField<>();
    private BuilderField<Boolean> isEnabled = new BuilderField<>();
    private BuilderField<Boolean> isAdmin = new BuilderField<>();

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UserBuilder username(String username) {
        this.username.setValue(username);
        return this;
    }

    public UserBuilder passwordHash(String passwordHash) {
        this.passwordHash.setValue(passwordHash);
        return this;
    }

    public UserBuilder isEnabled(Boolean isEnabled) {
        this.isEnabled.setValue(isEnabled);
        return this;
    }

    public UserBuilder isAdmin(Boolean isAdmin) {
        this.isAdmin.setValue(isAdmin);
        return this;
    }

    //--------------------------

    public String getUsername() {
        return username.getValue();
    }

    public String getPasswordHash() {
        return passwordHash.getValue();
    }

    public Boolean getIsEnabled() {
        return isEnabled.getValue();
    }

    public Boolean getIsAdmin() {
        return isAdmin.getValue();
    }

    //---------------------------

    public boolean isContainUsername() {
        return username.isContain();
    }

    public boolean isContainPasswordHash() {
        return passwordHash.isContain();
    }

    public boolean isContainEnabled() {
        return isEnabled.isContain();
    }

    public boolean isContainAdmin() {
        return isAdmin.isContain();
    }
}
