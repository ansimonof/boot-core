package org.myorg.module.core.config;

import org.myorg.modules.utils.BaseEnum;

public enum ServerStatus implements BaseEnum {

    NOT_INITIALIZED(1),
    INITIALIZED(2);

    private final int id;
    ServerStatus(int id) {
        this.id = id;
    }

    @Override
    public int intValue() {
        return id;
    }
}
