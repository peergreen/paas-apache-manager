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
 * $Id: RewriteManagerApplication.java 9287 2011-11-10 09:53:32Z richardd $
 * --------------------------------------------------------------------------
 */

package org.ow2.jonas.jpaas.apache.manager.rewrite.rest;

import org.ow2.jonas.jpaas.apache.manager.rewrite.api.RewriteManagerService;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.ws.rs.core.Application;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.HashSet;
import java.util.Set;

/**
 * REST application to provide the REST implementation of our services
 * in order to be wrapped by a servlet.
 *
 * @author David Richard
 */
@Component(name="RewriteManagerApplication", immediate = true)
@Provides(specifications={javax.ws.rs.core.Application.class})
public class RewriteManagerApplication extends Application {

    private Log logger = LogFactory.getLog(this.getClass());

    @Requires
    private RewriteManagerService rewriteManagerService;

    @ServiceProperty(name="jonas.jaxrs.context-path", value="/rewritemanager")
    private String rewriteContextName;

    public RewriteManagerApplication() {
    }

    @Override
    public Set<Class<?>> getClasses() {
        return null;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> objects = new HashSet<Object>();
        objects.add(new RewriteManager(rewriteManagerService));
        return objects;
    }

    @Validate
    public void start() {
        logger.debug("RewriteManagerApplication started");
    }

    @Invalidate
    public void stop() {
        logger.debug("RewriteManagerApplication stopped");
    }
}
