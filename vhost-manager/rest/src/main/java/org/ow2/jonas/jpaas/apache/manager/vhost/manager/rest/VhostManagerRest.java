/**
 * JPaaS
 * Copyright (C) 2012 Bull S.A.S.
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
 * $Id$
 * --------------------------------------------------------------------------
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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;

/**
 * ProxyManager REST implementation
 * @author David Richard
 */
public class VhostManagerRest implements IVhostManager {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(VhostManagerRest.class);

    /**
     * ProxyManager Service
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
     * Create a Virtual Host block directive
     *
     * @param address address of the virtual host
     */
    public Response createVirtualHost(String address) {
        Vhost vhost;
        try {
            long vhostID = vhostManagerService.createVirtualHost(address);
            vhost = new Vhost(vhostID, address, null);
        } catch (VhostManagerException e) {
            logger.error("Cannot create Virtual Host " + address, e);
            Error error = new Error();
            error.setMessage("Cannot create Virtual Host " + address + "." + EOL + e);
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
     * Create a Name-based Virtual Host block directive
     *
     * @param address    address of the virtual host
     * @param servername value of the ServerName directive
     */
    public Response createVirtualHost(String address, String servername) {
        Vhost vhost;
        try {
            long vhostID = vhostManagerService.createVirtualHost(address, servername);
            vhost = new Vhost(vhostID, address, servername);
        } catch (VhostManagerException e) {
            logger.error("Cannot create Virtual Host " + address + ", " + servername, e);
            Error error = new Error();
            error.setMessage("Cannot create Virtual Host " + address + ", " + servername + "." + EOL + e);
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
