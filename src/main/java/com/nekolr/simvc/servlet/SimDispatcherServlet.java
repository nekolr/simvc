package com.nekolr.simvc.servlet;

import com.nekolr.simvc.meta.MetaDataBean;
import com.nekolr.simvc.param.HandlerMapping;
import com.nekolr.simvc.utils.ScanUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author nekolr
 * @version 1.0
 * @description front controller
 */
public class SimDispatcherServlet extends HttpServlet {

    public static final String BASE_PACKAGE = "base-package";

    public static Map<String, MetaDataBean> metaDataBeanMap;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String servletPath = request.getServletPath();
        for (Map.Entry<String, MetaDataBean> entry : metaDataBeanMap.entrySet()) {
            if (entry.getKey().equals(servletPath)) {
                HandlerMapping.handlerMapping(request, response, entry);
                return;
            }
        }

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        String basePackage = config.getInitParameter(BASE_PACKAGE);
        metaDataBeanMap = ScanUtil.getMetaMap(basePackage, true);
    }
}
