package org.myorg.module.core.web.security.authorization;

import org.myorg.module.auth.access.context.UserAuthenticatedContext;
import org.myorg.module.auth.authentication.token.CustomAbstractAuthenticationToken;
import org.myorg.module.auth.authorization.CustomAccessDecisionVoter;
import org.myorg.module.core.access.context.source.CoreUserSource;
import org.myorg.module.core.access.context.source.PrivilegeAuthorizing;
import org.myorg.module.core.access.privilege.AccessOpCollection;
import org.myorg.module.core.access.privilege.PrivilegePair;
import org.myorg.module.core.database.domainobject.DbUser;
import org.myorg.module.core.database.service.user.UserDto;
import org.myorg.module.core.database.service.user.UserService;
import org.myorg.module.core.web.ControllerInfo;
import org.myorg.module.core.web.ControllerMappingInfoInitializer;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.modules.exception.ModuleExceptionBuilder;
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
        // Forbidding to execute query for unknown tokens
        if (!(authentication instanceof CustomAbstractAuthenticationToken)) {
            return ACCESS_DENIED;
        }

        if (!authentication.isAuthenticated()) {
            return ACCESS_DENIED;
        }

        Method method = (Method) authentication.getDetails();
        ControllerInfo controllerInfo = controllerMappingInfoInitializer.getControllersInfo().get(method);

        // Checking auth context
        Context<?> context = (Context<?>) authentication.getPrincipal();
        Class<? extends Context> requestContextClazz = context.getClass();
        Class<? extends Context> controllerContextClazz = controllerInfo.getContext();
        if (!controllerContextClazz.isAssignableFrom(requestContextClazz)) {
            return ACCESS_DENIED;
        }

        if (controllerInfo.getPrivilege() == null) {
            return ACCESS_GRANTED;
        }

        // Checking privileges
        if (context instanceof UserAuthenticatedContext) {
            CoreUserSource source = (CoreUserSource) context.getSource();
            UserDto user = userService.findById(source.getId(), context);
            if (user == null) {
                throw ModuleExceptionBuilder.buildNotFoundDomainObjectException(DbUser.class, source.getId());
            }
            if (user.isAdmin()) {
                return ACCESS_GRANTED;
            } else if (!user.isEnabled()) {
                return ACCESS_DENIED;
            }
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

