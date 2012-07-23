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
 * $Id: IAjpBalancerManager.java 9171 2011-10-13 14:30:56Z richardd $
 * --------------------------------------------------------------------------
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
