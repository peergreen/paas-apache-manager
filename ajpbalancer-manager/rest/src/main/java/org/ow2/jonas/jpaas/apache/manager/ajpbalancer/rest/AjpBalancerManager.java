/**
 * JPaaS
 * Copyright 2012 Bull S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id:$
 */ 

package org.ow2.jonas.jpaas.apache.manager.ajpbalancer.rest;

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
