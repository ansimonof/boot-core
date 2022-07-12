package org.myorg.module.core.exception;

import org.myorg.modules.modules.exception.ModuleException;

import java.util.HashMap;

public class CoreExceptionBuilder {

    public static ModuleException buildServerIsAlreadyInitialized() {
        return new ModuleException("server_is_already_initialized");
    }

    public static ModuleException buildAdminCannotBeBannedException() {
        return new ModuleException("admin_cannot_be_banned");
    }

    public static ModuleException buildAdminCannotRemovedException() {
        return new ModuleException("admin_cannot_be_removed");
    }

    public static ModuleException buildUserIsBannedException(String username) {
        return new ModuleException("user_is_banned", new HashMap<String, Object>() {{
            put("username", username);
        }});
    }

    public static ModuleException buildBadPasswordForUser(String username, String passwordHash) {
        return new ModuleException("bad_password_for_user", new HashMap<String, Object>() {{
            put("username", username);
            put("password_hash", passwordHash);
        }});
    }
}
