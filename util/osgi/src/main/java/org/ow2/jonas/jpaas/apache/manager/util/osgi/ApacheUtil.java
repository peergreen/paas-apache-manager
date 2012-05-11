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

package org.ow2.jonas.jpaas.apache.manager.util.osgi;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheManagerException;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheUtilService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.ow2.jonas.lib.bootstrap.JProp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The OSGi implementation of the ApacheUtilService Interface
 *
 * @author David Richard
 */
@Component(name="ApacheUtil", immediate = true)
@Provides
@Instantiate
public class ApacheUtil implements ApacheUtilService {
    /**
     * The logger.
     */
    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Property name to define apache configuration file path
     */
    private static final String APACHE_CONF_FILE_LOCATION_PROPERTY = "apache.conf.location";

    /**
     * Path to the apache configuration file
     */
    private String apacheConfigurationFile;

    /**
     * Property name to define virtual host configuration folder path
     */
    private static final String VHOST_CONF_FOLDER_LOCATION_PROPERTY = "vhost.conf.folder.location";

    /**
     * Path to the virtual host configuration folder
     */
    private String vhostConfigurationFolder;


    /**
     * VhostManager property file name
     */
    private static final String APACHE_MANAGER_PROPERTY_FILE_NAME = "apachemanager.properties";

    /**
     * Virtual Host file name template
     */
    private static final String VHOST_FILE_TEMPLATE = "vh-(\\d*).conf";


    @Validate
    public void start() throws Throwable {
        logger.debug("Starting ApacheUtil Service.");
        logger.info("Load default configuration");
        String apacheConfigurationFileLocation = getApacheConfigurationFileLocation();
        this.setApacheConfigurationFile(apacheConfigurationFileLocation);
        logger.info("ApacheConfigurationFile=" + apacheConfigurationFile);
        String vhostConfigurationFolderLocation = getVhostConfigurationFolderLocation();
        this.setVhostConfigurationFolder(vhostConfigurationFolderLocation);
        logger.info("VhostConfigurationFolder=" + vhostConfigurationFolder);

    }

    @Invalidate
    public void stop() throws Throwable {
        logger.debug("Stopping ApacheUtil.");
    }


    public String getApacheConfigurationFile() {
        return apacheConfigurationFile;
    }

    public void setApacheConfigurationFile(String apacheConfigurationFile) {
        this.apacheConfigurationFile = apacheConfigurationFile;
    }

    public String getVhostConfigurationFolder() {
        return vhostConfigurationFolder;
    }

    public void setVhostConfigurationFolder(String vhostConfigurationFolder) {
        this.vhostConfigurationFolder = vhostConfigurationFolder;
    }


    /**
     * Get the property apache configuration file location located in JONAS_BASE/conf/apachemanager.properties
     *
     * @return the location of the apache configuration file
     */
    private String getApacheConfigurationFileLocation() {
        JProp prop = JProp.getInstance(APACHE_MANAGER_PROPERTY_FILE_NAME);
        return prop.getValue(APACHE_CONF_FILE_LOCATION_PROPERTY);
    }

    /**
     * Get the property vhost configuration folder location located in JONAS_BASE/conf/apachemanager.properties
     *
     * @return the location of the vhost configuration folder
     */
    private String getVhostConfigurationFolderLocation() {
        JProp prop = JProp.getInstance(APACHE_MANAGER_PROPERTY_FILE_NAME);
        return prop.getValue(VHOST_CONF_FOLDER_LOCATION_PROPERTY);
    }


    /**
     * Load the configuration file
     * @param filePath path of the configuration file
     * @return a list with the configuration lines
     */
    public List<String> loadConfigurationFile(String filePath) {

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
     * Write the configuration in the file
     * @param filePath path of the file
     * @param fileStringList the configuration
     */
    public void flushConfigurationFile(String filePath,List<String> fileStringList) {

        try {
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
                String string = iterator.next();
                logger.debug("flush : " + string);
                pw.println(string);
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test if a virtual host exists
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @return
     */
    public boolean isVhostExist(String vhAddress, String vhServerName) {
        boolean result = false;
        String[] fileList = new File(vhostConfigurationFolder).list(new VhostFilter(VHOST_FILE_TEMPLATE));

        for (String fileName : fileList) {
            List<String> fileContentList = loadConfigurationFile(vhostConfigurationFolder + "/" + fileName);
            String fileContentString = fileContentList.toString();
            if (vhServerName == null) {
                if (fileContentString.contains("<VirtualHost " + vhAddress + ">")) {
                    result=true;
                    break;
                }
            } else {
                if (fileContentString.contains("<VirtualHost " + vhAddress + ">") &&
                        fileContentString.contains("ServerName " + vhServerName)) {
                    result=true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Return the path of a Virtual Host configuration file
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @return path of the Virtual Host configuration file
     */
    public String getVhostConfigurationFile(String vhAddress, String vhServerName) throws ApacheManagerException {
        String vhostFile = null;
        boolean vhostFound = false;
        String[] fileList = new File(vhostConfigurationFolder).list(new VhostFilter(VHOST_FILE_TEMPLATE));

        for (String fileName : fileList) {
            List<String> fileContentList = loadConfigurationFile(vhostConfigurationFolder + "/" + fileName);
            String fileContentString = fileContentList.toString();
            if (vhServerName == null) {
                if (fileContentString.contains("<VirtualHost " + vhAddress + ">")) {
                    vhostFound = true;
                    vhostFile = vhostConfigurationFolder + "/" + fileName;
                    break;
                }
            } else {
                if (fileContentString.contains("<VirtualHost " + vhAddress + ">") &&
                        fileContentString.contains("ServerName " + vhServerName)) {
                    vhostFound = true;
                    vhostFile = vhostConfigurationFolder + "/" + fileName;
                    break;
                }
            }
        }
        if (!vhostFound) {
            throw new ApacheManagerException("The Virtual Host " + vhAddress + " (ServerName = " + vhServerName
                    + ") does not exist");
        } else {
            return vhostFile;
        }
    }



    /**
     *  Add a directive at the end of a Virtual Host block.
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @param directive the directive to add
     * @param directiveArg argument(s) of the directive
     */
    public void addDirectiveInVhost(String vhAddress, String vhServerName, String directive, String directiveArg) throws ApacheManagerException {
        logger.debug("addDirectiveInVhost (" + vhAddress + "," + vhServerName + "," + directive + "," + directiveArg
                + ")");

            String vhostFile = getVhostConfigurationFile(vhAddress, vhServerName);
            List<String> fileStringList = loadConfigurationFile(vhostFile);
            List<String> newFileStringList = new LinkedList<String>();
            String line = directive + " " + directiveArg;
            String vhostEnd = "</VirtualHost>";

            for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
                String string = iterator.next();
                if (string.contains(vhostEnd)) {
                    newFileStringList.add(line);
                }
                newFileStringList.add(string);

            }

            flushConfigurationFile(vhostFile, newFileStringList);
    }

    /**
     *  Remove a directive of a Virtual Host block.  if the specified directive is present
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @param directive the directive to remove
     */
    public void removeDirectiveInVhostIfPossible(String vhAddress, String vhServerName, String directive, String directiveArg)
            throws ApacheManagerException {
        logger.debug("removeDirectiveInVhostIfPossible (" + vhAddress + "," + vhServerName + "," + directive + ","
                + directiveArg + ")");

            String vhostFile = getVhostConfigurationFile(vhAddress, vhServerName);
            List<String> fileStringList = loadConfigurationFile(vhostFile);
            List<String> newFileStringList = new LinkedList<String>();

            String line = directive + " " + directiveArg;
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
                throw new ApacheManagerException("Cannot remove the directive : there is no directive \"" + directive
                        + " " + directiveArg + "\" in the Virtual Host configuration file (" + vhostFile + ").");
            }

            flushConfigurationFile(vhostFile, newFileStringList);
    }


    /**
     * @return the vhost file template
     */
    public String getVhostFileTemplate() {
        return VHOST_FILE_TEMPLATE;
    }



}
