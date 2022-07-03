package org.myorg.module.core.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.myorg.module.auth.access.context.AuthenticatedContext;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.module.core.access.AccessPermission;
import org.myorg.module.core.access.privilege.AccessOp;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.module.core.database.service.apikey.ApiKeyBuilder;
import org.myorg.module.core.database.service.apikey.ApiKeyDto;
import org.myorg.module.core.database.service.apikey.ApiKeyService;
import org.myorg.module.core.privilege.ApiKeyRoleManagementPrivilege;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.exception.ModuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(CoreModuleConsts.REST_CONTROLLER_PREFIX + "/api-key")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @Autowired
    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @GetMapping("{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<ApiKeyDto> findById(
            final Context<?> context,
            @PathVariable final Long id
    ) throws ModuleException {
        ApiKeyDto apiKeyDto = apiKeyService.findById(id ,context);
        return ResponseEntity.ok(apiKeyDto);
    }

    @GetMapping("/list")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<Set<ApiKeyDto>> list(
            final Context<?> context
    ) throws ModuleException {
        return ResponseEntity.ok(apiKeyService.findAll(context));
    }

    @PostMapping("/create")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<ApiKeyDto> create(
            final Context<?> context,
            @RequestParam final ApiKeyForm creationForm
    ) throws ModuleException {
        ApiKeyDto apiKeyDto = apiKeyService.create(
                ApiKeyBuilder.builder()
                        .name(creationForm.name)
                        .value(creationForm.value),
                context);
        return ResponseEntity.ok(apiKeyDto);
    }

    @PatchMapping("/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<ApiKeyDto> update(
            final Context<?> context,
            @PathVariable final Long id,
            @RequestParam final ApiKeyForm apiKeyForm
    ) throws ModuleException {
        ApiKeyDto apiKeyDto = apiKeyService.update(
                id,
                ApiKeyBuilder.builder()
                        .name(apiKeyForm.name)
                        .value(apiKeyForm.value),
                context);

        return ResponseEntity.ok(apiKeyDto);
    }

    @DeleteMapping("/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.DELETE }
    )
    public ResponseEntity<Long> remove(
            final Context<?> context,
            @PathVariable final Long id,
            @RequestParam final ApiKeyForm apiKeyForm
    ) throws ModuleException {
        ApiKeyDto apiKeyDto = apiKeyService.update(
                id,
                ApiKeyBuilder.builder()
                        .name(apiKeyForm.name)
                        .value(apiKeyForm.value),
                context);

        return ResponseEntity.ok(id);
    }

    @GetMapping("/list-access-role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<Set<AccessRoleDto>> listAccessRoles(
            final Context<?> context,
            @RequestParam final Long id
    ) throws ModuleException {
        return ResponseEntity.ok(apiKeyService.findAllAccessRoles(id, context));
    }

    @PatchMapping("/add-access-role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<Boolean> addAccessRole(
            final Context<?> context,
            @RequestParam("api-key-id") final Long apiKeyId,
            @RequestParam("access-role-id") final Long accessRoleId
    ) throws ModuleException {
        apiKeyService.addAccessRole(apiKeyId, accessRoleId, context);
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/remove-access-role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<Boolean> removeAccessRole(
            final Context<?> context,
            @RequestParam("api-key-id") final Long apiKeyId,
            @RequestParam("access-role-id") final Long accessRoleId
    ) throws ModuleException {
        apiKeyService.removeAccessRole(apiKeyId, accessRoleId, context);
        return ResponseEntity.ok(true);
    }

    private static class ApiKeyForm {

        @JsonProperty("name")
        public String name;
        @JsonProperty("value")
        public String value;
    }
}
