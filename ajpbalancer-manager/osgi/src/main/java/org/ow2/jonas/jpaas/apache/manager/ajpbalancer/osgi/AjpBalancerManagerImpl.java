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
 * $Id: AjpBalancerManagerImpl.java 9171 2011-10-13 14:30:56Z richardd $
 * --------------------------------------------------------------------------
 */

package org.ow2.jonas.jpaas.apache.manager.ajpbalancer.osgi;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.ajpbalancer.api.AjpBalancerManagerService;
import org.ow2.jonas.lib.bootstrap.JProp;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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


    /**
     * AjpBalancerManager property file name
     */
    private static final String AJP_BALANCER_MANAGER_PROPERTY_FILE_NAME = "ajpbalancermanager.properties";

    @Validate
    public void start() {
        logger.info("Load default configuration");
        String proxyBalancerConfigurationFile = getAjpBalancerPropertyFileLocation();
        this.setProxyBalancerConfigurationFile(proxyBalancerConfigurationFile);

        logger.info("proxyBalancerConfigurationFile=" + proxyBalancerConfigurationFile);
    }

    /**
     * {@inheritDoc}
     */
    public void addBalancerMember(String proxyBalancer, String host, String port, String lbFactor) {

        logger.info("addBalancerMember (" + proxyBalancer + "," + host + "," + port + ", " + lbFactor + ")");

        String confFileLocation = getProxyBalancerConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeBalancerMember(String proxyBalancer, String host, String port) {
        logger.info("removeBalancerMember (" + proxyBalancer + "," + host + "," + port + ")");

        String confFileLocation = getProxyBalancerConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeProxyBalancer(String proxyBalancer) {
        logger.info("removeProxyBalancer(" + proxyBalancer + ")");
        String confFileLocation = getProxyBalancerConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
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

        String confFileLocation = getProxyBalancerConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void unmount(String proxyBalancer) {
        logger.info("unmount(" + proxyBalancer + ")");
        String confFileLocation = getProxyBalancerConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
        List<String> newFileStringList = new LinkedList<String>();

        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (!(string.contains("ProxyPass") && string.contains("balancer://" + proxyBalancer + "/"))) {
                newFileStringList.add(string);
            }
        }

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void unmount() {
        logger.info("unmount()");
        String confFileLocation = getProxyBalancerConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
        List<String> newFileStringList = new LinkedList<String>();

        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (!(string.contains("ProxyPass"))) {
                newFileStringList.add(string);
            }
        }

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    private List<String> loadConfigurationFile(String filePath) {

        List<String> fileStringList = new LinkedList<String>();
        try {
            InputStream ips = new FileInputStream(filePath);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);

            String line;
            while ((line = br.readLine()) != null) {
                fileStringList.add(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileStringList;

    }

    /**
     * {@inheritDoc}
     */
    private void flushConfigurationFile(String filePath,
                                        List<String> fileStringList) {

        try {
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
                String string = iterator.next();
                logger.info("flush : " + string);
                pw.println(string);
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getProxyBalancerConfigurationFile() {
        return proxyBalancerConfigurationFile;
    }

    public void setProxyBalancerConfigurationFile(String proxyBalancerConfigurationFile) {
        this.proxyBalancerConfigurationFile = proxyBalancerConfigurationFile;
    }

    /**
     * Get the property file location located in JONAS_BASE/conf/ajpbalancermanager.properties
     * with the key file.location
     *
     * @return the location of the ajpbalancermanager.properties file
     */
    private String getAjpBalancerPropertyFileLocation() {
        JProp prop = JProp.getInstance(AJP_BALANCER_MANAGER_PROPERTY_FILE_NAME);
        return prop.getValue(PROXY_BALANCER_CONF_FILE_LOCATION_PROPERTY);
    }

}
