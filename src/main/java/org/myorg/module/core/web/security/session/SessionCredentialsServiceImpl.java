package org.myorg.module.core.web.security.session;

import org.myorg.module.auth.access.context.source.UserSource;
import org.myorg.module.auth.service.session.SessionCredentialsService;
import org.myorg.module.core.access.context.source.CoreUserSource;
import org.myorg.module.core.access.privilege.PrivilegePair;
import org.myorg.module.core.access.privilege.getter.PrivilegeGetter;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.module.core.database.service.user.UserDto;
import org.myorg.module.core.database.service.user.UserService;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SessionCredentialsServiceImpl implements SessionCredentialsService {

    private final UserService userService;
    private final PrivilegeGetter privilegeGetter;

    @Autowired
    public SessionCredentialsServiceImpl(UserService userService, PrivilegeGetter privilegeGetter) {
        this.userService = userService;
        this.privilegeGetter = privilegeGetter;
    }

    @Override
    public boolean isUserExists(Object id) throws ModuleException {
        return userService.findById((Long) id) != null;
    }

    @Override
    public UserSource createSource(Object id) throws ModuleException {
        long userId = (long) id;
        UserDto userDto = userService.findById(userId);
        return new CoreUserSource(userId, getPrivileges(userDto));
    }

    private Set<PrivilegePair> getPrivileges(UserDto userDto) throws ModuleException {
        Set<AccessRoleDto> accessRoleDtos = userService.findAllAccessRoles(userDto.getId(), ContextUtils.createSystemContext());
        return privilegeGetter.mergeAccessRoles(accessRoleDtos);
    }

}
