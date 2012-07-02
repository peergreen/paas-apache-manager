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
 * $Id: JkManagerActivator.java 9002 2011-09-27 13:45:41Z gonzalem $
 * --------------------------------------------------------------------------
 */

package org.ow2.jonas.jpaas.apache.manager.jk.rest;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.http.HttpService;
import org.ow2.jonas.jpaas.apache.manager.jk.api.JkManagerService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * OSGi service to start a servlet to contain our REST service.
 *
 * @author Miguel González
 */
@Component(name="JkManagerActivator", immediate = true)
@Provides
public class JkManagerActivator {

    /**
     * The logger.
     */
    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Service context.
     */
    public static final String CONTEXT = "/jkmanager";

    @Property(name = "context", value = CONTEXT)
    private String context;

    @Requires
    private JkManagerService jkManagerService;

    @Requires
    private HttpService httpService;

    @Validate
    public void start() throws Throwable {
        logger.debug("Starting JkManager REST Service.");
        logger.debug("Context: '" + context + "'");
        logger.warn("service: " + jkManagerService.toString());
        httpService.registerServlet(context,
                new ServletContainer(new JkManagerApplication(jkManagerService)),
                null, null);
    }

    @Invalidate
    public void stop() throws Throwable {
        logger.debug("Going down.");
        httpService.unregister(context);
    }


}