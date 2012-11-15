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

package org.ow2.jonas.jpaas.apache.manager.rewrite.osgi;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.rewrite.api.RewriteManagerService;
import org.ow2.jonas.jpaas.apache.manager.util.api.ApacheUtilService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * The implementation of the RewriteManagerService Interface
 *
 * @author David Richard
 */
@Component(propagation = true)
@Provides
public class RewriteManagerImpl implements RewriteManagerService {

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(RewriteManagerImpl.class);


    /**
     * Property name to define the rewrite.conf path
     */
    private static final String REWRITE_CONF_FILE_LOCATION_PROPERTY = "rewrite.conf.location";

    /**
     * Path to the rewrite.conf file
     */
    private String rewriteConfigurationFile;

    @Requires
    private ApacheUtilService apacheUtilService;


    @Validate
    public void start() {
        logger.info("Load default configuration");
        String rewriteConfigurationFile = apacheUtilService.getPropertyValue(REWRITE_CONF_FILE_LOCATION_PROPERTY);
        this.setRewriteConfigurationFile(rewriteConfigurationFile);

        logger.info("rewriteConfigurationFile=" + rewriteConfigurationFile);
    }


    /**
     * {@inheritDoc}
     */
    public void addRewriteRuleInDirectory(String directoryDirective, String pattern, String substitution, String flag) {
        logger.info("addRewriteRuleInDirectory (" + directoryDirective + "," + pattern + ","
                + substitution + ", " + flag + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean directoryDirectiveAlreadyCreated = false;
        boolean inDirectoryDirective = false;
        boolean rewriteRuleAlreadyPresent = false;
        // We need to know if there is already this directory directive
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Directory " + directoryDirective + ">")) {
                directoryDirectiveAlreadyCreated = true;
                inDirectoryDirective=true;
            }
            if (string.contains("</Directory>")) {
                if (inDirectoryDirective && !rewriteRuleAlreadyPresent) {
                    newFileStringList.add("RewriteRule " + pattern + " " + substitution + " [" + flag + "]");
                    rewriteRuleAlreadyPresent=true;
                }
                inDirectoryDirective=false;
            }
            if (!rewriteRuleAlreadyPresent && inDirectoryDirective
                    && string.contains("RewriteRule " + pattern + " " + substitution)) {
                newFileStringList.add("RewriteRule " + pattern + " " + substitution + " [" + flag + "]");
                rewriteRuleAlreadyPresent=true;
            } else {
                newFileStringList.add(string);
            }
        }

        // If no directory directive with the same name was found, we create it
        if (!directoryDirectiveAlreadyCreated) {
            newFileStringList.add("<Directory " + directoryDirective + ">");
            newFileStringList.add("options +FollowSymlinks");
            newFileStringList.add("RewriteEngine On");
            newFileStringList.add("RewriteRule " + pattern + " " + substitution + " [" + flag + "]");
            newFileStringList.add("</Directory>");
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void addRewriteRuleInDirectory(String directoryDirective, String pattern, String substitution) {
        logger.info("addRewriteRuleInDirectory (" + directoryDirective + "," + pattern + ","
                + substitution + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean directoryDirectiveAlreadyCreated = false;
        boolean inDirectoryDirective = false;
        boolean rewriteRuleAlreadyPresent = false;
        // We need to know if there is already this directory directive
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Directory " + directoryDirective + ">")) {
                directoryDirectiveAlreadyCreated = true;
                inDirectoryDirective=true;
            }
            if (string.contains("</Directory>")) {
                if (inDirectoryDirective && !rewriteRuleAlreadyPresent) {
                    newFileStringList.add("RewriteRule " + pattern + " " + substitution);
                    rewriteRuleAlreadyPresent=true;
                }
                inDirectoryDirective=false;
            }
            if (!rewriteRuleAlreadyPresent && inDirectoryDirective
                    && string.contains("RewriteRule " + pattern + " " + substitution)) {
                newFileStringList.add("RewriteRule " + pattern + " " + substitution);
                rewriteRuleAlreadyPresent=true;
            } else {
                newFileStringList.add(string);
            }
        }

        // If no directory directive with the same name was found, we create it
        if (!directoryDirectiveAlreadyCreated) {
            newFileStringList.add("<Directory " + directoryDirective + ">");
            newFileStringList.add("options +FollowSymlinks");
            newFileStringList.add("RewriteEngine On");
            newFileStringList.add("RewriteRule " + pattern + " " + substitution);
            newFileStringList.add("</Directory>");
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void addRewriteRuleInProxy(String proxyDirective, String pattern, String substitution, String flag) {
        logger.info("addRewriteRuleInProxy (" + proxyDirective + "," + pattern + ","
                + substitution + ", " + flag + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean proxyDirectiveAlreadyCreated = false;
        boolean inProxyDirective = false;
        boolean rewriteRuleAlreadyPresent = false;
        // We need to know if there is already this proxy directive
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy " + proxyDirective + ">")) {
                proxyDirectiveAlreadyCreated = true;
                inProxyDirective=true;
            }
            if (string.contains("</Proxy>")) {
                if (inProxyDirective && !rewriteRuleAlreadyPresent) {
                    newFileStringList.add("RewriteRule " + pattern + " " + substitution + " [" + flag + "]");
                    rewriteRuleAlreadyPresent=true;
                }
                inProxyDirective=false;
            }
            if (!rewriteRuleAlreadyPresent && inProxyDirective
                    && string.contains("RewriteRule " + pattern + " " + substitution)) {
                newFileStringList.add("RewriteRule " + pattern + " " + substitution + " [" + flag + "]");
                rewriteRuleAlreadyPresent=true;
            } else {
                newFileStringList.add(string);
            }
        }

        // If no proxy directive with the same name was found, we create it
        if (!proxyDirectiveAlreadyCreated) {
            newFileStringList.add("<Proxy " + proxyDirective + ">");
            newFileStringList.add("options +FollowSymlinks");
            newFileStringList.add("RewriteEngine On");
            newFileStringList.add("RewriteRule " + pattern + " " + substitution + " [" + flag + "]");
            newFileStringList.add("</Proxy>");
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void addRewriteRuleInProxy(String proxyDirective, String pattern, String substitution) {
        logger.info("addRewriteRuleInProxy (" + proxyDirective + "," + pattern + ","
                + substitution + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean proxyDirectiveAlreadyCreated = false;
        boolean inProxyDirective = false;
        boolean rewriteRuleAlreadyPresent = false;
        // We need to know if there is already this proxy directive
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy " + proxyDirective + ">")) {
                proxyDirectiveAlreadyCreated = true;
                inProxyDirective=true;
            }
            if (string.contains("</Proxy>")) {
                if (inProxyDirective && !rewriteRuleAlreadyPresent) {
                    newFileStringList.add("RewriteRule " + pattern + " " + substitution);
                    rewriteRuleAlreadyPresent=true;
                }
                inProxyDirective=false;
            }
            if (!rewriteRuleAlreadyPresent && inProxyDirective
                    && string.contains("RewriteRule " + pattern + " " + substitution)) {
                newFileStringList.add("RewriteRule " + pattern + " " + substitution);
                rewriteRuleAlreadyPresent=true;
            } else {
                newFileStringList.add(string);
            }
        }

        // If no proxy directive with the same name was found, we create it
        if (!proxyDirectiveAlreadyCreated) {
            newFileStringList.add("<Proxy " + proxyDirective + ">");
            newFileStringList.add("options +FollowSymlinks");
            newFileStringList.add("RewriteEngine On");
            newFileStringList.add("RewriteRule " + pattern + " " + substitution);
            newFileStringList.add("</Proxy>");
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeRewriteRuleInDirectory(String directoryDirective, String pattern, String substitution) {
        logger.info("removeRewriteRuleInDirectory (" + directoryDirective + "," + pattern + "," + substitution + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean inDirectoryDirective = false;
        String rewriteCondTmp="";
        boolean rewriteCondMatch = false;
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Directory " + directoryDirective + ">")) {
                inDirectoryDirective=true;
            }
            if (string.contains("</Directory>")) {
                inDirectoryDirective=false;
            }
            if (inDirectoryDirective && string.contains("RewriteCond ")) {
                rewriteCondTmp+=string + System.getProperty("line.separator");
                rewriteCondMatch = true;
            }
            if (inDirectoryDirective && rewriteCondMatch && string.contains("RewriteRule ")) {
                if (!string.contains("RewriteRule " + pattern + " " + substitution)) {
                    rewriteCondTmp = rewriteCondTmp.substring(0, rewriteCondTmp.length() -1);
                    newFileStringList.add(rewriteCondTmp);
                }
                rewriteCondMatch = false;
            }
            if (!(string.contains("RewriteRule " + pattern + " " + substitution) && inDirectoryDirective)
                    && !rewriteCondMatch) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeRewriteRuleInProxy(String proxyDirective, String pattern, String substitution) {
        logger.info("removeRewriteRuleInProxy (" + proxyDirective + "," + pattern + "," + substitution + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();


        boolean inProxyDirective = false;
		String rewriteCondTmp="";
        boolean rewriteCondMatch = false;
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy " + proxyDirective + ">")) {
                inProxyDirective=true;
            }
            if (string.contains("</Proxy>")) {
                inProxyDirective=false;
            }
			if (inProxyDirective && string.contains("RewriteCond ")) {
                rewriteCondTmp+=string + System.getProperty("line.separator");
                rewriteCondMatch = true;
            }
            if (inProxyDirective && rewriteCondMatch && string.contains("RewriteRule ")) {
                if (!string.contains("RewriteRule " + pattern + " " + substitution)) {
                    rewriteCondTmp = rewriteCondTmp.substring(0, rewriteCondTmp.length() -1);
                    newFileStringList.add(rewriteCondTmp);
                }
                rewriteCondMatch = false;
            }
            if (!(string.contains("RewriteRule " + pattern + " " + substitution) && inProxyDirective)
                    && !rewriteCondMatch) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void addRewriteBaseInDirectory(String directoryDirective, String urlPath) {
        logger.info("addRewriteBaseInDirectory (" + directoryDirective + "," + urlPath + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean directoryDirectiveAlreadyCreated = false;
        boolean inDirectoryDirective = false;
        boolean rewriteBaseAlreadyPresent = false;
        // We need to know if there is already this directory directive
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Directory " + directoryDirective + ">")) {
                directoryDirectiveAlreadyCreated = true;
                inDirectoryDirective=true;
            }
            if (string.contains("</Directory>")) {
                if (inDirectoryDirective && !rewriteBaseAlreadyPresent) {
                    newFileStringList.add("RewriteBase " + urlPath);
                    rewriteBaseAlreadyPresent=true;
                }
                inDirectoryDirective=false;
            }
            if (!rewriteBaseAlreadyPresent && inDirectoryDirective
                    && string.contains("RewriteBase")) {
                newFileStringList.add("RewriteBase " + urlPath);
                rewriteBaseAlreadyPresent=true;
            } else {
                newFileStringList.add(string);
            }
        }

        // If no directory directive with the same name was found, we create it
        if (!directoryDirectiveAlreadyCreated) {
            newFileStringList.add("<Directory " + directoryDirective + ">");
            newFileStringList.add("options +FollowSymlinks");
            newFileStringList.add("RewriteEngine On");
            newFileStringList.add("RewriteBase " + urlPath);
            newFileStringList.add("</Directory>");
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void addRewriteBaseInProxy(String proxyDirective, String urlPath) {
        logger.info("addRewriteBaseInProxy (" + proxyDirective + "," + urlPath + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean proxyDirectiveAlreadyCreated = false;
        boolean inProxyDirective = false;
        boolean rewriteBaseAlreadyPresent = false;
        // We need to know if there is already this proxy directive
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy " + proxyDirective + ">")) {
                proxyDirectiveAlreadyCreated = true;
                inProxyDirective=true;
            }
            if (string.contains("</Proxy>")) {
                if (inProxyDirective && !rewriteBaseAlreadyPresent) {
                    newFileStringList.add("RewriteBase " + urlPath);
                    rewriteBaseAlreadyPresent=true;
                }
                inProxyDirective=false;
            }
            if (!rewriteBaseAlreadyPresent && inProxyDirective
                    && string.contains("RewriteBase")) {
                newFileStringList.add("RewriteBase " + urlPath);
                rewriteBaseAlreadyPresent=true;
            } else {
                newFileStringList.add(string);
            }
        }

        // If no proxy directive with the same name was found, we create it
        if (!proxyDirectiveAlreadyCreated) {
            newFileStringList.add("<Proxy " + proxyDirective + ">");
            newFileStringList.add("options +FollowSymlinks");
            newFileStringList.add("RewriteEngine On");
            newFileStringList.add("RewriteBase " + urlPath);
            newFileStringList.add("</Proxy>");
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeRewriteBaseInDirectory(String directoryDirective, String urlPath) {
        logger.info("removeRewriteBaseInDirectory (" + directoryDirective + "," + urlPath + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean inDirectoryDirective = false;
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Directory " + directoryDirective + ">")) {
                inDirectoryDirective=true;
            }
            if (string.contains("</Directory>")) {
                inDirectoryDirective=false;
            }
            if (!(string.contains("RewriteBase " + urlPath) && inDirectoryDirective)) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeRewriteBaseInProxy(String proxyDirective, String urlPath) {
        logger.info("removeRewriteBaseInProxy (" + proxyDirective + "," + urlPath + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean inProxyDirective = false;
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy " + proxyDirective + ">")) {
                inProxyDirective=true;
            }
            if (string.contains("</Proxy>")) {
                inProxyDirective=false;
            }
            if (!(string.contains("RewriteBase " + urlPath) && inProxyDirective)) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    public void addRewriteCondInDirectory(String directoryDirective, String rulePattern, String ruleSubstitution,
                                          String condTestString, String condPattern, String condFlag) {
        logger.info("addRewriteCondInDirectory (" + directoryDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern + "," + condFlag + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean directoryDirectiveAlreadyCreated = false;
        boolean inDirectoryDirective = false;
        boolean rewriteCondAlreadyPresent = false;
        // We need to know if there is already this directory directive
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Directory " + directoryDirective + ">")) {
                directoryDirectiveAlreadyCreated = true;
                inDirectoryDirective=true;
            }
            if (string.contains("</Directory>")) {
                if (inDirectoryDirective && !rewriteCondAlreadyPresent) {
                    newFileStringList.add("RewriteCond " + condTestString + " " + condPattern + " [" + condFlag + "]");
                    newFileStringList.add("RewriteRule " + rulePattern + " " + ruleSubstitution);
                    rewriteCondAlreadyPresent=true;
                }
                inDirectoryDirective=false;
            }
			if (inDirectoryDirective && string.contains("RewriteCond " + condTestString + " " + condPattern)) {
                rewriteCondAlreadyPresent=true;
            }
			if (inDirectoryDirective && rewriteCondAlreadyPresent && string.contains("RewriteRule ")) {
                if (!string.contains("RewriteRule " + rulePattern + " " + ruleSubstitution)) {
                    rewriteCondAlreadyPresent = false;
                }
            }
            if (!rewriteCondAlreadyPresent && inDirectoryDirective
                    && string.contains("RewriteRule " + rulePattern + " " + ruleSubstitution)) {
                newFileStringList.add("RewriteCond " + condTestString + " " + condPattern + " [" + condFlag + "]");
                rewriteCondAlreadyPresent=true;
                newFileStringList.add(string);
            } else {
                newFileStringList.add(string);
            }
        }

        // If no directory directive with the same name was found, we create it
        if (!directoryDirectiveAlreadyCreated) {
            newFileStringList.add("<Directory " + directoryDirective + ">");
            newFileStringList.add("options +FollowSymlinks");
            newFileStringList.add("RewriteEngine On");
            newFileStringList.add("RewriteCond " + condTestString + " " + condPattern + " [" + condFlag + "]");
            newFileStringList.add("RewriteRule " + rulePattern + " " + ruleSubstitution);
            newFileStringList.add("</Directory>");
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    public void addRewriteCondInDirectory(String directoryDirective, String rulePattern, String ruleSubstitution,
                                          String condTestString, String condPattern) {
        logger.info("addRewriteCondInDirectory (" + directoryDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean directoryDirectiveAlreadyCreated = false;
        boolean inDirectoryDirective = false;
        boolean rewriteCondAlreadyPresent = false;
        // We need to know if there is already this directory directive
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Directory " + directoryDirective + ">")) {
                directoryDirectiveAlreadyCreated = true;
                inDirectoryDirective=true;
            }
            if (string.contains("</Directory>")) {
                if (inDirectoryDirective && !rewriteCondAlreadyPresent) {
                    newFileStringList.add("RewriteCond " + condTestString + " " + condPattern);
                    newFileStringList.add("RewriteRule " + rulePattern + " " + ruleSubstitution);
                    rewriteCondAlreadyPresent=true;
                }
                inDirectoryDirective=false;
            }
			if (inDirectoryDirective && string.contains("RewriteCond " + condTestString + " " + condPattern)) {
                rewriteCondAlreadyPresent=true;
            }
			if (inDirectoryDirective && rewriteCondAlreadyPresent && string.contains("RewriteRule ")) {
                if (!string.contains("RewriteRule " + rulePattern + " " + ruleSubstitution)) {
                    rewriteCondAlreadyPresent = false;
                }
            }
            if (!rewriteCondAlreadyPresent && inDirectoryDirective
                    && string.contains("RewriteRule " + rulePattern + " " + ruleSubstitution)) {
                newFileStringList.add("RewriteCond " + condTestString + " " + condPattern);
                rewriteCondAlreadyPresent=true;
                newFileStringList.add(string);
            } else {
                newFileStringList.add(string);
            }
        }

        // If no directory directive with the same name was found, we create it
        if (!directoryDirectiveAlreadyCreated) {
            newFileStringList.add("<Directory " + directoryDirective + ">");
            newFileStringList.add("options +FollowSymlinks");
            newFileStringList.add("RewriteEngine On");
            newFileStringList.add("RewriteCond " + condTestString + " " + condPattern);
            newFileStringList.add("RewriteRule " + rulePattern + " " + ruleSubstitution);
            newFileStringList.add("</Directory>");
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    public void addRewriteCondInProxy(String proxyDirective, String rulePattern, String ruleSubstitution,
                                      String condTestString, String condPattern, String condFlag) {
        logger.info("addRewriteCondInProxy (" + proxyDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern + "," + condFlag + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean proxyDirectiveAlreadyCreated = false;
        boolean inProxyDirective = false;
        boolean rewriteCondAlreadyPresent = false;
        // We need to know if there is already this proxy directive
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy " + proxyDirective + ">")) {
                proxyDirectiveAlreadyCreated = true;
                inProxyDirective=true;
            }
            if (string.contains("</Proxy>")) {
                if (inProxyDirective && !rewriteCondAlreadyPresent) {
                    newFileStringList.add("RewriteCond " + condTestString + " " + condPattern + " [" + condFlag + "]");
                    newFileStringList.add("RewriteRule " + rulePattern + " " + ruleSubstitution);
                    rewriteCondAlreadyPresent=true;
                }
                inProxyDirective=false;
            }
			if (inProxyDirective && string.contains("RewriteCond " + condTestString + " " + condPattern)) {
                rewriteCondAlreadyPresent=true;
            }
			if (inProxyDirective && rewriteCondAlreadyPresent && string.contains("RewriteRule ")) {
                if (!string.contains("RewriteRule " + rulePattern + " " + ruleSubstitution)) {
                    rewriteCondAlreadyPresent = false;
                }
            }
            if (!rewriteCondAlreadyPresent && inProxyDirective
                    && string.contains("RewriteRule " + rulePattern + " " + ruleSubstitution)) {
                newFileStringList.add("RewriteCond " + condTestString + " " + condPattern + " [" + condFlag + "]");
                rewriteCondAlreadyPresent=true;
                newFileStringList.add(string);
            } else {
                newFileStringList.add(string);
            }
        }

        // If no proxy directive with the same name was found, we create it
        if (!proxyDirectiveAlreadyCreated) {
            newFileStringList.add("<Proxy " + proxyDirective + ">");
            newFileStringList.add("options +FollowSymlinks");
            newFileStringList.add("RewriteEngine On");
            newFileStringList.add("RewriteCond " + condTestString + " " + condPattern + " [" + condFlag + "]");
            newFileStringList.add("RewriteRule " + rulePattern + " " + ruleSubstitution);
            newFileStringList.add("</Proxy>");
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    public void addRewriteCondInProxy(String proxyDirective, String rulePattern, String ruleSubstitution,
                                      String condTestString, String condPattern) {
        logger.info("addRewriteCondInProxy (" + proxyDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean proxyDirectiveAlreadyCreated = false;
        boolean inProxyDirective = false;
        boolean rewriteCondAlreadyPresent = false;
        // We need to know if there is already this proxy directive
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy " + proxyDirective + ">")) {
                proxyDirectiveAlreadyCreated = true;
                inProxyDirective=true;
            }
            if (string.contains("</Proxy>")) {
                if (inProxyDirective && !rewriteCondAlreadyPresent) {
                    newFileStringList.add("RewriteCond " + condTestString + " " + condPattern);
                    newFileStringList.add("RewriteRule " + rulePattern + " " + ruleSubstitution);
                    rewriteCondAlreadyPresent=true;
                }
                inProxyDirective=false;
            }
			if (inProxyDirective && string.contains("RewriteCond " + condTestString + " " + condPattern)) {
                rewriteCondAlreadyPresent=true;
            }
			if (inProxyDirective && rewriteCondAlreadyPresent && string.contains("RewriteRule ")) {
                if (!string.contains("RewriteRule " + rulePattern + " " + ruleSubstitution)) {
                    rewriteCondAlreadyPresent = false;
                }
            }
            if (!rewriteCondAlreadyPresent && inProxyDirective
                    && string.contains("RewriteRule " + rulePattern + " " + ruleSubstitution)) {
                newFileStringList.add("RewriteCond " + condTestString + " " + condPattern);
                rewriteCondAlreadyPresent=true;
                newFileStringList.add(string);
            } else {
                newFileStringList.add(string);
            }
        }

        // If no proxy directive with the same name was found, we create it
        if (!proxyDirectiveAlreadyCreated) {
            newFileStringList.add("<Proxy " + proxyDirective + ">");
            newFileStringList.add("options +FollowSymlinks");
            newFileStringList.add("RewriteEngine On");
            newFileStringList.add("RewriteCond " + condTestString + " " + condPattern);
            newFileStringList.add("RewriteRule " + rulePattern + " " + ruleSubstitution);
            newFileStringList.add("</Proxy>");
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    public void removeRewriteCondInDirectory(String directoryDirective, String rulePattern, String ruleSubstitution,
                                             String condTestString, String condPattern) {
        logger.info("removeRewriteCondInDirectory (" + directoryDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean inDirectoryDirective = false;
        String rewriteCondTmp="";
        boolean rewriteCondMatch = false;
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Directory " + directoryDirective + ">")) {
                inDirectoryDirective=true;
            }
            if (string.contains("</Directory>")) {
                inDirectoryDirective=false;
            }
            if (inDirectoryDirective && string.contains("RewriteCond " + condTestString + " " + condPattern)) {
                rewriteCondTmp=string;
                rewriteCondMatch = true;
            }
            if (inDirectoryDirective && rewriteCondMatch && string.contains("RewriteRule ")) {
                if (!string.contains("RewriteRule " + rulePattern + " " + ruleSubstitution)) {
                    newFileStringList.add(rewriteCondTmp);
                }
                rewriteCondMatch = false;
            }
            if (!(string.contains("RewriteCond " + condTestString + " " + condPattern) && inDirectoryDirective)) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    public void removeRewriteCondInProxy(String proxyDirective, String rulePattern, String ruleSubstitution,
                                         String condTestString, String condPattern) {
        logger.info("removeRewriteCondInProxy (" + proxyDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean inProxyDirective = false;
        String rewriteCondTmp="";
        boolean rewriteCondMatch = false;
        for (ListIterator<String> iterator = fileStringList.listIterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy " + proxyDirective + ">")) {
                inProxyDirective=true;
            }
            if (string.contains("</Proxy>")) {
                inProxyDirective=false;
            }
            if (inProxyDirective && string.contains("RewriteCond " + condTestString + " " + condPattern)) {
                rewriteCondTmp=string;
                rewriteCondMatch = true;
            }
            if (inProxyDirective && rewriteCondMatch && string.contains("RewriteRule ")) {
                if (!string.contains("RewriteRule " + rulePattern + " " + ruleSubstitution)) {
                    newFileStringList.add(rewriteCondTmp);
                }
                rewriteCondMatch = false;
            }
            if (!(string.contains("RewriteCond " + condTestString + " " + condPattern) && inProxyDirective)) {
                newFileStringList.add(string);
            }
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeDirectoryDirective(String directoryDirective) {
        logger.info("removeDirectoryDirective(" + directoryDirective + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean inDirectoryDirective = false;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Directory " + directoryDirective + ">")) {
                inDirectoryDirective=true;
            }
            if (!inDirectoryDirective) {
                newFileStringList.add(string);
            }
            if (string.contains("</Directory>")) {
                inDirectoryDirective=false;
            }
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeProxyDirective(String proxyDirective) {
        logger.info("removeProxyDirective(" + proxyDirective + ")");

        List<String> fileStringList = apacheUtilService.loadConfigurationFile(rewriteConfigurationFile);
        List<String> newFileStringList = new LinkedList<String>();

        boolean inProxyDirective = false;
        for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
            String string = iterator.next();
            if (string.contains("<Proxy " + proxyDirective + ">")) {
                inProxyDirective=true;
            }
            if (!inProxyDirective) {
                newFileStringList.add(string);
            }
            if (string.contains("</Proxy>")) {
                inProxyDirective=false;
            }
        }

        apacheUtilService.flushConfigurationFile(rewriteConfigurationFile, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void init(String rewriteConfigurationFile) {
        setRewriteConfigurationFile(rewriteConfigurationFile);
        logger.info("rewriteConfigurationFile=" +  rewriteConfigurationFile);
    }

    public String getRewriteConfigurationFile() {
        return rewriteConfigurationFile;
    }

    public void setRewriteConfigurationFile(String rewriteConfigurationFile) {
        this.rewriteConfigurationFile = rewriteConfigurationFile;
    }
}
