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

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheUtilService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;


/**
 * REST application that implements REST service for IService.
 * The application instance is to be wrapped by a servlet.
 * @author David Richard
 */
@Component(name="ServiceApplication", immediate = true)
@Provides(specifications={javax.ws.rs.core.Application.class})
public class ServiceApplication extends Application {

    private Log logger = LogFactory.getLog(this.getClass());

    @Requires
    private ApacheUtilService apacheUtilService;

    @ServiceProperty(name="jonas.jaxrs.context-path", value="/apache-manager")
    private String contextName;

    public ServiceApplication(){
    }

    @Override
    public Set<Class<?>> getClasses() {
        return null;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> objects = new HashSet<Object>();
        objects.add(new ServiceRest(apacheUtilService));
        return objects;
    }

    @Validate
    public void start() {
        logger.debug("ServiceApplication started");
    }


    @Invalidate
    public void stop() {
        logger.debug("ServiceApplication stopped");
    }
}