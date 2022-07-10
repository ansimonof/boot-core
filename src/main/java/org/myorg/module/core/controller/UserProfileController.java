package org.myorg.module.core.controller;

import org.myorg.module.auth.access.context.UserAuthenticatedContext;
import org.myorg.module.auth.exception.AuthExceptionBuilder;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.module.core.access.AccessPermission;
import org.myorg.module.core.access.context.source.CoreUserSource;
import org.myorg.module.core.database.service.user.UserBuilder;
import org.myorg.module.core.database.service.user.UserDto;
import org.myorg.module.core.database.service.user.UserService;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.exception.ModuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(CoreModuleConsts.REST_CONTROLLER_PREFIX + "/user-profile")
public class UserProfileController {

    private final UserService userService;

    @Autowired
    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    @AccessPermission(
            context = UserAuthenticatedContext.class
    )
    public ResponseEntity<UserDto> getProfile(
            final Context<?> context
    ) throws ModuleException {
        if (!(context.getSource() instanceof CoreUserSource)) {
            throw AuthExceptionBuilder.buildInvalidRequestSourceException(
                    context.getSource().getClass(), CoreUserSource.class);
        }

        CoreUserSource userSource = (CoreUserSource) context.getSource();
        UserDto userDto = userService.findById(userSource.getId());

        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/")
    @AccessPermission(
            context = UserAuthenticatedContext.class
    )
    public ResponseEntity<UserDto> update(
            final Context<?> context,
            @RequestParam("time-zone") final Optional<String> oTimeZone
    ) throws ModuleException {
        if (!(context.getSource() instanceof CoreUserSource)) {
            throw AuthExceptionBuilder.buildInvalidRequestSourceException(
                    context.getSource().getClass(), CoreUserSource.class);
        }

        CoreUserSource userSource = (CoreUserSource) context.getSource();

        UserBuilder builder = UserBuilder.builder();
        oTimeZone.ifPresent(builder::timeZone);

        UserDto userDto = userService.update(userSource.getId(), builder, context);
        return ResponseEntity.ok(userDto);
    }
}
