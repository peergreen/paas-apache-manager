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

package org.ow2.jonas.jpaas.apache.manager.proxy.manager.osgi;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException;
import org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerService;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheManagerException;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheUtilService;
import org.ow2.jonas.lib.bootstrap.JProp;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The OSGi implementation of the ProxyManagerService Interface
 *
 * @author David Richard
 */
@Component(name="ProxyManager", immediate = true)
@Provides
@Instantiate
public class ProxyManager implements ProxyManagerService {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(ProxyManager.class);

    /**
     * Property name to define apache configuration file path
     */
    private static final String PROXY_CONF_FILE_LOCATION_PROPERTY = "proxy.conf.location";

    /**
     * Path to the proxy configuration file
     */
    private String proxyConfigurationFile;


    /**
     * ProxyManager property file name
     */
    private static final String PROXY_MANAGER_PROPERTY_FILE_NAME = "proxymanager.properties";

    @Requires
    private ApacheUtilService apacheUtilService;


    @Validate
    public void start() {
        logger.info("Load default configuration");
        String apacheConfigurationFileLocation = getApacheConfigurationFileLocation();
        this.setProxyConfigurationFile(apacheConfigurationFileLocation);
        logger.info("ProxyConfigurationFile=" + proxyConfigurationFile);
    }

    @Invalidate
    public void stop() throws Throwable {
        logger.debug("Stopping ProxyManager");
    }


    /**
     * Create a ProxyPass directive
     *
     * @param firstArg  value of the ProxyPass first argument (path)
     * @param secondArg value of the ProxyPass second argument (url)
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public void createProxyPass(String firstArg, String secondArg) throws ProxyManagerException {
        logger.debug("createProxyPass (" + firstArg + "," + secondArg + ")");
        List<String> fileStringList = apacheUtilService.loadConfigurationFile(proxyConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();
        String line = "ProxyPass " + firstArg + " " + secondArg;
        boolean found = false;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains(line)) {
                found = true;
            }
            newFileStringList.add(string);
        }
        if (!found) {
            newFileStringList.add(line);
        } else {
            throw new ProxyManagerException("Cannot add the ProxyPass directive : a directive \"" + line +
                    "\" is already present");
        }

        apacheUtilService.flushConfigurationFile(proxyConfigurationFile, newFileStringList);
    }

    /**
     * Create a ProxyPass directive in a Virtual Host
     *
     * @param firstArg  value of the ProxyPass first argument (path)
     * @param secondArg value of the ProxyPass second argument (url)
     * @param vhAddress address of the virtual host
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public void createProxyPass(String firstArg, String secondArg, String vhAddress) throws ProxyManagerException {
        logger.debug("createProxyPass (" + firstArg + "," + secondArg + "," + vhAddress + ")");
        createProxyPass(firstArg, secondArg, vhAddress, null);
    }

    /**
     * Create a ProxyPass directive in a Name-based Virtual Host
     *
     * @param firstArg     value of the ProxyPass first argument (path)
     * @param secondArg    value of the ProxyPass second argument (url)
     * @param vhAddress    address of the virtual host
     * @param vhNameServer value of the ServerName directive
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public void createProxyPass(String firstArg, String secondArg, String vhAddress, String vhNameServer) throws ProxyManagerException {
        logger.debug("createProxyPass (" + firstArg + "," + secondArg + "," + vhAddress + "," + vhNameServer + ")");

        String directiveArgs = firstArg + " " + secondArg;
        try {
            apacheUtilService.addDirectiveInVhost(vhAddress, vhNameServer, "ProxyPass", directiveArgs);
        } catch (ApacheManagerException e) {
            throw new ProxyManagerException(e.getMessage());
        }
    }

    /**
     * Delete a ProxyPass directive
     *
     *
     * @param firstArg  value of the ProxyPass first argument (path)
     * @param secondArg value of the ProxyPass second argument (url)
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public void deleteProxyPass(String firstArg, String secondArg) throws ProxyManagerException {
        logger.debug("removeProxyPass (" + firstArg + "," + secondArg + ")");
        List<String> fileStringList = apacheUtilService.loadConfigurationFile(proxyConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();
        String line = "ProxyPass " + firstArg + " " + secondArg;
        boolean found = false;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains(line)) {
                found = true;
            } else {
                newFileStringList.add(string);
            }
        }
        if (!found) {
            throw new ProxyManagerException("Cannot delete the directive : there is no directive \"" + line
                    + "\" in the Proxy configuration file.");
        }

        apacheUtilService.flushConfigurationFile(proxyConfigurationFile, newFileStringList);
    }

    /**
     * Delete a ProxyPass directive in a Virtual Host
     *
     *
     * @param firstArg  value of the ProxyPass first argument (path)
     * @param secondArg value of the ProxyPass second argument (url)
     * @param vhAddress address of the virtual host
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public void deleteProxyPass(String firstArg, String secondArg, String vhAddress) throws ProxyManagerException {
        logger.debug("deleteProxyPass (" + firstArg + "," + secondArg + "," + vhAddress + ")");
        deleteProxyPass(firstArg, secondArg, vhAddress, null);
    }

    /**
     * Delete a ProxyPass directive in a Name-based Virtual Host
     *
     *
     * @param firstArg     value of the ProxyPass first argument (path)
     * @param secondArg    value of the ProxyPass second argument (url)
     * @param vhAddress    address of the virtual host
     * @param vhNameServer value of the ServerName directive
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public void deleteProxyPass(String firstArg, String secondArg, String vhAddress, String vhNameServer)
            throws ProxyManagerException {

        logger.debug("deleteProxyPass (" + firstArg + "," + secondArg + "," + vhAddress + "," + vhNameServer + ")");
        String directiveArgs = firstArg + " " + secondArg;
        try {
            apacheUtilService.removeDirectiveInVhostIfPossible(vhAddress, vhNameServer, "ProxyPass", directiveArgs);
        } catch (ApacheManagerException e) {
            throw new ProxyManagerException(e.getMessage());
        }
    }

    public String getProxyConfigurationFile() {
        return proxyConfigurationFile;
    }

    public void setProxyConfigurationFile(String proxyConfigurationFile) {
        this.proxyConfigurationFile = proxyConfigurationFile;
    }

    /**
     * Get the property proxy configuration file location located in JONAS_BASE/conf/proxymanager.properties
     *
     * @return the location of the apache configuration file
     */
    private String getApacheConfigurationFileLocation() {
        JProp prop = JProp.getInstance(PROXY_MANAGER_PROPERTY_FILE_NAME);
        return prop.getValue(PROXY_CONF_FILE_LOCATION_PROPERTY);
    }

}
