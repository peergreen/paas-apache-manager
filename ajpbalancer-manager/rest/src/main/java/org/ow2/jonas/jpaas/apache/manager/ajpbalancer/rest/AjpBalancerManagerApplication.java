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

package org.ow2.jonas.jpaas.apache.manager.jk.rest;

import org.ow2.jonas.jpaas.apache.manager.ajpbalancer.api.AjpBalancerManagerService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * REST application to provide the REST implementation of our services
 * in order to be wrapped by a servlet.
 *
 * @author David Richard
 */
public class AjpBalancerManagerApplication extends Application {

    private AjpBalancerManagerService ajpBalancerManagerService;

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
}
