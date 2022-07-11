package org.myorg.module.core.database.domainobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myorg.modules.modules.database.DomainObject;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class DbAbstractConfig extends DomainObject {

    public static final String FIELD_KEY = "key";
    public static final String FIELD_VALUE = "value";

    public static final String QUERY_FIND_BY_KEY = "DbConfig.findByKey";

    @Column(name = FIELD_KEY, nullable = false, unique = true)
    protected String key;

    @Column(name = FIELD_VALUE)
    protected byte[] value;

}
