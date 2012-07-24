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
 * $Id: JkManager.java 9002 2011-09-27 13:45:41Z gonzalem $
 * --------------------------------------------------------------------------
 */

package org.ow2.jonas.jpaas.apache.manager.jk.rest;

import org.ow2.jonas.jpaas.apache.manager.jk.api.JkManagerException;
import org.ow2.jonas.jpaas.apache.manager.jk.api.JkManagerService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import org.ow2.jonas.jpaas.apache.manager.util.api.xml.Error;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class JkManager implements IJkManager{

    private JkManagerService jkManagerService;

    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * End of Line
     */
    public static final String EOL = "\n";

    public JkManager(JkManagerService jkManagerService) {
        this.jkManagerService = jkManagerService;
    }

    public void addNamedWorker(String name, String loadbalancer,
                               String host, String port,
                               String lbFactor) {
        logger.debug("Inbound call to addNamedWorker()");
        logger.debug("(name, host, port, type, lbFactor) = (" + name + ", "
                + loadbalancer + ", "
                + host + ", " + port + ", " + lbFactor + ")");
        boolean isSetLbFactor = (lbFactor != null);
        if(isSetLbFactor) {
            jkManagerService.addNamedWorker(name, loadbalancer, host, port, lbFactor);
        } else {
            jkManagerService.addNamedWorker(name, loadbalancer, host, port);
        }
    }

    public void addWorker(String loadbalancer, String host, String port,
                          String lbFactor) {
        logger.debug("Inbound call to addNamedWorker()");
        logger.debug("(host, port, type, lbFactor) = (" + loadbalancer + ", "
                + host + ", " + port
                + ", " + lbFactor + ")");
        boolean isSetLbFactor = (lbFactor != null);
        if(isSetLbFactor) {
            jkManagerService.addWorker(loadbalancer, host, port, lbFactor);
        } else {
            jkManagerService.addWorker(loadbalancer, host, port);
        }
    }

    public void removeNamedWorker(String name) {
        logger.debug("Inbound call to removeNamedWorker()");
        logger.debug("name = '" + name + "'");
        jkManagerService.removeNamedWorker(name);
    }

    public void disableNamedWorker(String name) {
        logger.debug("Inbound call to disableNamedWorker()");
        logger.debug("name = '" + name + "'");
        jkManagerService.disableNamedWorker(name);
    }

    public void enableNamedWorker(String name) {
        logger.debug("Inbound call to enableNamedWorker()");
        logger.debug("name = '" + name + "'");
        jkManagerService.enableNamedWorker(name);
    }

    public void stopNamedWorker(String name) {
        logger.debug("Inbound call to stopNamedWorker()");
        logger.debug("name = '" + name + "'");
        jkManagerService.stopNamedWorker(name);
    }

    public void mount(String loadbalancer, String path) {
        logger.debug("mount(" + loadbalancer + ", " + path + ")");
        if(path == null) {
            throw new IllegalArgumentException("Mount path cannot be null.");
        }
        jkManagerService.mount(loadbalancer, path);
    }

    public void unmount(String loadbalancer, String path) {
        logger.debug("unmount(" + loadbalancer + ")");
        if(path == null) {
            jkManagerService.unmount(loadbalancer);
        } else {
            jkManagerService.unmount(loadbalancer, path);
        }
    }

    public void unmount() {
        logger.debug("unmount()");
        jkManagerService.unmount();
    }

    public void init(String workersConfigurationFile, String reloadCmd) {
        logger.debug("Inbound call to init()");
        logger.debug("(workersConfigurationFile, reloadCmd) = ("
                + workersConfigurationFile + ", " + reloadCmd + ")");
        jkManagerService.init(workersConfigurationFile, reloadCmd);
    }

    public String isConfigured(String name) {
        logger.warn("Inbound call to stopNamedWorker()");
        logger.warn("name = '" + name + "'");
        return String.valueOf(jkManagerService.isConfigured(name));
    }

    public String isEnabled(String name) {
        logger.debug("Inbound call to stopNamedWorker()");
        logger.debug("name = '" + name + "'");
        return String.valueOf(jkManagerService.isEnabled(name));
    }

    public String getId() {
        logger.debug("Inbound call to getId()");
        return jkManagerService.getId();
    }

    public Response addNamedWorker(String name, String host, String port) {
        logger.debug("Inbound call to addNamedWorker()");
        logger.debug("(name, host, port) = ("
                + name + ", " + host + "," + port + ")");
        try {
            jkManagerService.addNamedWorker(name, host, port);
        } catch (JkManagerException e) {
            logger.error("Cannot create the Worker named " + name + ".", e.getCause());
            Error error = new Error();
            error.setMessage("Cannot create the Worker named " + name + "." + EOL + e.getCause());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.CREATED)
                .build();
    }

    public Response addWorker(String host, String port) {
        logger.debug("Inbound call to addWorker()");
        logger.debug("(host, port) = ("
                + host + "," + port + ")");
        try {
            jkManagerService.addWorker(host, port);
        } catch (JkManagerException e) {
            logger.error("Cannot create the Worker.", e.getCause());
            Error error = new Error();
            error.setMessage("Cannot create the Worker." + EOL + e.getCause());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.CREATED)
                .build();
    }

    public Response addLoadBalancer(String name, String workerList) {
        logger.debug("Inbound call to addLoadBalancer()");
        logger.debug("(name, workerList) = ("
                + name + "," + workerList + ")");
        try {
            String[] splitWorkers = workerList.split(",");
            List<String> workers = new LinkedList<String>();
            Collections.addAll(workers, splitWorkers);
            jkManagerService.addLoadBalancer(name, workers);
        } catch (JkManagerException e) {
            logger.error("Cannot create the Load Balancer.", e.getCause());
            Error error = new Error();
            error.setMessage("Cannot create the Balancer named " + name + "." + EOL + e.getCause());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.CREATED)
                .build();
    }

    public Response updateLoadBalancer(String name, String workerList) {
        logger.debug("Inbound call to updateLoadBalancer()");
        logger.debug("(name, workerList) = ("
                + name + "," + workerList + ")");
        try {
            String[] splitWorkers = workerList.split(",");
            List<String> workers = new LinkedList<String>();
            Collections.addAll(workers, splitWorkers);
            jkManagerService.updateLoadBalancer(name, workers);
        } catch (JkManagerException e) {
            logger.error("Cannot update the Load Balancer.", e.getCause());
            Error error = new Error();
            error.setMessage("Cannot update the Balancer named " + name + "." + EOL + e.getCause());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.OK)
                .build();
    }

    public Response removeLoadBalancer(String name) {
        logger.debug("Inbound call to removeLoadBalancer()");
        logger.debug("(name) = ("
                + name + ")");
        try {
            jkManagerService.removeLoadBalancer(name);
        } catch (JkManagerException e) {
            logger.error("Cannot remove the Load Balancer.", e.getCause());
            Error error = new Error();
            error.setMessage("Cannot remove the Balancer named " + name + "." + EOL + e.getCause());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.OK)
                .build();
    }
}
