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

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * RewriteManager Rest Interface.
 *
 * @author David Richard
 */
@Path("/")
public interface IRewriteManager {



    @POST
    @Path("/directory/{directorydirective}/rule")
    void addRewriteRuleInDirectory(@PathParam("directorydirective") String directoryDirective,
                           @FormParam("pattern") String pattern,
                           @FormParam("substitution") String substitution,
                           @FormParam("flag") String flag);

    @POST
    @Path("/proxy/{proxydirective}/rule")
    void addRewriteRuleInProxy(@PathParam("proxydirective") String proxyDirective,
                           @FormParam("pattern") String pattern,
                           @FormParam("substitution") String substitution,
                           @FormParam("flag") String flag);


    @DELETE
    @Path("/directory/{directorydirective}/rule")
    void removeRewriteRuleInDirectory(@PathParam("directorydirective") String directoryDirective,
                              @FormParam("pattern") String pattern,
                              @FormParam("substitution") String substitution);

    @DELETE
    @Path("/proxy/{proxydirective}/rule")
    void removeRewriteRuleInProxy(@PathParam("proxydirective") String proxyDirective,
                              @FormParam("pattern") String pattern,
                              @FormParam("substitution") String substitution);

    @POST
    @Path("/directory/{directorydirective}/base")
    void addRewriteBaseInDirectory(@PathParam("directorydirective") String directoryDirective,
                              @FormParam("path") String urlPath);

    @POST
    @Path("/proxy/{proxydirective}/base")
    void addRewriteBaseInProxy(@PathParam("proxydirective") String proxyDirective,
                              @FormParam("path") String urlPath);

    @DELETE
    @Path("/directory/{directorydirective}/base")
    void removeRewriteBaseInDirectory(@PathParam("directorydirective") String directoryDirective,
                              @FormParam("path") String urlPath);

    @DELETE
    @Path("/proxy/{proxydirective}/base")
    void removeRewriteBaseInProxy(@PathParam("proxydirective") String proxyDirective,
                              @FormParam("path") String urlPath);
    
    @POST
    @Path("/directory/{directorydirective}/cond")
    void addRewriteCondInDirectory(@PathParam("directorydirective") String directoryDirective,
                              @FormParam("rulepattern") String rulePattern,
                              @FormParam("rulesubstitution") String ruleSubstitution,
                              @FormParam("condtest") String condTestString,
                              @FormParam("condpattern") String condPattern,
                              @FormParam("condflag") String condFlag);

    @POST
    @Path("/proxy/{proxydirective}/cond")
    void addRewriteCondInProxy(@PathParam("proxydirective") String proxyDirective,
                              @FormParam("rulepattern") String rulePattern,
                              @FormParam("rulesubstitution") String ruleSubstitution,
                              @FormParam("condtest") String condTestString,
                              @FormParam("condpattern") String condPattern,
                              @FormParam("condflag") String condFlag);

    @DELETE
    @Path("/directory/{directorydirective}/cond")
    void removeRewriteCondInDirectory(@PathParam("directorydirective") String directoryDirective,
                              @FormParam("rulepattern") String rulePattern,
                              @FormParam("rulesubstitution") String ruleSubstitution,
                              @FormParam("condtest") String condTestString,
                              @FormParam("condpattern") String condPattern);
    
    @DELETE
    @Path("/proxy/{proxydirective}/cond")
    void removeRewriteCondInProxy(@PathParam("proxydirective") String proxyDirective,
                              @FormParam("rulepattern") String rulePattern,
                              @FormParam("rulesubstitution") String ruleSubstitution,
                              @FormParam("condtest") String condTestString,
                              @FormParam("condpattern") String condPattern);
    
    @DELETE
    @Path("/directory/{directorydirective}")
    void removeDirectoryDirective(@PathParam("directorydirective") String directoryDirective);

    @DELETE
    @Path("/proxy/{proxydirective}")
    void removeProxyDirective(@PathParam("proxydirective") String proxyDirective);


    @POST
    @Path("/init")
    void init(@FormParam("rewriteConfigurationFile") String rewriteConfigurationFile);

}
