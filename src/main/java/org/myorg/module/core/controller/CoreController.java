package org.myorg.module.core.controller;

import org.myorg.module.auth.access.context.UnauthenticatedContext;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.module.core.access.AccessPermission;
import org.myorg.module.core.access.privilege.getter.ModulePrivilegeGetter;
import org.myorg.module.core.config.ServerStatus;
import org.myorg.module.core.database.service.accessrole.AccessRoleBuilder;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.module.core.database.service.accessrole.AccessRoleService;
import org.myorg.module.core.database.service.accessrole.PrivilegeBuilder;
import org.myorg.module.core.database.service.config.CoreConfigService;
import org.myorg.module.core.database.service.user.UserBuilder;
import org.myorg.module.core.database.service.user.UserDto;
import org.myorg.module.core.database.service.user.UserService;
import org.myorg.module.core.exception.CoreExceptionBuilder;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.exception.ModuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(CoreModuleConsts.REST_CONTROLLER_PREFIX)
public class CoreController {

    private final UserService userService;
    private final AccessRoleService accessRoleService;
    private final List<? extends ModulePrivilegeGetter> privilegesGetter;
    private final CoreConfigService configService;

    @Autowired
    public CoreController(UserService userService,
                          AccessRoleService accessRoleService,
                          List<? extends ModulePrivilegeGetter> privilegesGetter,
                          CoreConfigService configService) {
        this.userService = userService;
        this.accessRoleService = accessRoleService;
        this.privilegesGetter = privilegesGetter;
        this.configService = configService;
    }

    @PostMapping("/init")
    @AccessPermission(
            context = UnauthenticatedContext.class
    )
    @Transactional(rollbackFor = Throwable.class)
    public ResponseEntity<Boolean> init(
            final Context<?> context,
            @RequestParam("username") final String username,
            @RequestParam("password-hash") final String passwordHash
    ) throws ModuleException {
        if (configService.getServerStatus() == ServerStatus.INITIALIZED) {
            throw CoreExceptionBuilder.buildServerIsAlreadyInitialized();
        }

        UserDto user = userService.create(
                UserBuilder.builder()
                        .username(username)
                        .passwordHash(passwordHash)
                        .isEnabled(true)
                        .isAdmin(true)
                        .timeZone("UTC+3"),
                context);

        Set<PrivilegeBuilder> privilegeBuilders = privilegesGetter.stream()
                .flatMap(o -> o.getPrivileges().stream())
                .map(p -> PrivilegeBuilder.builder().key(p.getKey()).ops(p.getAccessOpCollection().getOps()))
                .collect(Collectors.toSet());

        AccessRoleDto accessRole = accessRoleService.create(
                AccessRoleBuilder.builder().name("Server administrator"), context);
        accessRoleService.addPrivileges(accessRole.getId(), privilegeBuilders);

        userService.addAccessRole(user.getId(), accessRole.getId());

        configService.setServerStatus(ServerStatus.INITIALIZED);

        return ResponseEntity.ok(true);
    }

}
