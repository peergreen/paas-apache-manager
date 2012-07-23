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
 * $Id: AjpBalancerManagerApplication.java 9171 2011-10-13 14:30:56Z richardd $
 * --------------------------------------------------------------------------
 */

package org.ow2.jonas.jpaas.apache.manager.ajpbalancer.rest;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.ajpbalancer.api.AjpBalancerManagerService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * REST application that implements REST service for AjpBalancerManager.
 * The application instance is to be wrapped by a servlet.
 * @author David Richard
 */
@Component(name="AjpBalancerManagerApplication", immediate = true)
@Provides(specifications={javax.ws.rs.core.Application.class})
public class AjpBalancerManagerApplication extends Application {

    private Log logger = LogFactory.getLog(this.getClass());

    @Requires
    private AjpBalancerManagerService ajpBalancerManagerService;

    @ServiceProperty(name="jonas.jaxrs.context-path", value="/ajbbalancermanager")
    private String contextName;

    public AjpBalancerManagerApplication(){
    }

    public AjpBalancerManagerApplication(AjpBalancerManagerService ajpBalancerManagerService) {
        this.ajpBalancerManagerService = ajpBalancerManagerService;
    }

    @Override
    public Set<Class<?>> getClasses() {
        return null;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> objects = new HashSet<Object>();
        objects.add(new AjpBalancerManager(ajpBalancerManagerService));
        return objects;
    }

    @Validate
    public void start() {
        logger.debug("AjpBalancerManagerApplication started");
    }


    @Invalidate
    public void stop() {
        logger.debug("AjpBalancerManagerApplication stopped");
    }
}
