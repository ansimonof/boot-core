package org.myorg.module.core;

import org.myorg.module.auth.AuthModule;
import org.myorg.module.core.database.service.config.ConfigService;
import org.myorg.modules.modules.BootModule;
import org.myorg.modules.modules.Module;
import org.myorg.modules.modules.exception.ModuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@BootModule(
        uuid = CoreModuleConsts.UUID,
        dependencies = { AuthModule.class }
)
@PropertySource(
        value = "boot-properties/core.application.properties"
)
public class CoreModule extends Module {

    private final List<? extends ConfigService> configServices;

    @Autowired
    public CoreModule(List<? extends ConfigService> configServices) {
        this.configServices = configServices;
    }

    @Override
    public void onStart() throws ModuleException {
        configServices.forEach(ConfigService::createConfigIfNotExist);
    }

    @Override
    public void onDestroy() throws ModuleException {

    }
}
