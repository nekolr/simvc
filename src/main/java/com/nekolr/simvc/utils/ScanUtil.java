package com.nekolr.simvc.utils;


import com.nekolr.simvc.annotation.Controller;
import com.nekolr.simvc.annotation.RequestMapping;
import com.nekolr.simvc.annotation.ResponseBody;
import com.nekolr.simvc.meta.MetaDataBean;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * 包扫描工具类
 *
 * @author nekolr
 * @version 1.0
 * @description 借鉴了Spring的部分源码
 */
public class ScanUtil {

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable var3) {
            ;
        }
        if (cl == null) {
            cl = ScanUtil.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable var2) {
                    ;
                }
            }
        }
        return cl;
    }

    /**
     * 获取class类型集合
     *
     * @param basePackage
     * @param isRecursive
     * @return
     */
    public static List<Class<?>> getClassList(String basePackage, boolean isRecursive) {
        String formatPk = basePackage.replaceAll("\\.", "\\/");
        List<Class<?>> classList = new ArrayList<>();
        ClassLoader cl = getDefaultClassLoader();
        Enumeration resourceUrls = null;
        try {
            resourceUrls = cl != null ? cl.getResources(formatPk) : ClassLoader.getSystemResources(formatPk);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (resourceUrls.hasMoreElements()) {
            URL url = (URL) resourceUrls.nextElement();
            File file = new File(url.getFile());
            addClasses(classList, file.getAbsolutePath(), basePackage, isRecursive);
        }
        return classList;
    }

    /**
     * 获取指定注解或注解子类的class类型集合
     *
     * @param basePackage
     * @param isRecursive
     * @param annotationClass
     * @return
     */
    public static List<Class<?>> getClassListByAnnotation(String basePackage, boolean isRecursive, Class<? extends Annotation> annotationClass) {
        String formatPk = basePackage.replaceAll("\\.", "\\/");
        List<Class<?>> classList = new ArrayList<>();
        ClassLoader cl = getDefaultClassLoader();
        Enumeration resourceUrls = null;
        try {
            resourceUrls = cl != null ? cl.getResources(formatPk) : ClassLoader.getSystemResources(formatPk);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (resourceUrls.hasMoreElements()) {
            URL url = (URL) resourceUrls.nextElement();
            File file = new File(url.getFile());
            addClassesByAnnotation(classList, file.getAbsolutePath(), basePackage, isRecursive, annotationClass);
        }
        return classList;
    }

    /**
     * 获取扫描包路径
     *
     * @param basePackage
     * @return
     */
    public static List<File> getPackageFiles(String basePackage) {
        String formatPk = basePackage.replaceAll("\\.", "\\/");
        List<File> packageFiles = new ArrayList<>();
        ClassLoader cl = getDefaultClassLoader();
        Enumeration resourceUrls = null;
        try {
            resourceUrls = cl != null ? cl.getResources(formatPk) : ClassLoader.getSystemResources(formatPk);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (resourceUrls.hasMoreElements()) {
            URL url = (URL) resourceUrls.nextElement();
            File file = new File(url.getFile());
            packageFiles.add(file);
        }
        return packageFiles;
    }

    /**
     * 将class类型添加到集合
     *
     * @param classes
     * @param packagePath
     * @param packageName
     * @param isRecursive
     */
    public static void addClasses(List<Class<?>> classes, String packagePath, String packageName, boolean isRecursive) {
        File[] files = getClassFiles(packagePath);
        if (files != null) {
            for (int i = 0, len = files.length; i < len; i++) {
                String fileName = files[i].getName();
                if (files[i].isFile()) {
                    String className = getClassName(packageName, fileName);
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isRecursive) {
                        String subPackagePath = getSubPackagePath(packagePath, fileName);
                        String subPackageName = getSubPackageName(packageName, fileName);
                        addClasses(classes, subPackagePath, subPackageName, isRecursive);
                    }
                }
            }
        }
    }

    /**
     * 将含有指定注解或注解的子类的class添加到集合
     *
     * @param classes
     * @param packagePath
     * @param packageName
     * @param isRecursive
     * @param annotationClass
     */
    public static void addClassesByAnnotation(List<Class<?>> classes, String packagePath, String packageName,
                                              boolean isRecursive, Class<? extends Annotation> annotationClass) {
        File[] files = getClassFiles(packagePath);
        if (files != null) {
            for (int i = 0, len = files.length; i < len; i++) {
                String fileName = files[i].getName();
                if (files[i].isFile()) {
                    String className = getClassName(packageName, fileName);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(annotationClass)) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isRecursive) {
                        String subPackagePath = getSubPackagePath(packagePath, fileName);
                        String subPackageName = getSubPackageName(packageName, fileName);
                        addClassesByAnnotation(classes, subPackagePath, subPackageName, isRecursive, annotationClass);
                    }
                }
            }
        }
    }


    public static Map<String, MetaDataBean> getMetaMap(String basePackage, boolean isRecursive) {
        Map<String, MetaDataBean> map = new HashMap<>();
        if(basePackage!=null){
            List<File> files = getPackageFiles(basePackage);
            for (File file : files) {
                getMetaDataBeanMap(file.getAbsolutePath(), basePackage, isRecursive, map);
            }
        }
        return map;
    }

    /**
     * 获取封装元数据
     *
     * @param packagePath
     * @param packageName
     * @param isRecursive
     * @param map
     */
    public static void getMetaDataBeanMap(String packagePath, String packageName,
                                          boolean isRecursive, Map<String, MetaDataBean> map) {
        File[] files = getClassFiles(packagePath);
        if (files != null) {
            for (int i = 0, len = files.length; i < len; i++) {
                String fileName = files[i].getName();
                if (files[i].isFile()) {
                    String className = getClassName(packageName, fileName);
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(Controller.class)) {
                            String preUri = null;
                            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                                RequestMapping preRm = clazz.getAnnotation(RequestMapping.class);
                                preUri = preRm.value();
                            }
                            Method[] methods = clazz.getMethods();
                            for (Method method : methods) {
                                if (method.isAnnotationPresent(RequestMapping.class)) {
                                    RequestMapping nxRm = method.getAnnotation(RequestMapping.class);
                                    boolean isAjax = false;
                                    if (method.isAnnotationPresent(ResponseBody.class)) {
                                        isAjax = true;
                                    }
                                    String uri = preUri + nxRm.value();
                                    MetaDataBean metaDataBean = new MetaDataBean(clazz, method, uri, isAjax, nxRm.method());
                                    map.put(uri, metaDataBean);
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isRecursive) {
                        String subPackagePath = getSubPackagePath(packagePath, fileName);
                        String subPackageName = getSubPackageName(packageName, fileName);
                        getMetaDataBeanMap(subPackagePath, subPackageName, isRecursive, map);
                    }
                }
            }
        }
    }

    /**
     * 获取方法集合
     *
     * @param clazz
     * @param annotationClass
     * @return
     */
    public static List<Method> getMethodListByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Method> list = new ArrayList<>();
        if (clazz != null) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == annotationClass) {
                        list.add(method);
                        break;
                    }
                }
            }
        }
        return list;
    }


    /**
     * 从给定包路径获取class文件
     *
     * @param packagePath
     * @return
     */
    private static File[] getClassFiles(String packagePath) {
        return new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
    }

    /**
     * 通过给定的包名和文件名获取全类名
     *
     * @param packageName
     * @param fileName
     * @return
     */
    private static String getClassName(String packageName, String fileName) {
        if (!"".equals(packageName) && packageName != null) {
            return packageName + "." + fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
    }

    /**
     * 获取下级包名
     *
     * @param packageName
     * @param fileName
     * @return
     */
    private static String getSubPackageName(String packageName, String fileName) {
        if (!"".equals(packageName) && packageName != null) {
            return packageName + "." + fileName;
        } else {
            return fileName;
        }
    }

    /**
     * 获取下级包路径
     *
     * @param packagePath
     * @param fileName
     * @return
     */
    private static String getSubPackagePath(String packagePath, String fileName) {
        if (!"".equals(packagePath) && packagePath != null) {
            return packagePath + "/" + fileName;
        } else {
            return fileName;
        }
    }
}
