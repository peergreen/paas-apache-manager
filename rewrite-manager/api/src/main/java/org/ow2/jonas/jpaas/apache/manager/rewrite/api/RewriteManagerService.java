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

package org.ow2.jonas.jpaas.apache.manager.rewrite.api;

/**
 * The interface describing the RewriteManagerService available on a host to change the configuration of
 * the mod_rewrite plugin.
 *
 * @author David Richard
 */
public interface RewriteManagerService {

    /**
     * Add a RewriteRule with a flag in a directory directive
     *
     * @param directoryDirective
     *            the name of the directory directive
     * @param pattern
     *            the rule's pattern
     * @param substitution
     *            the rule's substitution
     * @param flag
     *            the rule's flag
     */
    void addRewriteRuleInDirectory(String directoryDirective, String pattern, String substitution, String flag);

    /**
     * Add a RewriteRule without flag in a directory directive
     *
     * @param directoryDirective
     *            the name of the directory directive
     * @param pattern
     *            the rule's pattern
     * @param substitution
     *            the rule's substitution
     */
    void addRewriteRuleInDirectory(String directoryDirective, String pattern, String substitution);

    /**
     * Add a RewriteRule with flag in a proxy directive
     *
     * @param proxyDirective
     *            the name of the proxy directive
     * @param pattern
     *            the rule's pattern
     * @param substitution
     *            the rule's substitution
     * @param flag
     *            the rule's flag
     */
    void addRewriteRuleInProxy(String proxyDirective, String pattern, String substitution, String flag);

    /**
     * Add a RewriteRule without flag in a proxy directive
     *
     * @param proxyDirective
     *            the name of the proxy directive
     * @param pattern
     *            the rule's pattern
     * @param substitution
     *            the rule's substitution
     */
    void addRewriteRuleInProxy(String proxyDirective, String pattern, String substitution);

    /**
     * Remove a RewriteRule and its RewriteCond in a directory directive
     *
     * @param directoryDirective
     *            the name of the directory directive
     * @param pattern
     *            the rule's pattern
     * @param substitution
     *            the rule's substitution
     */
    void removeRewriteRuleInDirectory(String directoryDirective, String pattern, String substitution);

    /**
     * Remove a RewriteRule and its RewriteCond in a proxy directive
     *
     * @param proxyDirective
     *            the name of the proxy directive
     * @param pattern
     *            the rule's pattern
     * @param substitution
     *            the rule's substitution
     */
    void removeRewriteRuleInProxy(String proxyDirective, String pattern, String substitution);

    /**
     * Add a RewriteBase in a directory directive
     *
     * @param directoryDirective
     *            the name of the directory directive
     * @param urlPath
     *            the base URL path
     */
    void addRewriteBaseInDirectory(String directoryDirective, String urlPath);

    /**
     * Add a RewriteBase in a proxy directive
     *
     * @param proxyDirective
     *            the name of the proxy directive
     * @param urlPath
     *            the base URL path
     */
    void addRewriteBaseInProxy(String proxyDirective, String urlPath);

    /**
     * Remove a RewriteBase in a directory directive
     *
     * @param directoryDirective
     *            the name of the directory directive
     * @param urlPath
     *            the base URL path
     */
    void removeRewriteBaseInDirectory(String directoryDirective, String urlPath);

    /**
     * Remove a RewriteBase in a proxy directive
     *
     * @param proxyDirective
     *            the name of the proxy directive
     * @param urlPath
     *            the base URL path
     */
    void removeRewriteBaseInProxy(String proxyDirective, String urlPath);

    /**
     * Add a RewriteCond with flag for a specific RewriteRule in a directory directive.
     *
     * @param directoryDirective
     *            the name of the directory directive
     * @param rulePattern
     *            the rule's pattern
     * @param ruleSubstitution
     *            the rule's substitution
     * @param condTestString
     *            the condition's test
     * @param condPattern
     *            the condition's pattern
     * @param condFlag
     *            the condition's flag
     */
    void addRewriteCondInDirectory(String directoryDirective, String rulePattern, String ruleSubstitution,
                                   String condTestString, String condPattern, String condFlag);

    /**
     * Add a RewriteCond without flag for a specific RewriteRule in a directory directive.
     *
     * @param directoryDirective
     *            the name of the directory directive
     * @param rulePattern
     *            the rule's pattern
     * @param ruleSubstitution
     *            the rule's substitution
     * @param condTestString
     *            the condition's test
     * @param condPattern
     *            the condition's pattern
     */
    void addRewriteCondInDirectory(String directoryDirective, String rulePattern, String ruleSubstitution,
                                   String condTestString, String condPattern);

    /**
     * Add a RewriteCond with flag for a specific RewriteRule in a proxy directive.
     *
     * @param proxyDirective
     *            the name of the proxy directive
     * @param rulePattern
     *            the rule's pattern
     * @param ruleSubstitution
     *            the rule's substitution
     * @param condTestString
     *            the condition's test
     * @param condPattern
     *            the condition's pattern
     * @param condFlag
     *            the condition's flag
     */
    void addRewriteCondInProxy(String proxyDirective, String rulePattern, String ruleSubstitution,
                                   String condTestString, String condPattern, String condFlag);

    /**
     * Add a RewriteCond without flag for a specific RewriteRule in a proxy directive.
     *
     * @param proxyDirective
     *            the name of the proxy directive
     * @param rulePattern
     *            the rule's pattern
     * @param ruleSubstitution
     *            the rule's substitution
     * @param condTestString
     *            the condition's test
     * @param condPattern
     *            the condition's pattern
     */
    void addRewriteCondInProxy(String proxyDirective, String rulePattern, String ruleSubstitution,
                                   String condTestString, String condPattern);


    /**
     * Remove a RewriteCond in a directory directive.
     *
     * @param directoryDirective
     *            the name of the directory directive
     * @param rulePattern
     *            the rule's pattern
     * @param ruleSubstitution
     *            the rule's substitution
     * @param condTestString
     *            the condition's test
     * @param condPattern
     *            the condition's pattern
     */
    void removeRewriteCondInDirectory(String directoryDirective, String rulePattern, String ruleSubstitution,
                                   String condTestString, String condPattern);

    /**
     * Remove a RewriteCond in a proxy directive.
     *
     * @param proxyDirective
     *            the name of the proxy directive
     * @param rulePattern
     *            the rule's pattern
     * @param ruleSubstitution
     *            the rule's substitution
     * @param condTestString
     *            the condition's test
     * @param condPattern
     *            the condition's pattern
     */
    void removeRewriteCondInProxy(String proxyDirective, String rulePattern, String ruleSubstitution,
                                   String condTestString, String condPattern);


    /**
     * Remove a directory directive.
     *
     * @param directoryDirective
     *            the name of the directory directive
     */
    void removeDirectoryDirective(String directoryDirective);

    /**
     * Remove a proxy directive.
     *
     * @param proxyDirective
     *            the name of the proxy directive
     */
    void removeProxyDirective(String proxyDirective);

    /**
     * init configuration
     * @param rewriteConfigurationFile
     *            Path to the rewrite.conf file
     */
    void init(String rewriteConfigurationFile);

}
