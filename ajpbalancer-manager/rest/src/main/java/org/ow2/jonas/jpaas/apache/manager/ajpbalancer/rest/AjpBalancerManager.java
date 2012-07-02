/**
 * JASMINe
 * Copyright (C) 2011 Bull S.A.S.
 * Contact: jasmine@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id: AjpBalancerManager.java 9171 2011-10-13 14:30:56Z richardd $
 * --------------------------------------------------------------------------
 */

package org.ow2.jonas.jpaas.apache.manager.jk.rest;

import org.ow2.jonas.jpaas.apache.manager.ajpbalancer.api.AjpBalancerManagerService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * The implementation of the IAjpBalancerManager Interface
 *
 * @author David Richard
 */
public class AjpBalancerManager implements IAjpBalancerManager {

    private AjpBalancerManagerService ajpBalancerManagerService;

    /**
     * The logger.
     */
    private Log logger = LogFactory.getLog(this.getClass());

    public AjpBalancerManager(AjpBalancerManagerService ajpBalancerManagerService) {
        this.ajpBalancerManagerService=ajpBalancerManagerService;
    }


    public void addBalancerMember(String proxyBalancer, String host, String port, String lbFactor) {
        logger.debug("Inbound call to addBalancerMember()");
        logger.debug("(proxyBalancer, host, port, lbFactor) = (" + proxyBalancer + ", "
                + host + ", " + port + ", " + lbFactor + ")");
        ajpBalancerManagerService.addBalancerMember(proxyBalancer,host,port,lbFactor);
    }

    public void removeBalancerMember(String proxyBalancer, String host, String port) {
        logger.debug("Inbound call to removeBalancerMember()");
        logger.debug("(proxyBalancer, host, port) = (" + proxyBalancer + "," + host + "," + port);
        ajpBalancerManagerService.removeBalancerMember(proxyBalancer,host,port);
    }

    public void removeProxyBalancer(String proxyBalancer) {
        logger.debug("Inbound call to removeProxyBalancer()");
        logger.debug("(proxyBalancer) = (" + proxyBalancer + ")");
        ajpBalancerManagerService.removeProxyBalancer(proxyBalancer);
    }

    public void init(String proxyBalancerConfigurationFile) {
        logger.debug("Inbound call to init()");
        logger.debug("(proxyBalancerConfigurationFile) = ("
                + proxyBalancerConfigurationFile + ")");
        ajpBalancerManagerService.init(proxyBalancerConfigurationFile);
    }

    public void mount(String proxyBalancer, String path) {
        logger.debug("Inbound call to mount()");
        logger.debug("(proxyBalancer, path) = (" + proxyBalancer + "," + path + ")");
        ajpBalancerManagerService.mount(proxyBalancer,path);
    }

    public void unmount(String proxyBalancer) {
        logger.debug("Inbound call to unmount()");
        logger.debug("(proxyBalancer) = (" + proxyBalancer + ")");
        ajpBalancerManagerService.unmount(proxyBalancer);
    }

    public void unmount() {
        logger.debug("Inbound call to unmount()");
        ajpBalancerManagerService.unmount();
    }
}
