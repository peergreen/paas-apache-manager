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
 * $Id: IRewriteManager.java 9287 2011-11-10 09:53:32Z richardd $
 * --------------------------------------------------------------------------
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
