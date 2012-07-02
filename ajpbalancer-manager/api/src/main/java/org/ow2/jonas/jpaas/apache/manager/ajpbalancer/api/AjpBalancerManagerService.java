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
 * $Id: AjpBalancerManagerService.java 9171 2011-10-13 14:30:56Z richardd $
 * --------------------------------------------------------------------------
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
