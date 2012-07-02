/**
 * JASMINe
 * Copyright (C) 2011 Bull S.A.S.
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
 * $Id: RewriteManagerImpl.java 9287 2011-11-10 09:53:32Z richardd $
 * --------------------------------------------------------------------------
 */

package org.ow2.jonas.jpaas.apache.manager.rewrite.osgi;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.jpaas.apache.manager.rewrite.api.RewriteManagerService;
import org.ow2.jonas.lib.bootstrap.JProp;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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


    /**
     * RewriteManager property file name
     */
    private static final String REWRITE_MANAGER_PROPERTY_FILE_NAME = "rewritemanager.properties";

    @Validate
    public void start() {
        logger.info("Load default configuration");
        String rewriteConfigurationFile = getRewritePropertyFileLocation();
        this.setRewriteConfigurationFile(rewriteConfigurationFile);

        logger.info("rewriteConfigurationFile=" + rewriteConfigurationFile);
    }


    /**
     * {@inheritDoc}
     */
    public void addRewriteRuleInDirectory(String directoryDirective, String pattern, String substitution, String flag) {
        logger.info("addRewriteRuleInDirectory (" + directoryDirective + "," + pattern + ","
                + substitution + ", " + flag + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void addRewriteRuleInDirectory(String directoryDirective, String pattern, String substitution) {
        logger.info("addRewriteRuleInDirectory (" + directoryDirective + "," + pattern + ","
                + substitution + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void addRewriteRuleInProxy(String proxyDirective, String pattern, String substitution, String flag) {
        logger.info("addRewriteRuleInProxy (" + proxyDirective + "," + pattern + ","
                + substitution + ", " + flag + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void addRewriteRuleInProxy(String proxyDirective, String pattern, String substitution) {
        logger.info("addRewriteRuleInProxy (" + proxyDirective + "," + pattern + ","
                + substitution + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeRewriteRuleInDirectory(String directoryDirective, String pattern, String substitution) {
        logger.info("removeRewriteRuleInDirectory (" + directoryDirective + "," + pattern + "," + substitution + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeRewriteRuleInProxy(String proxyDirective, String pattern, String substitution) {
        logger.info("removeRewriteRuleInProxy (" + proxyDirective + "," + pattern + "," + substitution + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void addRewriteBaseInDirectory(String directoryDirective, String urlPath) {
        logger.info("addRewriteBaseInDirectory (" + directoryDirective + "," + urlPath + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void addRewriteBaseInProxy(String proxyDirective, String urlPath) {
        logger.info("addRewriteBaseInProxy (" + proxyDirective + "," + urlPath + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeRewriteBaseInDirectory(String directoryDirective, String urlPath) {
        logger.info("removeRewriteBaseInDirectory (" + directoryDirective + "," + urlPath + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeRewriteBaseInProxy(String proxyDirective, String urlPath) {
        logger.info("removeRewriteBaseInProxy (" + proxyDirective + "," + urlPath + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    public void addRewriteCondInDirectory(String directoryDirective, String rulePattern, String ruleSubstitution,
                                          String condTestString, String condPattern, String condFlag) {
        logger.info("addRewriteCondInDirectory (" + directoryDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern + "," + condFlag + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    public void addRewriteCondInDirectory(String directoryDirective, String rulePattern, String ruleSubstitution,
                                          String condTestString, String condPattern) {
        logger.info("addRewriteCondInDirectory (" + directoryDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    public void addRewriteCondInProxy(String proxyDirective, String rulePattern, String ruleSubstitution,
                                      String condTestString, String condPattern, String condFlag) {
        logger.info("addRewriteCondInProxy (" + proxyDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern + "," + condFlag + ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    public void addRewriteCondInProxy(String proxyDirective, String rulePattern, String ruleSubstitution,
                                      String condTestString, String condPattern) {
        logger.info("addRewriteCondInProxy (" + proxyDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    public void removeRewriteCondInDirectory(String directoryDirective, String rulePattern, String ruleSubstitution,
                                             String condTestString, String condPattern) {
        logger.info("removeRewriteCondInDirectory (" + directoryDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    public void removeRewriteCondInProxy(String proxyDirective, String rulePattern, String ruleSubstitution,
                                         String condTestString, String condPattern) {
        logger.info("removeRewriteCondInProxy (" + proxyDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeDirectoryDirective(String directoryDirective) {
        logger.info("removeDirectoryDirective(" + directoryDirective + ")");
        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void removeProxyDirective(String proxyDirective) {
        logger.info("removeProxyDirective(" + proxyDirective + ")");
        String confFileLocation = getRewriteConfigurationFile();

        List<String> fileStringList = loadConfigurationFile(confFileLocation);
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

        flushConfigurationFile(confFileLocation, newFileStringList);
    }

    /**
     * {@inheritDoc}
     */
    public void init(String rewriteConfigurationFile) {
        setRewriteConfigurationFile(rewriteConfigurationFile);
        logger.info("rewriteConfigurationFile=" +  rewriteConfigurationFile);
    }


    /**
     * {@inheritDoc}
     */
    private List<String> loadConfigurationFile(String filePath) {

        List<String> fileStringList = new LinkedList<String>();
        try {
            InputStream ips = new FileInputStream(filePath);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);

            String line;
            while ((line = br.readLine()) != null) {
                fileStringList.add(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileStringList;

    }

    /**
     * {@inheritDoc}
     */
    private void flushConfigurationFile(String filePath,
                                        List<String> fileStringList) {

        try {
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for (Iterator<String> iterator = fileStringList.iterator(); iterator.hasNext();) {
                String string = iterator.next();
                logger.info("flush : " + string);
                pw.println(string);
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRewriteConfigurationFile() {
        return rewriteConfigurationFile;
    }

    public void setRewriteConfigurationFile(String rewriteConfigurationFile) {
        this.rewriteConfigurationFile = rewriteConfigurationFile;
    }

    /**
     * Get the property file location located in JONAS_BASE/conf/rewritemanager.properties
     * with the key file.location
     *
     * @return the location of the rewritemanager.properties file
     */
    private String getRewritePropertyFileLocation() {
        JProp prop = JProp.getInstance(REWRITE_MANAGER_PROPERTY_FILE_NAME);
        return prop.getValue(REWRITE_CONF_FILE_LOCATION_PROPERTY);
    }

}
