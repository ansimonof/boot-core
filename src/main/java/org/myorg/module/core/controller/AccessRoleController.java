package org.myorg.module.core.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myorg.module.auth.access.context.AuthenticatedContext;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.module.core.access.AccessPermission;
import org.myorg.module.core.access.privilege.AbstractPrivilege;
import org.myorg.module.core.access.privilege.AccessOp;
import org.myorg.module.core.access.privilege.getter.PrivilegeGetter;
import org.myorg.module.core.database.domainobject.DbAccessRole;
import org.myorg.module.core.database.service.accessrole.*;
import org.myorg.module.core.privilege.AccessRoleManagementPrivilege;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.modules.exception.ModuleExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(CoreModuleConsts.REST_CONTROLLER_PREFIX + "/access-role")
public class AccessRoleController {

    private final PrivilegeGetter privilegeGetter;
    private final AccessRoleService accessRoleService;

    @Autowired
    public AccessRoleController(PrivilegeGetter privilegeGetter, AccessRoleService accessRoleService) {
        this.privilegeGetter = privilegeGetter;
        this.accessRoleService = accessRoleService;
    }

    @GetMapping("/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = AccessRoleManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<AccessRoleDto> findById(
            final Context<?> context,
            @PathVariable final Long id
    ) throws ModuleException {
        AccessRoleDto accessRoleDto = accessRoleService.findById(id, context);
        if (accessRoleDto == null) {
            throw ModuleExceptionBuilder.buildNotFoundDomainObjectException(DbAccessRole.class, id);
        }
        return ResponseEntity.ok(accessRoleDto);
    }


    @GetMapping("/list")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = AccessRoleManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<Set<AccessRoleDto>> list(
            final Context<?> context
    ) throws ModuleException {
        return ResponseEntity.ok(accessRoleService.findAll(context));
    }

    @PostMapping("/create")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = AccessRoleManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<AccessRoleDto> create(
            final Context<?> context,
            @RequestParam final String name
    ) throws ModuleException {
        AccessRoleDto accessRoleDto = accessRoleService.create(AccessRoleBuilder.builder().name(name), context);
        return ResponseEntity.ok(accessRoleDto);
    }

    @PatchMapping("/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = AccessRoleManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<AccessRoleDto> update(
            final Context<?> context,
            @PathVariable final Long id,
            @RequestParam(required = false) final String name
    ) throws ModuleException {
        return ResponseEntity.ok(accessRoleService.update(id, AccessRoleBuilder.builder().name(name), context));
    }

    @DeleteMapping("/{id}")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = AccessRoleManagementPrivilege.class,
            ops = { AccessOp.DELETE }
    )
    public ResponseEntity<Long> remove(
            final Context<?> context,
            @PathVariable final Long id
    ) throws ModuleException {
        accessRoleService.remove(id, context);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/privilege/list")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = AccessRoleManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<List<PrivilegeDto>> privilegesList() {
        List<? extends AbstractPrivilege> privileges = privilegeGetter.getAllPrivileges();
        List<PrivilegeDto> result = new ArrayList<>();
        for (AbstractPrivilege privilege : privileges) {
            PrivilegeDto privilegeDto = new PrivilegeDto();
            privilegeDto.setKey(privilege.getKey());
            privilegeDto.setOps(privilege.getAccessOpCollection().getOps());

            result.add(privilegeDto);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/privilege")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = AccessRoleManagementPrivilege.class,
            ops = { AccessOp.READ }
    )
    public ResponseEntity<PrivilegeDto> findPrivilegeByKey(
            @RequestParam final String key
    ) throws ModuleException {
        AbstractPrivilege privilege = privilegeGetter.getAllPrivileges().stream()
                .filter(p -> Objects.equals(p.getKey(), key))
                .findFirst()
                .orElse(null);
        if (privilege == null) {
            throw ModuleExceptionBuilder.buildInvalidValueException(key);
        }

        PrivilegeDto privilegeDto = PrivilegeDto.builder()
                .key(privilege.getKey())
                .ops(privilege.getAccessOpCollection().getOps())
                .build();

        return ResponseEntity.ok(privilegeDto);
    }

    @PatchMapping("/set-privileges")
    @AccessPermission(
            context = AuthenticatedContext.class,
            privilege = AccessRoleManagementPrivilege.class,
            ops = { AccessOp.WRITE }
    )
    public ResponseEntity<AccessRoleDto> setPrivileges(
            final Context<?> context,
            @RequestParam(name = "access-role-id") final Long accessRoleId,
            @RequestBody final PrivilegeSet newPrivileges
    ) throws ModuleException {
        AccessRoleDto accessRoleDto = accessRoleService.addPrivileges(
                accessRoleId,
                newPrivileges.getPrivileges().stream()
                        .map(p -> PrivilegeBuilder.builder()
                                .key(p.getKey())
                                .ops(p.getOps())
                        ).collect(Collectors.toSet()),
                context);

        return ResponseEntity.ok(accessRoleDto);
    }

    @Data
    @NoArgsConstructor
    private static class PrivilegeSet {
        @JsonProperty("privileges")
        List<PrivilegeRequest> privileges;
    }

    @Data
    @NoArgsConstructor
    private static class PrivilegeRequest {
        @JsonProperty("key")
        String key;
        @JsonProperty("ops")
        AccessOp[] ops;
    }

}
