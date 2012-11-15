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

package org.ow2.jonas.jpaas.apache.manager.rewrite.rest;

import org.ow2.jonas.jpaas.apache.manager.rewrite.api.RewriteManagerService;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

/**
 * The implementation of the IRewriteManager Interface
 *
 * @author David Richard
 */
public class RewriteManager implements IRewriteManager {

    private RewriteManagerService rewriteManagerService;

    /**
     * The logger.
     */
    private Log logger = LogFactory.getLog(this.getClass());

    public RewriteManager(RewriteManagerService rewriteManagerService) {
        this.rewriteManagerService=rewriteManagerService;
    }

    public void addRewriteRuleInDirectory(String directoryDirective, String pattern, String substitution, String flag) {
        logger.debug("Inbound call to addRewriteRuleInDirectory()");
        logger.info("addRewriteRuleInDirectory (" + directoryDirective + "," + pattern + ","
                + substitution + ", " + flag + ")");

        boolean isSetFlag = (flag != null);
        if(isSetFlag) {
            rewriteManagerService.addRewriteRuleInDirectory(directoryDirective,pattern,substitution,flag);
        } else {
            rewriteManagerService.addRewriteRuleInDirectory(directoryDirective, pattern, substitution);
        }
    }
    
    public void addRewriteRuleInProxy(String proxyDirective, String pattern, String substitution, String flag) {
        logger.debug("Inbound call to addRewriteRuleInProxy()");
        logger.info("addRewriteRuleInProxy (" + proxyDirective + "," + pattern + ","
                + substitution + ", " + flag + ")");

        boolean isSetFlag = (flag != null);
        if(isSetFlag) {
            rewriteManagerService.addRewriteRuleInProxy(proxyDirective,pattern,substitution,flag);
        } else {
            rewriteManagerService.addRewriteRuleInProxy(proxyDirective, pattern, substitution);
        }
    }

    public void removeRewriteRuleInDirectory(String directoryDirective, String pattern, String substitution) {
        logger.debug("Inbound call to removeRewriteRuleInDirectory()");
        logger.info("removeRewriteRuleInDirectory (" + directoryDirective + "," + pattern + "," + substitution + ")");
        
        rewriteManagerService.removeRewriteRuleInDirectory(directoryDirective,pattern,substitution);
    }
    
    public void removeRewriteRuleInProxy(String proxyDirective, String pattern, String substitution) {
        logger.debug("Inbound call to removeRewriteRuleInProxy()");
        logger.info("removeRewriteRuleInProxy (" + proxyDirective + "," + pattern + "," + substitution + ")");
        
        rewriteManagerService.removeRewriteRuleInProxy(proxyDirective,pattern,substitution);
    }
    
    public void addRewriteBaseInDirectory(String directoryDirective, String urlPath) {
        logger.debug("Inbound call to addRewriteBaseInDirectory()");
        logger.info("addRewriteBaseInDirectory (" + directoryDirective + "," + urlPath + ")");

        rewriteManagerService.addRewriteBaseInDirectory(directoryDirective, urlPath);
    }
    
    public void addRewriteBaseInProxy(String proxyDirective, String urlPath) {
        logger.debug("Inbound call to addRewriteBaseInProxy()");
        logger.info("addRewriteBaseInProxy (" + proxyDirective + "," + urlPath + ")");

        rewriteManagerService.addRewriteBaseInProxy(proxyDirective, urlPath);
    }

    public void removeRewriteBaseInDirectory(String directoryDirective, String urlPath) {
        logger.debug("Inbound call to removeRewriteBaseInDirectory()");
        logger.info("removeRewriteBaseInDirectory (" + directoryDirective + "," + urlPath + ")");

        rewriteManagerService.removeRewriteBaseInDirectory(directoryDirective,urlPath);
    }

    public void removeRewriteBaseInProxy(String proxyDirective, String urlPath) {
        logger.debug("Inbound call to removeRewriteBaseInProxy()");
        logger.info("removeRewriteBaseInProxy (" + proxyDirective + "," + urlPath + ")");

        rewriteManagerService.removeRewriteBaseInProxy(proxyDirective,urlPath);
    }

    public void addRewriteCondInDirectory(String directoryDirective, String rulePattern, String ruleSubstitution,
                                      String condTestString, String condPattern, String condFlag) {
        logger.debug("Inbound call to addRewriteCondInDirectory()");
        logger.info("addRewriteCondInDirectory (" + directoryDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        boolean isSetFlag = (condFlag != null);
        if(isSetFlag) {
            rewriteManagerService.addRewriteCondInDirectory(directoryDirective,rulePattern,ruleSubstitution,
                    condTestString,condPattern,condFlag);
        } else {
            rewriteManagerService.addRewriteCondInDirectory(directoryDirective, rulePattern, ruleSubstitution,
                    condTestString,condPattern);
        }
    }

    public void addRewriteCondInProxy(String proxyDirective, String rulePattern, String ruleSubstitution,
                                      String condTestString, String condPattern, String condFlag) {
        logger.debug("Inbound call to addRewriteCondInProxy()");
        logger.info("addRewriteCondInProxy (" + proxyDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        boolean isSetFlag = (condFlag != null);
        if(isSetFlag) {
            rewriteManagerService.addRewriteCondInProxy(proxyDirective,rulePattern,ruleSubstitution,
                    condTestString,condPattern,condFlag);
        } else {
            rewriteManagerService.addRewriteCondInProxy(proxyDirective, rulePattern, ruleSubstitution,
                    condTestString,condPattern);
        }
    }

    public void removeRewriteCondInDirectory(String directoryDirective, String rulePattern, String ruleSubstitution,
                                             String condTestString, String condPattern) {
        logger.debug("Inbound call to removeRewriteCondInDirectory()");
        logger.info("removeRewriteCondInDirectory (" + directoryDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        rewriteManagerService.removeRewriteCondInDirectory(directoryDirective,rulePattern,ruleSubstitution,
                condTestString,condPattern);
    }

    public void removeRewriteCondInProxy(String proxyDirective, String rulePattern, String ruleSubstitution,
                                   String condTestString, String condPattern) {
        logger.debug("Inbound call to removeRewriteCondInProxy()");
        logger.info("removeRewriteCondInProxy (" + proxyDirective + "," + rulePattern + ","
                + ruleSubstitution + "," + condTestString + "," + condPattern +  ")");

        rewriteManagerService.removeRewriteCondInProxy(proxyDirective,rulePattern,ruleSubstitution,condTestString,
                condPattern);
    }

    public void removeDirectoryDirective(String directoryDirective) {
        logger.debug("Inbound call to removeDirectoryDirective()");
        logger.info("removeDirectoryDirective(" + directoryDirective + ")");    
       rewriteManagerService.removeDirectoryDirective(directoryDirective);
    }
    
    public void removeProxyDirective(String proxyDirective) {
        logger.debug("Inbound call to removeProxyDirective()");
        logger.info("removeProxyDirective(" + proxyDirective + ")");    
       rewriteManagerService.removeProxyDirective(proxyDirective);
    }

    public void init(String rewriteConfigurationFile) {
        logger.debug("Inbound call to init()");
        logger.debug("(rewriteConfigurationFile) = ("
                + rewriteConfigurationFile + ")");
        rewriteManagerService.init(rewriteConfigurationFile);
    }

}
