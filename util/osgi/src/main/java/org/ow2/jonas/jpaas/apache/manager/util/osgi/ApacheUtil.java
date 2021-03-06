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

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
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
     * Apache Manager property file name
     */
    private static final String APACHE_MANAGER_PROPERTY_FILE_NAME = "apachemanager.properties";

    /**
     * Virtual Host file name template
     */
    private static final String VHOST_FILE_TEMPLATE = "vh-(\\d*)\\.conf";

    /**
     * Directive ID template
     */
    private static final String DIRECTIVE_ID_TEMPLATE = "#id=(\\d*)";

    /**
     * Property name to define the apache cmd
     */
    private static final String CMD_APACHE_PROPERTY = "apache.cmd.name";

    /**
     * Property name to define the reload arg
     */
    private static final String CMD_RELOAD_ARG_PROPERTY = "apache.cmd.reload.arg";

    /**
     * Property name to define the start arg
     */
    private static final String CMD_START_ARG_PROPERTY = "apache.cmd.start.arg";

    /**
     * Property name to define the stop arg
     */
    private static final String CMD_STOP_ARG_PROPERTY = "apache.cmd.stop.arg";


    @Validate
    public void start() throws Throwable {
        logger.debug("Starting ApacheUtil Service.");
        logger.info("Load default configuration");
        String apacheConfigurationFileLocation = getPropertyValue(APACHE_CONF_FILE_LOCATION_PROPERTY);
        this.setApacheConfigurationFile(apacheConfigurationFileLocation);
        logger.info("ApacheConfigurationFile=" + apacheConfigurationFile);
        String vhostConfigurationFolderLocation = getPropertyValue(VHOST_CONF_FOLDER_LOCATION_PROPERTY);
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
     * Get a property value in Apache Manager properties file
     *
     * @param  propertyName the name of the property
     * @return the property value
     */
    public String getPropertyValue(String propertyName) {
    	//FIXME: replaced JProp by this but we should keep the data instead of reloading the resource each time
    	try {
    	    Properties props = new Properties();
            props.load(ApacheUtil.class.getClassLoader().getResourceAsStream(APACHE_MANAGER_PROPERTY_FILE_NAME));
    	    return props.getProperty(propertyName);
    	} catch (IOException e) {
    		logger.error("Unable to load a property", e);
    		return null;
    	}
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

            for (String string : fileStringList) {
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
     * @return true if the Virtual Host exists
     * @throws ApacheManagerException
     */
    public boolean isVhostExist(String vhAddress, String vhServerName) throws ApacheManagerException {
        try {
            getVhostID(vhAddress, vhServerName);
        } catch (ApacheManagerException e) {
            return false;
        }
        return true;
    }

    /**
     * Test if a virtual host exists
     * @param vhostID ID of the virtual host
     * @return true if the Virtual Host exists
     */
    public boolean isVhostExist(long vhostID) {
        String vhostFileName = "vh-" + String.valueOf(vhostID) + ".conf";
        String vhostFilePath = vhostConfigurationFolder +  "/" + vhostFileName;
        File file = new File(vhostFilePath);
        return file.exists();
    }

    /**
     * Return the path of a Virtual Host configuration file
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @return path of the Virtual Host configuration file
     * @throws ApacheManagerException
     */
    public String getVhostConfigurationFile(String vhAddress, String vhServerName) throws ApacheManagerException {
        long vhostID = getVhostID(vhAddress, vhServerName);
        return getVhostConfigurationFile(vhostID);
    }

    /**
     * Return the path of a Virtual Host configuration file
     * @param vhostID ID of the virtual host
     * @return path of the Virtual Host configuration file
     * @throws ApacheManagerException
     */
    public String getVhostConfigurationFile(long vhostID) throws ApacheManagerException {
        logger.debug("getVhostConfigurationFile (" + String.valueOf(vhostID) + ")");
        String vhostFileName = "vh-" + String.valueOf(vhostID) + ".conf";
        String vhostFilePath = vhostConfigurationFolder +  "/" + vhostFileName;
        if (!isVhostExist(vhostID)) {
            throw new ApacheManagerException("The Virtual Host ID=" + String.valueOf(vhostID) + " does not exist");
        } else {
            return vhostFilePath;
        }
    }



    /**
     *  Add a directive at the end of a Virtual Host block.
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @param directive the directive to add
     * @param directiveArg argument(s) of the directive
     * @return the directive ID
     * @throws ApacheManagerException
     */
    public long addDirectiveInVhost(String vhAddress, String vhServerName, String directive, String directiveArg)
            throws ApacheManagerException {
        logger.debug("addDirectiveInVhost (" + vhAddress + "," + vhServerName + "," + directive + "," + directiveArg
                + ")");

        long vhostID = getVhostID(vhAddress, vhServerName);
        return addDirectiveInVhost(vhostID, directive, directiveArg);
    }

    /**
     *  Add a directive at the end of a Virtual Host block.
     * @param vhostID ID of the virtual host
     * @param directive the directive to add
     * @param directiveArg argument(s) of the directive
     * @return the directive ID
     * @throws ApacheManagerException
     */
    public long addDirectiveInVhost(long vhostID, String directive, String directiveArg) throws ApacheManagerException {
        logger.debug("addDirectiveInVhost (" + vhostID + "," + directive + "," + directiveArg + ")");

        String vhostFile = getVhostConfigurationFile(vhostID);
        List<String> fileStringList = loadConfigurationFile(vhostFile);
        List<String> newFileStringList = new LinkedList<String>();
        String line = directive + " " + directiveArg;
        String vhostEnd = "</VirtualHost>";
        long directiveID = getAvailableDirectiveID(vhostFile);
        for (String string : fileStringList) {
            if (string.contains(vhostEnd)) {
                newFileStringList.add(getDirectiveBeginLine(directiveID));
                newFileStringList.add(line);
                newFileStringList.add(getDirectiveEndLine(directiveID));
            }
            newFileStringList.add(string);

        }
        flushConfigurationFile(vhostFile, newFileStringList);
        return directiveID;
    }

    /**
     *  Remove a directive of a Virtual Host block.  if the specified directive is present
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @param directiveID the ID of the directive to remove
     * @throws ApacheManagerException
     */
    public void removeDirectiveInVhostIfPossible(String vhAddress, String vhServerName, long directiveID)
            throws ApacheManagerException {
        logger.debug("removeDirectiveInVhostIfPossible (" + vhAddress + "," + vhServerName + "," + directiveID + ")");

        long vhostID = getVhostID(vhAddress, vhServerName);
        removeDirectiveInVhostIfPossible(vhostID, directiveID);
    }


    /**
     *  Remove a directive of a Virtual Host block.  if the specified directive is present
     * @param vhostID ID of the virtual host
     * @param directiveID the ID of the directive to remove
     * @throws ApacheManagerException
     */
    public void removeDirectiveInVhostIfPossible(long vhostID, long directiveID)
            throws ApacheManagerException {
        logger.debug("removeDirectiveInVhostIfPossible (" + vhostID + "," + directiveID + ")");

        String vhostFile = getVhostConfigurationFile(vhostID);
        List<String> fileStringList = loadConfigurationFile(vhostFile);
        List<String> newFileStringList = new LinkedList<String>();

        String startLine = getDirectiveBeginLine(directiveID);
        String endLine = getDirectiveEndLine(directiveID);
        boolean found = false;
        boolean inDirective = false;
        for (String string : fileStringList) {
            if (string.contains(startLine)) {
                inDirective = true;
                found = true;
            }
            if (!inDirective) {
                newFileStringList.add(string);
            }
            if (string.contains(endLine)) {
                inDirective = false;
            }
        }
        if (!found) {
            throw new ApacheManagerException("Cannot remove the directive : there is no directive \"" + directiveID
                    + "\" in the Virtual Host configuration file (" + vhostFile + ").");
        }

        flushConfigurationFile(vhostFile, newFileStringList);
    }


    /**
     * @return the vhost file template
     */
    public String getVhostFileTemplate() {
        return VHOST_FILE_TEMPLATE;
    }

    /**
     * Get a Virtual Host ID from its address and ServerName
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @return the Virtual host ID
     * @throws ApacheManagerException
     */
    public long getVhostID(String vhAddress, String vhServerName) throws ApacheManagerException {
        long vhostID = 0;
        boolean vhostFound = false;
        String[] fileList = new File(vhostConfigurationFolder).list(new VhostFilter(VHOST_FILE_TEMPLATE));
        Pattern pattern = Pattern.compile(VHOST_FILE_TEMPLATE);
        Matcher matcher;
        for (String fileName : fileList) {
            List<String> fileContentList = loadConfigurationFile(vhostConfigurationFolder + "/" + fileName);
            String fileContentString = fileContentList.toString();
            if (vhServerName == null) {
                if (fileContentString.contains("<VirtualHost " + vhAddress + ">")) {
                    vhostFound = true;
                    matcher = pattern.matcher(fileName);
                    matcher.find();
                    vhostID = Long.parseLong(matcher.group(1));

                    break;
                }
            } else {
                if (fileContentString.contains("<VirtualHost " + vhAddress + ">") &&
                        fileContentString.contains("ServerName " + vhServerName)) {
                    vhostFound = true;
                    matcher = pattern.matcher(fileName);
                    matcher.find();
                    vhostID = Long.parseLong(matcher.group(1));
                    break;
                }
            }
        }
        if (!vhostFound) {
            throw new ApacheManagerException("The Virtual Host " + vhAddress + " (ServerName = " + vhServerName
                    + ") does not exist");
        } else {
            return vhostID;
        }
    }

    /**
     * Get an ID which can be used for a new Directive for a specific file
     * @param file path of the file
     * @return an ID
     * @throws ApacheManagerException
     */
    private synchronized long getAvailableDirectiveID(String file) throws ApacheManagerException {
        try {
            long maxID = 0;
            String content = fileToString(file);
            Pattern pattern = Pattern.compile(DIRECTIVE_ID_TEMPLATE);
            Matcher matcher = pattern.matcher(content);
            long currentID;
            while (matcher.find()) {
                currentID = Long.parseLong(matcher.group(1));
                if (currentID > maxID) {
                    maxID = currentID;
                }
            }
            return maxID + 1;
        } catch (IllegalStateException e) {
            //if no match found
            return 1L;
        } catch (ApacheManagerException e) {
            throw new ApacheManagerException("Cannot get an available ID", e);
        }
    }

    /**
     *
     * @param id The ID of the directive
     * @return the ID line before a directive
     */
    private String getDirectiveBeginLine(long id) {
        return "#id=" + String.valueOf(id);
    }

    /**
     *
     * @param id The ID of the directive
     * @return the ID line after a directive
     */
    private String getDirectiveEndLine(long id) {
        return "#/id=" + String.valueOf(id);
    }


    /**
     * Get a list of Virtual Host files name.
     */
    public String[] getVhostFileNameList() {
        return new File(vhostConfigurationFolder).list(new VhostFilter(VHOST_FILE_TEMPLATE));
    }


    /**
     * Get the content of a file in a String
     * @param file path to the file to read
     * @return A String representation of the file
     * @throws ApacheManagerException
     */
    public String fileToString(String file) throws ApacheManagerException {
        String result = null;
        DataInputStream in = null;

        try {
            File f = new File(file);
            byte[] buffer = new byte[(int) f.length()];
            in = new DataInputStream(new FileInputStream(f));
            in.readFully(buffer);
            result = new String(buffer);
        } catch (IOException e) {
            throw new ApacheManagerException("Problem to read file " + file, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) { /* ignore it */
            }
        }
        return result;
    }

    /**
     *  Add a directive in a specific configuration file.
     * @param file path of the file
     * @param directive the directive to add
     * @param directiveArg argument(s) of the directive
     * @return the directive ID
     * @throws ApacheManagerException
     */
    public long addDirectiveInFile(String file, String directive, String directiveArg) throws ApacheManagerException {
        logger.debug("addDirectiveInFile (" + file + "," + directive + "," + directiveArg + ")");


        List<String> fileStringList = loadConfigurationFile(file);
        List<String> newFileStringList = new LinkedList<String>();
        String line = directive + " " + directiveArg;
        long directiveID = getAvailableDirectiveID(file);
        String startLine = getDirectiveBeginLine(directiveID);
        String endLine = getDirectiveEndLine(directiveID);
        boolean found = false;
        for (String string : fileStringList) {
            if (string.contains(line)) {
                found = true;
            }
            newFileStringList.add(string);
        }
        if (!found) {
            newFileStringList.add(startLine);
            newFileStringList.add(line);
            newFileStringList.add(endLine);
        } else {
            throw new ApacheManagerException("Cannot add the " + directive + " directive : a directive \""
                    + line + "\" is already present");
        }
        flushConfigurationFile(file, newFileStringList);
        return directiveID;
    }

    /**
     *  Remove a directive in a specific configuration file.  if the specified directive is present
     * @param file path of the file
     * @param directiveID the ID of the directive to remove
     * @throws ApacheManagerException
     */
    public void removeDirectiveInFile(String file, long directiveID) throws ApacheManagerException {
        logger.debug("removeDirectiveInFile (" + file + "," + directiveID + ")");


        List<String> fileStringList = loadConfigurationFile(file);
        List<String> newFileStringList = new LinkedList<String>();

        String startLine = getDirectiveBeginLine(directiveID);
        String endLine = getDirectiveEndLine(directiveID);
        boolean found = false;
        boolean inDirective = false;
        for (String string : fileStringList) {
            if (string.contains(startLine)) {
                inDirective = true;
                found = true;
            }
            if (!inDirective) {
                newFileStringList.add(string);
            }
            if (string.contains(endLine)) {
                inDirective = false;
            }
        }
        if (!found) {
            throw new ApacheManagerException("Cannot remove the directive : there is no directive \"" + directiveID
                    + "\" in the configuration file (" + file + ").");
        }

        flushConfigurationFile(file, newFileStringList);
    }

    /**
     * Get the Id of the directives which match the directive name and arguments
     *
     * @param file         path of the file
     * @param directive    the directive to add
     * @param directiveArg argument(s) of the directive
     * @return a list of Id
     * @throws org.ow2.jonas.jpaas.apache.manager.util.api.ApacheManagerException
     *
     */
    @Override
    public List<Long> getDirectivesIdInFile(String file, String directive, String directiveArg)
            throws ApacheManagerException {
        logger.debug("getDirectivesIdInFile (" + file + "," + directive + "," + directiveArg + ")");
        List<Long> idList = new LinkedList<Long>();
        try {
            String content = fileToString(file);
            String arguments = stringToRegex(directiveArg);
            arguments = arguments.replaceAll("\\s+", " ");
            Pattern pattern = Pattern.compile("#id=(\\d*)" + System.getProperty("line.separator") +
                    directive + "[ \\t]*" + arguments);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                idList.add(Long.valueOf(matcher.group(1)));
            }
            return idList;
        } catch (IllegalStateException e) {
            //if no match found
            logger.debug("No directive found");
            return idList;
        } catch (ApacheManagerException e) {
            throw new ApacheManagerException(e.getMessage(), e);
        }
    }

    /**
     * Escape special characters of a string to work in a Regex
     * @param s the string to transform
     * @return the regex
     */
    public String stringToRegex(String s) {
        StringBuilder b = new StringBuilder();
        for(int i=0; i<s.length(); ++i) {
            char ch = s.charAt(i);
            if ("\\.^$|?*+[]{}()".indexOf(ch) != -1)
                b.append('\\').append(ch);
            else
                b.append(ch);
        }
        return b.toString();
    }

    /**
     * @return Get the reload command
     */
    private String getApacheReloadCmd() {
        String apacheCmd =  getPropertyValue(CMD_APACHE_PROPERTY);
        String reloadArg = getPropertyValue(CMD_RELOAD_ARG_PROPERTY);
        return apacheCmd + " " + reloadArg;
    }

    /**
     * @return Get the start command
     */
    private String getApacheStartCmd() {
        String apacheCmd =  getPropertyValue(CMD_APACHE_PROPERTY);
        String startArg = getPropertyValue(CMD_START_ARG_PROPERTY);
        return apacheCmd + " " + startArg;
    }

    /**
     * @return Get the stop command
     */
    private String getApacheStopCmd() {
        String apacheCmd =  getPropertyValue(CMD_APACHE_PROPERTY);
        String stopArg = getPropertyValue(CMD_STOP_ARG_PROPERTY);
        return apacheCmd + " " + stopArg;
    }

    /**
     * reload apache2 configuration
     */
    public void reloadApache() throws ApacheManagerException {

        String command = getApacheReloadCmd();

        try {
            logger.debug("Execute command {0}", command);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new ApacheManagerException("Cannot reload Apache HTTP configuration : {0}", e);
        }
    }

    /**
     * start apache2
     */
    public void startApache() throws ApacheManagerException {

        String command = getApacheStartCmd();

        try {
            logger.debug("Execute command {0}", command);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new ApacheManagerException("Cannot start Apache HTTP : {0}", e);
        }
    }

    /**
     * stop apache2
     */
    public void stopApache() throws ApacheManagerException {

        String command = getApacheStopCmd();

        try {
            logger.debug("Execute command {0}", command);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new ApacheManagerException("Cannot stop Apache HTTP : {0}", e);
        }
    }
}
