package org.myorg.module.core.controller;

import org.myorg.module.auth.access.context.UnauthenticatedContext;
import org.myorg.module.auth.access.context.UserAuthenticatedContext;
import org.myorg.module.auth.exception.AuthExceptionBuilder;
import org.myorg.module.core.CoreModuleConsts;
import org.myorg.module.core.access.AccessPermission;
import org.myorg.module.core.access.context.source.CoreUserSource;
import org.myorg.module.core.application.Application;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.exception.ModuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(CoreModuleConsts.REST_CONTROLLER_PREFIX + "/application")
public class ApplicationController {

    private List<? extends Application> applications;

    @Autowired(required = false)
    public ApplicationController(List<? extends Application> applications) {
        this.applications = applications;
    }

    @GetMapping("/list")
    @AccessPermission(
            context = UnauthenticatedContext.class
    )
    public ResponseEntity<List<String>> list() {
        return ResponseEntity.ok(applications.stream()
                .map(Application::getUuid)
                .collect(Collectors.toList()));
    }

    @GetMapping("/user-app-list")
    @AccessPermission(
            context = UserAuthenticatedContext.class
    )
    public ResponseEntity<List<String>> listApplications(
            final Context<?> context
    ) throws ModuleException {
        if (!(context.getSource() instanceof CoreUserSource)) {
            throw AuthExceptionBuilder.buildInvalidRequestSourceException(
                    context.getSource().getClass(), CoreUserSource.class);
        }

        CoreUserSource userSource = (CoreUserSource) context.getSource();
        long userId = userSource.getId();

        List<String> appUuids = new ArrayList<>();
        for (Application application : applications) {
            if (application.isUserHasAccess(userId)) {
                appUuids.add(application.getUuid());
            }
        }

        return ResponseEntity.ok(appUuids);
    }

}
