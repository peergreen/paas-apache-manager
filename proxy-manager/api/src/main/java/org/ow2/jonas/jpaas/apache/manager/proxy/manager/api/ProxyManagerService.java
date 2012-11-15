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
