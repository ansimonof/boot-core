package org.myorg.module.core.database.domainobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myorg.module.core.access.privilege.AccessOp;
import org.myorg.module.core.database.converter.AccessOpsConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeEmbeddable {

    public static final String FIELD_KEY = "key";
    public static final String FIELD_VALUE = "value";

    @Column(name = FIELD_KEY, nullable = false)
    private String key;

    @Column(name = FIELD_VALUE, nullable = false)
    @Convert(converter = AccessOpsConverter.class)
    private AccessOp[] value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivilegeEmbeddable that = (PrivilegeEmbeddable) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
