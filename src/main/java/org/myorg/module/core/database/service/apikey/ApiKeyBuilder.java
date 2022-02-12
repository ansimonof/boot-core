package org.myorg.module.core.database.service.apikey;

import org.myorg.modules.utils.DomainObjectBuilder;

public class ApiKeyBuilder extends DomainObjectBuilder {

    private final BuilderField<String> name = new BuilderField<>();
    private final BuilderField<String> value = new BuilderField<>();

    public static ApiKeyBuilder builder() {
        return new ApiKeyBuilder();
    }

    public ApiKeyBuilder name(String name) {
        this.name.setValue(name);
        return this;
    }

    public ApiKeyBuilder value(String value) {
        this.value.setValue(value);
        return this;
    }

    //-------------------------

    public String getName() {
        return name.getValue();
    }

    public String getValue() {
        return value.getValue();
    }

    //-------------------------

    public boolean isContainName() {
        return name.isContain();
    }

    public boolean isContainValue() {
        return value.isContain();
    }
}
