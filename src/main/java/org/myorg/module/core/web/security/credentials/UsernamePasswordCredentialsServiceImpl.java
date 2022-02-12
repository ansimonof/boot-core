package org.myorg.module.core.web.security.credentials;

import org.myorg.module.auth.access.context.source.UserSource;
import org.myorg.module.auth.service.UsernamePasswordCredentialsService;
import org.myorg.module.auth.service.credentials.UsernamePasswordCredentials;
import org.myorg.module.core.access.context.source.CoreUserSource;
import org.myorg.module.core.access.privilege.PrivilegePair;
import org.myorg.module.core.access.privilege.getter.PrivilegeGetter;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.module.core.database.service.user.UserDto;
import org.myorg.module.core.database.service.user.UserService;
import org.myorg.modules.modules.exception.ModuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class UsernamePasswordCredentialsServiceImpl implements UsernamePasswordCredentialsService {

    private final UserService userService;
    private final PrivilegeGetter privilegeGetter;

    @Autowired
    public UsernamePasswordCredentialsServiceImpl(UserService userService, PrivilegeGetter privilegeGetter) {
        this.userService = userService;
        this.privilegeGetter = privilegeGetter;
    }

    @Override
    public boolean isCorrect(UsernamePasswordCredentials credentials) throws ModuleException {
        UserDto userDto = userService.findByUsername(credentials.getUsername());
        return userDto != null && Objects.equals(userDto.getPasswordHash(), credentials.getPasswordHash());
    }

    @Override
    public UserSource createSource(UsernamePasswordCredentials credentials) throws ModuleException {
        UserDto userDto = userService.findByUsername(credentials.getUsername());
        return new CoreUserSource(userDto.getId(), getPrivileges(userDto));
    }

    private Set<PrivilegePair> getPrivileges(UserDto userDto) throws ModuleException {
        Set<AccessRoleDto> accessRolesDto = userService.findAllAccessRoles(userDto.getId());
        return privilegeGetter.mergeAccessRoles(accessRolesDto);
    }
}
