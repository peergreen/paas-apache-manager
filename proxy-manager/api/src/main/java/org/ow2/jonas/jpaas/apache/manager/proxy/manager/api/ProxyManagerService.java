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

package org.ow2.jonas.jpaas.apache.manager.proxy.manager.api;

/**
 * The interface describing the ProxyManagerService to manage proxy module on an Apache server
 *
 * @author David Richard
 */
public interface ProxyManagerService {

    /**
     * Create a ProxyPass directive
     * @param firstArg value of the ProxyPass first argument (path)
     * @param secondArg  value of the ProxyPass second argument (url)
     * @throws ProxyManagerException
     */
    public void createProxyPass(String firstArg, String secondArg) throws ProxyManagerException;

    /**
     * Create a ProxyPass directive in a Virtual Host
     * @param firstArg value of the ProxyPass first argument (path)
     * @param secondArg  value of the ProxyPass second argument (url)
     * @param vhAddress address of the virtual host
     * @throws ProxyManagerException
     */
    public void createProxyPass(String firstArg, String secondArg, String vhAddress) throws ProxyManagerException;

    /**
     * Create a ProxyPass directive in a Name-based Virtual Host
     * @param firstArg value of the ProxyPass first argument (path)
     * @param secondArg  value of the ProxyPass second argument (url)
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive
     * @throws ProxyManagerException
     */
    public void createProxyPass(String firstArg, String secondArg, String vhAddress, String vhServerName)
            throws ProxyManagerException;


    /**
     * Delete a ProxyPass directive
     *
     * @param firstArg value of the ProxyPass first argument (path)
     * @param secondArg  value of the ProxyPass second argument (url)
     * @throws ProxyManagerException
     */
    public void deleteProxyPass(String firstArg, String secondArg) throws ProxyManagerException;

    /**
     * Delete a ProxyPass directive in a Virtual Host
     *
     * @param firstArg value of the ProxyPass first argument (path)
     * @param secondArg  value of the ProxyPass second argument (url)
     * @param vhAddress address of the virtual host
     * @throws ProxyManagerException
     */
    public void deleteProxyPass(String firstArg, String secondArg, String vhAddress) throws ProxyManagerException;

    /**
     * Delete a ProxyPass directive in a Name-based Virtual Host
     *
     * @param firstArg value of the ProxyPass first argument (path)
     * @param secondArg  value of the ProxyPass second argument (url)
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive
     * @throws ProxyManagerException
     */
    public void deleteProxyPass(String firstArg, String secondArg, String vhAddress, String vhServerName)
            throws ProxyManagerException;

}
