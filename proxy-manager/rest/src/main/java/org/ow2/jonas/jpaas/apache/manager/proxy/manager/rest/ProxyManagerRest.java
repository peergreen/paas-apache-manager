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

package org.ow2.jonas.jpaas.apache.manager.proxy.manager.rest;

import org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException;
import org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerService;
import org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.rest.IProxyManager;
import org.ow2.jonas.jpaas.apache.manager.util.api.xml.Directive;
import org.ow2.jonas.jpaas.apache.manager.util.api.xml.Error;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * ProxyManager REST implementation
 * @author David Richard
 */
public class ProxyManagerRest implements IProxyManager {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(ProxyManagerRest.class);

    /**
     * ProxyManager Service
     */
    private ProxyManagerService proxyManagerService;

    /**
     * End of Line
     */
    public static final String EOL = "\n";


    public ProxyManagerRest(ProxyManagerService proxyManagerService) {
        this.proxyManagerService = proxyManagerService;
    }


    /**
     * Create a ProxyPass directive
     *
     * @param path value of the ProxyPass first argument (path)
     * @param url  value of the ProxyPass second argument (url)
     */
    public Response createProxyPass(String path, String url) {
        Directive directive;
        try {
            long directiveID = proxyManagerService.createProxyPass(path, url);
            String directiveValue = path + " " + url;
            directive = new Directive(directiveID, "ProxyPass", directiveValue);
        } catch (ProxyManagerException e) {
            logger.error("Cannot create ProxyPass " + path + ", " + url, e);
            Error error = new Error();
            error.setMessage("Cannot create ProxyPass " + path + ", " + url + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(directive)
                .type(MediaType.APPLICATION_XML_TYPE)
                .build();
    }

    /**
     * Create a ProxyPass directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param path    value of the ProxyPass first argument (path)
     * @param url     value of the ProxyPass second argument (url)
     */
    public Response createVhostProxyPass(Long vhostID, String path, String url) {
        Directive directive;
        try {
            long directiveID = proxyManagerService.createVhostProxyPass(vhostID, path, url);
            String directiveValue = path + " " + url;
            directive = new Directive(directiveID, "ProxyPass", directiveValue);
        } catch (ProxyManagerException e) {
            logger.error("Cannot create ProxyPass " + path + ", " + url + " in Virtual Host " + vhostID, e);
            Error error = new Error();
            error.setMessage("Cannot create ProxyPass " + path + ", " + url + " in Virtual Host " + vhostID
                    + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(directive)
                .type(MediaType.APPLICATION_XML_TYPE)
                .build();
    }

    /**
     * Delete a ProxyPass directive
     *
     * @param directiveID the ID of the directive to remove
     */
    public Response deleteProxyPass(Long directiveID) {
        try {
            proxyManagerService.deleteProxyPass(directiveID);
        } catch (ProxyManagerException e) {
            logger.error("Cannot delete ProxyPass " + directiveID, e);
            Error error = new Error();
            error.setMessage("Cannot delete ProxyPass " + directiveID + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * Delete a ProxyPass directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param directiveID the ID of the directive to remove
     */
    public Response deleteVhostProxyPass(Long vhostID, Long directiveID) {
        try {
            proxyManagerService.deleteVhostProxyPass(vhostID, directiveID);
        } catch (ProxyManagerException e) {
            logger.error("Cannot delete ProxyPass " + directiveID + " in Virtual Host " + vhostID, e);
            Error error = new Error();
            error.setMessage("Cannot delete ProxyPass " + directiveID + " in Virtual Host " + vhostID
                    + "." + EOL + e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .type(MediaType.APPLICATION_XML_TYPE)
                    .build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
