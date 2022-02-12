package org.myorg.module.core.web.security.authorization;

import org.myorg.module.auth.access.context.Context;
import org.myorg.module.auth.access.context.UserAuthenticatedContext;
import org.myorg.module.auth.authentication.token.CustomAbstractAuthenticationToken;
import org.myorg.module.auth.authorization.CustomAccessDecisionVoter;
import org.myorg.module.core.access.context.source.CoreUserSource;
import org.myorg.module.core.access.context.source.PrivilegeAuthorizing;
import org.myorg.module.core.access.privilege.AccessOpCollection;
import org.myorg.module.core.access.privilege.PrivilegePair;
import org.myorg.module.core.database.service.user.UserDto;
import org.myorg.module.core.database.service.user.UserService;
import org.myorg.module.core.web.ControllerInfo;
import org.myorg.module.core.web.ControllerMappingInfoInitializer;
import org.myorg.modules.modules.exception.ModuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Component
public class PrivilegeAccessDecisionVoter implements CustomAccessDecisionVoter {

    private final ControllerMappingInfoInitializer controllerMappingInfoInitializer;
    private final UserService userService;

    @Autowired
    public PrivilegeAccessDecisionVoter(ControllerMappingInfoInitializer controllerMappingInfoInitializer, UserService userService) {
        this.controllerMappingInfoInitializer = controllerMappingInfoInitializer;
        this.userService = userService;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection collection) {
        try {
            return authorize(authentication, object, collection);
        } catch (ModuleException e) {
            throw new RuntimeException("Error during authorization", e);
        }
    }

    private int authorize(Authentication authentication, Object object, Collection collection) throws ModuleException {
        // Запрещаем выполнять запрос для других Authentication
        if (!(authentication instanceof CustomAbstractAuthenticationToken)) {
            return ACCESS_DENIED;
        }

        if (!authentication.isAuthenticated()) {
            return ACCESS_DENIED;
        }

        Method method = (Method) authentication.getDetails();
        ControllerInfo controllerInfo = controllerMappingInfoInitializer.getControllersInfo().get(method);

        // Чекаем контекст
        Context<?> context = (Context<?>) authentication.getPrincipal();
        Class<? extends Context> requestContextClazz = context.getClass();
        Class<? extends Context> controllerContextClazz = controllerInfo.getContext();
        if (!controllerContextClazz.isAssignableFrom(requestContextClazz)) {
            return ACCESS_DENIED;
        }

        if (controllerInfo.getPrivilege() == null) {
            return ACCESS_GRANTED;
        }

        // Чекаем привилегии
        if (context instanceof UserAuthenticatedContext) {
            try {
                CoreUserSource source = (CoreUserSource) context.getSource();
                UserDto user = userService.findById(source.getId());
                if (user != null && user.isAdmin()) {
                    return ACCESS_GRANTED;
                }
            } catch (ModuleException ignore) { }
        }

        PrivilegePair controllerPrivilege = new PrivilegePair(
                controllerInfo.getPrivilege().getKey(),
                controllerInfo.getAccessOps()
        );

        Map<String, AccessOpCollection> contextPrivileges = ((PrivilegeAuthorizing) context.getSource()).getPrivileges();
        PrivilegePair appropriateRequestPrivilege = contextPrivileges.keySet().stream()
                .filter(key -> Objects.equals(key, controllerPrivilege.getKey()))
                .map(key -> new PrivilegePair(key, contextPrivileges.get(key)))
                .findAny()
                .orElse(null);

        if (appropriateRequestPrivilege != null
                && appropriateRequestPrivilege.getAccessOpCollection().contains(controllerPrivilege.getAccessOpCollection())
        ) {
            return ACCESS_GRANTED;
        }

        return ACCESS_DENIED;
    }
}

