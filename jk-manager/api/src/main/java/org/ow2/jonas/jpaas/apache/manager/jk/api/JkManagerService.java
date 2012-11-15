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
package org.ow2.jonas.jpaas.apache.manager.jk.api;

import java.util.List;

/**
 * The interface describing the JkManagerService available on a host to change the configuration of the mod_jk plugin.
 *
 * @author Julien Vey
 */
public interface JkManagerService {

    /**
     * @param name
     *            the name of the worker
     * @param host
     *            the host of the worker
     * @param port
     *            the port of the worker
     */
    void addNamedWorker(String name, String host, String port) throws JkManagerException;

    /**
     * @param name
     *            the name of the worker
     * @param loadbalancer
     *            the name of the loadbalancer to connect
     * @param host
     *            the host of the worker
     * @param port
     *            the port of the worker
     */
    void addNamedWorker(String name, String loadbalancer, String host, String port);

    /**
     * @param name
     *            the name of the worker
     * @param loadbalancer
     *            the name of the loadbalancer to connect
     * @param host
     *            the host to add
     * @param port
     *            the port of the host
     * @param lbFactor
     *            the load balancing factor
     */
    void addNamedWorker(String name, String loadbalancer, String host, String port, String lbFactor);

    /**
     * @param host
     *            the host to add
     * @param port
     *            the port of the host
     */
    void addWorker(String host, String port) throws JkManagerException;

    /**
     * @param loadbalancer
     *            the name of the loadbalancer to connect
     * @param host
     *            the host to add
     * @param port
     *            the port of the host
     */
    void addWorker(String loadbalancer, String host, String port);

    /**
     * @param loadbalancer
     *            the name of the loadbalancer to connect
     * @param host
     *            the host to add
     * @param port
     *            the port of the host
     * @param lbFactor
     *            the load balancing factor
     */
    void addWorker(String loadbalancer, String host, String port, String lbFactor);

    /**
     * Remove a named worker
     * @param name
     *            the name of the worker
     */
    void removeNamedWorker(String name);

    /**
     * Disable a named worker
     * @param name
     *            the name of the worker
     */
    void disableNamedWorker(String name);

    /**
     * Enable a named worker
     * @param name
     *            the name of the worker
     */
    void enableNamedWorker(String name);

    /**
     * Stop a named worker
     * @param name
     *            the name of the worker
     */
    void stopNamedWorker(String name);

    /**
     * init configuration
     */
    void init(String workersConfigurationFile, String reloadCmd);


    /**
     * Mount a a path for all workers in load balancing
     * @param loadbalancer the loadbalancer to send the requests
     * @param path the path to mount it
     */
    void mount(String loadbalancer, String path);

    /**
     * Delete all mount points for a load balancer.
     * @param loadbalancer the loadbalancer to unmount
     */
    void unmount(String loadbalancer);

    /**
     * Delete a mount point for a worker.
     * @param loadbalancer the loadbalancer to unmount
     * @param path the path to unmount
     */
    void unmount(String loadbalancer, String path);

    /**
     * Unmount all mount points
     */
    void unmount();

    /**
     * @return true if configured
     * @param name
     *            the name of the worker
     */
    boolean isConfigured(String name);

    /**
     * @return true if enabled
     * @param name
     *            the name of the worker
     */
    boolean isEnabled(String name);

    String getId();

    /**
     * Create a Load Balancer
     * @param name Name of the Load Balancer
     * @param workerList The balance workers
     */
    void addLoadBalancer(String name, List<String> workerList) throws JkManagerException;

    /**
     * Update the workers list of a Load Balancer
     * @param name Name of the Load Balancer
     * @param workerList The balance workers
     */
    void updateLoadBalancer(String name, List<String> workerList) throws JkManagerException;

    /**
     * Remove a Load Balancer
     * @param name Name of the Load Balancer
     */
    void removeLoadBalancer(String name) throws JkManagerException;

    /**
     * Mount a path in a Vhost for all workers in load balancing
     * @param vhostID ID of the virtual host
     * @param loadbalancer the loadbalancer to send the requests
     * @param path the path to mount it
     */
    long mountInVhost(long vhostID, String loadbalancer, String path) throws JkManagerException;

    /**
     * Delete all mount points for a load balancer.
     * @param vhostID ID of the virtual host
     * @param loadbalancer the loadbalancer to unmount
     */
    void unmountInVhost(long vhostID, String loadbalancer) throws JkManagerException;

    /**
     * Delete a mount point for a worker.
     * @param vhostID ID of the virtual host
     * @param loadbalancer the loadbalancer to unmount
     * @param path the path to unmount
     */
    void unmountInVhost(long vhostID, String loadbalancer, String path) throws JkManagerException;

}
