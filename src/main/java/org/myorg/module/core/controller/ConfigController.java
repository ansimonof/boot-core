package org.myorg.module.core.controller;

import org.myorg.module.auth.access.context.AuthenticatedContext;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.module.core.access.AccessPermission;
import org.myorg.module.core.config.ServerStatus;
import org.myorg.module.core.database.service.config.CoreConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CoreModuleConsts.REST_CONTROLLER_PREFIX + "/config")
public class ConfigController {

    private final CoreConfigService coreConfigService;

    @Autowired
    public ConfigController(CoreConfigService coreConfigService) {
        this.coreConfigService = coreConfigService;
    }

    @GetMapping("/server-status")
    @AccessPermission(
            context = AuthenticatedContext.class
    )
    public ResponseEntity<ServerStatus> getServerStatus() {
        return ResponseEntity.ok(coreConfigService.getServerStatus());
    }
}
