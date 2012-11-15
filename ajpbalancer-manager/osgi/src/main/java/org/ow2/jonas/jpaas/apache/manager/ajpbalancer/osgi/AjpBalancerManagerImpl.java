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

package org.ow2.jonas.jpaas.apache.manager.ajpbalancer.osgi;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.ajpbalancer.api.AjpBalancerManagerService;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheUtilService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * The implementation of the AjpBalancerManagerService Interface
 *
 * @author David Richard
 */
@Component(propagation = true)
@Provides
public class AjpBalancerManagerImpl implements AjpBalancerManagerService {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(AjpBalancerManagerImpl.class);


    /**
     * Property name to define the proxy_balancer.conf path
     */
    private static final String PROXY_BALANCER_CONF_FILE_LOCATION_PROPERTY = "proxy_balancer.conf.location";

    /**
     * Path to the proxy_balancer.conf file
     */
    private String proxyBalancerConfigurationFile;

    @Requires
    private ApacheUtilService apacheUtilService;

    @Validate
    public void start() {
        logger.info("Load default configuration");
        String proxyBalancerConfigurationFile =
                apacheUtilService.getPropertyValue(PROXY_BALANCER_CONF_FILE_LOCATION_PROPERTY);
        this.setProxyBalancerConfigurationFile(proxyBalancerConfigurationFile);

        logger.info("proxyBalancerConfigurationFile=" + proxyBalancerConfigurationFile);
    }

    /**
     * {@inheritDoc}
     */
    public void addBalancerMember(String proxyBalancer, String host, String port, String lbFactor) {
        logger.info("addBalancerMember (" + proxyBalancer + "," + host + "," + port + ", " + lbFactor + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(proxyBalancerConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean proxyBalancerAlreadyCreated = false;
        boolean inProxyBalancer = false;
        boolean balancerMemberAlreadyPresent = false;
        // We need to know if there is already this proxy balancer
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy balancer://" + proxyBalancer + ">")) {
                proxyBalancerAlreadyCreated = true;
                inProxyBalancer=true;
            }
            if (string.contains("</Proxy>")) {
                if (inProxyBalancer && !balancerMemberAlreadyPresent) {
                    newFileStringList.add("BalancerMember ajp://" + host + ":" + port
                            + " loadfactor=" + lbFactor);
                    balancerMemberAlreadyPresent=true;
                }
                inProxyBalancer=false;
            }
            if (!balancerMemberAlreadyPresent && inProxyBalancer
                    && string.contains("BalancerMember ajp://" + host + ":" + port + " loadfactor=")) {
                newFileStringList.add("BalancerMember ajp://" + host + ":" + port
                        + " loadfactor=" + lbFactor);
                balancerMemberAlreadyPresent=true;
            } else {
                newFileStringList.add(string);
            }

        }

        // If no proxyBalancer with the same name was found, we create it
        if (!proxyBalancerAlreadyCreated) {
            newFileStringList.add("<Proxy balancer://" + proxyBalancer + ">");
            newFileStringList.add("Allow from all");
            newFileStringList.add("BalancerMember ajp://" + host + ":" + port + " loadfactor=" + lbFactor);
            newFileStringList.add("</Proxy>");
        }

        apacheUtilService.flushConfigurationFile(proxyBalancerConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeBalancerMember(String proxyBalancer, String host, String port) {
        logger.info("removeBalancerMember (" + proxyBalancer + "," + host + "," + port + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(proxyBalancerConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean inProxyBalancer = false;
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy balancer://" + proxyBalancer + ">")) {
                inProxyBalancer=true;
            }
            if (string.contains("</Proxy>")) {
                inProxyBalancer=false;
            }
            if (!(string.contains("BalancerMember ajp://" + host + ":" + port) && inProxyBalancer)) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(proxyBalancerConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeProxyBalancer(String proxyBalancer) {
        logger.info("removeProxyBalancer(" + proxyBalancer + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(proxyBalancerConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean inProxyBalancer = false;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy balancer://" + proxyBalancer + ">")) {
                inProxyBalancer=true;
            }
            if (!inProxyBalancer && !(string.contains("ProxyPass")
                    && string.contains("balancer://" + proxyBalancer + "/"))) {
                newFileStringList.add(string);
            }
            if (string.contains("</Proxy>")) {
                inProxyBalancer=false;
            }
        }

        apacheUtilService.flushConfigurationFile(proxyBalancerConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void init(String proxyBalancerConfigurationFile) {
        setProxyBalancerConfigurationFile(proxyBalancerConfigurationFile);
        logger.info("proxyBalancerConfigurationFile=" +  proxyBalancerConfigurationFile);
    }

    /**
     * {@inheritDoc}
     */
    public void mount(String proxyBalancer, String path) {
        logger.info("mountWorker (" + proxyBalancer + ", " + path + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(proxyBalancerConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean alreadyMount = false;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if(string.contains("ProxyPass") && string.contains("balancer://" + proxyBalancer + "/")) {
                alreadyMount = true;
            }
            newFileStringList.add(string);
        }

        if (!alreadyMount) {
            newFileStringList.add("ProxyPass " + path + " " + "balancer://" + proxyBalancer + "/");
        }

        apacheUtilService.flushConfigurationFile(proxyBalancerConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void unmount(String proxyBalancer) {
        logger.info("unmount(" + proxyBalancer + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(proxyBalancerConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (!(string.contains("ProxyPass") && string.contains("balancer://" + proxyBalancer + "/"))) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(proxyBalancerConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void unmount() {
        logger.info("unmount()");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(proxyBalancerConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (!(string.contains("ProxyPass"))) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(proxyBalancerConfigurationFile, newFileStringList);
    }

    public String getProxyBalancerConfigurationFile() {
        return proxyBalancerConfigurationFile;
    }

    public void setProxyBalancerConfigurationFile(String proxyBalancerConfigurationFile) {
        this.proxyBalancerConfigurationFile = proxyBalancerConfigurationFile;
    }
}
