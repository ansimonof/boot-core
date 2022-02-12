package org.myorg.module.core.access.privilege;

import org.myorg.modules.utils.BaseEnum;

public enum AccessOp implements BaseEnum {

    READ(1),
    WRITE(2),
    DELETE(4),
    EXECUTE(8);

    private int value;
    AccessOp(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }
}
