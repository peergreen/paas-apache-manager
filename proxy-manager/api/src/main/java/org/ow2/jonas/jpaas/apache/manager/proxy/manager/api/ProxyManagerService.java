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
     * @param path value of the ProxyPass first argument (path)
     * @param url  value of the ProxyPass second argument (url)
     * @throws ProxyManagerException
     */
    public long createProxyPass(String path, String url) throws ProxyManagerException;

    /**
     * Create a ProxyPass directive in a Virtual Host
     *
     * @param vhAddress address of the virtual host
     * @param path value of the ProxyPass first argument (path)
     * @param url  value of the ProxyPass second argument (url)
     * @return the directive ID
     * @throws ProxyManagerException
     */
    public long createVhostProxyPass(String vhAddress, String path, String url) throws ProxyManagerException;

    /**
     * Create a ProxyPass directive in a Name-based Virtual Host
     *
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive
     * @param path value of the ProxyPass first argument (path)
     * @param url  value of the ProxyPass second argument (url)
     * @return the directive ID
     * @throws ProxyManagerException
     */
    public long createVhostProxyPass(String vhAddress, String vhServerName, String path, String url)
            throws ProxyManagerException;

    /**
     * Create a ProxyPass directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param path value of the ProxyPass first argument (path)
     * @param url  value of the ProxyPass second argument (url)
     * @return the directive ID
     * @throws ProxyManagerException
     */
    public long createVhostProxyPass(long vhostID, String path, String url) throws ProxyManagerException;


    /**
     * Delete a ProxyPass directive
     *
     * @param directiveID the ID of the directive to remove
     * @throws ProxyManagerException
     */
    public void deleteProxyPass(long directiveID) throws ProxyManagerException;

    /**
     * Delete a ProxyPass directive in a Virtual Host
     *
     * @param vhAddress address of the virtual host
     * @param directiveID the ID of the directive to remove
     * @throws ProxyManagerException
     */
    public void deleteVhostProxyPass(String vhAddress, long directiveID) throws ProxyManagerException;

    /**
     * Delete a ProxyPass directive in a Name-based Virtual Host
     *
     * @param vhAddress address of the virtual host
     * @param vhServerName value of the ServerName directive
     * @param directiveID the ID of the directive to remove
     * @throws ProxyManagerException
     */
    public void deleteVhostProxyPass(String vhAddress, String vhServerName, long directiveID)
            throws ProxyManagerException;

    /**
     * Delete a ProxyPass directive in a Virtual Host
     *
     * @param vhostID ID of the virtual host
     * @param directiveID the ID of the directive to remove
     * @throws ProxyManagerException
     */
    public void deleteVhostProxyPass(long vhostID, long directiveID) throws ProxyManagerException;



}
