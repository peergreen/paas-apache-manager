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


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerService;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;


/**
 * REST application that implements REST service for ProxyManager.
 * The application instance is to be wrapped by a servlet.
 * @author David Richard
 */
@Component(name="ProxyManagerApplication", immediate = true)
@Provides(specifications={javax.ws.rs.core.Application.class})
public class ProxyManagerApplication extends Application {

    private Log logger = LogFactory.getLog(this.getClass());

    @Requires
    private ProxyManagerService proxyManagerService;

    @ServiceProperty(name="jonas.jaxrs.context-path", value="/proxymanager")
    private String proxyContextName;

    public ProxyManagerApplication(){
    }

    @Override
    public Set<Class<?>> getClasses() {
        return null;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> objects = new HashSet<Object>();
        objects.add(new ProxyManagerRest(proxyManagerService));
        return objects;
    }

    @Validate
    public void start() {
        logger.debug("ProxyManagerApplication started");
    }


    @Invalidate
    public void stop() {
        logger.debug("ProxyManagerApplication stopped");
    }
}
