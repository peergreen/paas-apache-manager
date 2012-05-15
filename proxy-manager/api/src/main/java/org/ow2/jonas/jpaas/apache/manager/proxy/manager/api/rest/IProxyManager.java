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

package org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Proxy Manager REST API
 * @author David Richard
 */
@Path("/")
public interface IProxyManager {

    /**
     * Create a ProxyPass directive
     * @param path value of the ProxyPass first argument (path)
     * @param url  value of the ProxyPass second argument (url)
     */
    @POST
    @Path("/proxypass")
    public Response createProxyPass(@QueryParam("path") String path, @QueryParam("url") String url);


    /**
     * Create a ProxyPass directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param path value of the ProxyPass first argument (path)
     * @param url  value of the ProxyPass second argument (url)
     */
    @POST
    @Path("/vhost/{id}/proxypass")
    public Response createVhostProxyPass(@PathParam("id") Long vhostID, @QueryParam("path") String path,
                                         @QueryParam("url") String url);

    /**
     * Delete a ProxyPass directive
     *
     * @param path value of the ProxyPass first argument (path)
     * @param url  value of the ProxyPass second argument (url)
     */
    @DELETE
    @Path("/proxypass")
    public Response deleteProxyPass(@QueryParam("path") String path, @QueryParam("url") String url);

    /**
     * Delete a ProxyPass directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param path value of the ProxyPass first argument (path)
     * @param url  value of the ProxyPass second argument (url)
     */
    @DELETE
    @Path("/vhost/{id}/proxypass")
    public Response deleteVhostProxyPass(@PathParam("id") Long vhostID, @QueryParam("path") String path,
                                         @QueryParam("url") String url);

}
