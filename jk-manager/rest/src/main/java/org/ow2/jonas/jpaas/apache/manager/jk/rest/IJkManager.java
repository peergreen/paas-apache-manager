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
 * $Id: IJkManager.java 9023 2011-09-28 12:53:39Z gonzalem $
 * --------------------------------------------------------------------------
 */
package org.ow2.jonas.jpaas.apache.manager.jk.rest;

import javax.ws.rs.*;

@Path("/")
public interface IJkManager {

    @POST
    @Path("/worker/{name}/loadbalancer/{loadbalancer}")
    void addNamedWorker(@PathParam("name") String name,
                        @PathParam("loadbalancer") String loadbalancer,
                        @FormParam("host") String host,
                        @FormParam("port") String port,
                        @FormParam("lbFactor") String lbFactor);

    @POST
    @Path("/worker/loadbalancer/{loadbalancer}")
    void addWorker(@PathParam("loadbalancer")String loadbalancer,
                   @FormParam("host")String host,
                   @FormParam("port")String port,
                   @FormParam("lbFactor")String lbFactor);

    @DELETE
    @Path("/worker/{name}")
    void removeNamedWorker(@PathParam("name") String name);

    @POST
    @Path("/worker/{name}/disable")
    void disableNamedWorker(@PathParam("name") String name);

    @POST
    @Path("/worker/{name}/enable")
    void enableNamedWorker(@PathParam("name") String name);

    @POST
    @Path("/worker/{name}/stop")
    void stopNamedWorker(@PathParam("name") String name);

    @POST
    @Path("/mount/{loadbalancer}")
    void mount(@PathParam("loadbalancer") String loadbalancer,
               @FormParam("path") String path);

    @DELETE
    @Path("/mount/{loadbalancer}")
    void unmount(@PathParam("loadbalancer") String loadbalancer,
                 @QueryParam("path") String path);

    @DELETE
    @Path("/mount")
    void unmount();

    @POST
    @Path("/reload")
    void reload();

    @POST
    @Path("/init")
    void init(@FormParam("workersConfigurationFileName")
              String workersConfigurationFile,
              @FormParam("reloadCmd")
              String reloadCmd);

    @GET
    @Path("/worker/{name}/configured")
    String isConfigured(@PathParam("name") String name);

    @GET
    @Path("/worker/{name}/enabled")
    String isEnabled(@PathParam("name") String name);

    @GET
    @Path("/id")
    String getId();

}
