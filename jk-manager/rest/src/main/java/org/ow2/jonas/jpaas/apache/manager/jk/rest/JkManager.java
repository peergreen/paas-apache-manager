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

package org.ow2.jonas.jpaas.apache.manager.jk.rest;

import org.ow2.jonas.jpaas.apache.manager.jk.api.JkManagerException;
import org.ow2.jonas.jpaas.apache.manager.jk.api.JkManagerService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import org.ow2.jonas.jpaas.apache.manager.util.api.xml.Error;

import javax.ws.rs.FormParam;
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
            logger.error("Cannot create the Worker named " + name + ".", e);
            Error error = new Error();
            error.setMessage("Cannot create the Worker named " + name + "." + EOL + e);
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
            logger.error("Cannot create the Worker.", e);
            Error error = new Error();
            error.setMessage("Cannot create the Worker." + EOL + e);
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
            logger.error("Cannot create the Load Balancer.", e);
            Error error = new Error();
            error.setMessage("Cannot create the Load Balancer named " + name + "." + EOL + e);
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
            logger.error("Cannot update the Load Balancer.", e);
            Error error = new Error();
            error.setMessage("Cannot update the Load Balancer named " + name + "." + EOL + e);
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
            logger.error("Cannot remove the Load Balancer.", e);
            Error error = new Error();
            error.setMessage("Cannot remove the Load Balancer named " + name + "." + EOL + e);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }

    public Response mountInVhost(Long vhostID, String loadbalancer, String path) {
        logger.debug("Inbound call to mount()");
        logger.debug("(vhostID, loadbalancer, path) = ("
                + vhostID + ", " + loadbalancer + ", " + path +  ")");
        try {
            jkManagerService.mountInVhost(vhostID, loadbalancer, path);
        } catch (JkManagerException e) {
            logger.error("Cannot mount the Load Balancer.", e);
            Error error = new Error();
            error.setMessage("Cannot mount the Load Balancer named " + loadbalancer + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.CREATED)
                .build();
    }

    public Response unmountInVhost(Long vhostID, String loadbalancer, String path) {
        logger.debug("Inbound call to unmountInVhost()");
        logger.debug("(vhostID, loadbalancer, path) = ("
                + vhostID + ", " + loadbalancer + ", " + path +  ")");
        try {
            if (path==null) {
                jkManagerService.unmountInVhost(vhostID, loadbalancer);
            } else {
                jkManagerService.unmountInVhost(vhostID, loadbalancer, path);
            }
        } catch (JkManagerException e) {
            Error error = new Error();
            if (path==null) {
                logger.error("Cannot unmount the Load Balancer named " + loadbalancer + " in path " + path +
                        "." + EOL + e);
                error.setMessage("Cannot unmount the Load Balancer named " + loadbalancer + " in path " + path +
                        "." + EOL + e);
            } else {
                logger.error("Cannot unmount the Load Balancer named " + loadbalancer + "." + EOL + e);
                error.setMessage("Cannot unmount the Load Balancer named " + loadbalancer + "." + EOL + e);
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }
}
