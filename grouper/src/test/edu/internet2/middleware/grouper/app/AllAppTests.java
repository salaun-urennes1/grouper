/**
 * Copyright 2014 Internet2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * @author mchyzer
 * $Id: AllAppTests.java,v 1.1 2009-03-20 19:56:42 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.app;

import edu.internet2.middleware.grouper.app.attestation.AllAttestationTests;
import edu.internet2.middleware.grouper.app.deprovisioning.AllDeprovisioningTests;
import edu.internet2.middleware.grouper.app.gsh.AllGshTests;
import edu.internet2.middleware.grouper.app.loader.AllLoaderTests;
import edu.internet2.middleware.grouper.app.messaging.AllAppMessagingTests;
import edu.internet2.middleware.grouper.app.messaging.MessageConsumerDaemonTest;
import edu.internet2.middleware.grouper.app.provisioning.AllProvisioningTests;
import edu.internet2.middleware.grouper.app.reports.AllReportsTests;
import edu.internet2.middleware.grouper.app.tableSync.AllTableSyncTests;
import edu.internet2.middleware.grouper.app.upgradeTasks.AllUpgradeTasksTests;
import edu.internet2.middleware.grouper.app.usdu.AllUsduTests;
import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 *
 */
public class AllAppTests {

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for edu.internet2.middleware.grouper.app");
    //$JUnit-BEGIN$
    suite.addTestSuite(MessageConsumerDaemonTest.class);
    //$JUnit-END$
    if (GrouperConfig.retrieveConfig().propertyValueBoolean("junit.test.gsh", false)) {
      suite.addTest(AllGshTests.suite());
    }
    
    if (GrouperConfig.retrieveConfig().propertyValueBoolean("junit.test.loader", true)) {
      suite.addTest(AllLoaderTests.suite());
    }
    
    // TODO change to true in 2.5+
    if (GrouperConfig.retrieveConfig().propertyValueBoolean("junit.test.tableSync", false)) {
      suite.addTest(AllTableSyncTests.suite());
    }
    
    suite.addTest(AllAttestationTests.suite());
    suite.addTest(AllDeprovisioningTests.suite());
    suite.addTest(AllAppMessagingTests.suite());
    suite.addTest(AllProvisioningTests.suite());
    suite.addTest(AllReportsTests.suite());
    suite.addTest(AllUsduTests.suite());
    suite.addTest(AllUpgradeTasksTests.suite());
    return suite;
  }

}
