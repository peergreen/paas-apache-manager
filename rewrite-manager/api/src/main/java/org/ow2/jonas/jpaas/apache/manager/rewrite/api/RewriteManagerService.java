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
 * $Id: RewriteManagerService.java 9287 2011-11-10 09:53:32Z richardd $
 * --------------------------------------------------------------------------
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
