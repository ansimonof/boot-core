package org.myorg.module.core.database.domainobject;

import lombok.*;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.modules.modules.database.DomainObject;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = CoreModuleConsts.DB_PREFIX + "user",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = { DbUser.FIELD_USERNAME}
                )
        }
)
@NamedQueries(
        value = {
                @NamedQuery(
                        name = DbUser.QUERY_FIND_ALL,
                        query = "select u from DbUser u"
                ),
                @NamedQuery(
                        name = DbUser.QUERY_FIND_BY_USERNAME,
                        query = "select u from DbUser u where u.username = :" + DbUser.FIELD_USERNAME
                )
        }
)
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = { "username" })
@NoArgsConstructor
@AllArgsConstructor
public class DbUser extends DomainObject {

    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASSWORD_HASH = "password_hash";
    public static final String FIELD_IS_ENABLED = "is_enabled";
    public static final String FIELD_IS_ADMIN = "is_admin";

    public static final String QUERY_FIND_ALL = "DbUser.findAll";
    public static final String QUERY_FIND_BY_USERNAME = "DbUser.findByUsername";

    @Column(name = FIELD_USERNAME, nullable = false, updatable = false)
    private String username;

    @Column(name = FIELD_PASSWORD_HASH, nullable = false)
    private String passwordHash;

    @Column(name = FIELD_IS_ENABLED, nullable = false)
    private boolean isEnabled;

    @Column(name = FIELD_IS_ADMIN, nullable = false)
    private boolean isAdmin;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(
//            name = "fk_access_role",
//            referencedColumnName = "id"
//    )
//    private DbAccessRole accessRole;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = CoreModuleConsts.DB_PREFIX + "users_access_roles",
            joinColumns = @JoinColumn(
                    name = "fk_user",
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
