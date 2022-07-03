package org.myorg.module.core.exception;

import org.myorg.modules.modules.exception.ModuleException;

import java.util.HashMap;

public class CoreExceptionBuilder {

    public static ModuleException buildAdminCannotBeBannedException() {
        return new ModuleException("admin_cannot_be_banned");
    }

    public static ModuleException buildUserIsBannedException(String username) {
        return new ModuleException("user_is_banned", new HashMap<String, Object>() {{
            put("username", username);
        }});
    }
}
