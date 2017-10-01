package com.nekolr.simvc.param;

import com.nekolr.simvc.asm.ReadMethodArgNameClassVisitor;
import com.nekolr.simvc.utils.ScanUtil;
import org.objectweb.asm.ClassReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MethodResolver {

    /**
     * 获取方法的参数名称
     *
     * @param className
     * @param methodName
     * @return
     * @throws IOException
     */
    public static List<String> getMethodParamNames(String className, String methodName) throws IOException {
        List<String> paramList = new ArrayList<>();
        String fileName = getClassFileName(className);
        InputStream is = new FileInputStream(new File(fileName));
        ClassReader classReader = new ClassReader(is);
        ReadMethodArgNameClassVisitor classVisitor = new ReadMethodArgNameClassVisitor();
        classReader.accept(classVisitor, 0);
        for (Map.Entry<String, List<String>> entry : classVisitor.nameArgMap.entrySet()) {
            if (entry.getKey().equals(methodName)) {
                for (String s : entry.getValue()) {
                    paramList.add(s);
                }
            }
        }
        return paramList;
    }

    /**
     * 获取class文件名
     *
     * @param className
     * @return
     */
    private static String getClassFileName(String className) {
        String formatName = className.replaceAll("\\.", "\\/") + ".class";
        ClassLoader cl = ScanUtil.getDefaultClassLoader();
        Enumeration resourceUrls = null;
        try {
            resourceUrls = cl != null ? cl.getResources(formatName) : ClassLoader.getSystemResources(formatName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        URL url = (URL) resourceUrls.nextElement();
        return url.getFile();
    }

    /**
     * 封装参数
     *
     * @param paramList
     * @param paramTypes
     * @param parameterMap
     * @param request
     * @param response
     * @return
     */
    public static Object[] getInvokeParams(List<String> paramList, Class<?>[] paramTypes, Map<String, String[]> parameterMap, HttpServletRequest request, HttpServletResponse response) {
        Object[] invokeParams = new Object[paramList.size()];
        if (paramList != null) {
            for (int i = 0, len = paramTypes.length; i < len; i++) {
                Class<?> type = paramTypes[i];
                if ("javax.servlet.http.HttpServletRequest".equals(type.getName()))
                    invokeParams[i] = request;
                else if ("javax.servlet.http.HttpServletResponse".equals(type.getName()))
                    invokeParams[i] = response;
                else {
                    String paramName = paramList.get(i);
                    for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                        if (paramName.equals(entry.getKey())) {
                            String[] values = entry.getValue();
                            invokeParams[i] = transform(type, values);
                        }
                    }
                }
            }
        }
        return invokeParams;
    }

    /**
     * 类型转换
     *
     * @param type
     * @param values
     * @return
     */
    private static Object transform(Class<?> type, String[] values) {
        if (type.isArray()) {
            Class<?> clazz = type.getComponentType();
            Object[] params = (Object[]) Array.newInstance(clazz, values.length);
            for (int i = 0; i < values.length; i++) {
                params[i] = typeTransform(clazz, values[i]);
            }
            return params;
        } else {
            return typeTransform(type, values[0]);
        }
    }


    private static Object typeTransform(Class<?> type, String value) {
        String typeName = type.getName();
        if ("java.lang.String".equals(typeName))
            return value.toString();
        else if ("java.lang.Integer".equals(typeName) || "int".equals(typeName))
            return Integer.valueOf(value);
        else if ("java.lang.Boolean".equals(typeName) || "boolean".equals(typeName))
            return Boolean.valueOf(value);
        else if ("java.lang.Double".equals(typeName) || "double".equals(typeName))
            return Double.valueOf(value);
        else if ("java.lang.Float".equals(typeName) || "float".equals(typeName))
            return Float.valueOf(value);
        else if ("java.lang.Long".equals(typeName) || "long".equals(typeName))
            return Long.valueOf(value);
        else if ("java.lang.Short".equals(typeName) || "short".equals(typeName))
            return Short.valueOf(value);
        else
            return null;
    }
}
