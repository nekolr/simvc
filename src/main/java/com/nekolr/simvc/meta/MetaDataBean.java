package com.nekolr.simvc.meta;

import com.nekolr.simvc.annotation.RequestMethod;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 *
 */
public class MetaDataBean implements Serializable {

    private Class<?> clazz;

    private Method method;

    private Object instance;

    private boolean isAjax;

    private String uri;

    private RequestMethod[] type;

    public MetaDataBean(Class<?> clazz, Method method, String uri, boolean isAjax, RequestMethod[] type) {
        this.clazz = clazz;
        this.method = method;
        this.uri = uri;
        this.isAjax = isAjax;
        this.type = type;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isAjax() {
        return isAjax;
    }

    public String getUri() {
        return uri;
    }

    public RequestMethod[] getType() {
        return type;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }
}
