package org.mustream.common.net;

import java.io.Serializable;

public class MustreamPackage implements Serializable {
    private final Object object;
    private final Class<?> objectClass;

    public MustreamPackage(Object object) {
        this.object = object;
        objectClass = object == null ?
                null : object.getClass();
    }

    public <T> T getObject() {
        return (T) object;
    }

    public Class<?> getObjectClass() {
        return objectClass;
    }
}
