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
     * @param vhNameServer value of the NameServer directive (null for a virtual host without NameServer directive)
     * @return
     */
    public boolean isVhostExist(String vhAddress, String vhNameServer) {
        String vhostFile = getVhostConfigurationFile(vhAddress, vhNameServer);
        File f = new File(vhostFile);
        return f.exists();
    }

    /**
     * Return the path of a Virtual Host configuration file
     * @param vhAddress address of the virtual host
     * @param vhNameServer value of the NameServer directive (null for a virtual host without NameServer directive)
     * @return path of the Virtual Host configuration file
     */
    public String getVhostConfigurationFile(String vhAddress, String vhNameServer) {
        String vhostFile;
        if (vhNameServer == null) {
            logger.debug("getVhostConfigurationFile (" + vhAddress + ", null)");
            vhostFile = vhostConfigurationFolder + File.separator + "vh-" + vhAddress + ".conf";
        } else {
            logger.debug("getVhostConfigurationFile (" + vhAddress + "," + vhNameServer + ")");
            vhostFile = vhostConfigurationFolder + File.separator + "vh-" + vhAddress + "-" + vhNameServer + ".conf";
        }
        //Replace "*" and ":" in the file name to avoid problem with Apache
        vhostFile = vhostFile.replaceAll("\\*", "wildcard").replaceAll(":", "_");
        return vhostFile;
    }

}
