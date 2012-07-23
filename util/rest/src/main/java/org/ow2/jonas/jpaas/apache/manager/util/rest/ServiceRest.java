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

package org.ow2.jonas.jpaas.apache.manager.util.rest;

import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheManagerException;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheUtilService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.ow2.jonas.jpaas.apache.manager.util.api.xml.Error;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Apache Service REST implementation
 * @author David Richard
 */
public class ServiceRest implements IService {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(IService.class);

    /**
     * ApacheUtil Service
     */
    private ApacheUtilService apacheUtilService;

    /**
     * End of Line
     */
    public static final String EOL = "\n";

    public ServiceRest(ApacheUtilService apacheUtilService) {
        this.apacheUtilService = apacheUtilService;
    }

    /**
     * start apache2
     */
    public Response startApache() {
        try {
            apacheUtilService.startApache();
        } catch (ApacheManagerException e) {
            logger.error("Cannot start the Apache server.", e.getCause());
            Error error = new Error();
            error.setMessage("Cannot start the Apache server." + EOL + e.getCause());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response
                .status(Response.Status.ACCEPTED)
                .build();
    }

    /**
     * stop apache2
     */
    public Response stopApache() {
        try {
            apacheUtilService.stopApache();
        } catch (ApacheManagerException e) {
            logger.error("Cannot stop the Apache server.", e.getCause());
            Error error = new Error();
            error.setMessage("Cannot stop the Apache server." + EOL + e.getCause());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response
                .status(Response.Status.ACCEPTED)
                .build();
    }

    /**
     * reload apache2
     */
    public Response reloadApache() {
        try {
            apacheUtilService.reloadApache();
        } catch (ApacheManagerException e) {
            logger.error("Cannot reload the Apache server.", e.getCause());
            Error error = new Error();
            error.setMessage("Cannot reload the Apache server." + EOL + e.getCause());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response
                .status(Response.Status.ACCEPTED)
                .build();
    }
}
