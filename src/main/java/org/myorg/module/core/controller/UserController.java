package org.myorg.module.core.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myorg.module.auth.access.context.AuthenticatedContext;
import org.myorg.module.auth.access.context.UnauthenticatedContext;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.module.core.access.AccessPermission;
import org.myorg.module.core.access.privilege.AccessOp;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.module.core.database.service.user.UserBuilder;
import org.myorg.module.core.database.service.user.UserDto;
import org.myorg.module.core.database.service.user.UserService;
import org.myorg.module.core.privilege.UserManagementPrivilege;
import org.myorg.modules.modules.exception.ModuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(CoreModuleConsts.REST_CONTROLLER_PREFIX + "/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    @AccessPermission(
            context = UnauthenticatedContext.class
    )
    public ResponseEntity<UserDto> registration(
            @RequestBody final RegistrationForm registrationForm
    ) throws ModuleException {
        UserDto user = userService.create(
                UserBuilder.builder()
                        .username(registrationForm.username)
                        .passwordHash(registrationForm.passwordHash)
                        .isEnabled(true)
                        .isAdmin(false)
        );
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<UserDto> findById(
            @RequestParam final Long id
    ) throws ModuleException {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/list_access_role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<Set<AccessRoleDto>> listAccessRoles(
            @RequestParam final Long id
    ) throws ModuleException {
        return ResponseEntity.ok(userService.findAllAccessRoles(id));
    }

    @PutMapping("/add_access_role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<Boolean> addAccessRole(
            @RequestParam final Long userId,
            @RequestParam final Long accessRoleId
    ) throws ModuleException {
        userService.addAccessRole(userId, accessRoleId);
        return ResponseEntity.ok(true);
    }

    @PutMapping("/remove_access_role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = UserManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<Boolean> removeAccessRole(
            @RequestParam final Long userId,
            @RequestParam final Long accessRoleId
    ) throws ModuleException {
        userService.removeAccessRole(userId, accessRoleId);
        return ResponseEntity.ok(true);
    }

    @Data
    @NoArgsConstructor
    private static class RegistrationForm {

        @JsonProperty("username")
        private String username;
        @JsonProperty("password_hash")
        private String passwordHash;
    }

}
