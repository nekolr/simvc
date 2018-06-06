package com.nekolr.simvc.servlet;

import com.nekolr.simvc.meta.MetaDataBean;
import com.nekolr.simvc.param.HandlerMapping;
import com.nekolr.simvc.utils.ReflectionUtil;
import com.nekolr.simvc.utils.ScanUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author nekolr
 * @version 1.0
 * @description front controller
 */
public class SimDispatcherServlet extends HttpServlet {

    /**
     * 默认扫描包位置
     */
    private static final String BASE_PACKAGE = "base-package";

    /**
     * bean定义集合
     */
    private Map<String, MetaDataBean> metaDataBeanMap;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String servletPath = request.getServletPath();
        for (Map.Entry<String, MetaDataBean> entry : metaDataBeanMap.entrySet()) {
            if (entry.getKey().equals(servletPath)) {
                HandlerMapping.handlerMapping(request, response, entry.getValue());
                return;
            }
        }
    }

    @Override
    public void init(ServletConfig config) {
        String basePackage = config.getInitParameter(BASE_PACKAGE);
        metaDataBeanMap = ScanUtil.getMetaMap(basePackage, true);
        this.fillInstanceMap();
    }

    private void fillInstanceMap() {
        for (Map.Entry<String, MetaDataBean> entry : metaDataBeanMap.entrySet()) {
            MetaDataBean metaDataBean = entry.getValue();
            Object instance = ReflectionUtil.instanceBean(metaDataBean);
            metaDataBean.setInstance(instance);
        }
    }
}
