/**
 * 
 */
package com.icloudobject.topo.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ebay.cloud.cms.sysmgmt.server.CMSServer;
import com.icloudobject.topo.util.CMSDelegate;

/**
 * @author Liangfei
 *
 */
public class TopoListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String cmsHome = System.getenv("CMS_HOME");
        if (cmsHome != null) {
            System.setProperty("CMS_HOME", cmsHome);
        } else {
            System.setProperty("CMS_HOME", ".");
        }
        CMSServer.getCMSServer().start();
        
        CMSDelegate.getInstance();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        CMSServer.getCMSServer().shutdown();
    }
}
