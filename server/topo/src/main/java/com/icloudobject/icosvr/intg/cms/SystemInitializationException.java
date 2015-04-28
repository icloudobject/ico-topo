/**
 * www.iCloudObject.com
 * 
 */
package com.icloudobject.icosvr.intg.cms;

/**
 * @author liasu
 *
 */
public class SystemInitializationException extends RuntimeException {

    private static final long serialVersionUID = -2588966155528011558L;

    public SystemInitializationException() {
    }
    
    public SystemInitializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
