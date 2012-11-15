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

package org.ow2.jonas.jpaas.apache.manager.ajpbalancer.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * AjpBalancerManager Rest Interface.
 *
 * @author David Richard
 */
@Path("/")
public interface IAjpBalancerManager {

    @POST
    @Path("/proxybalancer/{proxybalancer}")
    void addBalancerMember(@PathParam("proxybalancer") String proxyBalancer,
                           @FormParam("host") String host,
                           @FormParam("port") String port,
                           @FormParam("lbFactor") String lbFactor);

    @DELETE
    @Path("/proxybalancer/{proxybalancer}/member")
    void removeBalancerMember(@PathParam("proxybalancer") String proxyBalancer,
                              @FormParam("host") String host,
                              @FormParam("port") String port);

    @DELETE
    @Path("/proxybalancer/{proxybalancer}")
    void removeProxyBalancer(@PathParam("proxybalancer") String proxyBalancer);


    @POST
    @Path("/init")
    void init(@FormParam("proxyBalancerConfigurationFile") String proxyBalancerConfigurationFile);


    @POST
    @Path("/mount/{proxybalancer}")
    void mount(@PathParam("proxybalancer") String proxyBalancer,
               @FormParam("path") String path);


    @DELETE
    @Path("/mount")
    void unmount();


    @DELETE
    @Path("/mount/{proxybalancer}")
    void unmount(@PathParam("proxybalancer") String proxyBalancer);

}
