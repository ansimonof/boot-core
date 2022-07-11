package org.myorg.module.core.config;

import org.myorg.module.core.utils.config.EnumConfig;

public class CoreConfigDescriptions {

    public static class Server {

        public static final EnumConfig<ServerStatus> STATUS =
                new EnumConfig<>("server.status", ServerStatus.NOT_INITIALIZED, ServerStatus.class);
    }
}
