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
 * $Id: RestConnectionTest.java 9002 2011-09-27 13:45:41Z gonzalem $
 * --------------------------------------------------------------------------
 */

package org.ow2.jasmine.agent.remote.jkmanager.rest.tests;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;
import static org.mockito.Mockito.*;
import static org.ops4j.pax.exam.junit.JUnitOptions.mockitoBundles;
import static org.junit.Assert.*;

import java.io.*;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
//import org.ow2.chameleon.testing.helpers.OSGiHelper;
import org.ow2.jonas.jpaas.apache.manager.jk.api.JkManagerService;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;


/**
 * Test class to check the availability of connections to the REST interface.
 * @author Miguel González
 */
@RunWith(JUnit4TestRunner.class)
public class RestConnectionTest {

    // Warning: This is a workaround! The maven-surefire-plugin messes
    //  the paxexam plugin, so we can't use the build-helper-maven-plugin
    //  to find an empty port and pass it through surefire as a system property.
    //  Anyway, this is open to extension if someone manages to make them
    //  work together.
    private static final String PORT = System.getProperty("rest.servlet.port", "62000");

    private static final String HOST = "http://localhost";

    private static final String PATH = "/jkmanager";

    public static final String ENTRY_POINT = HOST + ":" + PORT + PATH;

    private static final String MOCK_STRING = "mock";

    private static final Logger log = Logger.getLogger(RestConnectionTest.class);

    @Inject
    private BundleContext bundleContext;

    private ServiceRegistration jkManagerRegistration;

    private JkManagerService jkManager;

    @Before
    public void setUp() {
        jkManager = mock(JkManagerService.class);
        when(jkManager.getId()).thenReturn(MOCK_STRING);
        jkManagerRegistration = bundleContext.registerService(JkManagerService.class.getName(), jkManager, null);
    }

    @After
    public void tearDown(){
    }

    @Configuration
    public static Option[] configuration()
    {
        log.info("Entry point: '" + ENTRY_POINT + "'");
        Option[] options = options(webProfile(),
                // Uncomment this line to enable debug mode
//                vmOptions(vmOption("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")),
                provision(

                        // Jersey bundles
                        mavenBundle("com.sun.jersey", "jersey-core", "1.8"),
                        mavenBundle("com.sun.jersey", "jersey-server", "1.8"),
                        mavenBundle("com.sun.jersey", "jersey-client", "1.8"),
                        // Logs - Required by DeployMe REST
                        mavenBundle("org.ow2.bundles", "ow2-util-log").version("1.0.27"),
                        mavenBundle("org.ow2.bundles", "ow2-util-i18n").version("1.0.27"),
                        mavenBundle("org.ops4j.pax.logging", "pax-logging-api").versionAsInProject(),
                        // Felix Shell
                        mavenBundle()
                                .groupId("org.apache.felix")
                                .artifactId("org.apache.felix.shell")
                                .versionAsInProject(),
                        // OSGi helper
                        mavenBundle().groupId("org.ow2.chameleon.testing")
                                .artifactId("osgi-helpers")
                                .versionAsInProject(),
                        mavenBundle()
                                .groupId("org.apache.felix")
                                .artifactId("org.apache.felix.ipojo")
                                .version("1.8.0"),
                        mavenBundle()
                                .groupId("org.apache.felix")
                                .artifactId("org.apache.felix.ipojo.arch")
                                .version("1.6.0"),

                        mockitoBundles(),

                        // ClusterDaemon bundles
                        mavenBundle("org.ow2.jasmine", "agent.common")
                                .versionAsInProject(),
                        mavenBundle("org.ow2.jasmine", "agent.remote.jkmanager.rest")
                                .versionAsInProject()
                ),
                systemProperty("org.osgi.service.http.port").value(PORT),
                felix());
        return options;
    }


    @Test
    public void testGetId()
            throws JAXBException, IOException, SAXException {
        WebResource wr = Client.create().resource(ENTRY_POINT).path("/id");

        String id = wr.get(String.class);
        assertEquals(MOCK_STRING, id);
        verify(jkManager).getId();
    }

    @Test
    public void testIsConfigured() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/worker/" + MOCK_STRING + "/configured");

        String response = wr.get(String.class);

        boolean b = Boolean.parseBoolean(response);
        assertFalse(b);
        verify(jkManager).isConfigured(MOCK_STRING);
    }

    @Test
    public void testReload() {
        WebResource wr = Client.create().resource(ENTRY_POINT).path("/reload");

        wr.post();

        verify(jkManager).reload();
    }

    @Test
    public void testIsEnabled() {
        WebResource wr = Client.create().resource(ENTRY_POINT).path("/worker/"
                + MOCK_STRING + "/enabled");

        String response = wr.get(String.class);

        boolean b = Boolean.parseBoolean(response);
        assertFalse(b);
        verify(jkManager).isEnabled(MOCK_STRING);

    }

    @Test
    public void testRemoveNamedWorker() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/worker/" + MOCK_STRING);

        wr.delete();

        verify(jkManager).removeNamedWorker(MOCK_STRING);
    }

    @Test
    public void testDisableNamedWorker() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/worker/" + MOCK_STRING + "/disable");

        wr.post();

        verify(jkManager).disableNamedWorker(MOCK_STRING);
    }

    @Test
    public void testEnableNamedWorker() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/worker/" + MOCK_STRING + "/enable");

        wr.post();

        verify(jkManager).enableNamedWorker(MOCK_STRING);
    }

    @Test
    public void testStopNamedWorker() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/worker/" + MOCK_STRING + "/stop");

        wr.post();

        verify(jkManager).stopNamedWorker(MOCK_STRING);
    }

    @Test
    public void testInit() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/init");

        Form form = new Form();

        form.add("workersConfigurationFileName", MOCK_STRING);
        form.add("reloadCmd", MOCK_STRING);

        wr.post(form);

        verify(jkManager).init(MOCK_STRING, MOCK_STRING);
    }

    @Test
    public void testAddNamedWorkerWithLbFactor() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/worker/" + MOCK_STRING + "/loadbalancer/" + MOCK_STRING);

        Form form = new Form();

        form.add("host", MOCK_STRING);
        form.add("port", MOCK_STRING);
        form.add("lbFactor", MOCK_STRING);

        wr.post(form);

        verify(jkManager).addNamedWorker(MOCK_STRING, MOCK_STRING,
                MOCK_STRING, MOCK_STRING, MOCK_STRING);

    }

    @Test
    public void testAddNamedWorkerWithoutLbFactor() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/worker/" + MOCK_STRING + "/loadbalancer/" + MOCK_STRING);

        Form form = new Form();

        form.add("host", MOCK_STRING);
        form.add("port", MOCK_STRING);

        wr.post(form);

        verify(jkManager).addNamedWorker(MOCK_STRING, MOCK_STRING,
                MOCK_STRING, MOCK_STRING);

    }

    @Test
    public void testAddWorkerWithLbFactor() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/worker/loadbalancer/" + MOCK_STRING);

        Form form = new Form();

        form.add("host", MOCK_STRING);
        form.add("port", MOCK_STRING);
        form.add("lbFactor", MOCK_STRING);

        wr.post(form);

        verify(jkManager).addWorker(MOCK_STRING, MOCK_STRING,
                MOCK_STRING, MOCK_STRING);

    }

    @Test
    public void testAddWorkerWithoutLbFactor() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/worker/loadbalancer/" + MOCK_STRING);

        Form form = new Form();

        form.add("host", MOCK_STRING);
        form.add("port", MOCK_STRING);

        wr.post(form);

        verify(jkManager).addWorker(MOCK_STRING, MOCK_STRING, MOCK_STRING);

    }

    @Test
    public void testMount() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/mount/" + MOCK_STRING);

        Form form = new Form();

        form.add("path", MOCK_STRING);

        wr.post(form);

        verify(jkManager).mount(MOCK_STRING, MOCK_STRING);

    }

    @Test
    public void testUnmountLoadBalancer() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/mount/" + MOCK_STRING);

        wr.delete();

        verify(jkManager).unmount(MOCK_STRING);

    }

    @Test
    public void testUnmountLoadBalancerPath() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/mount/" + MOCK_STRING);

        wr.queryParam("path", MOCK_STRING).delete();

        verify(jkManager).unmount(MOCK_STRING, MOCK_STRING);

    }

    @Test
    public void testUnmountWithoutArguments() {
        WebResource wr = Client.create().resource(ENTRY_POINT)
                .path("/mount");

        wr.delete();

        verify(jkManager).unmount();

    }


}
