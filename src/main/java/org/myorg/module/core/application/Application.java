package org.myorg.module.core.application;

import org.myorg.modules.modules.exception.ModuleException;

public interface Application {

    String getUuid();

    boolean isUserHasAccess(long userId) throws ModuleException;
}
