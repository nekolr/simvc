package com.nekolr.simvc.utils;

import com.nekolr.simvc.meta.MetaDataBean;

import java.lang.reflect.InvocationTargetException;

public class ReflectionUtil {

    public static Object instanceBean(MetaDataBean metaDataBean) {
        Object obj = null;
        try {
            obj = metaDataBean.getClazz().getConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return obj;

    }
}
