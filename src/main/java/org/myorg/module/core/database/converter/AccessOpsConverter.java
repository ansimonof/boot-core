package org.myorg.module.core.database.converter;


import org.myorg.module.core.access.privilege.AccessOp;
import org.myorg.module.core.access.privilege.AccessOpCollection;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AccessOpsConverter implements AttributeConverter<AccessOp[], Integer> {

    @Override
    public Integer convertToDatabaseColumn(AccessOp[] attribute) {
        return new AccessOpCollection(attribute).getValue();
    }

    @Override
    public AccessOp[] convertToEntityAttribute(Integer value) {
        return new AccessOpCollection(value).getOps();
    }
}
