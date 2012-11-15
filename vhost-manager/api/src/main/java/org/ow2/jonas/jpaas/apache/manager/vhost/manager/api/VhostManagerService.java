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

    /**
     * Return the content of a Virtual Host configuration file
     * @param vhostID ID of the virtual host
     * @return vhost file content
     * @throws VhostManagerException
     */
    public String getVhostContent(long vhostID) throws VhostManagerException;

}
