package org.myorg.module.core.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myorg.module.auth.access.context.AuthenticatedContext;
import org.myorg.module.core.exception.CoreExceptionBuilder;
import org.myorg.modules.access.context.Context;
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
import org.myorg.module.core.privilege.UserManagementPrivilege;
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
    public UserController(UserService userService, SessionRegistryService sessionRegistryService) {
        this.userService = userService;
        this.sessionRegistryService = sessionRegistryService;
    }

    @PostMapping("/registration")
    @AccessPermission(
            context = UnauthenticatedContext.class
    )
    public ResponseEntity<UserDto> registration(
            final Context<?> context,
            @RequestBody final RegistrationForm registrationForm
    ) throws ModuleException {
        UserDto user = userService.create(
                UserBuilder.builder()
                        .username(registrationForm.username)
                        .passwordHash(registrationForm.passwordHash)
                        .isEnabled(true)
                        .isAdmin(false),
                context
        );
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logon")
    @AccessPermission(
            context = UnauthenticatedContext.class
    )
    public ResponseEntity<SessionUser> logon(
            final Context<?> context,
            @RequestBody final LogonForm logonForm
    ) throws ModuleException {
        UserDto user = userService.findByUsername(logonForm.username, context);
        if (user == null) {
            throw ModuleExceptionBuilder.buildNotFoundDomainObjectException(DbUser.class, DbUser.FIELD_USERNAME, logonForm.username);
        } else if (!Objects.equals(user.getPasswordHash(), logonForm.passwordHash)) {
            throw ModuleExceptionBuilder.buildInvalidValueException(logonForm.passwordHash);
        } else if (!user.isEnabled()) {
            throw CoreExceptionBuilder.buildUserIsBannedException(logonForm.username);
        }

        SessionUser sessionUser = sessionRegistryService.auth(logonForm.username, user.getId());
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
            final Context<?> context,
            @PathVariable final Long id
    ) throws ModuleException {
        return ResponseEntity.ok(userService.findById(id, context));
    }

    @DeleteMapping("/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.DELETE }
    )
    public ResponseEntity<Long> remove(
            final Context<?> context,
            @PathVariable final Long id
    ) throws ModuleException {
        userService.remove(id, context);
        return ResponseEntity.ok(id);
    }

    @PatchMapping("/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<UserDto> banUser(
            final Context<?> context,
            @PathVariable final Long id
    ) throws ModuleException {
        return ResponseEntity.ok(userService.banUser(id, context));
    }

    @GetMapping("/list-access-role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<Set<AccessRoleDto>> listAccessRoles(
            final Context<?> context,
            @RequestParam final Long id
    ) throws ModuleException {
        return ResponseEntity.ok(userService.findAllAccessRoles(id, context));
    }

    @PatchMapping("/add-access-role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<Boolean> addAccessRole(
            final Context<?> context,
            @RequestParam("user-id") final Long userId,
            @RequestParam("access-role-id") final Long accessRoleId
    ) throws ModuleException {
        userService.addAccessRole(userId, accessRoleId, context);
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/remove-access-role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<Boolean> removeAccessRole(
            final Context<?> context,
            @RequestParam("user-id") final Long userId,
            @RequestParam("access-role-id") final Long accessRoleId
    ) throws ModuleException {
        userService.removeAccessRole(userId, accessRoleId, context);
        return ResponseEntity.ok(true);
    }

    @Data
    @NoArgsConstructor
    private static class RegistrationForm {

        @JsonProperty("username")
        public String username;
        @JsonProperty("password_hash")
        public String passwordHash;
    }

    @Data
    @NoArgsConstructor
    private static class LogonForm {

        @JsonProperty("username")
        public String username;
        @JsonProperty("password_hash")
        public String passwordHash;
    }

}
