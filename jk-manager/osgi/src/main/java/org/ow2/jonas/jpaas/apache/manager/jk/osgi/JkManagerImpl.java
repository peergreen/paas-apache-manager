/**
 * JASMINe
 * Copyright (C) 2010 Bull S.A.S.
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
 * $Id: JkManagerImpl.java 9171 2011-10-13 14:30:56Z richardd $
 * --------------------------------------------------------------------------
 */

package org.ow2.jonas.jpaas.apache.manager.jk.osgi;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.jk.api.JkManagerService;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheUtilService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * The implementation of the JkManagerService Interface
 *
 * @author Julien Vey
 */
@Component(propagation = true)
@Provides
public class JkManagerImpl implements JkManagerService {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(JkManagerImpl.class);

    /**
     * Property name to define the workers.properties path
     */
    private static final String WORKERS_PROPERTY_FILE_LOCATION_PROPERTY = "workers.properties.location";

    /**
     * Property name to define the jk.conf path
     */
    private static final String JK_CONF_FILE_LOCATION_PROPERTY = "jk.conf.location";

    /**
     * Path to the workers.properties file
     */
    private String workersConfigurationFile;

    /**
     * Path to the jk.conf file
     */
    private String jkConfigurationFile;

    /**
     * Reload cmd
     */
    private String reloadCmd;

    @Requires
    private ApacheUtilService apacheUtilService;


    @Validate
    public void start() {
        logger.info("Load default configuration");
        String workersConfigurationFile = apacheUtilService.getPropertyValue(WORKERS_PROPERTY_FILE_LOCATION_PROPERTY);
        this.setWorkersConfigurationFile(workersConfigurationFile);

        String jkConfFile = apacheUtilService.getPropertyValue(JK_CONF_FILE_LOCATION_PROPERTY);
        this.setJkConfigurationFile(jkConfFile);

        logger.info("workersConfigurationFile=" + workersConfigurationFile);
        logger.info("jkConfigurationFile=" + jkConfigurationFile);
    }

    /**
     * {@inheritDoc}
     */
    public void addWorker(String loadbalancer, String host, String port) {
        String workerName = "worker";
        workerName += Math.round(Math.random() * 100000) + 100;
        addNamedWorker(workerName, loadbalancer, host, port, "1");
    }

    /**
     * {@inheritDoc}
     */
    public void addWorker(String loadbalancer, String host, String port, String lbFactor) {
        String workerName = "worker";
        workerName += Math.round(Math.random() * 100000) + 100;
        addNamedWorker(workerName, loadbalancer, host, port, lbFactor);
    }

    /**
     * {@inheritDoc}
     */
    public void addNamedWorker(String name, String loadbalancer, String host, String port) {
        addNamedWorker(name, loadbalancer, host, port, "1");
    }

    /**
     * {@inheritDoc}
     */
    public void addNamedWorker(String name, String loadbalancer, String host, String port, String lbFactor) {

        logger.info("addNamedWorker (" + name + "," + loadbalancer + "," + host + "," + port + ", " + lbFactor + ")");

        String confFileLocation = getWorkersConfigurationFile();

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(confFileLocation);
        List<String> newFileStringList = new LinkedList<String>();

        boolean alreadyConfigured = false;
        // We need to know if there is already a load balancer worker
        boolean inLoadBalancer = false;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("worker." + loadbalancer + ".balance_workers=") && !string.contains(name)) {
                string += ",";
                string += name;
                inLoadBalancer = true;
            } else {
                if (string.contains("worker." + name + ".host")) {
                    alreadyConfigured = true;
                }
            }
            newFileStringList.add(string);
        }

        if (!alreadyConfigured) {
            newFileStringList.add("worker." + name + ".port=" + port);
            newFileStringList.add("worker." + name + ".host=" + host);
            newFileStringList.add("worker." + name + ".type=ajp13");
            newFileStringList.add("worker." + name + ".lbfactor=" + lbFactor);
            newFileStringList.add("worker." + name + ".activation=" + "a");
        }

        // If no load balancer worker was found, we create it
        if(!inLoadBalancer) {
            newFileStringList.add("worker." + loadbalancer + ".type=lb");
            newFileStringList.add("worker." + loadbalancer + ".balance_workers=" + name);
//            newFileStringList.add("worker.loadbalancer.sticky_session=false");
            for (int i = 0; i < newFileStringList.size(); i++) {
                String string = newFileStringList.get(i);

                // Add the new loadbalancer worker to the worker list
                if(string.contains("worker.list=")) {
                    if(string.split("=")[1].split(",").length > 0) {
                        string += ",";
                    }
                    string += loadbalancer;
                    newFileStringList.set(i, string);
                    break;
                }
            }
        }

        apacheUtilService.flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeNamedWorker(String name) {

        logger.info("removeNamedWorker (" + name + ")");

        String confFileLocation = getWorkersConfigurationFile();

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(confFileLocation);
        List<String> newFileStringList = new LinkedList<String>();

        boolean loadBalancerIsEmpty = false;
        String lbToDelete = null;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("balance_workers=")) {

                String[] balancerString = string.split("=");
                String[] balancedString = balancerString[1].split(",");
                // if there is only one balanced worker remaining, we have to delete the whole load balancer
                if(balancedString.length == 1) {
                    loadBalancerIsEmpty = true;
                    lbToDelete = balancerString[0].split("\\.")[1];
                } else {
                    String newBalancerString = balancerString[0] + "=";
                    boolean first = true;
                    for (int i = 0 ; i < balancedString.length; i++) {
                        if (! balancedString[i].equals(name)) {
                            if (!first) {
                                newBalancerString += ",";
                            } else {
                                first = false;
                            }
                            newBalancerString += balancedString[i];
                        }
                    }
                    newFileStringList.add(newBalancerString);
                }

            } else if (!string.contains(name)) {
                newFileStringList.add(string);
            }
        }

        if(loadBalancerIsEmpty && lbToDelete != null) {
            for(int i = 0; i < newFileStringList.size(); i++) {
                String string = newFileStringList.get(i);

                // Delete the load balancer from the worker list
                if(string.contains("worker.list=")) {
                    String[] listString = string.split("=");
                    String[] workerListString = listString[1].split(",");
                    String newWorkerString = listString[0] + "=";
                    boolean first = true;
                    for (int j = 0 ; j < workerListString.length; j++) {
                        if (! workerListString[j].equals(lbToDelete)) {
                            if (!first) {
                                newWorkerString += ",";

                            } else {
                                first = false;
                            }
                            newWorkerString += workerListString[j];
                        }
                    }
                    newFileStringList.set(i, newWorkerString);

                } else if(string.contains("worker." + lbToDelete)) {
                    newFileStringList.remove(string);
                }
            }
        }

        apacheUtilService.flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void disableNamedWorker(String name) {
        logger.info("disableNamedWorker (" + name + ")");
        modifyStateNamedWorker(name, "d");

    }

    /**
     * {@inheritDoc}
     */
    public void enableNamedWorker(String name) {
        logger.info("enableNamedWorker (" + name + ")");
        modifyStateNamedWorker(name, "a");
    }

    /**
     * {@inheritDoc}
     */
    public void stopNamedWorker(String name) {
        logger.info("stopNamedWorker (" + name + ")");
        modifyStateNamedWorker(name, "s");
    }

    /**
     * {@inheritDoc}
     */
    private void modifyStateNamedWorker(String name, String state) {

        String confFileLocation = getWorkersConfigurationFile();

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(confFileLocation);
        List<String> newFileStringList = new LinkedList<String>();
        boolean found = false;

        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (!string.contains("balance_workers=") && string.contains(name) && string.contains("activation")) {
                String newBalancedString = "worker." + name + ".activation=" + state;
                newFileStringList.add(newBalancedString);
                found = true;
            } else {
                newFileStringList.add(string);
            }
        }
        if (!found) {
            String activationBalancedString = "worker." + name + ".activation=" + state;
            newFileStringList.add(activationBalancedString);
        }

        apacheUtilService.flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * Init configuration
     * Overrides default one
     * @return
     */
    public void init(String workersConfigurationFile, String reloadCmd) {
        setWorkersConfigurationFile(workersConfigurationFile);
        logger.info("workersConfigurationFile=" +  workersConfigurationFile);
    }

    /**
     * {@inheritDoc}
     */
    public void mount(String loadbalancer, String path) {
        logger.info("mountWorker (" + path + ", " + loadbalancer + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(jkConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean alreadyConfigured = false;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if(string.contains("JkMountCopy")) {
                continue;
            }
            if (string.contains("JkMount") && string.contains(path)) {
                alreadyConfigured = true;
            }
            newFileStringList.add(string);
        }

        if (!alreadyConfigured) {
            newFileStringList.add("JkMount " + path + " " + loadbalancer );
        }

        newFileStringList.add("");
        newFileStringList.add("JkMountCopy  All");

        apacheUtilService.flushConfigurationFile(jkConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void unmount() {
        logger.info("unmount()");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(jkConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean alreadyConfigured = false;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (!string.contains("JkMount") || string.contains("JkMountCopy")) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(jkConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void unmount(String loadbalancer) {
        logger.info("unmount(" + loadbalancer + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(jkConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean alreadyConfigured = false;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (!(string.contains("JkMount") && string.contains(loadbalancer))) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(jkConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void unmount(String loadbalancer, String path) {
        logger.info("unmount(" + loadbalancer + ", " + path + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(jkConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean alreadyConfigured = false;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (!(string.contains("JkMount") && string.contains(loadbalancer)
                    && string.contains(path))) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(jkConfigurationFile, newFileStringList);
    }

    /**
     * @return true if configured
     */
    public boolean isConfigured(String name) {


        List<String> fileStringList =
                apacheUtilService.loadConfigurationFile(getWorkersConfigurationFile());

        logger.info("name=" +  name);

        boolean isConfiguredInBalancer = false;
        boolean isConfiguredInWorker = false;

        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();

            if (string.contains("balance_workers=") && string.contains(name)) {
                logger.info("isConfiguredInBalancer=" +  true);
                isConfiguredInBalancer = true;
            }
            if (string.contains("worker." + name + ".host")) {
                logger.info("isConfiguredInWorker=" +  true);
                isConfiguredInWorker = true;

            }
        }
        if (isConfiguredInBalancer && isConfiguredInWorker) {
            logger.info("-> is configured");
        } else {
            logger.info("-> is not configured");
        }

        return isConfiguredInBalancer && isConfiguredInWorker;
    }

    /**
     * @return true if enabled
     */
    public boolean isEnabled(String name) {

        logger.info("name=" +  name);

        boolean isConfiguredFlag = isConfigured(name);

        if (!isConfiguredFlag) {
            return false;
        } else {
            List<String> fileStringList =
                    apacheUtilService.loadConfigurationFile(getWorkersConfigurationFile());

            for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
                String string = iterator.next();

                if (string.contains("worker." + name + ".activation")) {
                    logger.info("worker." + name + ".activation detected");
                    String[] prop = string.split("=");
                    if (prop[1].trim().toLowerCase().equals("a")) {
                        logger.info("worker." + name + ".activation = a -> enabled");
                        return true;
                    } else {
                        logger.info("worker." + name + ".activation != a -> disabled");
                        return false;
                    }
                }
            }
            logger.info("worker." + name + " -> enabled");

            return true;
        }
    }

    public String getId() {
        return "";
    }

    private String getWorkersConfigurationFile() {
        return workersConfigurationFile;
    }

    private void setWorkersConfigurationFile(String workersConfigurationFile) {
        this.workersConfigurationFile = workersConfigurationFile;
    }

    private void setJkConfigurationFile(String jkConfigurationFile) {
        this.jkConfigurationFile = jkConfigurationFile;
    }
}
