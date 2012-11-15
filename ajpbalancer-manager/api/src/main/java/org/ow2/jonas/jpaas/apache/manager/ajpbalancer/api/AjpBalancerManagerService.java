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

package org.ow2.jonas.jpaas.apache.manager.ajpbalancer.api;

/**
 * The interface describing the AjpBalancerManagerService available on a host to change the configuration of
 * the mod_proxy_balancer plugin.
 *
 * @author David Richard
 */
public interface AjpBalancerManagerService {

    /**
     * @param proxyBalancer
     *            the name of the proxyBalancer to connect
     * @param host
     *            the host to add
     * @param port
     *            the port of the host
     * @param lbFactor
     *            the load balancing factor
     */
    void addBalancerMember(String proxyBalancer, String host, String port, String lbFactor);

    /**
     * @param proxyBalancer
     *            the name of the proxyBalancer
     * @param host
     *            the host to add
     * @param port
     *            the port of the host
     */
    void removeBalancerMember(String proxyBalancer, String host, String port);
    
     /**
     * @param proxyBalancer
     *            the name of the proxyBalancer
     */
    void removeProxyBalancer(String proxyBalancer);

    /**
     * init configuration
     * @param proxyBalancerConfigurationFile
     *            Path to the proxy_balancer.conf file
     */
    void init(String proxyBalancerConfigurationFile);


    /**
     * Mount a proxyBalancer
     * @param proxyBalancer
     *            the proxyBalancer to mount
     * @param path
     *            the path to mount it
     */
    void mount(String proxyBalancer, String path);

    /**
     * Unmount a proxyBalancer
     * @param proxyBalancer the proxyBalancer to unmount
     */
    void unmount(String proxyBalancer);

    /**
     * Unmount all proxyBalancers
     */
    void unmount();
}
