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
 * $Id: AjpBalancerManagerActivator.java 9171 2011-10-13 14:30:56Z richardd $
 * --------------------------------------------------------------------------
 */

package org.ow2.jonas.jpaas.apache.manager.jk.rest;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.service.http.HttpService;
import org.ow2.jonas.jpaas.apache.manager.ajpbalancer.api.AjpBalancerManagerService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * OSGi service to start a servlet to contain our REST service.
 *
 * @author David Richard
 */
@Component(name="AjpBalancerManagerActivator", immediate = true)
@Provides
public class AjpBalancerManagerActivator {
    /**
     * The logger.
     */
    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Service context.
     */
    public static final String CONTEXT = "/ajpbalancermanager";

    @Property(name = "context", value = CONTEXT)
    private String context;

    @Requires
    private AjpBalancerManagerService ajpBalancerManagerService;

    @Requires
    private HttpService httpService;


    @Validate
    public void start() throws Throwable {
        logger.debug("Starting AjpBalancerManager REST Service.");
        logger.debug("Context: '" + context + "'");
        logger.warn("service: " + ajpBalancerManagerService.toString());
        httpService.registerServlet(context,
                new ServletContainer(new AjpBalancerManagerApplication(ajpBalancerManagerService)),
                null, null);
    }

    @Invalidate
    public void stop() throws Throwable {
        logger.debug("Going down.");
        httpService.unregister(context);
    }

}
