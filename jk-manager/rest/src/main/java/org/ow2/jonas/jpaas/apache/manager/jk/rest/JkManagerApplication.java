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
 * $Id: JkManagerApplication.java 9002 2011-09-27 13:45:41Z gonzalem $
 * --------------------------------------------------------------------------
 */

package org.ow2.jonas.jpaas.apache.manager.jk.rest;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.ow2.jonas.jpaas.apache.manager.jk.api.JkManagerService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;

/**
 * REST application to provide the REST implementation of our services
 * in order to be wrapped by a servlet.
 *
 * @author Miguel González
 */
@Component(name="JkManagerApplication", immediate = true)
@Provides(specifications={javax.ws.rs.core.Application.class})
public class JkManagerApplication extends Application {

    private Log logger = LogFactory.getLog(this.getClass());

    @Requires
    private JkManagerService jkManagerService;

    @ServiceProperty(name="jonas.jaxrs.context-path", value="/jkmanager")
    private String jkContextName;


    public JkManagerApplication(){
    }

    @Override
    public Set<Class<?>> getClasses() {
        return null;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> objects = new HashSet<Object>();
        objects.add(new JkManager(jkManagerService));
        return objects;
    }

    @Validate
    public void start() {
        logger.debug("JkManagerApplication started");
    }

    @Invalidate
    public void stop() {
        logger.debug("JkManagerApplication stopped");
    }
}