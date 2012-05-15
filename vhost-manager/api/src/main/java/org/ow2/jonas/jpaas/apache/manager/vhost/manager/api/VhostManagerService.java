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

package org.ow2.jonas.jpaas.apache.manager.vhost.manager.api;

import org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.xml.VhostList;

import java.util.List;

/**
 * The interface describing the VhostManagerService to manage Virtual Host on an Apache server
 *
 * @author David Richard
 */
public interface VhostManagerService {


    /**
     * Create a Virtual Host block directive
     *
     * @param address address of the virtual host
     * @throws VhostManagerException
     * @return the Virtual Host ID
     */
    long createVirtualHost(String address) throws VhostManagerException;

    /**
     * Delete a Virtual Host block directive
     *
     * @param address address of the virtual host
     * @throws VhostManagerException
     */
    void deleteVirtualHost(String address) throws VhostManagerException;

    /**
     * Create a Name-based Virtual Host block directive
     *
     * @param address address of the virtual host
     * @param serverName value of the ServerName directive
     * @throws VhostManagerException
     * @return the Virtual Host ID
     */
    long createVirtualHost(String address, String serverName) throws VhostManagerException;

    /**
     * Delete a Name-based Virtual Host block directive
     *
     * @param address address of the virtual host
     * @param serverName value of the ServerName directive
     * @throws VhostManagerException
     */
    void deleteVirtualHost(String address, String serverName) throws VhostManagerException;

    /**
     * Delete a Virtual Host block directive
     *
     * @param vhostID ID of the virtual host
     * @throws VhostManagerException
     */
    public void deleteVirtualHost(long vhostID) throws VhostManagerException;


    /**
     * Create a DocumentRoot directive in a Virtual Host
     *
     * @param address address of the virtual host
     * @param documentRoot value of the DocumentRoot directive to create
     * @throws VhostManagerException
     */
    void createDocumentRoot(String address, String documentRoot) throws VhostManagerException;


    /**
     * Delete a DocumentRoot directive in a Virtual Host
     *
     * @param address address of the virtual host
     * @throws VhostManagerException
     */
    void deleteDocumentRoot(String address) throws VhostManagerException;

    /**
     * Create a DocumentRoot directive in a Name-based Virtual Host
     *
     * @param address address of the virtual host
     * @param serverName value of the ServerName directive
     * @param documentRoot value of the DocumentRoot directive to create
     * @throws VhostManagerException
     */
    void createDocumentRoot(String address, String serverName, String documentRoot) throws VhostManagerException;


    /**
     * Delete a DocumentRoot directive in a Name-based Virtual Host
     *
     *
     * @param address address of the virtual host
     * @param serverName value of the ServerName directive
     * @throws VhostManagerException
     */
    void deleteDocumentRoot(String address, String serverName) throws VhostManagerException;

    /**
     * Create a DocumentRoot directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param documentRoot value of the DocumentRoot directive to create
     * @throws VhostManagerException
     */
    public void createDocumentRoot(long vhostID, String documentRoot) throws VhostManagerException;

    /**
     * Delete a DocumentRoot directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @throws VhostManagerException
     */
    public void deleteDocumentRoot(long vhostID) throws VhostManagerException;

    /**
     * Create a ServerAlias directive in a Virtual Host
     *
     *
     * @param address address of the virtual host
     * @param serverAlias argument(s) of the ServerAlias directive to create
     * @throws VhostManagerException
     */
    void createServerAlias(String address, List<String> serverAlias) throws VhostManagerException;

    /**
     * Delete a ServerAlias directive in a Virtual Host
     *
     *
     * @param address address of the virtual host
     * @throws VhostManagerException
     */
    void deleteServerAlias(String address) throws VhostManagerException;

    /**
     * Create a ServerAlias directive in a Name-based Virtual Host
     *
     *
     * @param address address of the virtual host
     * @param serverName value of the ServerName directive
     * @param serverAlias argument(s) of the ServerAlias directive to create
     * @throws VhostManagerException
     */
    void createServerAlias(String address, String serverName, List<String> serverAlias) throws VhostManagerException;

    /**
     * Delete a ServerAlias directive in a Name-based Virtual Host
     *
     *
     * @param address address of the virtual host
     * @param serverName value of the ServerName directive
     * @throws VhostManagerException
     */
    void deleteServerAlias(String address, String serverName) throws VhostManagerException;

    /**
     * Create a ServerAlias directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param serverAlias argument(s) of the ServerAlias directive to create
     * @throws VhostManagerException
     */
    public void createServerAlias(long vhostID, List<String> serverAlias) throws VhostManagerException;

    /**
     * Delete a ServerAlias directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @throws VhostManagerException
     */
    public void deleteServerAlias(long vhostID) throws VhostManagerException;

    /**
     * Create a ServerPath directive in a Virtual Host
     *
     * @param address address of the virtual host
     * @param serverPath value of the ServerPath directive to create
     * @throws VhostManagerException
     */
    void createServerPath(String address, String serverPath) throws VhostManagerException;

    /**
     * Delete a ServerPath directive in a Virtual Host
     *
     *
     * @param address address of the virtual host
     * @throws VhostManagerException
     */
    void deleteServerPath(String address) throws VhostManagerException;

    /**
     * Create a ServerPath directive in a Name-based Virtual Host
     *
     * @param address address of the virtual host
     * @param serverName value of the ServerName directive
     * @param serverPath value of the ServerPath directive to create
     * @throws VhostManagerException
     */
    void createServerPath(String address, String serverName, String serverPath) throws VhostManagerException;

    /**
     * Delete a ServerPath directive in a Name-based Virtual Host
     *
     *
     * @param address address of the virtual host
     * @param serverName value of the ServerName directive
     * @throws VhostManagerException
     */
    void deleteServerPath(String address, String serverName) throws VhostManagerException;

    /**
     * Create a ServerPath directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param serverPath value of the ServerPath directive to create
     * @throws VhostManagerException
     */
    public void createServerPath(long vhostID, String serverPath) throws VhostManagerException;

    /**
     * Delete a ServerPath directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @throws VhostManagerException
     */
    public void deleteServerPath(long vhostID) throws VhostManagerException;

    /**
     * Get the Virtual Host list
     * @return  the Virtual Host list
     * @throws VhostManagerException
     */
    public VhostList getVhostList() throws VhostManagerException;

}
