package org.myorg.module.core.access.privilege;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccessOpCollection {

    private int value;

    public AccessOpCollection(int value) {
        this.value = value;
    }

    public AccessOpCollection() {
        this(0);
    }

    public AccessOpCollection(AccessOp... ops) {
        setOps(ops);
    }

    public boolean contains(int otherValue) {
        return (value & otherValue) == otherValue;
    }

    public boolean contains(AccessOpCollection opCollection) {
        return contains(opCollection.value);
    }

    public boolean contains(AccessOp op) {
        return (value & op.intValue()) != 0;
    }

    public boolean contains(AccessOp... ops) {
        for (AccessOp op : ops) {
            if (!contains(op)) {
                return false;
            }
        }
        return true;
    }

    public void setOps(AccessOp[] ops) {
        value = 0;
        addOps(ops);
    }

    public void addOps(AccessOp[] ops) {
        for (AccessOp op : ops) {
            value |= op.intValue();
        }
    }

    public int getValue() {
        return value;
    }

    public AccessOp[] getOps() {
        List<AccessOp> result = new ArrayList<>();
        for (AccessOp op : AccessOp.values()) {
            if (contains(op)) {
                result.add(op);
            }
        }

        return result.toArray(new AccessOp[0]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessOpCollection that = (AccessOpCollection) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}