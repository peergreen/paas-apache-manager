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
