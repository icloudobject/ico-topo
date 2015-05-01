package com.icloudobject.topo.listener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * A simple init check for system.
 * 
 * @author Liangfei
 *
 */
public class InitializationIntercepter implements HandlerInterceptor {

//    private static AtomicBoolean initialized = new AtomicBoolean();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (!initialized.get()) {
//            synchronized (loader) {
//                loader.sysInit();
//                initialized.set(true);
//            }
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

}
