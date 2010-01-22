/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.grouper.xml.export;

import junit.textui.TestRunner;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Member;
import edu.internet2.middleware.grouper.helper.GrouperTest;
import edu.internet2.middleware.grouper.member.TestMember;
import edu.internet2.middleware.grouper.misc.GrouperVersion;


/**
 *
 */
public class XmlExportMemberTest extends GrouperTest {

  /** grouperSession */
  private GrouperSession grouperSession;

  
  /**
   * 
   * @see edu.internet2.middleware.grouper.helper.GrouperTest#setUp()
   */
  @Override
  protected void setUp() {
    super.setUp();
    
    this.grouperSession = GrouperSession.startRootSession();
  }

 
  /**
   * 
   * @see edu.internet2.middleware.grouper.helper.GrouperTest#tearDown()
   */
  @Override
  protected void tearDown() {
    
    GrouperSession.stopQuietly(this.grouperSession);
    
    super.tearDown();
  }



  /**
   * 
   * @param args
   */
  public static void main(String[] args) {

    TestRunner.run(XmlExportMemberTest.class);
    //TestRunner.run(new XmlExportMemberTest("testConvertToString"));

  }
  
  /**
   * 
   * @param name
   */
  public XmlExportMemberTest(String name) {
    super(name);
  }

  /**
   * 
   */
  public void testConvertToXml() {
    
    XmlExportMember xmlExportMember = new XmlExportMember();
    
    xmlExportMember.setContextId("contextId");
    xmlExportMember.setHibernateVersionNumber(3L);
    xmlExportMember.setSourceId("sourceId");
    xmlExportMember.setSubjectId("subjectId");
    xmlExportMember.setSubjectType("subjectType");
    xmlExportMember.setUuid("uuid");
    
    String xml = xmlExportMember.toXml(new GrouperVersion(GrouperVersion.GROUPER_VERSION));
    
    xmlExportMember = XmlExportMember.fromXml(new GrouperVersion(GrouperVersion.GROUPER_VERSION), xml);
    
    assertEquals("contextId", xmlExportMember.getContextId());
    assertEquals(3L, xmlExportMember.getHibernateVersionNumber());
    assertEquals("subjectId", xmlExportMember.getSubjectId());
    assertEquals("sourceId", xmlExportMember.getSourceId());
    assertEquals("subjectType", xmlExportMember.getSubjectType());
    assertEquals("uuid", xmlExportMember.getUuid());
    
  }
  
  /**
   * 
   */
  public void testConvertToMember() {
    Member member = TestMember.exampleMember();
    
    XmlExportMember xmlExportMember = new XmlExportMember(new GrouperVersion(GrouperVersion.GROUPER_VERSION), member);

    //now go back
    member = xmlExportMember.toMember();
    
    assertEquals("contextId", member.getContextId());
    assertEquals(new Long(3L), member.getHibernateVersionNumber());
    assertEquals("subjectId", member.getSubjectId());
    assertEquals("subjectSourceId", member.getSubjectSourceId());
    assertEquals("subjectTypeId", member.getSubjectTypeId());
    assertEquals("uuid", member.getUuid());
    
  }
}
