package org.myorg.module.core.controller;

import org.myorg.module.core.CoreModuleConsts;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CoreModuleConsts.REST_CONTROLLER_PREFIX)
public class CoreController {

//    private final UserService userService;
//
//    @Autowired
//    public CoreController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @PostMapping("/init")
//    @AccessPermission(
//            context = UnauthenticatedContext.class
//    )
//    public ResponseEntity<UserDto> init(
//            final Context<?> context,
//            @RequestBody final InitForm initForm
//    ) throws ModuleException {
//        if (userService.findAdmins(context).size() > 0) {
//            throw ModuleExceptionBuilder.buildInternalServerErrorException("Server is already initialized");
//        }
//
//        UserDto user = userService.create(
//                UserBuilder.builder()
//                        .username(initForm.username)
//                        .passwordHash(initForm.passwordHash)
//                        .isEnabled(true)
//                        .isAdmin(true),
//                context
//        );
//        return ResponseEntity.ok(user);
//    }
//
//    @Data
//    @NoArgsConstructor
//    private static class InitForm {
//        @JsonProperty("username")
//        private String username;
//        @JsonProperty("password_hash")
//        private String passwordHash;
//    }
}
