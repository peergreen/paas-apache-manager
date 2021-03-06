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

package org.ow2.jonas.jpaas.apache.manager.vhost.manager.osgi;


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheManagerException;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheUtilService;
import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.VhostManagerException;
import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.VhostManagerService;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.xml.Vhost;
import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.xml.VhostList;
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

    /**
     * Path to the virtual host configuration folder
     */
    private String vhostConfigurationFolder;

    @Requires
    private ApacheUtilService apacheUtilService;


    @Validate
    public void start() {
        logger.info("Starting VhostManager");
        vhostConfigurationFolder = apacheUtilService.getVhostConfigurationFolder();
    }

    @Invalidate
    public void stop() throws Throwable {
        logger.debug("Stopping VhostManager");
    }

    /**
     * If it is not done, write a directive in Apache configuration file
     * to include the Virtual Host configuration folder.
     */
    public void includeVhostFolderIfNecessary() {
        logger.debug("includeVhostFolderIfNecessary ()");
        List<String> fileStringList = apacheUtilService.loadConfigurationFile(
                apacheUtilService.getApacheConfigurationFile());
        List<String> newFileStringList = new LinkedList<String>();
        boolean found = false;

        for (String string : fileStringList) {
            if (string.contains("Include " + vhostConfigurationFolder + "/*.conf")) {
                found = true;
            }
            newFileStringList.add(string);
        }
        if (!found) {
            newFileStringList.add("Include " + vhostConfigurationFolder + "/*.conf");
        }

        apacheUtilService.flushConfigurationFile(apacheUtilService.getApacheConfigurationFile(), newFileStringList);
    }


    /**
     * Create a Virtual Host block directive
     *
     * @param address address of the virtual host
     * @throws VhostManagerException
     * @return the Virtual Host ID
     */
    public long createVirtualHost(String address) throws VhostManagerException {
        logger.debug("createVirtualHost (" + address + ")");
        return createVirtualHost(address, null);
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
     * @return the Virtual Host ID
     */
    public long createVirtualHost(String address, String serverName) throws VhostManagerException {
        logger.debug("createVirtualHost (" + address + "," + serverName +")");
        try {
            if (apacheUtilService.isVhostExist(address, serverName)) {
                throw new VhostManagerException("The Virtual Host " + address + " (ServerName = " + serverName + ") " +
                        "is already present");
            } else {
                includeVhostFolderIfNecessary();
                List<String> fileStringList = new LinkedList<String>();
                fileStringList.add("NameVirtualHost " + address);
                fileStringList.add("<VirtualHost " + address + ">");
                if (serverName != null) {
                    fileStringList.add("ServerName " + serverName);
                }
                fileStringList.add("</VirtualHost>");
                long vhostID = getAvailableID();
                String vhostFile = getVhostFileFromID(vhostID);
                apacheUtilService.flushConfigurationFile(vhostFile, fileStringList);
                return vhostID;
            }
        } catch (ApacheManagerException e) {
            throw new VhostManagerException(e.getMessage(), e.getCause());
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
        logger.debug("deleteVirtualHost (" + address + "," + serverName + ")");
        try {
            long vhostID = apacheUtilService.getVhostID(address, serverName);
            deleteVirtualHost(vhostID);
        } catch (ApacheManagerException e) {
            throw new VhostManagerException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Delete a Virtual Host block directive
     *
     * @param vhostID ID of the virtual host
     * @throws VhostManagerException
     */
    public void deleteVirtualHost(long vhostID) throws VhostManagerException {
        logger.debug("deleteVirtualHost (" + String.valueOf(vhostID) + ")");

        try {
            String vhostFile = apacheUtilService.getVhostConfigurationFile(vhostID);
            File file = new File(vhostFile);
            file.delete();
        } catch (ApacheManagerException e) {
            throw new VhostManagerException(e.getMessage(), e.getCause());
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
     * Create a DocumentRoot directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param documentRoot value of the DocumentRoot directive to create
     * @throws VhostManagerException
     */
    public void createDocumentRoot(long vhostID, String documentRoot) throws VhostManagerException {
        logger.debug("createDocumentRoot (" + String.valueOf(vhostID) + documentRoot +")");
        addDirectiveIfPossible(vhostID, "DocumentRoot", documentRoot);
    }

    /**
     * Delete a DocumentRoot directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @throws VhostManagerException
     */
    public void deleteDocumentRoot(long vhostID) throws VhostManagerException {
        logger.debug("deleteDocumentRoot (" + String.valueOf(vhostID) + ")");
        removeDirectiveIfPossible(vhostID, "DocumentRoot");
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
     * Create a ServerAlias directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param serverAlias argument(s) of the ServerAlias directive to create
     * @throws VhostManagerException
     */
    public void createServerAlias(long vhostID, List<String> serverAlias) throws VhostManagerException {
        logger.debug("createServerAlias (" + String.valueOf(vhostID) + "," + serverAlias.toString() + ")");
        String serverAliasArgs = "";
        for (String value : serverAlias) {
            serverAliasArgs =  serverAliasArgs + " " + value;
        }
        addDirectiveIfPossible(vhostID, "ServerAlias", serverAliasArgs);
    }

    /**
     * Delete a ServerAlias directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @throws VhostManagerException
     */
    public void deleteServerAlias(long vhostID) throws VhostManagerException {
        logger.debug("deleteServerAlias (" + String.valueOf(vhostID) + ")");

        removeDirectiveIfPossible(vhostID, "ServerAlias");
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
     * Create a ServerPath directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param serverPath value of the ServerPath directive to create
     * @throws VhostManagerException
     */
    public void createServerPath(long vhostID, String serverPath) throws VhostManagerException {
        logger.debug("createServerPath (" + String.valueOf(vhostID) + "," + serverPath + ")");
        addDirectiveIfPossible(vhostID, "ServerPath", serverPath);
    }

    /**
     * Delete a ServerPath directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @throws VhostManagerException
     */
    public void deleteServerPath(long vhostID) throws VhostManagerException {
        logger.debug("deleteServerPath (" + String.valueOf(vhostID) + ")");
        removeDirectiveIfPossible(vhostID, "ServerPath");
    }


    /**
     *  Add a directive at the beginning of a Virtual Host block, if the specified directive is not already present
     * @param vhAddress    address of the virtual host
     * @param vhServerName value of the ServerName directive
     * @param directive the directive to add
     * @param directiveArg argument(s) of the directive
     * @throws VhostManagerException
     */
    private void addDirectiveIfPossible(String vhAddress, String vhServerName, String directive, String directiveArg)
            throws VhostManagerException {
        logger.debug("addDirectiveIfPossible (" + vhAddress + "," + vhServerName + "," + directive + ","
                + directiveArg  +")");

        try {
            long vhostID = apacheUtilService.getVhostID(vhAddress, vhServerName);
            addDirectiveIfPossible(vhostID, directive, directiveArg);

        } catch (ApacheManagerException e) {
            throw new VhostManagerException(e.getMessage(), e.getCause());
        }
    }

    /**
     *  Remove a directive of a Virtual Host block, if the specified directive is present
     * @param vhAddress    address of the virtual host
     * @param vhServerName value of the ServerName directive
     * @param directive the directive to remove
     * @throws VhostManagerException
     */
    private void removeDirectiveIfPossible(String vhAddress, String vhServerName, String directive)
            throws VhostManagerException {
        logger.debug("removeDirectiveIfPossible (" + vhAddress + "," + vhServerName + "," + directive + ")");

        try {
            long vhostID = apacheUtilService.getVhostID(vhAddress, vhServerName);
            removeDirectiveIfPossible(vhostID, directive);
        } catch (ApacheManagerException e) {
            throw new VhostManagerException(e.getMessage(), e.getCause());
        }
    }

    /**
     *  Add a directive at the beginning of a Virtual Host block, if the specified directive is not already present
     * @param vhostID ID of the virtual host
     * @param directive the directive to add
     * @param directiveArg argument(s) of the directive
     * @throws VhostManagerException
     */
    private void addDirectiveIfPossible(long vhostID, String directive, String directiveArg)
            throws VhostManagerException {
        logger.debug("addDirectiveIfPossible (" + String.valueOf(vhostID) + "," + directive + "," + directiveArg  +")");

        try {
            String vhostFile = apacheUtilService.getVhostConfigurationFile(vhostID);

            List<String> fileStringList = apacheUtilService.loadConfigurationFile(vhostFile);
            List<String> newFileStringList = new LinkedList<String>();

            String line = directive + " " + directiveArg;
            boolean found = false;
            Iterator<String> iterator;
            for (iterator = fileStringList.iterator(); iterator.hasNext();) {
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

        } catch (ApacheManagerException e) {
            throw new VhostManagerException(e.getMessage(), e.getCause());
        }
    }

    /**
     *  Remove a directive of a Virtual Host block, if the specified directive is present
     * @param vhostID ID of the virtual host
     * @param directive the directive to remove
     * @throws VhostManagerException
     */
    private void removeDirectiveIfPossible(long vhostID, String directive)
            throws VhostManagerException {
        logger.debug("removeDirectiveIfPossible (" + String.valueOf(vhostID) + "," + directive + ")");

        try {
            String vhostFile = apacheUtilService.getVhostConfigurationFile(vhostID);
            List<String> fileStringList = apacheUtilService.loadConfigurationFile(vhostFile);
            List<String> newFileStringList = new LinkedList<String>();

            boolean found = false;
            Iterator<String> iterator;
            for (iterator = fileStringList.iterator(); iterator.hasNext();) {
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
        } catch (ApacheManagerException e) {
            throw new VhostManagerException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Get an ID which can be used for a new Virtual Host
     * @return an ID
     */
    private synchronized long getAvailableID() {
        String [] fileList = new File(apacheUtilService.getVhostConfigurationFolder()).list();
        Pattern p = Pattern.compile(apacheUtilService.getVhostFileTemplate());
        long maxID = 0;
        for (String fileName : fileList) {
            Matcher m = p.matcher(fileName);
            if (m.matches()) {
                long currentID;
                currentID = Long.parseLong(m.group(1));
                if (currentID > maxID) {
                    maxID = currentID;
                }
            }
        }
        return maxID + 1;
    }

    /**
     * Return the path of a vhost file
     * @param vhostID ID of the virtual host
     * @return vhost file path
     */
    private String getVhostFileFromID(long vhostID) throws VhostManagerException {
            String fileName = "vh-" + String.valueOf(vhostID) + ".conf";
            return vhostConfigurationFolder + "/" +  fileName;
    }

    /**
     * Get the Virtual Host list
     * @return  the Virtual Host list
     * @throws VhostManagerException
     */
    public VhostList getVhostList() throws VhostManagerException {
        String [] fileList = apacheUtilService.getVhostFileNameList();
        Pattern idPattern = Pattern.compile(apacheUtilService.getVhostFileTemplate());
        Pattern addressPattern = Pattern.compile("<VirtualHost (.*?)>");
        Pattern serverNamePattern = Pattern.compile("ServerName (.*?)\n");
        List<Vhost> vhostList = new LinkedList<Vhost>();
        for (String fileName : fileList) {
            String content = null;
            try {
                content = apacheUtilService.fileToString(vhostConfigurationFolder + "/" + fileName);
            } catch (ApacheManagerException e) {
                throw new VhostManagerException(e.getMessage(), e.getCause());
            }
            Matcher matcher;
            Long vhostID;
            String address;
            String serverName;
            //get Virtual Host ID
            matcher = idPattern.matcher(fileName);
            matcher.find();
            vhostID = Long.parseLong(matcher.group(1));

            //get Virtual Host Address
            matcher = addressPattern.matcher(content);
            matcher.find();
            address = matcher.group(1);

            //get Virtual Host Address
            matcher = serverNamePattern.matcher(content);
            try {
                matcher.find();
                serverName = matcher.group(1);
            } catch (IllegalStateException e) {
                serverName = null;
            }
            vhostList.add(new Vhost(vhostID, address, serverName));
        }

        return new VhostList(vhostList);
    }

    /**
     * Return the content of a Virtual Host configuration file
     * @param vhostID ID of the virtual host
     * @return vhost file content
     * @throws VhostManagerException
     */
    public String getVhostContent(long vhostID) throws VhostManagerException {
        try {
            if (apacheUtilService.isVhostExist(vhostID)) {
                String vhostFile = getVhostFileFromID(vhostID);
                return apacheUtilService.fileToString(vhostFile);
            } else {
                throw new VhostManagerException("The Virtual Host ID=" + vhostID + " does not exist");
            }
        } catch (ApacheManagerException e) {
            throw new VhostManagerException(e.getMessage(), e.getCause());
        }
    }

}
