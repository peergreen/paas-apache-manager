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
     * @param vhNameServer value of the NameServer directive (null for a virtual host without NameServer directive)
     * @return
     */
    public boolean isVhostExist(String vhAddress, String vhNameServer);

    /**
     * Return the path of a Virtual Host configuration file
     * @param vhAddress address of the virtual host
     * @param vhNameServer value of the NameServer directive (null for a virtual host without NameServer directive)
     * @return path of the Virtual Host configuration file
     */
    public String getVhostConfigurationFile(String vhAddress, String vhNameServer);

    /**
     * If it is not done, write a directive in Apache configuration file
     * to include the Virtual Host configuration folder.
     */
    public void includeVhostFolderIfNecessary();

    /**
     *  Add a directive at the end of a Virtual Host block.
     * @param vhAddress address of the virtual host
     * @param vhNameServer value of the NameServer directive (null for a virtual host without NameServer directive)
     * @param directive the directive to add
     * @param directiveArg argument(s) of the directive
     */
    public void addDirectiveInVhost(String vhAddress, String vhNameServer, String directive, String directiveArg) throws ApacheManagerException;

    /**
     *  Remove a directive of a Virtual Host block.  if the specified directive is present
     * @param vhAddress address of the virtual host
     * @param vhNameServer value of the NameServer directive (null for a virtual host without NameServer directive)
     * @param directive the directive to remove
     */
    public void removeDirectiveInVhostIfPossible(String vhAddress, String vhNameServer, String directive, String directiveArg)
            throws ApacheManagerException;

}
