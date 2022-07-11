package org.myorg.module.core.database.domainobject;

import org.myorg.module.core.CoreModuleConsts;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(
        name = CoreModuleConsts.DB_PREFIX + "config"
)
@NamedQueries(
        value = {
                @NamedQuery(
                        name = DbAbstractConfig.QUERY_FIND_BY_KEY,
                        query = "select c from DbCoreConfig c where c.key = :" + DbAbstractConfig.FIELD_KEY
                )
        }
)
public class DbCoreConfig extends DbAbstractConfig {
}
