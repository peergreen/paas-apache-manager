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

package org.ow2.jonas.jpaas.apache.manager.vhost.manager.osgi;


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheUtilService;
import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.VhostManagerException;
import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.VhostManagerService;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * The OSGi implementation of the VhostManagerService Interface
 *
 * @author David Richard
 */
@Component(name="VhostManager", immediate = true)
@Provides
@Instantiate
public class VhostManager implements VhostManagerService {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(VhostManager.class);


    /**
     * Index of the first line of the virtual host block directive
     */
    private static final int VHOST_FIRST_LINE_INDEX = 2;

    @Requires
    private ApacheUtilService apacheUtilService;


    @Validate
    public void start() {
        logger.info("Starting VhostManager");
    }

    @Invalidate
    public void stop() throws Throwable {
        logger.debug("Stopping VhostManager");
    }


    /**
     * Create a Virtual Host block directive
     *
     * @param address address of the virtual host
     * @throws VhostManagerException
     */
    public void createVirtualHost(String address) throws VhostManagerException {
        logger.debug("createVirtualHost (" + address + ")");
        createVirtualHost(address, null);
    }

    /**
     * Delete a Virtual Host block directive
     *
     * @param address address of the virtual host
     * @throws VhostManagerException
     */
    public void deleteVirtualHost(String address) throws VhostManagerException {
        logger.debug("deleteVirtualHost (" + address + ")");
        deleteVirtualHost(address, null);
    }

    /**
     * Create a Name-based Virtual Host block directive
     *
     * @param address    address of the virtual host
     * @param serverName value of the ServerName directive
     * @throws VhostManagerException
     */
    public void createVirtualHost(String address, String serverName) throws VhostManagerException {
        logger.debug("createVirtualHost (" + address + "," + serverName +")");
        if (apacheUtilService.isVhostExist(address, serverName)) {
            throw new VhostManagerException("The Virtual Host " + address + " (ServerName = " + serverName + ") is" +
                    " already present");
        } else {
            apacheUtilService.includeVhostFolderIfNecessary();
            List<String> fileStringList = new LinkedList<String>();
            fileStringList.add("NameVirtualHost " + address);
            fileStringList.add("<VirtualHost " + address + ">");
            if (serverName != null) {
                fileStringList.add("ServerName " + serverName);
            }
            fileStringList.add("</VirtualHost>");
            String vhostFile = apacheUtilService.getVhostConfigurationFile(address, serverName);
            apacheUtilService.flushConfigurationFile(vhostFile, fileStringList);
        }

    }

    /**
     * Delete a Name-based Virtual Host block directive
     *
     * @param address    address of the virtual host
     * @param serverName value of the ServerName directive
     * @throws VhostManagerException
     */
    public void deleteVirtualHost(String address, String serverName) throws VhostManagerException {
        logger.debug("deleteVirtualHost (" + address + "," + serverName +")");

        if (apacheUtilService.isVhostExist(address, serverName)) {
            String vhostFile = apacheUtilService.getVhostConfigurationFile(address, serverName);
            File file = new File(vhostFile);
            file.delete();
        } else {
            throw new VhostManagerException("The Virtual Host " + address + " (ServerName = " + serverName + ") does" +
                    " not exist");
        }
    }

    /**
     * Create a DocumentRoot directive in a Virtual Host
     *
     * @param address      address of the virtual host
     * @param documentRoot value of the DocumentRoot directive to create
     * @throws VhostManagerException
     */
    public void createDocumentRoot(String address, String documentRoot) throws VhostManagerException {
        logger.debug("createDocumentRoot (" + address + "," + documentRoot +")");
        createDocumentRoot(address, null, documentRoot);
    }

    /**
     * Delete a DocumentRoot directive in a Virtual Host
     *
     * @param address      address of the virtual host
     * @throws VhostManagerException
     */
    public void deleteDocumentRoot(String address) throws VhostManagerException {
        logger.debug("deleteDocumentRoot (" + address +")");
        deleteDocumentRoot(address, null);
    }

    /**
     * Create a DocumentRoot directive in a Name-based Virtual Host
     *
     * @param address      address of the virtual host
     * @param serverName   value of the ServerName directive
     * @param documentRoot value of the DocumentRoot directive to create
     * @throws VhostManagerException
     */
    public void createDocumentRoot(String address, String serverName, String documentRoot) throws VhostManagerException {
        logger.debug("createDocumentRoot (" + address + "," + serverName + "," + documentRoot +")");
        addDirectiveIfPossible(address, serverName, "DocumentRoot", documentRoot);
    }

    /**
     * Delete a DocumentRoot directive in a Name-based Virtual Host
     *
     *
     * @param address      address of the virtual host
     * @param serverName   value of the ServerName directive
     * @throws VhostManagerException
     */
    public void deleteDocumentRoot(String address, String serverName) throws VhostManagerException {
        logger.debug("deleteDocumentRoot (" + address + "," + serverName + ")");
        removeDirectiveIfPossible(address, serverName, "DocumentRoot");
    }

    /**
     * Create a ServerAlias directive in a Virtual Host
     *
     *
     * @param address     address of the virtual host
     * @param serverAlias argument(s) of the ServerAlias directive to create
     * @throws VhostManagerException
     */
    public void createServerAlias(String address, List<String> serverAlias) throws VhostManagerException {
        logger.debug("createServerAlias (" + address + "," + serverAlias.toString() + ")");
        createServerAlias(address, null, serverAlias);
    }

    /**
     * Delete a ServerAlias directive in a Virtual Host
     *
     *
     * @param address     address of the virtual host
     * @throws VhostManagerException
     */
    public void deleteServerAlias(String address) throws VhostManagerException {
        logger.debug("deleteServerAlias (" + address + ")");
        deleteServerAlias(address, null);
    }

    /**
     * Create a ServerAlias directive in a Name-based Virtual Host
     *
     *
     * @param address     address of the virtual host
     * @param serverName  value of the ServerName directive
     * @param serverAlias argument(s) of the ServerAlias directive to create
     * @throws VhostManagerException
     */
    public void createServerAlias(String address, String serverName, List<String> serverAlias) throws VhostManagerException {
        logger.debug("createServerAlias (" + address + "," + serverName + "," + serverAlias.toString() + ")");
        String serverAliasArgs = "";
        for (String value : serverAlias) {
            serverAliasArgs =  serverAliasArgs + " " + value;
        }
        addDirectiveIfPossible(address, serverName, "ServerAlias", serverAliasArgs);
    }

    /**
     * Delete a ServerAlias directive in a Name-based Virtual Host
     *
     *
     * @param address     address of the virtual host
     * @param serverName  value of the ServerName directive
     * @throws VhostManagerException
     */
    public void deleteServerAlias(String address, String serverName) throws VhostManagerException {
        logger.debug("deleteServerAlias (" + address + "," + serverName + ")");

        removeDirectiveIfPossible(address, serverName, "ServerAlias");

    }

    /**
     * Create a ServerPath directive in a Virtual Host
     *
     * @param address    address of the virtual host
     * @param serverPath value of the ServerPath directive to create
     * @throws VhostManagerException
     */
    public void createServerPath(String address, String serverPath) throws VhostManagerException {
        logger.debug("createServerPath (" + address + "," + serverPath + ")");
        createServerPath(address, null, serverPath);
    }

    /**
     * Delete a ServerPath directive in a Virtual Host
     *
     *
     * @param address    address of the virtual host
     * @throws VhostManagerException
     */
    public void deleteServerPath(String address) throws VhostManagerException {
        logger.debug("deleteServerPath (" + address + ")");
        deleteServerPath(address,null);
    }

    /**
     * Create a ServerPath directive in a Name-based Virtual Host
     *
     * @param address    address of the virtual host
     * @param serverName value of the ServerName directive
     * @param serverPath value of the ServerPath directive to create
     * @throws VhostManagerException
     */
    public void createServerPath(String address, String serverName, String serverPath) throws VhostManagerException {
        logger.debug("createServerPath (" + address + "," + serverName + "," + serverPath + ")");
        addDirectiveIfPossible(address, serverName, "ServerPath", serverPath);
    }

    /**
     * Delete a ServerPath directive in a Name-based Virtual Host
     *
     * @param address    address of the virtual host
     * @param serverName value of the ServerName directive
     * @throws VhostManagerException
     */
    public void deleteServerPath(String address, String serverName) throws VhostManagerException {
        logger.debug("deleteServerPath (" + address + "," + serverName + ")");
        removeDirectiveIfPossible(address, serverName, "ServerPath");
    }


    /**
     *  Add a directive at the beginning of a Virtual Host block, if the specified directive is not already present
     * @param vhAddress    address of the virtual host
     * @param vhNameServer value of the ServerName directive
     * @param directive the directive to add
     * @param directiveArg argument(s) of the directive
     */
    private void addDirectiveIfPossible(String vhAddress, String vhNameServer, String directive, String directiveArg)
            throws VhostManagerException {
        logger.debug("addDirectiveIfPossible (" + vhAddress + "," + vhNameServer + "," + directive + ","
                + directiveArg  +")");

        if (apacheUtilService.isVhostExist(vhAddress, vhNameServer)) {
            String vhostFile = apacheUtilService.getVhostConfigurationFile(vhAddress, vhNameServer);
            List<String> fileStringList = apacheUtilService.loadConfigurationFile(vhostFile);
            List<String> newFileStringList = new LinkedList<String>();

            String line = directive + " " + directiveArg;
            boolean found = false;
            for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
                String string = iterator.next();
                if (string.contains(line)) {
                    found = true;
                }
                newFileStringList.add(string);
            }
            if (!found) {
                newFileStringList.add(VHOST_FIRST_LINE_INDEX, line);
            } else {
                throw new VhostManagerException("Cannot add the directive : a directive \"" + directive + "\" is " +
                        "already present");
            }

            apacheUtilService.flushConfigurationFile(vhostFile, newFileStringList);
        } else {
            throw new VhostManagerException("The Virtual Host " + vhAddress + " (ServerName = " + vhNameServer
                    + ") does not exist");
        }
    }

    /**
     *  Remove a directive of a Virtual Host block, if the specified directive is present
     * @param vhAddress    address of the virtual host
     * @param vhNameServer value of the ServerName directive
     * @param directive the directive to remove
     */
    private void removeDirectiveIfPossible(String vhAddress, String vhNameServer, String directive)
            throws VhostManagerException {
        logger.debug("removeDirectiveIfPossible (" + vhAddress + "," + vhNameServer + "," + directive + ")");

        if (apacheUtilService.isVhostExist(vhAddress, vhNameServer)) {
            String vhostFile = apacheUtilService.getVhostConfigurationFile(vhAddress, vhNameServer);
            List<String> fileStringList = apacheUtilService.loadConfigurationFile(vhostFile);
            List<String> newFileStringList = new LinkedList<String>();

            boolean found = false;
            for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
                String string = iterator.next();
                if (string.contains(directive)) {
                    found = true;
                } else {
                    newFileStringList.add(string);
                }
            }
            if (!found) {
                throw new VhostManagerException("Cannot remove the directive : there is no directive \"" + directive + "\""
                        + " in the Virtual Host configuration file (" + vhostFile + ").");
            }

            apacheUtilService.flushConfigurationFile(vhostFile, newFileStringList);
        } else {
            throw new VhostManagerException("The Virtual Host " + vhAddress + " (ServerName = " + vhNameServer
                    + ") does not exist");
        }
    }


}
