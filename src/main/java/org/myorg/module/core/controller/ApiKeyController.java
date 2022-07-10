package org.myorg.module.core.controller;

import org.myorg.module.auth.access.context.AuthenticatedContext;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.module.core.access.AccessPermission;
import org.myorg.module.core.access.privilege.AccessOp;
import org.myorg.module.core.database.domainobject.DbApiKey;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.module.core.database.service.apikey.ApiKeyBuilder;
import org.myorg.module.core.database.service.apikey.ApiKeyDto;
import org.myorg.module.core.database.service.apikey.ApiKeyService;
import org.myorg.module.core.privilege.ApiKeyRoleManagementPrivilege;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.modules.exception.ModuleExceptionBuilder;
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

    @GetMapping("/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<ApiKeyDto> findById(
            @PathVariable final long id) {
        ApiKeyDto apiKeyDto = apiKeyService.findById(id);
        return ResponseEntity.ok(apiKeyDto);
    }

    @GetMapping("/list")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<Set<ApiKeyDto>> list() {
        return ResponseEntity.ok(apiKeyService.findAll());
    }

    @PostMapping("/create")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<ApiKeyDto> create(
            final Context<?> context,
            @RequestParam("name") final String name,
            @RequestParam("value") final String value
    ) throws ModuleException {
        ApiKeyDto apiKeyDto = apiKeyService.create(
                ApiKeyBuilder.builder().name(name).value(value),
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
            @PathVariable final long id,
            @RequestParam("name") final String name,
            @RequestParam("value") final String value
    ) throws ModuleException {
        ApiKeyDto apiKeyDto = apiKeyService.update(
                id,
                ApiKeyBuilder.builder().name(name).value(value),
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
            @PathVariable final long id
    ) throws ModuleException {
        if (apiKeyService.findById(id) == null) {
            throw ModuleExceptionBuilder.buildNotFoundDomainObjectException(DbApiKey.class, id);
        }

        apiKeyService.remove(id);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/list-access-role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<Set<AccessRoleDto>> listAccessRoles(
            @RequestParam final long id
    ) throws ModuleException {
        return ResponseEntity.ok(apiKeyService.findAllAccessRoles(id));
    }

    @PatchMapping("/add-access-role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<Boolean> addAccessRole(
            @RequestParam("api-key-id") final long apiKeyId,
            @RequestParam("access-role-id") final long accessRoleId
    ) throws ModuleException {
        apiKeyService.addAccessRole(apiKeyId, accessRoleId);
        return ResponseEntity.ok(true);
    }

    @PatchMapping("/remove-access-role")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = ApiKeyRoleManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<Boolean> removeAccessRole(
            @RequestParam("api-key-id") final long apiKeyId,
            @RequestParam("access-role-id") final long accessRoleId
    ) throws ModuleException {
        apiKeyService.removeAccessRole(apiKeyId, accessRoleId);
        return ResponseEntity.ok(true);
    }

}
