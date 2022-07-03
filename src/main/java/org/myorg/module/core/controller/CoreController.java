package org.myorg.module.core.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myorg.module.auth.access.context.UnauthenticatedContext;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.module.core.access.AccessPermission;
import org.myorg.module.core.database.service.user.UserBuilder;
import org.myorg.module.core.database.service.user.UserDto;
import org.myorg.module.core.database.service.user.UserService;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.modules.exception.ModuleExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CoreModuleConsts.REST_CONTROLLER_PREFIX)
public class CoreController {

    private final UserService userService;

    @Autowired
    public CoreController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/init")
    @AccessPermission(
            context = UnauthenticatedContext.class
    )
    public ResponseEntity<UserDto> init(
            final Context<?> context,
            @RequestBody final InitForm initForm
    ) throws ModuleException {
        if (userService.findAdmins(context).size() > 0) {
            throw ModuleExceptionBuilder.buildInternalServerErrorException("Server is already initialized");
        }

        UserDto user = userService.create(
                UserBuilder.builder()
                        .username(initForm.username)
                        .passwordHash(initForm.passwordHash)
                        .isEnabled(true)
                        .isAdmin(true),
                context
        );
        return ResponseEntity.ok(user);
    }

    @Data
    @NoArgsConstructor
    private static class InitForm {
        @JsonProperty("username")
        private String username;
        @JsonProperty("password_hash")
        private String passwordHash;
    }
}
