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
