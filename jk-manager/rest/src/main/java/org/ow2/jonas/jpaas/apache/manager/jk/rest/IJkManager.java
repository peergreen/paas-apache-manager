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

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public interface IJkManager {

    @POST
    @Path("/worker/{name}/loadbalancer/{loadbalancer}")
    void addNamedWorker(@PathParam("name") String name,
            @PathParam("loadbalancer") String loadbalancer,
            @QueryParam("host") String host,
            @QueryParam("port") String port,
            @QueryParam("lbFactor") String lbFactor);

    @POST
    @Path("/worker/loadbalancer/{loadbalancer}")
    void addWorker(@PathParam("loadbalancer")String loadbalancer,
            @QueryParam("host")String host,
            @QueryParam("port")String port,
            @QueryParam("lbFactor")String lbFactor);

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
            @QueryParam("path") String path);

    @DELETE
    @Path("/mount/{loadbalancer}")
    void unmount(@PathParam("loadbalancer") String loadbalancer,
            @QueryParam("path") String path);

    @DELETE
    @Path("/mount")
    void unmount();

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

    @POST
    @Path("/worker/{name}")
    @Produces(MediaType.APPLICATION_XML)
    public Response addNamedWorker(@PathParam("name") String name,
            @QueryParam("host") String host,
            @QueryParam("port") String port);

    @POST
    @Path("/worker")
    @Produces(MediaType.APPLICATION_XML)
    public Response addWorker(@QueryParam("host") String host,
            @QueryParam("port") String port);

    @POST
    @Path("/loadbalancer/{name}")
    @Produces(MediaType.APPLICATION_XML)
    public Response addLoadBalancer(@PathParam("name") String name,
            @QueryParam("wl") String workerList);

    @PUT
    @Path("/loadbalancer/{name}")
    @Produces(MediaType.APPLICATION_XML)
    public Response updateLoadBalancer(@PathParam("name") String name,
            @QueryParam("wl") String workerList);

    @DELETE
    @Path("/loadbalancer/{name}")
    @Produces(MediaType.APPLICATION_XML)
    public Response removeLoadBalancer(@PathParam("name") String name);

    @POST
    @Path("/vhost/{vhostID}/mount/{loadbalancer}")
    public Response mountInVhost(@PathParam("vhostID") Long vhostID,
            @PathParam("loadbalancer") String loadbalancer,
            @QueryParam("path") String path);

    @DELETE
    @Path("/vhost/{vhostID}/mount/{loadbalancer}")
    public Response unmountInVhost(@PathParam("vhostID") Long vhostID,
            @PathParam("loadbalancer") String loadbalancer,
            @QueryParam("path") String path);
}
