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

package org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.rest;

import javax.ws.rs.DELETE;
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
public interface IProxyManager {

    /**
     * Create a ProxyPass directive
     * @param path value of the ProxyPass first argument (path)
     * @param url  value of the ProxyPass second argument (url)
     */
    @POST
    @Path("/proxypass")
    @Produces(MediaType.APPLICATION_XML)
    public Response createProxyPass(@QueryParam("path") String path, @QueryParam("url") String url);


    /**
     * Create a ProxyPass directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param path value of the ProxyPass first argument (path)
     * @param url  value of the ProxyPass second argument (url)
     */
    @POST
    @Path("/vhost/{vhostID}/proxypass")
    @Produces(MediaType.APPLICATION_XML)
    public Response createVhostProxyPass(@PathParam("vhostID") Long vhostID, @QueryParam("path") String path,
                                         @QueryParam("url") String url);

    /**
     * Delete a ProxyPass directive
     *
     * @param directiveID ID of the ProxyPass directive
     */
    @DELETE
    @Path("/proxypass/{directiveID}")
    @Produces(MediaType.APPLICATION_XML)
    public Response deleteProxyPass(@PathParam("directiveID") Long directiveID);

    /**
     * Delete a ProxyPass directive in a Virtual Host
     *
     * @param directiveID ID of the ProxyPass directive
     */
    @DELETE
    @Path("/vhost/{vhostID}/proxypass/{directiveID}")
    @Produces(MediaType.APPLICATION_XML)
    public Response deleteVhostProxyPass(@PathParam("vhostID") Long vhostID,
                                         @PathParam("directiveID") Long directiveID);

}
