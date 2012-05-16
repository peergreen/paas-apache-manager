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

package org.ow2.jonas.jpaas.apache.manager.util.api;

import java.util.List;

/**
 * The interface describing the ApacheUtilService which provides services for Apache management modules
 *
 * @author David Richard
 */
public interface ApacheUtilService {

    /**
     * Load the configuration file
     * @param filePath path of the configuration file
     * @return a list with the configuration lines
     */
    public List<String> loadConfigurationFile(String filePath);

    /**
     * Write the configuration in the file
     * @param filePath path of the file
     * @param fileStringList the configuration
     */
    public void flushConfigurationFile(String filePath, List<String> fileStringList);

    /**
     * Test if a virtual host exists
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @return true if the Virtual Host exists
     * @throws ApacheManagerException
     */
    public boolean isVhostExist(String vhAddress, String vhServerName) throws ApacheManagerException;

    /**
     * Test if a virtual host exists
     * @param vhostID ID of the virtual host
     * @return true if the Virtual Host exists
     */
    public boolean isVhostExist(long vhostID);

    /**
     * Return the path of a Virtual Host configuration file
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @return path of the Virtual Host configuration file
     * @throws ApacheManagerException
     */
    public String getVhostConfigurationFile(String vhAddress, String vhServerName) throws ApacheManagerException;

    /**
     * Return the path of a Virtual Host configuration file
     * @param vhostID ID of the virtual host
     * @return path of the Virtual Host configuration file
     * @throws ApacheManagerException
     */
    public String getVhostConfigurationFile(long vhostID) throws ApacheManagerException;

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
            throws ApacheManagerException;

    /**
     *  Add a directive at the end of a Virtual Host block.
     * @param vhostID ID of the virtual host
     * @param directive the directive to add
     * @param directiveArg argument(s) of the directive
     * @return the directive ID
     * @throws ApacheManagerException
     */
    public long addDirectiveInVhost(long vhostID, String directive, String directiveArg) throws ApacheManagerException;

    /**
     *  Remove a directive of a Virtual Host block.  if the specified directive is present
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @param directiveID the ID of the directive to remove
     * @throws ApacheManagerException
     */
    public void removeDirectiveInVhostIfPossible(String vhAddress, String vhServerName, long directiveID)
            throws ApacheManagerException;

    /**
     *  Remove a directive of a Virtual Host block.  if the specified directive is present
     * @param vhostID ID of the virtual host
     * @param directiveID the ID of the directive to remove
     * @throws ApacheManagerException
     */
    public void removeDirectiveInVhostIfPossible(long vhostID, long directiveID)
            throws ApacheManagerException;

    /**
     * Get a Virtual Host ID from its address and ServerName
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive (null for a virtual host without ServerName directive)
     * @return the Virtual host ID
     * @throws ApacheManagerException
     */
    public long getVhostID(String vhAddress, String vhServerName) throws ApacheManagerException;


    /**
     *
     * @return the path of the vhost folder
     */
    public String getVhostConfigurationFolder();


    /**
     *
     * @return the path of the apache configuration file
     */
    public String getApacheConfigurationFile();


    /**
     *
     * @return the vhost file template
     */
    public String getVhostFileTemplate();

    /**
     * Get a list of Virtual Host files name.
     */
    public String[] getVhostFileNameList();

    /**
     * Get the content of a file in a String
     * @param file path to the file to read
     * @return A String representation of the file
     * @throws ApacheManagerException
     */
    public String fileToString(String file) throws ApacheManagerException;


    /**
     *  Add a directive in a specific configuration file.
     * @param file path of the file
     * @param directive the directive to add
     * @param directiveArg argument(s) of the directive
     * @return the directive ID
     * @throws ApacheManagerException
     */
    public long addDirectiveInFile(String file, String directive, String directiveArg) throws ApacheManagerException;

    /**
     *  Remove a directive in a specific configuration file.  if the specified directive is present
     * @param file path of the file
     * @param directiveID the ID of the directive to remove
     * @throws ApacheManagerException
     */
    public void removeDirectiveInFile(String file, long directiveID) throws ApacheManagerException;

}
