/**
 * JASMINe
 * Copyright (C) 2010 Bull S.A.S.
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
 * $Id: JkManagerService.java 9171 2011-10-13 14:30:56Z richardd $
 * --------------------------------------------------------------------------
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

}
