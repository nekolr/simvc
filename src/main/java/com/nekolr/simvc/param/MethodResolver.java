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
        String formatName = className.replaceAll("\\.", "\\/")+".class";
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
                        if (paramName.equals(entry.getKey()))
                            invokeParams[i] = entry.getValue();
                        //TODO:缺失参数处理
                    }
                }
            }
        }
        return invokeParams;
    }
}
