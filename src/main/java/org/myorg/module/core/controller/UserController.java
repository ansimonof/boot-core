package org.myorg.module.core.controller;

import org.myorg.module.auth.access.context.AuthenticatedContext;
import org.myorg.module.auth.access.context.UnauthenticatedContext;
import org.myorg.module.auth.access.context.UserSessionAuthenticatedContext;
import org.myorg.module.auth.service.session.SessionRegistryService;
import org.myorg.module.auth.service.session.SessionUser;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.module.core.access.AccessPermission;
import org.myorg.module.core.access.privilege.AccessOp;
import org.myorg.module.core.database.domainobject.DbUser;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.module.core.database.service.user.UserBuilder;
import org.myorg.module.core.database.service.user.UserDto;
import org.myorg.module.core.database.service.user.UserService;
import org.myorg.module.core.exception.CoreExceptionBuilder;
import org.myorg.module.core.privilege.UserManagementPrivilege;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.modules.exception.ModuleExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping(CoreModuleConsts.REST_CONTROLLER_PREFIX + "/user")
public class UserController {

    private final UserService userService;
    private final SessionRegistryService sessionRegistryService;

    @Autowired
    public UserController(UserService userService,
                          SessionRegistryService sessionRegistryService) {
        this.userService = userService;
        this.sessionRegistryService = sessionRegistryService;
    }

    @PostMapping("/registration")
    @AccessPermission(
            context = UnauthenticatedContext.class
    )
    public ResponseEntity<UserDto> registration(
            final Context<?> context,
            @RequestParam("username") final String username,
            @RequestParam("password-hash") final String passwordHash
    ) throws ModuleException {
        UserDto user = userService.create(
                UserBuilder.builder()
                        .username(username)
                        .passwordHash(passwordHash)
                        .isAdmin(false)
                        .isEnabled(true)
                        .timeZone("UTC+3"),
                context
        );
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logon")
    @AccessPermission(
            context = UnauthenticatedContext.class
    )
    public ResponseEntity<SessionUser> logon(
            @RequestParam("username") final String username,
            @RequestParam("password-hash") final String passwordHash
    ) throws ModuleException {
        UserDto user = userService.findByUsername(username);
        if (user == null) {
            throw ModuleExceptionBuilder.buildNotFoundDomainObjectException(DbUser.class, DbUser.FIELD_USERNAME, username);
        } else if (!Objects.equals(user.getPasswordHash(), passwordHash)) {
            throw CoreExceptionBuilder.buildBadPasswordForUser(username, passwordHash);
        } else if (!user.isEnabled()) {
            throw CoreExceptionBuilder.buildUserIsBannedException(username);
        }

        SessionUser sessionUser = sessionRegistryService.auth(username, user.getId());
        return ResponseEntity.ok(sessionUser);
    }

    @PostMapping("/logout")
    @AccessPermission(
            context = UserSessionAuthenticatedContext.class
    )
    public ResponseEntity<Boolean> logout(
            final Context<?> context
    ) {
        UserSessionAuthenticatedContext sessionAuthenticatedContext = (UserSessionAuthenticatedContext) context;
        sessionRegistryService.logout(sessionAuthenticatedContext.getSession());
        return ResponseEntity.ok(true);
    }

    @GetMapping("/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<UserDto> findById(
            @PathVariable final long id
    ) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @DeleteMapping("/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.DELETE }
    )
    public ResponseEntity<Long> remove(
            @PathVariable final long id
    ) throws ModuleException {
        UserDto userDto = userService.findById(id);
        if (userDto == null) {
            throw ModuleExceptionBuilder.buildNotFoundDomainObjectException(DbUser.class, id);
        }
        userService.remove(id);
        return ResponseEntity.ok(id);
    }

    @PatchMapping("/enable-status/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<UserDto> banUser(
            final Context<?> context,
            @PathVariable final long id,
            @RequestParam("enabled") final boolean enabled
    ) throws ModuleException {
        return ResponseEntity.ok(userService.update(id, UserBuilder.builder().isEnabled(enabled), context));
    }

    @GetMapping("/list-access-role/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<Set<AccessRoleDto>> listAccessRoles(
            @PathVariable final long id
    ) throws ModuleException {
        return ResponseEntity.ok(userService.findAllAccessRoles(id));
    }

    @PatchMapping("/add-access-role/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<Boolean> addAccessRole(
            @PathVariable final long id,
            @RequestParam("access-role-id") final long accessRoleId
    ) throws ModuleException {
        userService.addAccessRole(id, accessRoleId);
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/remove-access-role/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<Boolean> removeAccessRole(
            @PathVariable final long id,
            @RequestParam("access-role-id") final long accessRoleId
    ) throws ModuleException {
        userService.removeAccessRole(id, accessRoleId);
        return ResponseEntity.ok(true);
    }

}
