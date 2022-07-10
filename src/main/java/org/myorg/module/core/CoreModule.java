package org.myorg.module.core;

import org.myorg.module.auth.AuthModule;
import org.myorg.modules.modules.BootModule;
import org.myorg.modules.modules.Module;
import org.myorg.modules.modules.exception.ModuleException;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@BootModule(
        uuid = CoreModuleConsts.UUID,
        dependencies = { AuthModule.class }
)
@PropertySource(
        value = "boot-properties/core.application.properties"
)
public class CoreModule extends Module {

    @Override
    public void onStart() throws ModuleException {

    }

    @Override
    public void onDestroy() throws ModuleException {

    }
}
