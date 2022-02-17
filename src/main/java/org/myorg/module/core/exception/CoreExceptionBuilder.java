package org.myorg.module.core.exception;

import org.myorg.modules.modules.exception.ModuleException;

public class CoreExceptionBuilder {

    public static ModuleException buildAdminCannotBeBannedException() {
        return new ModuleException("admin_cannot_be_banned");
    }
}
