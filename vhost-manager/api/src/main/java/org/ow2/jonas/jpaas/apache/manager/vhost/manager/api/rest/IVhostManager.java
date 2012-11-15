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
