package com.nekolr.simvc.param;

import com.alibaba.fastjson.JSON;
import com.nekolr.simvc.meta.MetaDataBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class HandlerMapping {

    public static void handlerMapping(HttpServletRequest request, HttpServletResponse response, Map.Entry<String, MetaDataBean> entry) throws ServletException {
        MetaDataBean metaDataBean = entry.getValue();
        try {
            List<String> paramList = MethodResolver.getMethodParamNames(metaDataBean.getClazz().getName(), metaDataBean.getMethod().getName());
            Class<?>[] paramTypes = metaDataBean.getMethod().getParameterTypes();
            Object[] invokeParams = MethodResolver.getInvokeParams(paramList, paramTypes, request.getParameterMap(), request, response);
            try {
                Object obj = metaDataBean.getClazz().newInstance();
                try {
                    Object result = metaDataBean.getMethod().invoke(obj, invokeParams);
                    if (metaDataBean.isAjax()) {
                        PrintWriter out = response.getWriter();
                        out.write(JSON.toJSONString(result));
                        out.flush();
                        return;
                    } else if (metaDataBean.getMethod().getReturnType() == String.class) {
                        String path = (String) result;
                        request.getRequestDispatcher("/WEB-INF/" + path+".html").forward(request, response);
                        return;
                    }
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
