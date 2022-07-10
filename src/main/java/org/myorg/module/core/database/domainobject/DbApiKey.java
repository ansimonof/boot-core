package org.myorg.module.core.database.domainobject;

import lombok.*;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.modules.modules.database.DomainObject;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = CoreModuleConsts.DB_PREFIX + "api_key",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = { DbApiKey.FIELD_NAME }
                )
        }
)
@NamedQueries(
        value = {
                @NamedQuery(
                        name = DbApiKey.QUERY_FIND_BY_NAME,
                        query = "select ak from DbApiKey ak where ak.name = :" + DbApiKey.FIELD_NAME
                ),
                @NamedQuery(
                        name = DbApiKey.QUERY_FIND_ALL,
                        query = "select ak from DbApiKey ak"
                )
        }
)
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = { "name" })
@NoArgsConstructor
@AllArgsConstructor
public class DbApiKey extends DomainObject {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_VALUE = "value";

    public static final String QUERY_FIND_BY_NAME = "DbApiKey.findByName";
    public static final String QUERY_FIND_ALL = "DbApiKey.findAll";

    @Column(name = FIELD_NAME, nullable = false)
    private String name;

    @Column(name = FIELD_VALUE, nullable = false)
    private byte[] value;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = CoreModuleConsts.DB_PREFIX + "api_key_access_roles",
            joinColumns = @JoinColumn(
                    name = "fk_api_key",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "fk_access_role",
                    referencedColumnName = "id"
            )
    )
    private Set<DbAccessRole> accessRoles = new HashSet<>();

    public void addAccessRole(DbAccessRole dbAccessRole) {
        accessRoles.add(dbAccessRole);
    }

    public void removeAccessRole(DbAccessRole dbAccessRole) {
        accessRoles.remove(dbAccessRole);
    }
}
