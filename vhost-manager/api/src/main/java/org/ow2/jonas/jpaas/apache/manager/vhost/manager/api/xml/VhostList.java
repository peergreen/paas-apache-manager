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

package org.ow2.jonas.jpaas.apache.manager.vhost.manager.api.xml;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Vhost XML element
 * @author David Richard
 */
@XmlRootElement
public class VhostList {

    private List<Vhost> vhost;


    public VhostList() {
        this.vhost = new ArrayList<Vhost>();
    }

    public VhostList(List<Vhost> vhost) {
        this.vhost = vhost;
    }

    public List<Vhost> getVhost() {
        return vhost;
    }

    public void setVhost(List<Vhost> vhost) {
        this.vhost = vhost;
    }

}
