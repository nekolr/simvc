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

/**
 *
 */
public class HandlerMapping {

    public static void handlerMapping(HttpServletRequest request,
                                      HttpServletResponse response,
                                      MetaDataBean metaDataBean) throws ServletException {
        try {
            List<String> paramList = MethodResolver.getMethodParamNames(metaDataBean.getClazz().getName(), metaDataBean.getMethod().getName());
            Class<?>[] paramTypes = metaDataBean.getMethod().getParameterTypes();
            Object[] invokeParams = MethodResolver.getInvokeParams(paramList, paramTypes, request.getParameterMap(), request, response);
            Object result = metaDataBean.getMethod().invoke(metaDataBean.getInstance(), invokeParams);
            if (metaDataBean.isAjax()) {
                PrintWriter out = response.getWriter();
                out.write(JSON.toJSONString(result));
                out.flush();
                return;
            } else if (metaDataBean.getMethod().getReturnType() == String.class) {
                String path = (String) result;
                request.getRequestDispatcher(path).forward(request, response);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
