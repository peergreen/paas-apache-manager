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

package org.ow2.jonas.jpaas.apache.manager.vhost.manager.rest;

import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.VhostManagerException;
import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.VhostManagerService;
import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.rest.IVhostManager;
import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.xml.Vhost;
import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.xml.VhostList;
import org.ow2.jonas.jpaas.apache.manager.util.api.xml.Error;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;

/**
 * VhostManager REST implementation
 * @author David Richard
 */
public class VhostManagerRest implements IVhostManager {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(VhostManagerRest.class);

    /**
     * VhostManager Service
     */
    private VhostManagerService vhostManagerService;

    /**
     * End of Line
     */
    public static final String EOL = "\n";

    public VhostManagerRest(VhostManagerService vhostManagerService) {
        this.vhostManagerService = vhostManagerService;
    }


    /**
     * Get the Virtual Host list
     */
    public Response getVhostList() {
        VhostList list;
        try {
            list = vhostManagerService.getVhostList();
        } catch (VhostManagerException e) {
            logger.error("Cannot get the Virtual Host list", e);
            Error error = new Error();
            error.setMessage("Cannot get the Virtual Host list." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response
                .status(Response.Status.OK)
                .entity(list)
                .type(MediaType.APPLICATION_XML_TYPE)
                .build();
    }

    /**
     * Get a Virtual Host content
     */
    public Response getVhost(Long vhostID) {
        String vhost;
        try {
            vhost = vhostManagerService.getVhostContent(vhostID);
        } catch (VhostManagerException e) {
            logger.error("Cannot get the Virtual Host " + vhostID, e);
            Error error = new Error();
            error.setMessage("Cannot get the Virtual Host " + vhostID + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response
                .status(Response.Status.OK)
                .entity(vhost)
                .build();
    }


    /**
     * Create a Virtual Host block directive
     *
     * @param address    address of the virtual host
     * @param servername value of the ServerName directive
     */
    public Response createVirtualHost(String address, String servername) {
        Vhost vhost;
        if (servername.equals("")) {
            servername = null;
        }
        try {
            long vhostID = vhostManagerService.createVirtualHost(address, servername);
            vhost = new Vhost(vhostID, address, servername);
        } catch (VhostManagerException e) {
            logger.error("Cannot create Virtual Host (Address=" + address + ", ServerName= " + servername +")", e);
            Error error = new Error();
            error.setMessage("Cannot create Virtual Host (Address=" + address + ", ServerName= " + servername +")."
                    + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(vhost)
                .type(MediaType.APPLICATION_XML_TYPE)
                .build();
    }

    /**
     * Delete a Virtual Host block directive
     *
     * @param vhostID ID of the virtual host
     */
    public Response deleteVirtualHost(Long vhostID) {
        try {
            vhostManagerService.deleteVirtualHost(vhostID);
        } catch (VhostManagerException e) {
            logger.error("Cannot delete Virtual Host " + vhostID, e);
            Error error = new Error();
            error.setMessage("Cannot delete Virtual Host " + vhostID + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Create a DocumentRoot directive in a Virtual Host
     *
     * @param vhostID      ID of the virtual host
     * @param documentRoot value of the DocumentRoot directive to create
     */
    public Response createDocumentRoot(Long vhostID, String documentRoot) {
        try {
            vhostManagerService.createDocumentRoot(vhostID, documentRoot);
        } catch (VhostManagerException e) {
            logger.error("Cannot create DocumentRoot in Virtual Host " + vhostID, e);
            Error error = new Error();
            error.setMessage("Cannot create DocumentRoot in Virtual Host " + vhostID + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * Delete a DocumentRoot directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     */
    public Response deleteDocumentRoot(Long vhostID) {
        try {
            vhostManagerService.deleteDocumentRoot(vhostID);
        } catch (VhostManagerException e) {
            logger.error("Cannot delete DocumentRoot in Virtual Host " + vhostID, e);
            Error error = new Error();
            error.setMessage("Cannot delete DocumentRoot in Virtual Host " + vhostID + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Create a ServerAlias directive in a Virtual Host
     *
     * @param vhostID     ID of the virtual host
     * @param serverAlias argument(s) of the ServerAlias directive to create
     */
    public Response createServerAlias(Long vhostID, String serverAlias) {
        try {
            String[] argumentList = serverAlias.split("_");
            List<String> aliasList = new LinkedList<String>();
            for (String arg : argumentList) {
                aliasList.add(arg);
            }
            vhostManagerService.createServerAlias(vhostID, aliasList);
        } catch (VhostManagerException e) {
            logger.error("Cannot create ServerAlias in Virtual Host " + vhostID, e);
            Error error = new Error();
            error.setMessage("Cannot create ServerAlias in Virtual Host " + vhostID + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * Delete a ServerAlias directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     */
    public Response deleteServerAlias(Long vhostID) {
        try {
            vhostManagerService.deleteServerAlias(vhostID);
        } catch (VhostManagerException e) {
            logger.error("Cannot delete ServerAlias in Virtual Host " + vhostID, e);
            Error error = new Error();
            error.setMessage("Cannot delete ServerAlias in Virtual Host " + vhostID + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Create a ServerPath directive in a Virtual Host
     *
     * @param vhostID    ID of the virtual host
     * @param serverPath value of the ServerPath directive to create
     */
    public Response createServerPath(Long vhostID, String serverPath) {
        try {
            vhostManagerService.createServerPath(vhostID, serverPath);
        } catch (VhostManagerException e) {
            logger.error("Cannot create ServerPath in Virtual Host " + vhostID, e);
            Error error = new Error();
            error.setMessage("Cannot create ServerPath in Virtual Host " + vhostID + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * Delete a ServerPath directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     */
    public Response deleteServerPath(Long vhostID) {
        try {
            vhostManagerService.deleteServerPath(vhostID);
        } catch (VhostManagerException e) {
            logger.error("Cannot delete ServerPath in Virtual Host " + vhostID, e);
            Error error = new Error();
            error.setMessage("Cannot delete ServerPath in Virtual Host " + vhostID + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
