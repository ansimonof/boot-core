package org.myorg.module.core.web;

import org.myorg.modules.access.context.Context;
import org.myorg.module.auth.access.context.UnauthenticatedContext;
import org.myorg.module.core.access.AccessPermission;
import org.myorg.module.core.access.privilege.AbstractPrivilege;
import org.myorg.module.core.access.privilege.AccessOp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

@Service
public class ControllerMappingInfoInitializer {

    private final Map<Method, ControllerInfo> controllersInfo;

    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public ControllerMappingInfoInitializer(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
        this.controllersInfo = new HashMap<>();
    }

    private ControllerInfo validateParamsAndGetInfo(Class<? extends Context> context,
                                          Class<? extends AbstractPrivilege> privilegeClazz,
                                          AccessOp[] ops) {
        if (context == UnauthenticatedContext.class && privilegeClazz != AbstractPrivilege.class) {
            throw new RuntimeException("Context is unauthorized, but there is privilege");
        }

        if (privilegeClazz != AbstractPrivilege.class) {
            int modifiers = privilegeClazz.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                throw new RuntimeException("Cannot instationate privilege");
            }

            AbstractPrivilege privilege;
            try {
                Constructor<? extends AbstractPrivilege> constructor =
                        privilegeClazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                privilege = constructor.newInstance();
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                throw new RuntimeException(e);
            }

            if (!privilege.getAccessOpCollection().contains(ops)) {
                throw new RuntimeException("Privilege " + privilege.getKey() + " doesn't have such access operations");
            }

            return new ControllerInfo(privilege, ops, context);
        } else if (ops != null && ops.length != 0) {
            throw new RuntimeException("No privilege, but there are access operations");
        } else {
            return new ControllerInfo(null, null, context);
        }
    }

    @PostConstruct
    private void init() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (RequestMappingInfo mappingInfo : handlerMethods.keySet()) {
            HandlerMethod handlerMethod = handlerMethods.get(mappingInfo);
            Method method = handlerMethod.getMethod();

            ControllerInfo controllerInfo;
            if (method.isAnnotationPresent(AccessPermission.class)) {
                AccessPermission accessPermissionAnn = method.getAnnotation(AccessPermission.class);
                Class<? extends Context> context = accessPermissionAnn.context();
                Class<? extends AbstractPrivilege> privilegeClazz = accessPermissionAnn.privilege();
                AccessOp[] ops = accessPermissionAnn.ops();

                controllerInfo = validateParamsAndGetInfo(context, privilegeClazz, ops);
            } else {
                controllerInfo = new ControllerInfo(null, null, UnauthenticatedContext.class);
            }

            controllersInfo.put(method, controllerInfo);
        }
    }

    public Map<Method, ControllerInfo> getControllersInfo() {
        return controllersInfo;
    }
}
