package org.myorg.module.core.access;

import org.myorg.modules.access.context.Context;
import org.myorg.module.core.access.privilege.AbstractPrivilege;
import org.myorg.module.core.access.privilege.AccessOp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessPermission {

    Class<? extends Context> context();
    Class<? extends AbstractPrivilege> privilege() default AbstractPrivilege.class;
    AccessOp[] ops() default {};
}