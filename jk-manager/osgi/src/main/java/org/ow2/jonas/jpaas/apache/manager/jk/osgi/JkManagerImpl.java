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

package org.ow2.jonas.jpaas.apache.manager.jk.osgi;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.jk.api.JkManagerException;
import org.ow2.jonas.jpaas.apache.manager.jk.api.JkManagerService;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheManagerException;
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
     * @param name the name of the worker
     * @param host the host of the worker
     * @param port the port of the worker
     */
    @Override
    public void addNamedWorker(String name, String host, String port) throws JkManagerException {
        logger.debug("addNamedWorker (" + name + "," + host + "," + port + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(workersConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean nameConflict = false;
        // We need to know if there is already a worker or a loadBalancer with this name
        for (String string : fileStringList) {
            if (string.contains("worker." + name + ".host") || string.contains("worker." + name + ".type=lb")) {
                nameConflict = true;
                throw new JkManagerException("A worker or a Load Balancer named " + name + " is already present");
            }
            newFileStringList.add(string);
        }

        if (!nameConflict) {
            newFileStringList.add("worker." + name + ".port=" + port);
            newFileStringList.add("worker." + name + ".host=" + host);
            newFileStringList.add("worker." + name + ".type=ajp13");
            newFileStringList.add("worker." + name + ".activation=" + "a");
        }

        apacheUtilService.flushConfigurationFile(workersConfigurationFile, newFileStringList);
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
        logger.debug("addNamedWorker (" + name + "," + loadbalancer + "," + host + "," + port + ", " + lbFactor + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(workersConfigurationFile);
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
            boolean workerListExists = false;
            for (int i = 0; i < newFileStringList.size(); i++) {
                String string = newFileStringList.get(i);

                // Add the new loadbalancer worker to the worker list
                if(string.contains("worker.list=")) {
                    workerListExists = true;
                    if(string.split("=")[1].split(",").length > 0) {
                        string += ",";
                    }
                    string += loadbalancer;
                    newFileStringList.set(i, string);
                    break;
                }
            }
            if (!workerListExists) {
                newFileStringList.add("worker.list=" + name);
            }
        }

        apacheUtilService.flushConfigurationFile(workersConfigurationFile, newFileStringList);
    }

    /**
     * @param host the host to add
     * @param port the port of the host
     */
    @Override
    public void addWorker(String host, String port) throws JkManagerException {
        String workerName = "worker";
        workerName += Math.round(Math.random() * 100000) + 100;
        addNamedWorker(workerName, host, port);
    }

    /**
     * {@inheritDoc}
     */
    public void removeNamedWorker(String name) {
        logger.debug("removeNamedWorker (" + name + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(workersConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean loadBalancerIsEmpty = false;
        String lbToDelete = null;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("balance_workers=")) {

                String[] balancerString = string.split("=");
                String[] balancedString = balancerString[1].split(",");
                // if there is only one balanced worker remaining, we have to delete the whole load balancer
                if(balancedString.length == 1 && balancedString[0].equals(name)) {
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
            for(ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
                String string = iterator.next();

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
                    iterator.set(newWorkerString);

                } else if(string.contains("worker." + lbToDelete + ".")) {
                    iterator.remove();
                }
            }
        }

        apacheUtilService.flushConfigurationFile(workersConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void disableNamedWorker(String name) {
        logger.debug("disableNamedWorker (" + name + ")");
        modifyStateNamedWorker(name, "d");

    }

    /**
     * {@inheritDoc}
     */
    public void enableNamedWorker(String name) {
        logger.debug("enableNamedWorker (" + name + ")");
        modifyStateNamedWorker(name, "a");
    }

    /**
     * {@inheritDoc}
     */
    public void stopNamedWorker(String name) {
        logger.debug("stopNamedWorker (" + name + ")");
        modifyStateNamedWorker(name, "s");
    }

    /**
     * {@inheritDoc}
     */
    private void modifyStateNamedWorker(String name, String state) {
        List<String> fileStringList = apacheUtilService.loadConfigurationFile(workersConfigurationFile);
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

        apacheUtilService.flushConfigurationFile(workersConfigurationFile, newFileStringList);
    }

    /**
     * Init configuration
     * Overrides default one
     * @return
     */
    public void init(String workersConfigurationFile, String reloadCmd) {
        setWorkersConfigurationFile(workersConfigurationFile);
        logger.debug("workersConfigurationFile=" +  workersConfigurationFile);
    }

    /**
     * {@inheritDoc}
     */
    public void mount(String loadbalancer, String path) {
        logger.debug("mountWorker (" + path + ", " + loadbalancer + ")");

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
        newFileStringList.add("JkMount /jkmanager jkstatus");
        newFileStringList.add("JkMountCopy  All");

        apacheUtilService.flushConfigurationFile(jkConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void unmount() {
        logger.debug("unmount()");

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
        logger.debug("unmount(" + loadbalancer + ")");

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
        logger.debug("unmount(" + loadbalancer + ", " + path + ")");

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
                apacheUtilService.loadConfigurationFile(workersConfigurationFile);

        logger.debug("name=" +  name);

        boolean isConfiguredInBalancer = false;
        boolean isConfiguredInWorker = false;

        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();

            if (string.contains("balance_workers=") && string.contains(name)) {
                logger.debug("isConfiguredInBalancer=" +  true);
                isConfiguredInBalancer = true;
            }
            if (string.contains("worker." + name + ".host")) {
                logger.debug("isConfiguredInWorker=" +  true);
                isConfiguredInWorker = true;

            }
        }
        if (isConfiguredInBalancer && isConfiguredInWorker) {
            logger.debug("-> is configured");
        } else {
            logger.debug("-> is not configured");
        }

        return isConfiguredInBalancer && isConfiguredInWorker;
    }

    /**
     * @return true if enabled
     */
    public boolean isEnabled(String name) {
        logger.debug("name=" +  name);

        boolean isConfiguredFlag = isConfigured(name);

        if (!isConfiguredFlag) {
            return false;
        } else {
            List<String> fileStringList =
                    apacheUtilService.loadConfigurationFile(workersConfigurationFile);

            for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
                String string = iterator.next();

                if (string.contains("worker." + name + ".activation")) {
                    logger.debug("worker." + name + ".activation detected");
                    String[] prop = string.split("=");
                    if (prop[1].trim().toLowerCase().equals("a")) {
                        logger.debug("worker." + name + ".activation = a -> enabled");
                        return true;
                    } else {
                        logger.debug("worker." + name + ".activation != a -> disabled");
                        return false;
                    }
                }
            }
            logger.debug("worker." + name + " -> enabled");

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

    /**
     * Create a Load Balancer
     * @param name Name of the Load Balancer
     * @param workerList The balance workers
     */
    public void addLoadBalancer(String name, List<String> workerList) throws JkManagerException {
        logger.debug("addLoadBalancer (" + name + "," + workerList.toString() + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(workersConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean nameConflict = false;
        // We need to know if there is already a worker or a LoadBalancer with this name
        for (String string : fileStringList) {
            if (string.contains("worker." + name + ".host") || string.contains("worker." + name + ".type=lb")) {
                nameConflict = true;
                throw new JkManagerException("A Load Balancer or a worker named " + name + " is already present.");
            }
            newFileStringList.add(string);
        }

        if (!nameConflict) {
            newFileStringList.add("worker." + name + ".type=lb");
            String wl = "";
            boolean first = true;
            for(String s : workerList) {
                if (first) {
                    first=false;
                } else {
                    wl += ",";
                }
                wl += s;
            }
            newFileStringList.add("worker." + name + ".balance_workers=" + wl);

            boolean workerListExists = false;
            for (int i = 0; i < newFileStringList.size(); i++) {
                String string = newFileStringList.get(i);

                // Add the new loadbalancer worker to the worker list
                if (string.contains("worker.list=")) {
                    workerListExists = true;
                    if(string.split("=")[1].split(",").length > 0) {
                        string += ",";
                    }
                    string += name;
                    newFileStringList.set(i, string);
                    break;
                }
            }
            if (!workerListExists) {
                newFileStringList.add("worker.list=" + name);
            }

        }

        apacheUtilService.flushConfigurationFile(workersConfigurationFile, newFileStringList);
    }

    /**
     * Update the workers list of a Load Balancer
     * @param name Name of the Load Balancer
     * @param workerList The balance workers
     */
    public void updateLoadBalancer(String name, List<String> workerList) throws JkManagerException {
        logger.debug("updateLoadBalancer (" + name + "," + workerList.toString() + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(workersConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean loadBalancerExists = false;
        for (String string : fileStringList) {
            if (string.contains("worker." + name + ".balance_workers=")) {
                loadBalancerExists = true;
                String wl = "";
                boolean first = true;
                for(String s : workerList) {
                    if (first) {
                        first=false;
                    } else {
                        wl += ",";
                    }
                    wl += s;
                }
                newFileStringList.add("worker." + name + ".balance_workers=" + wl);
            } else {
                newFileStringList.add(string);
            }
        }
        if (!loadBalancerExists) {
            throw new JkManagerException("The Load Balancer named " + name + " does not exist.");
        }

        apacheUtilService.flushConfigurationFile(workersConfigurationFile, newFileStringList);
    }

    /**
     * Remove a Load Balancer
     * @param name Name of the Load Balancer
     */
    public void removeLoadBalancer(String name) throws JkManagerException {
        logger.debug("removeLoadBalancer (" + name + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(workersConfigurationFile);

        boolean loadBalancerExists = false;
        for(ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            // Delete the load balancer from the worker list
            if (string.contains("worker.list=")) {
                String[] listString = string.split("=");
                String[] workerListString = listString[1].split(",");
                String newWorkerString = listString[0] + "=";
                boolean first = true;
                for (int j = 0 ; j < workerListString.length; j++) {
                    if (! workerListString[j].equals(name)) {
                        if (!first) {
                            newWorkerString += ",";

                        } else {
                            first = false;
                        }
                        newWorkerString += workerListString[j];
                    }
                }
                iterator.set(newWorkerString);

            } else if (string.contains("worker." + name + ".")) {
                loadBalancerExists = true;
                iterator.remove();
            }
        }
        if (!loadBalancerExists) {
            throw new JkManagerException("The Load Balancer named " + name + " does not exist.");
        }
        apacheUtilService.flushConfigurationFile(workersConfigurationFile, fileStringList);
    }

    /**
     * Mount a path in a Vhost for all workers in load balancing
     *
     * @param vhostID ID of the virtual host
     * @param loadbalancer the loadbalancer to send the requests
     * @param path         the path to mount it
     */
    @Override
    public long mountInVhost(long vhostID, String loadbalancer, String path) throws JkManagerException {
        logger.debug("mountInVhost (" + vhostID + "," + loadbalancer + ", " + path + ")");
        String directiveArgs = path + " " + loadbalancer;
        try {
            return apacheUtilService.addDirectiveInVhost(vhostID, "JkMount", directiveArgs);
        } catch (ApacheManagerException e) {
            throw new JkManagerException("Cannot mount the loadbalancer " + loadbalancer +
                    " in the vhost.", e);
        }
    }

    /**
     * Delete all mount points for a load balancer.
     *
     * @param vhostID ID of the virtual host
     * @param loadbalancer the loadbalancer to unmount
     */
    @Override
    public void unmountInVhost(long vhostID, String loadbalancer) throws JkManagerException {
        logger.debug("unmountInVhost (" + vhostID + "," + loadbalancer + ")");

        try {
            String vhostFilePath = apacheUtilService.getVhostConfigurationFile(vhostID);
            String content = apacheUtilService.fileToString(vhostFilePath);
            String lb = apacheUtilService.stringToRegex(loadbalancer);
            Pattern pattern = Pattern.compile("#id=(\\d*)" + System.getProperty("line.separator") +
                    "JkMount" + "[ \\t]*" + ".*?[ \\t]*(" + lb + ")[\\s]");
            Matcher matcher = pattern.matcher(content);
            List<Long> idList = new LinkedList<Long>();
            while (matcher.find()) {
                idList.add(Long.valueOf(matcher.group(1)));
            }
            for (Long id : idList) {
                apacheUtilService.removeDirectiveInVhostIfPossible(vhostID, id);
            }
        } catch (ApacheManagerException e) {
            throw new JkManagerException(e.getMessage(), e);
        }
    }

    /**
     * Delete a mount point for a worker.
     *
     * @param vhostID ID of the virtual host
     * @param loadbalancer the loadbalancer to unmount
     * @param path         the path to unmount
     */
    @Override
    public void unmountInVhost(long vhostID, String loadbalancer, String path) throws JkManagerException {
        logger.debug("unmountInVhost (" + vhostID + "," + loadbalancer + ", " + path + ")");
        String directiveArgs = path + " " + loadbalancer;
        String vhostFilePath = null;
        try {
            vhostFilePath = apacheUtilService.getVhostConfigurationFile(vhostID);
            List<Long> idList;
            idList = apacheUtilService.getDirectivesIdInFile(vhostFilePath, "JkMount", directiveArgs);
            for (Long id : idList) {
                apacheUtilService.removeDirectiveInVhostIfPossible(vhostID, id);
            }
        } catch (ApacheManagerException e) {
            throw new JkManagerException(e.getMessage(), e);
        }
    }
}
