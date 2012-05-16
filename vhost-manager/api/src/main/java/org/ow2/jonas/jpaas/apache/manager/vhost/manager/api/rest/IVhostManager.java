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

package org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Proxy Manager REST API
 * @author David Richard
 */
@Path("/")
public interface IVhostManager {

    /**
     * Get the Virtual Host list
     *
     */
    @GET
    @Path("/vhost/list")
    @Produces(MediaType.APPLICATION_XML)
    public Response getVhostList();

    /**
     * Get a Virtual Host content
     *
     */
    @GET
    @Path("/vhost/{id}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getVhost(@PathParam("id") Long vhostID);

    /**
     * Create a Virtual Host block directive
     *
     * @param address address of the virtual host
     * @param servername value of the ServerName directive
     */
    @POST
    @Path("/vhost")
    @Produces(MediaType.APPLICATION_XML)
    public Response createVirtualHost(@QueryParam("address") String address, @DefaultValue("") @QueryParam("servername") String servername);

    /**
     * Delete a Virtual Host block directive
     *
     * @param vhostID ID of the virtual host
     */
    @DELETE
    @Path("/vhost/{id}")
    @Produces(MediaType.APPLICATION_XML)
    public Response deleteVirtualHost(@PathParam("id") Long vhostID);

    /**
     * Create a DocumentRoot directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param documentRoot value of the DocumentRoot directive to create
     */
    @POST
    @Path("/vhost/{id}/documentroot")
    @Produces(MediaType.APPLICATION_XML)
    public Response createDocumentRoot(@PathParam("id") Long vhostID, @QueryParam("value") String documentRoot);

    /**
     * Delete a DocumentRoot directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     */
    @DELETE
    @Path("/vhost/{id}/documentroot")
    @Produces(MediaType.APPLICATION_XML)
    public Response deleteDocumentRoot(@PathParam("id") Long vhostID);

    /**
     * Create a ServerAlias directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param serverAlias argument(s) of the ServerAlias directive to create
     */
    @POST
    @Path("/vhost/{id}/serveralias")
    @Produces(MediaType.APPLICATION_XML)
    public Response createServerAlias(@PathParam("id") Long vhostID, @QueryParam("value") String serverAlias);

    /**
     * Delete a ServerAlias directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     */
    @DELETE
    @Path("/vhost/{id}/serveralias")
    @Produces(MediaType.APPLICATION_XML)
    public Response deleteServerAlias(@PathParam("id") Long vhostID);

    /**
     * Create a ServerPath directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param serverPath value of the ServerPath directive to create
     */
    @POST
    @Path("/vhost/{id}/serverpath")
    @Produces(MediaType.APPLICATION_XML)
    public Response createServerPath(@PathParam("id") Long vhostID, @QueryParam("value") String serverPath);

    /**
     * Delete a ServerPath directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     */
    @DELETE
    @Path("/vhost/{id}/serverpath")
    @Produces(MediaType.APPLICATION_XML)
    public Response deleteServerPath(@PathParam("id") Long vhostID);

}
