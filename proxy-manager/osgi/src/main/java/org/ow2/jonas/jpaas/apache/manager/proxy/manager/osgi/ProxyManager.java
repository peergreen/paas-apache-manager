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
     * @param path  value of the ProxyPass first argument (path)
     * @param url value of the ProxyPass second argument (url)
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public long createProxyPass(String path, String url) throws ProxyManagerException {
        logger.debug("createProxyPass (" + path + "," + url + ")");
        String directiveArg = path + " " + url;
        try {
            return apacheUtilService.addDirectiveInFile(proxyConfigurationFile, "ProxyPass", directiveArg);
        } catch (ApacheManagerException e) {
            throw new ProxyManagerException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Create a ProxyPass directive in a Virtual Host
     *
     * @param vhAddress address of the virtual host
     * @param path  value of the ProxyPass first argument (path)
     * @param url value of the ProxyPass second argument (url)
     * @return the directive ID
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public long createVhostProxyPass(String vhAddress, String path, String url) throws ProxyManagerException {
        logger.debug("createVhostProxyPass (" + vhAddress + "," + path + "," + url + ")");
        return createVhostProxyPass(vhAddress, null, path, url);
    }

    /**
     * Create a ProxyPass directive in a Name-based Virtual Host
     *
     * @param vhAddress    address of the virtual host
     * @param vhServerName value of the ServerName directive
     * @param path     value of the ProxyPass first argument (path)
     * @param url    value of the ProxyPass second argument (url)
     * @return the directive ID
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public long createVhostProxyPass(String vhAddress, String vhServerName, String path, String url) throws ProxyManagerException {
        logger.debug("createVhostProxyPass (" + vhAddress + "," + vhServerName + "," + path + "," + url + ")");

        String directiveArgs = path + " " + url;
        try {
            return apacheUtilService.addDirectiveInVhost(vhAddress, vhServerName, "ProxyPass", directiveArgs);
        } catch (ApacheManagerException e) {
            throw new ProxyManagerException(e.getMessage());
        }
    }

    /**
     * Create a ProxyPass directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param path    value of the ProxyPass first argument (path)
     * @param url     value of the ProxyPass second argument (url)
     * @return the directive ID
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    public long createVhostProxyPass(long vhostID, String path, String url) throws ProxyManagerException {
        logger.debug("createVhostProxyPass (" + String.valueOf(vhostID) + "," + path + "," + url + ")");

        String directiveArgs = path + " " + url;
        try {
            return apacheUtilService.addDirectiveInVhost(vhostID, "ProxyPass", directiveArgs);
        } catch (ApacheManagerException e) {
            throw new ProxyManagerException(e.getMessage());
        }
    }

    /**
     * Delete a ProxyPass directive
     *
     * @param directiveID the ID of the directive to remove
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public void deleteProxyPass(long directiveID) throws ProxyManagerException {
        logger.debug("deleteProxyPass (" + directiveID + ")");
        try {
            apacheUtilService.removeDirectiveInFile(proxyConfigurationFile, directiveID);
        } catch (ApacheManagerException e) {
            throw new ProxyManagerException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Delete a ProxyPass directive in a Virtual Host
     *
     * @param vhAddress address of the virtual host
     * @param directiveID the ID of the directive to remove
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public void deleteVhostProxyPass(String vhAddress,  long directiveID) throws ProxyManagerException {
        logger.debug("deleteVhostProxyPass (" + vhAddress + "," + directiveID + ")");
        deleteVhostProxyPass(vhAddress, null, directiveID);
    }

    /**
     * Delete a ProxyPass directive in a Name-based Virtual Host
     *
     * @param vhAddress    address of the virtual host
     * @param vhServerName value of the ServerName directive
     * @param directiveID the ID of the directive to remove
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    @Override
    public void deleteVhostProxyPass(String vhAddress, String vhServerName, long directiveID)
            throws ProxyManagerException {

        logger.debug("deleteVhostProxyPass (" + vhAddress + "," + vhServerName + "," + directiveID + ")");
        try {
            apacheUtilService.removeDirectiveInVhostIfPossible(vhAddress, vhServerName, directiveID);
        } catch (ApacheManagerException e) {
            throw new ProxyManagerException(e.getMessage());
        }
    }

    /**
     * Delete a ProxyPass directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param directiveID the ID of the directive to remove
     * @throws org.ow2.jonas.jpaas.apache.manager.proxy.manager.api.ProxyManagerException
     *
     */
    public void deleteVhostProxyPass(long vhostID, long directiveID) throws ProxyManagerException {
        logger.debug("deleteVhostProxyPass (" + String.valueOf(vhostID) + "," + directiveID + ")");
        try {
            apacheUtilService.removeDirectiveInVhostIfPossible(vhostID, directiveID);
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
