/*
 * @author mchyzer
 * $Id: GrouperClientWsTest.java,v 1.9 2009-12-30 04:23:02 mchyzer Exp $
 */
package edu.internet2.middleware.grouperClient.poc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.textui.TestRunner;

import org.apache.commons.lang.StringUtils;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GroupSave;
import edu.internet2.middleware.grouper.GroupType;
import edu.internet2.middleware.grouper.GroupTypeFinder;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.SubjectFinder;
import edu.internet2.middleware.grouper.helper.GroupHelper;
import edu.internet2.middleware.grouper.helper.GrouperTest;
import edu.internet2.middleware.grouper.helper.SessionHelper;
import edu.internet2.middleware.grouper.helper.SubjectTestHelper;
import edu.internet2.middleware.grouper.misc.CompositeType;
import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouper.ws.util.RestClientSettings;
import edu.internet2.middleware.grouperClient.GrouperClient;
import edu.internet2.middleware.grouperClient.api.GcGroupSave;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;
import edu.internet2.middleware.grouperClient.ws.GcWebServiceError;
import edu.internet2.middleware.grouperClient.ws.GrouperClientWs;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroup;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupSaveResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupToSave;
import edu.internet2.middleware.grouperClient.ws.beans.WsMemberChangeSubjectResults;
import edu.internet2.middleware.subject.Subject;

/**
 * 
 */
public class GrouperClientWsTest extends GrouperTest {

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    TestRunner.run(new GrouperClientWsTest("testAssignGrouperPrivileges"));
    //TestRunner.run(new GrouperClientWsTest("testGroupSaveLookupNameSame"));
    //TestRunner.run(new GrouperClientWsTest("testGroupSaveNoLookup"));
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.helper.GrouperTest#setUp()
   */
  @Override
  protected void setUp() {

    // dont do this, it deletes types
    // super.setUp();

    String wsUserLabel = GrouperClientUtils.propertiesValue(
        "grouperClient.webService.user.label", true);
    String wsUserString = GrouperClientUtils.propertiesValue(
        "grouperClient.webService." + wsUserLabel, true);

    RestClientSettings.resetData(wsUserString, false);

    GrouperClientUtils.grouperClientOverrideMap().put("encrypt.key",
        "sdfklj24lkj34lk34");
    GrouperClientUtils.grouperClientOverrideMap().put(
        "encrypt.disableExternalFileLookup", "false");

    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.addMember.output",
            "Index ${index}: success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: ${wsSubject.id}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.getMembers.output",
            "GroupIndex ${groupIndex}: success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: group: ${wsGroup.name}: subjectIndex: ${subjectIndex}: ${wsSubject.id}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.deleteMember.output",
            "Index ${index}: success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: ${wsSubject.id}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.hasMember.output",
            "Index ${index}: success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: ${wsSubject.id}: ${hasMember}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.getGroups.output",
            "SubjectIndex ${subjectIndex}: success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: subject: ${wsSubject.id}: groupIndex: ${groupIndex}: ${wsGroup.name}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.groupSave.output",
            "Success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: ${wsGroup.name}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.stemSave.output",
            "Success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: ${wsStem.name}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.groupDelete.output",
            "Index ${index}: success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: ${wsGroup.name}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.stemDelete.output",
            "Index ${index}: success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: ${wsStem.name}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.getGrouperPrivilegesLite.output",
            "Index ${index}: success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: ${objectType}: ${objectName}: subject: ${wsSubject.id}: ${wsGrouperPrivilegeResult.privilegeType}: ${wsGrouperPrivilegeResult.privilegeName}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.assignGrouperPrivilegesLite.output",
            "Success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: ${objectType}: ${objectName}: subject: ${wsSubject.id}: ${wsAssignGrouperPrivilegesLiteResult.privilegeType}: ${wsAssignGrouperPrivilegesLiteResult.privilegeName}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.findGroups.output",
            "Index ${index}: name: ${wsGroup.name}, displayName: ${wsGroup.displayName}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.findStems.output",
            "Index ${index}: name: ${wsStem.name}, displayName: ${wsStem.displayName}$newline$");
    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.memberChangeSubject.output",
            "Success: ${resultMetadata.success}: code: ${resultMetadata.resultCode}: oldSubject: ${wsSubjectOld.id}, newSubject: ${wsSubjectNew.id}$newline$");

    GrouperClientUtils
    .grouperClientOverrideMap()
    .put(
        "webService.getSubjects.output",
        "Index: ${index}: success: ${success}, code: ${wsSubject.resultCode}, subject: ${wsSubject.id}$newline$");
    
    GrouperClientUtils.grouperClientOverrideMap().put(
        "grouperClient.alias.subjectIds", "pennIds");
    GrouperClientUtils.grouperClientOverrideMap().put(
        "grouperClient.alias.subjectIdentifiers", "pennKeys");
    GrouperClientUtils.grouperClientOverrideMap().put(
        "grouperClient.alias.SubjectId", "PennId");
    GrouperClientUtils.grouperClientOverrideMap().put(
        "grouperClient.alias.SubjectIdentifier", "PennKey");
    GrouperClientUtils.grouperClientOverrideMap().put(
        "grouperClient.alias.subjectId", "pennId");
    GrouperClientUtils.grouperClientOverrideMap().put(
        "grouperClient.alias.subjectIdentifier", "pennKey");

    GrouperClientUtils
        .grouperClientOverrideMap()
        .put(
            "webService.hasMember.output",
            "Index ${index}: success: ${wsHasMemberResult.resultMetadata.success}: code: ${wsHasMemberResult.resultMetadata.resultCode}: ${wsHasMemberResult.wsSubject.id}: ${hasMember}$newline$");

    GrouperClient.exitOnError = false;
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.helper.GrouperTest#tearDown()
   */
  @Override
  protected void tearDown() {

    GrouperClientUtils.grouperClientOverrideMap().clear();

    super.tearDown();

  }

  /**
   * @param name
   */
  public GrouperClientWsTest(String name) {
    super(name);
  }

  /**
   * @throws Exception
   */
  public void testAddMember() throws Exception {

    // make sure group exists
    // GrouperSession grouperSession = GrouperSession.startRootSession();
    // Group group = Group.saveGroup(grouperSession, "aStem:aGroup", null,
    // "aStem:aGroup", "aGroup", null, null, true);
    //    
    // //give permissions
    // String wsUserLabel =
    // GrouperClientUtils.propertiesValue("grouperClient.webService.user.label",
    // true);
    // String wsUserString =
    // GrouperClientUtils.propertiesValue("grouperClient.webService." +
    // wsUserLabel, true);
    // Subject wsUser = SubjectFinder.findByIdOrIdentifier(wsUserString, true);

    GrouperSession grouperSession = GrouperSession.startRootSession();
    Group group = GroupFinder.findByName(grouperSession, "aStem:aGroup", true);
    
    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=addMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1",
                  " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern
          .compile("^Index (\\d+): success: T: code: ([A-Z_]+): (.*+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      // #####################################################
      // run again, should be already added
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=addMemberWs --groupName=aStem:aGroup --pennIds=test.subject.0,test.subject.1",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      // #####################################################
      // run again, with uuid
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=addMemberWs --groupUuid=" + group.getUuid() + " --pennIds=test.subject.0,test.subject.1",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=addMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=addMemberWs --groupName=aStem:aGroup --subjectIdentifiers=id.test.subject.0,id.test.subject.1 --outputTemplate=${index}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("01", output);

      // #####################################################
      // run again, with field
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=addMemberWs --groupName=aStem:aGroup --pennKeys=id.test.subject.0,id.test.subject.1 --fieldName=members",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("fieldName")
              && GrouperClientWs.mostRecentRequest.contains("members")
              && !GrouperClientWs.mostRecentRequest.contains("txType"));

      // #####################################################
      // run again, with txType
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=addMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --txType=NONE",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName")
              && !GrouperClientWs.mostRecentRequest.contains("members")
              && GrouperClientWs.mostRecentRequest.contains("txType")
              && GrouperClientWs.mostRecentRequest.contains("NONE")
              && !GrouperClientWs.mostRecentRequest
                  .contains("includeGroupDetail")
              && !GrouperClientWs.mostRecentRequest
                  .contains("includeSubjectDetail")
              && !GrouperClientWs.mostRecentRequest
                  .contains("includeGroupDetail")
              && !GrouperClientWs.mostRecentRequest
                  .contains("includeSubjectDetail"));

      // #####################################################
      // run again, with includeGroupDetail and includeSubjectDetail
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=addMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --includeGroupDetail=true --includeSubjectDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      assertTrue(!GrouperClientWs.mostRecentRequest.contains("txType")
          && !GrouperClientWs.mostRecentRequest.contains("NONE")
          && GrouperClientWs.mostRecentRequest.contains("includeGroupDetail")
          && GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));

      // #####################################################
      // run again, with subject attributes
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=addMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --subjectAttributeNames=name --outputTemplate=${index}:$space$${wsAddMemberResult.wsSubject.getAttributeValue(0)}$newline$",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertTrue(outputLines[0], outputLines[0]
          .contains("my name is test.subject.0"));

      assertTrue(outputLines[1], outputLines[1]
          .contains("my name is test.subject.1"));

      assertTrue(GrouperClientWs.mostRecentRequest.contains(">name<"));
      assertTrue(GrouperClientWs.mostRecentResponse
          .contains("my name is test.subject.0"));

      // #####################################################
      // run again, with default subject source
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=addMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --defaultSubjectSource=jdbc",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("jdbc"));

      // #####################################################
      // run again, subjects ids coming from file
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      String subjectIdsFileName = "subjectIdsFile_"
          + GrouperClientUtils.uniqueId() + ".txt";
      File subjectIdsFile = new File(subjectIdsFileName);

      GrouperClientUtils.saveStringIntoFile(subjectIdsFile,
          "test.subject.0\ntest.subject.1");

      try {
        GrouperClient.main(GrouperClientUtils.splitTrim(
            "--operation=addMemberWs --groupName=aStem:aGroup --subjectIdsFile="
                + subjectIdsFileName, " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));

        matcher = pattern.matcher(outputLines[1]);

        assertTrue(outputLines[1], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));

        // #####################################################
        // run again, with params
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=addMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --paramName0=whatever --paramValue0=someValue",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));

        matcher = pattern.matcher(outputLines[1]);

        assertTrue(outputLines[1], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));

        assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
            && GrouperClientWs.mostRecentRequest.contains("someValue"));

        // #####################################################
        // run again, with replaceAllExisting
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=addMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --replaceAllExisting=true",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));

        matcher = pattern.matcher(outputLines[1]);

        assertTrue(outputLines[1], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("SUCCESS_ALREADY_EXISTED", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));

        assertTrue(GrouperClientWs.mostRecentRequest
            .contains("replaceAllExisting"));

        // #######################################################
        // get members, make sure all there

        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=hasMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        pattern = Pattern
            .compile("^Index (\\d+): success: T: code: ([A-Z_]+): (.+): (false|true)$");
        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("IS_MEMBER", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));
        assertEquals("true", matcher.group(4));

        matcher = pattern.matcher(outputLines[1]);

        assertTrue(outputLines[1], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("IS_MEMBER", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));
        assertEquals("true", matcher.group(4));

      } finally {
        if (subjectIdsFile.exists()) {
          subjectIdsFile.delete();
        }
      }
    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testGetGrouperPrivilegeLite() throws Exception {

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGrouperPrivilegesLiteWs --groupName=aStem:aGroup --subjectId=test.subject.0",
                  " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern
          .compile("^Index (\\d+): success: T: code: ([A-Z_]+): (group|stem): (.+): subject: (.+): (.+): (.+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertEquals(GrouperClientUtils.length(outputLines), 3);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("admin", matcher.group(7));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("read", matcher.group(7));

      matcher = pattern.matcher(outputLines[2]);

      assertTrue(outputLines[2], matcher.matches());

      assertEquals("2", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("view", matcher.group(7));

      // #####################################################
      // run again with subject identifier, and privilege type
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGrouperPrivilegesLiteWs --groupName=aStem:aGroup --subjectIdentifier=id.test.subject.0 --privilegeType=access",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(GrouperClientUtils.length(outputLines), 3);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("admin", matcher.group(7));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("read", matcher.group(7));

      matcher = pattern.matcher(outputLines[2]);

      assertTrue(outputLines[2], matcher.matches());

      assertEquals("2", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("view", matcher.group(7));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("access")
              && GrouperClientWs.mostRecentRequest.contains("privilegeType")
              && GrouperClientWs.mostRecentRequest
                  .contains("id.test.subject.0"));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=getGrouperPrivilegesLiteWs --groupName=aStem:aGroup --subjectId=test.subject.0 --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGrouperPrivilegesLiteWs --groupName=aStem:aGroup --pennKey=id.test.subject.0 --outputTemplate=${index}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      assertEquals("012", output);

      System.setOut(systemOut);

      // #####################################################
      // run with privilege name
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGrouperPrivilegesLiteWs --groupName=aStem:aGroup --pennKey=id.test.subject.0 --privilegeName=admin",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, GrouperClientUtils.length(outputLines), 1);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals(outputLines[0], "SUCCESS_ALLOWED", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("admin", matcher.group(7));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("admin")
              && GrouperClientWs.mostRecentRequest.contains("privilegeName")
              && GrouperClientWs.mostRecentRequest
                  .contains("id.test.subject.0"));

      // #####################################################
      // run again, with stem
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGrouperPrivilegesLiteWs --stemName=aStem --pennKey=id.test.subject.0",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(GrouperClientUtils.length(outputLines), 2);
      assertTrue(outputLines[0], matcher.matches());

      // #####################################################
      // run again, with stem with no results
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGrouperPrivilegesLiteWs --stemName=aStem --pennKey=id.test.subject.6",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(outputLines[0], StringUtils.isBlank(output));

      // Index 0: success: T: code: SUCCESS: stem: aStem: subject:
      // test.subject.0: naming: create
      // Index 1: success: T: code: SUCCESS: stem: aStem: subject:
      // test.subject.0: naming: stem

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("stem", matcher.group(3));
      assertEquals("aStem", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("naming", matcher.group(6));
      assertEquals("create", matcher.group(7));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("stem", matcher.group(3));
      assertEquals("aStem", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("naming", matcher.group(6));
      assertEquals("stem", matcher.group(7));

      // #####################################################
      // run again, with includeGroupDetail and includeSubjectDetail
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGrouperPrivilegesLiteWs --groupName=aStem:aGroup --subjectId=test.subject.0 --includeGroupDetail=true --includeSubjectDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(3, GrouperClientUtils.length(outputLines));
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("admin", matcher.group(7));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("read", matcher.group(7));

      matcher = pattern.matcher(outputLines[2]);

      assertTrue(outputLines[2], matcher.matches());

      assertEquals("2", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("view", matcher.group(7));

      assertTrue(GrouperClientWs.mostRecentRequest
          .contains("includeGroupDetail")
          && GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));

      // #####################################################
      // run again, with subject attributes
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGrouperPrivilegesLiteWs --groupName=aStem:aGroup --subjectId=test.subject.0 --subjectAttributeNames=name --outputTemplate=${index}:$space$${wsSubject.getAttributeValue(0)}$newline$",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertTrue(outputLines[0], outputLines[0]
          .contains("my name is test.subject.0"));

      assertTrue(outputLines[1], outputLines[1]
          .contains("my name is test.subject.0"));

      assertTrue(GrouperClientWs.mostRecentRequest.contains(">name<"));
      assertTrue(GrouperClientWs.mostRecentResponse
          .contains("my name is test.subject.0"));

      // #####################################################
      // run again, with params
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGrouperPrivilegesLiteWs --groupName=aStem:aGroup --subjectId=test.subject.0 --paramName0=whatever --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(GrouperClientUtils.length(outputLines), 3);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("admin", matcher.group(7));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("read", matcher.group(7));

      matcher = pattern.matcher(outputLines[2]);

      assertTrue(outputLines[2], matcher.matches());

      assertEquals("2", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("group", matcher.group(3));
      assertEquals("aStem:aGroup", matcher.group(4));
      assertEquals("test.subject.0", matcher.group(5));
      assertEquals("access", matcher.group(6));
      assertEquals("view", matcher.group(7));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
          && GrouperClientWs.mostRecentRequest.contains("someValue"));

    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testAssignGrouperPrivilegeLite() throws Exception {

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesLiteWs --groupName=aStem:aGroup --subjectId=test.subject.0 --privilegeName=optin --allowed=true",
                  " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern
          .compile("^Success: T: code: ([A-Z_]+): (group|stem): (.+): subject: (.+): (.+): (.+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertEquals(GrouperClientUtils.length(outputLines), 1);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_ALLOWED", matcher.group(1));
      assertEquals("group", matcher.group(2));
      assertEquals("aStem:aGroup", matcher.group(3));
      assertEquals("test.subject.0", matcher.group(4));
      assertEquals("access", matcher.group(5));
      assertEquals("optin", matcher.group(6));

      // #####################################################
      // run again with subject identifier, and privilege type
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesLiteWs --groupName=aStem:aGroup --subjectIdentifier=id.test.subject.0 --privilegeType=access --privilegeName=optin --allowed=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(GrouperClientUtils.length(outputLines), 1);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_ALLOWED_ALREADY_EXISTED", matcher.group(1));
      assertEquals("group", matcher.group(2));
      assertEquals("aStem:aGroup", matcher.group(3));
      assertEquals("test.subject.0", matcher.group(4));
      assertEquals("access", matcher.group(5));
      assertEquals("optin", matcher.group(6));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("access")
              && GrouperClientWs.mostRecentRequest.contains("privilegeType")
              && GrouperClientWs.mostRecentRequest
                  .contains("id.test.subject.0"));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=assignGrouperPrivilegesLiteWs --groupName=aStem:aGroup --subjectId=test.subject.0 --privilegeName=optin --allowed=true --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesLiteWs --groupName=aStem:aGroup --pennKey=id.test.subject.0 --privilegeName=optin --allowed=true --outputTemplate=${wsSubject.identifierLookup}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      assertEquals("id.test.subject.0", output);

      System.setOut(systemOut);

      // #####################################################
      // run again, with stem
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesLiteWs --stemName=aStem --pennKey=id.test.subject.0 --privilegeName=stem --allowed=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(GrouperClientUtils.length(outputLines), 1);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_ALLOWED_ALREADY_EXISTED", matcher.group(1));
      assertEquals("stem", matcher.group(2));
      assertEquals("aStem", matcher.group(3));
      assertEquals("test.subject.0", matcher.group(4));
      assertEquals("naming", matcher.group(5));
      assertEquals("stem", matcher.group(6));

      // #####################################################
      // run again, with includeGroupDetail and includeSubjectDetail
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesLiteWs --groupName=aStem:aGroup --subjectId=test.subject.0 --includeGroupDetail=true --includeSubjectDetail=true --privilegeName=optin --allowed=false",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(GrouperClientUtils.length(outputLines), 1);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_NOT_ALLOWED", matcher.group(1));
      assertEquals("group", matcher.group(2));
      assertEquals("aStem:aGroup", matcher.group(3));
      assertEquals("test.subject.0", matcher.group(4));
      assertEquals("access", matcher.group(5));
      assertEquals("optin", matcher.group(6));

      assertTrue(GrouperClientWs.mostRecentRequest
          .contains("includeGroupDetail")
          && GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));

      // #####################################################
      // run again, with subject attributes
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesLiteWs --groupName=aStem:aGroup --subjectId=test.subject.0 --subjectAttributeNames=name --privilegeName=optin --allowed=false --outputTemplate=${index}:$space$${wsSubject.getAttributeValue(0)}$newline$",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertTrue(outputLines[0], outputLines[0]
          .contains("my name is test.subject.0"));

      assertTrue(GrouperClientWs.mostRecentRequest.contains(">name<"));
      assertTrue(GrouperClientWs.mostRecentResponse
          .contains("my name is test.subject.0"));

      // #####################################################
      // run again, with params
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesLiteWs --groupName=aStem:aGroup --privilegeName=optin --allowed=false --subjectId=test.subject.0 --paramName0=whatever --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(GrouperClientUtils.length(outputLines), 1);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_NOT_ALLOWED_DIDNT_EXIST", matcher.group(1));
      assertEquals("group", matcher.group(2));
      assertEquals("aStem:aGroup", matcher.group(3));
      assertEquals("test.subject.0", matcher.group(4));
      assertEquals("access", matcher.group(5));
      assertEquals("optin", matcher.group(6));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
          && GrouperClientWs.mostRecentRequest.contains("someValue"));

    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testGroupDelete() throws Exception {

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=groupDeleteWs --groupNames=aStem:aGroup", " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern
          .compile("^Index (\\d+): success: T: code: ([A-Z_]+): (.*+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("aStem:aGroup", matcher.group(3));

      // #####################################################
      // run again, should be already deleted
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=groupDeleteWs --groupNames=aStem:aGroup", " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_GROUP_NOT_FOUND", matcher.group(2));
      assertEquals("aStem:aGroup", matcher.group(3));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=groupDeleteWs --groupNames=aStem:aGroup --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=groupDeleteWs --groupNames=aStem:aGroup --outputTemplate=${index}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("0", output);

      // #####################################################
      // run again, with txType
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=groupDeleteWs --groupNames=aStem:aGroup --txType=NONE",
          " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_GROUP_NOT_FOUND", matcher.group(2));
      assertEquals("aStem:aGroup", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("txType")
              && GrouperClientWs.mostRecentRequest.contains("NONE"));

      // #####################################################
      // run again, with includeSubjectDetail
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=groupDeleteWs --groupNames=aStem:aGroup --includeGroupDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_GROUP_NOT_FOUND", matcher.group(2));
      assertEquals("aStem:aGroup", matcher.group(3));

      assertTrue(!GrouperClientWs.mostRecentRequest.contains("txType")
          && !GrouperClientWs.mostRecentRequest.contains("NONE")
          && GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));

      // #####################################################
      // run again, with params
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=groupDeleteWs --groupNames=aStem:aGroup --paramName0=whatever --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_GROUP_NOT_FOUND", matcher.group(2));
      assertEquals("aStem:aGroup", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
          && GrouperClientWs.mostRecentRequest.contains("someValue"));

    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testStemDelete() throws Exception {

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=stemDeleteWs --stemNames=aStem:aStem0", " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern
          .compile("^Index (\\d+): success: T: code: ([A-Z_]+): (.*+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("aStem:aStem0", matcher.group(3));

      // #####################################################
      // run again, should be already deleted
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=stemDeleteWs --stemNames=aStem:aStem0", " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_STEM_NOT_FOUND", matcher.group(2));
      assertEquals("aStem:aStem0", matcher.group(3));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=stemDeleteWs --stemNames=aStem:aStem0 --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=stemDeleteWs --stemNames=aStem:aStem0 --outputTemplate=${index}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("0", output);

      // #####################################################
      // run again, with txType
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=stemDeleteWs --stemNames=aStem:aStem0 --txType=NONE",
          " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_STEM_NOT_FOUND", matcher.group(2));
      assertEquals("aStem:aStem0", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("txType")
              && GrouperClientWs.mostRecentRequest.contains("NONE"));

      // #####################################################
      // run again, with params
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=stemDeleteWs --stemNames=aStem:aStem0 --paramName0=whatever --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_STEM_NOT_FOUND", matcher.group(2));
      assertEquals("aStem:aStem0", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
          && GrouperClientWs.mostRecentRequest.contains("someValue"));

    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * normal save
   * @throws Exception
   */
  public void testGroupSaveNoLookup() throws Exception {
    
    WsGroupToSave wsGroupToSave = new WsGroupToSave();
    wsGroupToSave.setSaveMode("INSERT");

    WsGroup wsGroup = new WsGroup();
    wsGroup.setDisplayExtension("a group4");
    wsGroup.setExtension("aGroup4");
    wsGroup.setName("aStem:aGroup4");
    wsGroupToSave.setWsGroup(wsGroup);
    WsGroupSaveResults wsGroupSaveResults = new GcGroupSave().addGroupToSave(wsGroupToSave).execute();
    
    //prints SUCCESS_INSERTED when it works
    String resultCode = wsGroupSaveResults.getResults()[0].getResultMetadata().getResultCode();
    
    assertEquals("SUCCESS_INSERTED", resultCode);
  }

  /**
   * normal save
   * @throws Exception
   */
  public void testGroupSaveLookupNameSame() throws Exception {
    
    WsGroupToSave wsGroupToSave = new WsGroupToSave();
    wsGroupToSave.setSaveMode("INSERT");
    wsGroupToSave.setWsGroupLookup(new WsGroupLookup("aStem:aGroup4", null));
    WsGroup wsGroup = new WsGroup();
    wsGroup.setDisplayExtension("a group4");
    wsGroup.setExtension("aGroup4");
    wsGroup.setName("aStem:aGroup4");
    wsGroupToSave.setWsGroup(wsGroup);
    WsGroupSaveResults wsGroupSaveResults = new GcGroupSave().addGroupToSave(wsGroupToSave).execute();
    
    //prints SUCCESS_INSERTED when it works
    String resultCode = wsGroupSaveResults.getResults()[0].getResultMetadata().getResultCode();
    
    assertEquals("SUCCESS_INSERTED", resultCode);
  }

  /**
   * normal save
   * @throws Exception
   */
  public void testGroupSaveInsertAlreadyExists() throws Exception {
    
    {
      WsGroupToSave wsGroupToSave = new WsGroupToSave();
      wsGroupToSave.setSaveMode("INSERT");
      wsGroupToSave.setWsGroupLookup(new WsGroupLookup(null, null));
      WsGroup wsGroup = new WsGroup();
      wsGroup.setDisplayExtension("a group4");
      wsGroup.setExtension("aGroup4");
      wsGroup.setName("aStem:aGroup4");
      wsGroupToSave.setWsGroup(wsGroup);
      WsGroupSaveResults wsGroupSaveResults = new GcGroupSave().addGroupToSave(wsGroupToSave).execute();
      
      //prints SUCCESS_INSERTED when it works
      String resultCode = wsGroupSaveResults.getResults()[0].getResultMetadata().getResultCode();
      
      assertEquals("SUCCESS_INSERTED", resultCode);
    }
    
    {
      WsGroupToSave wsGroupToSave = new WsGroupToSave();
      wsGroupToSave.setSaveMode("INSERT");
      wsGroupToSave.setWsGroupLookup(new WsGroupLookup(null, null));
      WsGroup wsGroup = new WsGroup();
      wsGroup.setDisplayExtension("a group4");
      wsGroup.setExtension("aGroup4");
      wsGroup.setName("aStem:aGroup4");
      wsGroupToSave.setWsGroup(wsGroup);
      try {
        @SuppressWarnings("unused")
        WsGroupSaveResults wsGroupSaveResults = new GcGroupSave().addGroupToSave(wsGroupToSave).execute();
        
        fail("Should not insert twice");
  
        //System.out.println(resultCode);
      } catch (GcWebServiceError gwse) {
        WsGroupSaveResults wsGroupSaveResults = (WsGroupSaveResults)gwse.getContainerResponseObject();
  
        String resultCode = wsGroupSaveResults.getResults()[0].getResultMetadata().getResultCode();
        
        assertEquals("GROUP_ALREADY_EXISTS", resultCode);
      }
    }    
  }
  
  /**
   * update a group to an existing name
   * @throws Exception
   */
  public void testGroupSaveUpdateExistingName() throws Exception {
    
    {
      WsGroupToSave wsGroupToSave = new WsGroupToSave();
      wsGroupToSave.setSaveMode("INSERT");
      wsGroupToSave.setWsGroupLookup(new WsGroupLookup(null, null));
      WsGroup wsGroup = new WsGroup();
      wsGroup.setDisplayExtension("a group4");
      wsGroup.setExtension("aGroup4");
      wsGroup.setName("aStem:aGroup4");
      wsGroupToSave.setWsGroup(wsGroup);
      WsGroupSaveResults wsGroupSaveResults = new GcGroupSave().addGroupToSave(wsGroupToSave).execute();
      
      //prints SUCCESS_INSERTED when it works
      String resultCode = wsGroupSaveResults.getResults()[0].getResultMetadata().getResultCode();
      
      assertEquals("SUCCESS_INSERTED", resultCode);
    }
    String uuid = null;
    {
      WsGroupToSave wsGroupToSave = new WsGroupToSave();
      wsGroupToSave.setSaveMode("INSERT");
      wsGroupToSave.setWsGroupLookup(new WsGroupLookup(null, null));
      WsGroup wsGroup = new WsGroup();
      wsGroup.setDisplayExtension("a group5");
      wsGroup.setExtension("aGroup5");
      wsGroup.setName("aStem:aGroup5");
      wsGroupToSave.setWsGroup(wsGroup);
      WsGroupSaveResults wsGroupSaveResults = new GcGroupSave().addGroupToSave(wsGroupToSave).execute();
      
      uuid = wsGroupSaveResults.getResults()[0].getWsGroup().getUuid();
      
      //prints SUCCESS_INSERTED when it works
      String resultCode = wsGroupSaveResults.getResults()[0].getResultMetadata().getResultCode();
      
      assertEquals("SUCCESS_INSERTED", resultCode);
    
    }
    
    {
      WsGroupToSave wsGroupToSave = new WsGroupToSave();
      wsGroupToSave.setSaveMode("UPDATE");
      wsGroupToSave.setWsGroupLookup(new WsGroupLookup(null, uuid));
      WsGroup wsGroup = new WsGroup();
      wsGroup.setDisplayExtension("a group4");
      wsGroup.setExtension("aGroup4");
      wsGroup.setName("aStem:aGroup4");
      wsGroupToSave.setWsGroup(wsGroup);
      try {
        @SuppressWarnings("unused")
        WsGroupSaveResults wsGroupSaveResults = new GcGroupSave().addGroupToSave(wsGroupToSave).execute();
        
        fail("Should not update to existing name");
  
        //System.out.println(resultCode);
      } catch (GcWebServiceError gwse) {
        WsGroupSaveResults wsGroupSaveResults = (WsGroupSaveResults)gwse.getContainerResponseObject();
  
        String resultCode = wsGroupSaveResults.getResults()[0].getResultMetadata().getResultCode();
        
        assertEquals("EXCEPTION", resultCode);
      }
    }    
      
  }

  /**
   * @throws Exception
   */
  public void testGroupSave() throws Exception {

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));
    String output = null;
    String[] outputLines = null;
    Pattern pattern = null;
    Matcher matcher = null;
    try {
      systemOut.println("Umlaut: �");
      //try with name with slash
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=groupSaveWs --name=aStem:newGroup0�", " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      systemOut.println(output);
      
      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");
//�
      pattern = Pattern.compile("^Success: T: code: ([A-Z_]+): (.*+)$");
      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_INSERTED", matcher.group(1));
      assertEquals("aStem:newGroup0�", matcher.group(2));
      
      // ##########################
      //try with name with slash

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=groupSaveWs --name=aStem:newGroup0/1", " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      pattern = Pattern.compile("^Success: T: code: ([A-Z_]+): (.*+)$");
      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_INSERTED", matcher.group(1));
      assertEquals("aStem:newGroup0/1", matcher.group(2));

      // #####################################################
      // run again, with clientVersion

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=groupSaveWs --name=aStem:newGroup0", " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      pattern = Pattern.compile("^Success: T: code: ([A-Z_]+): (.*+)$");
      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_INSERTED", matcher.group(1));
      assertEquals("aStem:newGroup0", matcher.group(2));

      // #####################################################
      // run again, with clientVersion

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=groupSaveWs --name=aStem:newGroup0 --clientVersion=v1_3_000",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS", matcher.group(1));
      assertEquals("aStem:newGroup0", matcher.group(2));

      // #####################################################
      // run again, should be already added
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=groupSaveWs --name=aStem:newGroup0", " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_NO_CHANGES_NEEDED", matcher.group(1));
      assertEquals("aStem:newGroup0", matcher.group(2));

      // #####################################################
      // run again, should be already added
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=groupSaveWs --name=aStem:newGroup0 --displayExtension=newGroup0displayExtension",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_UPDATED", matcher.group(1));
      assertEquals("aStem:newGroup0", matcher.group(2));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=groupSaveWs --name=aStem:newGroup0 --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=groupSaveWs --name=aStem:newGroup0 --outputTemplate=${index}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("0", output);

      // #####################################################
      // run again, with field
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=groupSaveWs --name=aStem:newGroup0 --saveMode=UPDATE",
          " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_NO_CHANGES_NEEDED", matcher.group(1));
      assertEquals("aStem:newGroup0", matcher.group(2));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("saveMode")
              && GrouperClientWs.mostRecentRequest.contains("UPDATE"));

      // #####################################################
      // run again, with txType
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=groupSaveWs --name=aStem:newGroup0 --txType=NONE", " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_NO_CHANGES_NEEDED", matcher.group(1));
      assertEquals("aStem:newGroup0", matcher.group(2));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("txType")
              && GrouperClientWs.mostRecentRequest.contains("NONE")
              && !GrouperClientWs.mostRecentRequest
                  .contains("includeGroupDetail"));

      // #####################################################
      // run again, with includeGroupDetail
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=groupSaveWs --name=aStem:newGroup0 --includeGroupDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_NO_CHANGES_NEEDED", matcher.group(1));
      assertEquals("aStem:newGroup0", matcher.group(2));

      assertTrue(!GrouperClientWs.mostRecentRequest.contains("txType")
          && !GrouperClientWs.mostRecentRequest.contains("NONE")
          && GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));

      // #####################################################
      // run again, with groupLookupName
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=groupSaveWs --name=aStem:newGroup1 --groupLookupName=aStem:newGroup0",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_UPDATED", matcher.group(1));
      assertEquals("aStem:newGroup1", matcher.group(2));

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("txType")
              && !GrouperClientWs.mostRecentRequest.contains("NONE")
              && !GrouperClientWs.mostRecentRequest
                  .contains("includeGroupDetail")
              && GrouperClientWs.mostRecentRequest.contains("wsGroupLookup")
              && GrouperClientWs.mostRecentRequest.contains("aStem:newGroup1")
              && GrouperClientWs.mostRecentRequest.contains("aStem:newGroup0"));

      // #####################################################
      // run again, with saveMode
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=groupSaveWs --name=aStem:newGroup3 --saveMode=INSERT",
          " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_INSERTED", matcher.group(1));
      assertEquals("aStem:newGroup3", matcher.group(2));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("saveMode")
          && GrouperClientWs.mostRecentRequest.contains("INSERT"));

      // #####################################################
      // run again, description
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=groupSaveWs --name=aStem:newGroup0 --description=aDescription",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("SUCCESS_UPDATED", matcher.group(1));
        assertEquals("aStem:newGroup0", matcher.group(2));

        assertTrue(GrouperClientWs.mostRecentRequest.contains("description")
            && GrouperClientWs.mostRecentRequest.contains("aDescription"));

        // #####################################################
        // run again, with params
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=groupSaveWs --name=aStem:newGroup0 --paramName0=whatever --paramValue0=someValue",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("SUCCESS_UPDATED", matcher.group(1));
        assertEquals("aStem:newGroup0", matcher.group(2));

        assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
            && GrouperClientWs.mostRecentRequest.contains("someValue"));

        // #####################################################
        // run again, with typeNames
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient.main(GrouperClientUtils.splitTrim(
            "--operation=groupSaveWs --name=aStem:newGroup0 --typeNames=aType",
            " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("SUCCESS_UPDATED", matcher.group(1));
        assertEquals("aStem:newGroup0", matcher.group(2));

        assertTrue(GrouperClientWs.mostRecentRequest.contains("typeNames")
            && GrouperClientWs.mostRecentRequest.contains("aType"));

        // #####################################################
        // run again, with attributes
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=groupSaveWs --name=aStem:newGroup0 --typeNames=aType --attributeName0=attr_1 --attributeValue0=whatever",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("SUCCESS_UPDATED", matcher.group(1));
        assertEquals("aStem:newGroup0", matcher.group(2));

        assertTrue(GrouperClientWs.mostRecentRequest.contains("attr_1")
            && GrouperClientWs.mostRecentRequest.contains("whatever"));

        // #####################################################
        // run again, with groupDetailParamName0
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=groupSaveWs --name=aStem:newGroup0 --groupDetailParamName0=something --groupDetailParamValue0=whatever",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("SUCCESS_UPDATED", matcher.group(1));
        assertEquals("aStem:newGroup0", matcher.group(2));

        assertTrue(GrouperClientWs.mostRecentRequest.contains("something")
            && GrouperClientWs.mostRecentRequest.contains("whatever"));

        // #####################################################
        // run again, with composite

        GrouperClient.main(GrouperClientUtils.splitTrim(
            "--operation=groupSaveWs --name=aStem:leftGroup", " "));
        GrouperClient.main(GrouperClientUtils.splitTrim(
            "--operation=groupSaveWs --name=aStem:rightGroup", " "));

        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=groupSaveWs --name=aStem:newGroup0 --compositeType=union --leftGroupName=aStem:leftGroup --rightGroupName=aStem:rightGroup --includeGroupDetail=true",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("SUCCESS_UPDATED", matcher.group(1));
        assertEquals("aStem:newGroup0", matcher.group(2));

        assertTrue(GrouperClientWs.mostRecentRequest.contains("union")
            && GrouperClientWs.mostRecentRequest.contains("aStem:leftGroup")
            && GrouperClientWs.mostRecentRequest.contains("aStem:rightGroup"));

      } finally {
      }
    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testStemSave() throws Exception {

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=stemSaveWs --name=aStem:newStem0", " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern.compile("^Success: T: code: ([A-Z_]+): (.*+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_INSERTED", matcher.group(1));
      assertEquals("aStem:newStem0", matcher.group(2));

      // #####################################################
      // run again, with clientVersion

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=stemSaveWs --name=aStem:newStem0 --clientVersion=v1_3_000",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS", matcher.group(1));
      assertEquals("aStem:newStem0", matcher.group(2));

      // #####################################################
      // run again, should be already added
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=stemSaveWs --name=aStem:newStem0", " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_NO_CHANGES_NEEDED", matcher.group(1));
      assertEquals("aStem:newStem0", matcher.group(2));

      // #####################################################
      // run again, should be already added
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=stemSaveWs --name=aStem:newStem0 --displayExtension=newStem0displayExtension",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_UPDATED", matcher.group(1));
      assertEquals("aStem:newStem0", matcher.group(2));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=stemSaveWs --name=aStem:newStem0 --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=stemSaveWs --name=aStem:newStem0 --outputTemplate=${index}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("0", output);

      // #####################################################
      // run with custom template with function
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=stemSaveWs --name=aStem:newStem0 --outputTemplate=a${grouperClientUtils.defaultString(wsStemSaveResult.resultMetadata.resfsdfCode)}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("a", output);

      // #####################################################
      // run again, with field
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils
          .splitTrim(
              "--operation=stemSaveWs --name=aStem:newStem0 --saveMode=UPDATE",
              " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_NO_CHANGES_NEEDED", matcher.group(1));
      assertEquals("aStem:newStem0", matcher.group(2));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("saveMode")
              && GrouperClientWs.mostRecentRequest.contains("UPDATE"));

      // #####################################################
      // run again, with txType
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=stemSaveWs --name=aStem:newStem0 --txType=NONE", " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_NO_CHANGES_NEEDED", matcher.group(1));
      assertEquals("aStem:newStem0", matcher.group(2));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("txType")
              && GrouperClientWs.mostRecentRequest.contains("NONE")
              && !GrouperClientWs.mostRecentRequest
                  .contains("includeGroupDetail"));

      // #####################################################
      // run again, with groupLookupName
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=stemSaveWs --name=aStem:newStem1 --stemLookupName=aStem:newStem0",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_UPDATED", matcher.group(1));
      assertEquals("aStem:newStem1", matcher.group(2));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("wsStemLookup")
              && GrouperClientWs.mostRecentRequest.contains("aStem:newStem1")
              && GrouperClientWs.mostRecentRequest.contains("aStem:newStem0"));

      // #####################################################
      // run again, with saveMode
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils
          .splitTrim(
              "--operation=stemSaveWs --name=aStem:newStem0 --saveMode=INSERT",
              " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_INSERTED", matcher.group(1));
      assertEquals("aStem:newStem0", matcher.group(2));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("saveMode")
          && GrouperClientWs.mostRecentRequest.contains("INSERT"));

      // #####################################################
      // run again, description
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=stemSaveWs --name=aStem:newStem0 --description=aDescription",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_UPDATED", matcher.group(1));
      assertEquals("aStem:newStem0", matcher.group(2));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("description")
          && GrouperClientWs.mostRecentRequest.contains("aDescription"));

      // #####################################################
      // run again, with params
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=stemSaveWs --name=aStem:newStem0 --paramName0=whatever --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS_UPDATED", matcher.group(1));
      assertEquals("aStem:newStem0", matcher.group(2));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
          && GrouperClientWs.mostRecentRequest.contains("someValue"));

    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testGetGroups() throws Exception {

    // make sure group exists
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Group group = Group.saveGroup(grouperSession, "aStem:aGroup", null,
        "aStem:aGroup", "aGroup", null, null, true);
    Group group2 = Group.saveGroup(grouperSession, "aStem:aGroup2", null,
        "aStem:aGroup2", "aGroup2", null, null, true);
    Group group3 = Group.saveGroup(grouperSession, "aStem:aGroup3", null,
        "aStem:aGroup3", "aGroup3", null, null, true);

    // give permissions
    String wsUserLabel = GrouperClientUtils.propertiesValue(
        "grouperClient.webService.user.label", true);
    String wsUserString = GrouperClientUtils.propertiesValue(
        "grouperClient.webService." + wsUserLabel, true);
    Subject wsUser = SubjectFinder.findByIdOrIdentifier(wsUserString, true);

    group.grantPriv(wsUser, AccessPrivilege.READ, false);
    group.grantPriv(wsUser, AccessPrivilege.VIEW, false);
    group2.grantPriv(wsUser, AccessPrivilege.READ, false);
    group2.grantPriv(wsUser, AccessPrivilege.VIEW, false);
    group3.grantPriv(wsUser, AccessPrivilege.READ, false);
    group3.grantPriv(wsUser, AccessPrivilege.VIEW, false);

    // add some subjects
    group.addMember(SubjectTestHelper.SUBJ0, false);
    group2.addMember(SubjectTestHelper.SUBJ0, false);
    group2.addMember(SubjectTestHelper.SUBJ1, false);
    group3.addMember(SubjectTestHelper.SUBJ1, false);

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getGroupsWs --subjectIds=test.subject.0,test.subject.1",
          " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      System.out.println(output);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern
          .compile("^SubjectIndex (\\d+): success: T: code: ([A-Z_]+): subject: (.*): groupIndex: (\\d+): (.*+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));
      assertEquals("0", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup2", matcher.group(5)));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));
      assertEquals("1", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup2", matcher.group(5)));

      matcher = pattern.matcher(outputLines[2]);

      assertTrue(outputLines[2], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));
      assertEquals("0", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup2",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup3", matcher.group(5)));

      matcher = pattern.matcher(outputLines[3]);

      assertTrue(outputLines[3], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));
      assertEquals("1", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup2",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup3", matcher.group(5)));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=getGroupsWs --subjectIds=test.subject.0,test.subject.1 --ousdfsdfate=whatever",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.0,id.test.subject.1 --outputTemplate=${subjectIndex}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("0011", output);

      // #####################################################
      // run again, with includeGroupDetail and includeSubjectDetail
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGroupsWs --subjectIds=test.subject.0,test.subject.1 --includeGroupDetail=true --includeSubjectDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));
      assertEquals("0", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup2", matcher.group(5)));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));
      assertEquals("1", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup2", matcher.group(5)));

      matcher = pattern.matcher(outputLines[2]);

      assertTrue(outputLines[2], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));
      assertEquals("0", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup2",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup3", matcher.group(5)));

      matcher = pattern.matcher(outputLines[3]);

      assertTrue(outputLines[3], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));
      assertEquals("1", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup2",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup3", matcher.group(5)));

      assertTrue(GrouperClientWs.mostRecentRequest
          .contains("includeGroupDetail")
          && GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));

      // #####################################################
      // run again, with subject attributes
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGroupsWs --subjectIds=test.subject.0,test.subject.1 --subjectAttributeNames=name --outputTemplate=${subjectIndex}:$space$${wsGetGroupsResult.wsSubject.getAttributeValue(0)}$newline$",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertTrue(output, outputLines[0].contains("my name is test.subject.0"));

      assertTrue(output, outputLines[2].contains("my name is test.subject.1"));

      assertTrue(GrouperClientWs.mostRecentRequest.contains(">name<"));
      assertTrue(GrouperClientWs.mostRecentResponse
          .contains("my name is test.subject.0"));

      // #####################################################
      // run again, with default subject source
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGroupsWs --subjectIds=test.subject.0,test.subject.1 --defaultSubjectSource=jdbc",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));
      assertEquals("0", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup2", matcher.group(5)));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));
      assertEquals("1", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup2", matcher.group(5)));

      matcher = pattern.matcher(outputLines[2]);

      assertTrue(outputLines[2], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));
      assertEquals("0", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup2",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup3", matcher.group(5)));

      matcher = pattern.matcher(outputLines[3]);

      assertTrue(outputLines[3], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));
      assertEquals("1", matcher.group(4));
      assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup2",
          matcher.group(5))
          || GrouperClientUtils.equals("aStem:aGroup3", matcher.group(5)));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("jdbc"));

      // #####################################################
      // run again, subjects ids coming from file
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      String subjectIdsFileName = "subjectIdsFile_"
          + GrouperClientUtils.uniqueId() + ".txt";
      File subjectIdsFile = new File(subjectIdsFileName);

      GrouperClientUtils.saveStringIntoFile(subjectIdsFile,
          "test.subject.0\ntest.subject.1");

      try {
        GrouperClient.main(GrouperClientUtils.splitTrim(
            "--operation=getGroupsWs --subjectIdsFile=" + subjectIdsFileName,
            " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("SUCCESS", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));
        assertEquals("0", matcher.group(4));
        assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup",
            matcher.group(5))
            || GrouperClientUtils.equals("aStem:aGroup2", matcher.group(5)));

        matcher = pattern.matcher(outputLines[1]);

        assertTrue(outputLines[1], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("SUCCESS", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));
        assertEquals("1", matcher.group(4));
        assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup",
            matcher.group(5))
            || GrouperClientUtils.equals("aStem:aGroup2", matcher.group(5)));

        matcher = pattern.matcher(outputLines[2]);

        assertTrue(outputLines[2], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("SUCCESS", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));
        assertEquals("0", matcher.group(4));
        assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup2",
            matcher.group(5))
            || GrouperClientUtils.equals("aStem:aGroup3", matcher.group(5)));

        matcher = pattern.matcher(outputLines[3]);

        assertTrue(outputLines[3], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("SUCCESS", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));
        assertEquals("1", matcher.group(4));
        assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup2",
            matcher.group(5))
            || GrouperClientUtils.equals("aStem:aGroup3", matcher.group(5)));

        // #####################################################
        // run again, with params
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=getGroupsWs --subjectIds=test.subject.0,test.subject.1 --memberFilter=Immediate",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("SUCCESS", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));
        assertEquals("0", matcher.group(4));
        assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup",
            matcher.group(5))
            || GrouperClientUtils.equals("aStem:aGroup2", matcher.group(5)));

        matcher = pattern.matcher(outputLines[1]);

        assertTrue(outputLines[1], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("SUCCESS", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));
        assertEquals("1", matcher.group(4));
        assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup",
            matcher.group(5))
            || GrouperClientUtils.equals("aStem:aGroup2", matcher.group(5)));

        matcher = pattern.matcher(outputLines[2]);

        assertTrue(outputLines[2], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("SUCCESS", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));
        assertEquals("0", matcher.group(4));
        assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup2",
            matcher.group(5))
            || GrouperClientUtils.equals("aStem:aGroup3", matcher.group(5)));

        matcher = pattern.matcher(outputLines[3]);

        assertTrue(outputLines[3], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("SUCCESS", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));
        assertEquals("1", matcher.group(4));
        assertTrue(matcher.group(5), GrouperClientUtils.equals("aStem:aGroup2",
            matcher.group(5))
            || GrouperClientUtils.equals("aStem:aGroup3", matcher.group(5)));

        assertTrue(GrouperClientWs.mostRecentRequest.contains("memberFilter")
            && GrouperClientWs.mostRecentRequest.contains("Immediate"));

      } finally {
        if (subjectIdsFile.exists()) {
          subjectIdsFile.delete();
        }
      }
    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testHasMember() throws Exception {

    // make sure group exists
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Group group = Group.saveGroup(grouperSession, "aStem:aGroup", null,
        "aStem:aGroup", "aGroup", null, null, true);

    // give permissions
    String wsUserLabel = GrouperClientUtils.propertiesValue(
        "grouperClient.webService.user.label", true);
    String wsUserString = GrouperClientUtils.propertiesValue(
        "grouperClient.webService." + wsUserLabel, true);
    Subject wsUser = SubjectFinder.findByIdOrIdentifier(wsUserString, true);

    group.grantPriv(wsUser, AccessPrivilege.READ, false);
    group.grantPriv(wsUser, AccessPrivilege.VIEW, false);

    // add some subjects
    group.addMember(SubjectTestHelper.SUBJ0, false);
    group.addMember(SubjectTestHelper.SUBJ1, false);

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=hasMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1",
                  " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern
          .compile("^Index (\\d+): success: T: code: ([A-Z_]+): (.+): (false|true)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("IS_MEMBER", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));
      assertEquals("true", matcher.group(4));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("IS_MEMBER", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));
      assertEquals("true", matcher.group(4));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=hasMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=hasMemberWs --groupName=aStem:aGroup --subjectIdentifiers=id.test.subject.0,id.test.subject.1 --outputTemplate=${index}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("01", output);

      // #####################################################
      // run again, with field
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=hasMemberWs --groupName=aStem:aGroup --pennKeys=id.test.subject.0,id.test.subject.1 --fieldName=members",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("IS_MEMBER", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));
      assertEquals("true", matcher.group(4));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("IS_MEMBER", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));
      assertEquals("true", matcher.group(4));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("fieldName")
              && GrouperClientWs.mostRecentRequest.contains("members")
              && !GrouperClientWs.mostRecentRequest.contains("txType"));

      // #####################################################
      // run again, with uuid
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=hasMemberWs --groupUuid=" + group.getUuid() + " --pennKeys=id.test.subject.0,id.test.subject.1",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("IS_MEMBER", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));
      assertEquals("true", matcher.group(4));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("IS_MEMBER", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));
      assertEquals("true", matcher.group(4));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("uuid"));

      // #####################################################
      // run again, with includeGroupDetail and includeSubjectDetail
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=hasMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --includeGroupDetail=true --includeSubjectDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("IS_MEMBER", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));
      assertEquals("true", matcher.group(4));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("IS_MEMBER", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));
      assertEquals("true", matcher.group(4));

      assertTrue(!GrouperClientWs.mostRecentRequest.contains("txType")
          && !GrouperClientWs.mostRecentRequest.contains("NONE")
          && GrouperClientWs.mostRecentRequest.contains("includeGroupDetail")
          && GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));

      // #####################################################
      // run again, with subject attributes
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=hasMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --subjectAttributeNames=name --outputTemplate=${index}:$space$${wsHasMemberResult.wsSubject.getAttributeValue(0)}$newline$",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertTrue(outputLines[0], outputLines[0]
          .contains("my name is test.subject.0"));

      assertTrue(outputLines[1], outputLines[1]
          .contains("my name is test.subject.1"));

      assertTrue(GrouperClientWs.mostRecentRequest.contains(">name<"));
      assertTrue(GrouperClientWs.mostRecentResponse
          .contains("my name is test.subject.0"));

      // #####################################################
      // run again, with default subject source
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=hasMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --defaultSubjectSource=jdbc",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("IS_MEMBER", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));
      assertEquals("true", matcher.group(4));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("IS_MEMBER", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));
      assertEquals("true", matcher.group(4));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("jdbc"));

      // #####################################################
      // run again, subjects ids coming from file
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      String subjectIdsFileName = "subjectIdsFile_"
          + GrouperClientUtils.uniqueId() + ".txt";
      File subjectIdsFile = new File(subjectIdsFileName);

      GrouperClientUtils.saveStringIntoFile(subjectIdsFile,
          "test.subject.0\ntest.subject.1");

      try {
        GrouperClient.main(GrouperClientUtils.splitTrim(
            "--operation=hasMemberWs --groupName=aStem:aGroup --subjectIdsFile="
                + subjectIdsFileName, " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("IS_MEMBER", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));
        assertEquals("true", matcher.group(4));

        matcher = pattern.matcher(outputLines[1]);

        assertTrue(outputLines[1], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("IS_MEMBER", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));
        assertEquals("true", matcher.group(4));

        // #####################################################
        // run again, with params
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=hasMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --paramName0=whatever --paramValue0=someValue",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("IS_MEMBER", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));

        matcher = pattern.matcher(outputLines[1]);

        assertTrue(outputLines[1], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("IS_MEMBER", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));
        assertEquals("true", matcher.group(4));

        assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
            && GrouperClientWs.mostRecentRequest.contains("someValue"));

        // #####################################################
        // run again, with memberFilter
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=hasMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --memberFilter=Immediate",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("IS_MEMBER", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));
        assertEquals("true", matcher.group(4));

        matcher = pattern.matcher(outputLines[1]);

        assertTrue(outputLines[1], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("IS_MEMBER", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));
        assertEquals("true", matcher.group(4));

        assertTrue(GrouperClientWs.mostRecentRequest.contains("memberFilter")
            && GrouperClientWs.mostRecentRequest.contains("Immediate"));

      } finally {
        if (subjectIdsFile.exists()) {
          subjectIdsFile.delete();
        }
      }
    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testDeleteMember() throws Exception {

    // make sure group exists
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Group group = Group.saveGroup(grouperSession, "aStem:aGroup", null,
        "aStem:aGroup", "aGroup", null, null, true);

    // give permissions
    String wsUserLabel = GrouperClientUtils.propertiesValue(
        "grouperClient.webService.user.label", true);
    String wsUserString = GrouperClientUtils.propertiesValue(
        "grouperClient.webService." + wsUserLabel, true);
    Subject wsUser = SubjectFinder.findByIdOrIdentifier(wsUserString, true);

    group.grantPriv(wsUser, AccessPrivilege.READ, false);
    group.grantPriv(wsUser, AccessPrivilege.VIEW, false);
    group.grantPriv(wsUser, AccessPrivilege.ADMIN, false);

    // add some subjects
    group.addMember(SubjectTestHelper.SUBJ0, false);
    group.addMember(SubjectTestHelper.SUBJ1, false);

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient.main(GrouperClientUtils.splitTrim(
                  "--operation=deleteMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1",
                  " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern
          .compile("^Index (\\d+): success: T: code: ([A-Z_]+): (.*+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      // #####################################################
      // run again, should be already deleted
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils
              .splitTrim(
                  "--operation=deleteMemberWs --groupName=aStem:aGroup --pennIds=test.subject.0,test.subject.1",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals(outputLines[0], "0", matcher.group(1));
      assertEquals(outputLines[0], "SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals(outputLines[0], "test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      // #####################################################
      // run again, with uuid
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient.main(GrouperClientUtils
              .splitTrim(
                  "--operation=deleteMemberWs --groupUuid=" + group.getUuid() + " --pennIds=test.subject.0,test.subject.1",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals(outputLines[0], "0", matcher.group(1));
      assertEquals(outputLines[0], "SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals(outputLines[0], "test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=deleteMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=deleteMemberWs --groupName=aStem:aGroup --subjectIdentifiers=id.test.subject.0,id.test.subject.1 --outputTemplate=${index}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("01", output);

      // #####################################################
      // run again, with field
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=deleteMemberWs --groupName=aStem:aGroup --pennKeys=id.test.subject.0,id.test.subject.1 --fieldName=members",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("fieldName")
              && GrouperClientWs.mostRecentRequest.contains("members")
              && !GrouperClientWs.mostRecentRequest.contains("txType"));

      // #####################################################
      // run again, with txType
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=deleteMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --txType=NONE",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName")
              && !GrouperClientWs.mostRecentRequest.contains("members")
              && GrouperClientWs.mostRecentRequest.contains("txType")
              && GrouperClientWs.mostRecentRequest.contains("NONE")
              && !GrouperClientWs.mostRecentRequest
                  .contains("includeGroupDetail")
              && !GrouperClientWs.mostRecentRequest
                  .contains("includeSubjectDetail")
              && !GrouperClientWs.mostRecentRequest
                  .contains("includeGroupDetail")
              && !GrouperClientWs.mostRecentRequest
                  .contains("includeSubjectDetail"));

      // #####################################################
      // run again, with includeGroupDetail and includeSubjectDetail
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=deleteMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --includeGroupDetail=true --includeSubjectDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      assertTrue(!GrouperClientWs.mostRecentRequest.contains("txType")
          && !GrouperClientWs.mostRecentRequest.contains("NONE")
          && GrouperClientWs.mostRecentRequest.contains("includeGroupDetail")
          && GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));

      // #####################################################
      // run again, with subject attributes
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=deleteMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --subjectAttributeNames=name --outputTemplate=${index}:$space$${wsDeleteMemberResult.wsSubject.getAttributeValue(0)}$newline$",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertTrue(outputLines[0], outputLines[0]
          .contains("my name is test.subject.0"));

      assertTrue(outputLines[1], outputLines[1]
          .contains("my name is test.subject.1"));

      assertTrue(GrouperClientWs.mostRecentRequest.contains(">name<"));
      assertTrue(GrouperClientWs.mostRecentResponse
          .contains("my name is test.subject.0"));

      // #####################################################
      // run again, with default subject source
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=deleteMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --defaultSubjectSource=jdbc",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals("0", matcher.group(1));
      assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals("1", matcher.group(1));
      assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("jdbc"));

      // #####################################################
      // run again, subjects ids coming from file
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      String subjectIdsFileName = "subjectIdsFile_"
          + GrouperClientUtils.uniqueId() + ".txt";
      File subjectIdsFile = new File(subjectIdsFileName);

      GrouperClientUtils.saveStringIntoFile(subjectIdsFile,
          "test.subject.0\ntest.subject.1");

      try {
        GrouperClient.main(GrouperClientUtils.splitTrim(
            "--operation=deleteMemberWs --groupName=aStem:aGroup --subjectIdsFile="
                + subjectIdsFileName, " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));

        matcher = pattern.matcher(outputLines[1]);

        assertTrue(outputLines[1], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));

        // #####################################################
        // run again, with params
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=deleteMemberWs --groupName=aStem:aGroup --subjectIds=test.subject.0,test.subject.1 --paramName0=whatever --paramValue0=someValue",
                    " "));
        System.out.flush();
        output = new String(baos.toByteArray());

        System.setOut(systemOut);

        outputLines = GrouperClientUtils.splitTrim(output, "\n");

        matcher = pattern.matcher(outputLines[0]);

        assertTrue(outputLines[0], matcher.matches());

        assertEquals("0", matcher.group(1));
        assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
        assertEquals("test.subject.0", matcher.group(3));

        matcher = pattern.matcher(outputLines[1]);

        assertTrue(outputLines[1], matcher.matches());

        assertEquals("1", matcher.group(1));
        assertEquals("SUCCESS_WASNT_IMMEDIATE", matcher.group(2));
        assertEquals("test.subject.1", matcher.group(3));

        assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
            && GrouperClientWs.mostRecentRequest.contains("someValue"));

      } finally {
        if (subjectIdsFile.exists()) {
          subjectIdsFile.delete();
        }
      }
    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testMemberChangeSubject() throws Exception {

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=memberChangeSubjectWs --oldSubjectId=test.subject.0 --newSubjectId=test.subject.1 --actAsSubjectId=GrouperSystem",
                  " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern
          .compile("^Success: T: code: ([A-Z_]+): oldSubject: (.+), newSubject: (.+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertEquals(1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS", matcher.group(1));
      assertEquals("test.subject.0", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      // #####################################################
      // run again, should be already moved
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // this should fail with member not found
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=memberChangeSubjectWs --oldSubjectId=test.subject.0 --newSubjectId=test.subject.1 --actAsSubjectId=GrouperSystem",
                    " "));
        fail("Should not get here");
      } catch (GcWebServiceError gwse) {
        WsMemberChangeSubjectResults wsMemberChangeSubjectResults = (WsMemberChangeSubjectResults) gwse
            .getContainerResponseObject();
        assertEquals("PROBLEM_WITH_CHANGE", wsMemberChangeSubjectResults
            .getResultMetadata().getResultCode());
      }

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=memberChangeSubjectWs --oldSubjectId=test.subject.0 --newSubjectId=test.subject.1 --actAsSubjectId=GrouperSystem --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template and pennkeys
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=memberChangeSubjectWs --oldPennKey=id.test.subject.1 --newPennKey=id.test.subject.0 --actAsPennId=GrouperSystem --outputTemplate=${index}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("0", output);

      // #####################################################
      // run again, with delete old member
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=memberChangeSubjectWs --oldSubjectIdentifier=id.test.subject.0 --newSubjectIdentifier=id.test.subject.1 --actAsSubjectId=GrouperSystem --deleteOldMember=false",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS", matcher.group(1));
      assertEquals("test.subject.0", matcher.group(2));
      assertEquals("test.subject.1", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("deleteOldMember"));

      // #####################################################
      // run again, with includeSubjectDetail
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=memberChangeSubjectWs --oldSubjectId=test.subject.1 --newSubjectId=test.subject.0 --actAsSubjectId=GrouperSystem --includeSubjectDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS", matcher.group(1));
      assertEquals("test.subject.1", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest
          .contains("includeSubjectDetail"));

      // #####################################################
      // run again, with subject attributes
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=memberChangeSubjectWs --oldSubjectId=test.subject.0 --newSubjectId=test.subject.1 --actAsSubjectId=GrouperSystem --subjectAttributeNames=name --outputTemplate=${index}:$space$${wsSubjectOld.getAttributeValue(0)}$newline$",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertTrue(outputLines[0], outputLines[0]
          .contains("my name is test.subject.0"));

      assertTrue(GrouperClientWs.mostRecentRequest.contains(">name<"));
      assertTrue(GrouperClientWs.mostRecentResponse
          .contains("my name is test.subject.0"));

      // #####################################################
      // run again, with params
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=memberChangeSubjectWs --oldSubjectId=test.subject.1 --newSubjectId=test.subject.0 --actAsSubjectId=GrouperSystem --paramName0=whatever --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals("SUCCESS", matcher.group(1));
      assertEquals("test.subject.1", matcher.group(2));
      assertEquals("test.subject.0", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
          && GrouperClientWs.mostRecentRequest.contains("someValue"));

    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testSendFile() throws Exception {

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    File file = new File("testSendFile.txt");
    file.deleteOnExit();

    try {
      String contents = "<WsRestAddMemberRequest>"
          + "<wsGroupLookup><groupName>aStem:aGroup</groupName></wsGroupLookup>"
          + "<subjectLookups><WsSubjectLookup>"
          + "<subjectId>test.subject.0</subjectId></WsSubjectLookup>"
          + "</subjectLookups></WsRestAddMemberRequest>";
      GrouperClientUtils.saveStringIntoFile(file, contents);

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=sendFile --fileName=testSendFile.txt --urlSuffix=groups/aStem:aGroup/members",
                  " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(output.contains("<resultCode>SUCCESS</resultCode>")
          && output.contains("        "));

      // #####################################################
      // run again, with contents, and no formatting
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=sendFile --fileContents="
                      + contents
                      + " --urlSuffix=groups/aStem:aGroup/members --indentOutput=false",
                  " "));

      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(output.contains("<resultCode>SUCCESS</resultCode>")
          && !output.contains("        "));

    } finally {
      System.setOut(systemOut);
      file.delete();
    }

  }

  /**
   * @throws Exception
   */
  public void testFindGroups() throws Exception {

    // set some stuff to query
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Group group = GroupFinder.findByName(grouperSession, "aStem:aGroup", true);

    GroupType groupType = GroupTypeFinder.find("aType", true);
    group.addType(groupType, false);
    group.setAttribute("attr_1", "something");
    group.store();

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findGroupsWs --queryFilterType=FIND_BY_GROUP_NAME_APPROXIMATE --groupName=aStem:aGroup",
                  " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern
          .compile("^Index (\\d+): name: (.+), displayName: (.+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem:aGroup", matcher.group(2));
      assertEquals(output, "aStem:aGroup", matcher.group(3));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=findGroupsWs --queryFilterType=FIND_BY_GROUP_NAME_APPROXIMATE --groupName=aStem:aGroup --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findGroupsWs --queryFilterType=FIND_BY_GROUP_NAME_APPROXIMATE --groupName=aStem:aGroup --outputTemplate=${index}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("0", output);

      // #####################################################
      // run again, with includeGroupDetail
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findGroupsWs  --queryFilterType=FIND_BY_GROUP_NAME_APPROXIMATE --groupName=aStem:aGroup --includeGroupDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem:aGroup", matcher.group(2));
      assertEquals(output, "aStem:aGroup", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest
          .contains("includeGroupDetail"));

      // #####################################################
      // run again, with params
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findGroupsWs --queryFilterType=FIND_BY_GROUP_NAME_APPROXIMATE --groupName=aStem:aGroup --paramName0=whatever --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem:aGroup", matcher.group(2));
      assertEquals(output, "aStem:aGroup", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
          && GrouperClientWs.mostRecentRequest.contains("someValue"));

      // #####################################################
      // run again, with uuid
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findGroupsWs --queryFilterType=FIND_BY_GROUP_UUID --groupUuid=abc",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertTrue(output, GrouperClientUtils.isBlank(output));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("groupUuid")
          && GrouperClientWs.mostRecentRequest.contains("abc"));

      // #####################################################
      // run again, with stem
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findGroupsWs --queryFilterType=FIND_BY_GROUP_NAME_APPROXIMATE --groupName=aStem:aGroup --stemName=aStem",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem:aGroup", matcher.group(2));
      assertEquals(output, "aStem:aGroup", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("stemName")
          && GrouperClientWs.mostRecentRequest.contains(">aStem<"));

      // #####################################################
      // run again, with group type
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findGroupsWs --queryFilterType=FIND_BY_TYPE --groupTypeName=aType",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem:aGroup", matcher.group(2));
      assertEquals(output, "aStem:aGroup", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("FIND_BY_TYPE")
          && GrouperClientWs.mostRecentRequest.contains("groupTypeName")
          && GrouperClientWs.mostRecentRequest.contains("aType"));

      // #####################################################
      // run again, with group attribute
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findGroupsWs --queryFilterType=FIND_BY_EXACT_ATTRIBUTE --groupAttributeName=attr_1 --groupAttributeValue=something",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem:aGroup", matcher.group(2));
      assertEquals(output, "aStem:aGroup", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest
          .contains("FIND_BY_EXACT_ATTRIBUTE")
          && GrouperClientWs.mostRecentRequest.contains("groupAttributeName")
          && GrouperClientWs.mostRecentRequest.contains("attr_1"));

      // #####################################################
      // run again, with sub filters
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findGroupsWs --queryFilterType=OR --queryFilterType0=OR --queryFilterType00=FIND_BY_GROUP_NAME_APPROXIMATE --groupName00=aStem:aGroup --queryFilterType01=FIND_BY_GROUP_NAME_APPROXIMATE --groupName01=aStem:aGroup --queryFilterType1=FIND_BY_GROUP_NAME_APPROXIMATE --groupName1=aStem:aGroup",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem:aGroup", matcher.group(2));
      assertEquals(output, "aStem:aGroup", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("OR")
          && GrouperClientWs.mostRecentRequest
              .contains("FIND_BY_GROUP_NAME_APPROXIMATE"));

      // #####################################################
      // run again, search by names

      Group group1 = Group.saveGroup(grouperSession, "aStem:aGroup1", null, "aStem:aGroup1", null, null, null, true);
      
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findGroupsWs --groupNames=aStem:aGroup,aStem:aGroup1",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertEquals(output, 2, outputLines.length);
      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem:aGroup", matcher.group(2));
      assertEquals(output, "aStem:aGroup", matcher.group(3));
      
      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals(output, "1", matcher.group(1));
      assertEquals(output, "aStem:aGroup1", matcher.group(2));
      assertEquals(output, "aStem:aGroup1", matcher.group(3));

      // #####################################################
      // run again, search by uuids

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findGroupsWs --groupUuids=" + group.getId() + "," + group1.getId(),
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertEquals(output, 2, outputLines.length);
      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem:aGroup", matcher.group(2));
      assertEquals(output, "aStem:aGroup", matcher.group(3));
      
      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals(output, "1", matcher.group(1));
      assertEquals(output, "aStem:aGroup1", matcher.group(2));
      assertEquals(output, "aStem:aGroup1", matcher.group(3));

    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testFindStems() throws Exception {

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findStemsWs --stemQueryFilterType=FIND_BY_STEM_NAME --stemName=aStem",
                  " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Pattern pattern = Pattern
          .compile("^Index (\\d+): name: (.+), displayName: (.+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem", matcher.group(2));
      assertEquals(output, "aStem", matcher.group(3));

      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=findStemsWs --stemQueryFilterType=FIND_BY_STEM_NAME --groupName=aStem:aGroup --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();

      System.setOut(systemOut);

      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findStemsWs --stemQueryFilterType=FIND_BY_STEM_NAME --stemName=aStem --outputTemplate=${index}",
                  " "));

      System.out.flush();

      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertEquals("0", output);

      // #####################################################
      // run again, with parentStemScope
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findStemsWs  --stemQueryFilterType=FIND_BY_PARENT_STEM_NAME --parentStemName=aStem --parentStemNameScope=ALL_IN_SUBTREE",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem:aStem0", matcher.group(2));
      assertEquals(output, "aStem:aStem0", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("parentStemName")
              && GrouperClientWs.mostRecentRequest
                  .contains("parentStemNameScope"));

      // #####################################################
      // run again, with params
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findStemsWs --stemQueryFilterType=FIND_BY_STEM_NAME --stemName=aStem --paramName0=whatever --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem", matcher.group(2));
      assertEquals(output, "aStem", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
          && GrouperClientWs.mostRecentRequest.contains("someValue"));

      // #####################################################
      // run again, with uuid
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findStemsWs --stemQueryFilterType=FIND_BY_STEM_UUID --stemUuid=abc",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertTrue(output, GrouperClientUtils.isBlank(output));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("stemUuid")
          && GrouperClientWs.mostRecentRequest.contains("abc"));

      // #####################################################
      // run again, with stem attribute
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findStemsWs --stemQueryFilterType=FIND_BY_APPROXIMATE_ATTRIBUTE --stemAttributeName=name --stemAttributeValue=aStem",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 2, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem", matcher.group(2));
      assertEquals(output, "aStem", matcher.group(3));

      matcher = pattern.matcher(outputLines[1]);
      assertTrue(outputLines[1], matcher.matches());

      assertEquals(output, "1", matcher.group(1));
      assertEquals(output, "aStem:aStem0", matcher.group(2));
      assertEquals(output, "aStem:aStem0", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest
          .contains("FIND_BY_APPROXIMATE_ATTRIBUTE")
          && GrouperClientWs.mostRecentRequest.contains("stemAttributeName")
          && GrouperClientWs.mostRecentRequest.contains("aStem"));

      // #####################################################
      // run again, with sub filters
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findStemsWs --stemQueryFilterType=OR --stemQueryFilterType0=OR --stemQueryFilterType00=FIND_BY_STEM_NAME --stemName00=aStem --stemQueryFilterType01=FIND_BY_STEM_NAME --stemName01=aStem --stemQueryFilterType1=FIND_BY_STEM_NAME --stemName1=aStem",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      matcher = pattern.matcher(outputLines[0]);

      assertEquals(output, 1, outputLines.length);
      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem", matcher.group(2));
      assertEquals(output, "aStem", matcher.group(3));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("OR")
          && GrouperClientWs.mostRecentRequest.contains("FIND_BY_STEM_NAME"));

      // #####################################################
      // run again, search by names
      GrouperSession grouperSession = GrouperSession.startRootSession();
      Stem aStem = StemFinder.findByName(grouperSession, "aStem", true);
      Stem aStem0 = StemFinder.findByName(grouperSession, "aStem:aStem0", true);
      
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findStemsWs --stemNames=aStem,aStem:aStem0",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertEquals(output, 2, outputLines.length);
      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem", matcher.group(2));
      assertEquals(output, "aStem", matcher.group(3));
      
      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals(output, "1", matcher.group(1));
      assertEquals(output, "aStem:aStem0", matcher.group(2));
      assertEquals(output, "aStem:aStem0", matcher.group(3));

      // #####################################################
      // run again, search by uuids

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=findStemsWs --stemUuids=" + aStem.getUuid() + "," + aStem0.getUuid(),
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertEquals(output, 2, outputLines.length);
      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals(output, "0", matcher.group(1));
      assertEquals(output, "aStem", matcher.group(2));
      assertEquals(output, "aStem", matcher.group(3));
      
      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals(output, "1", matcher.group(1));
      assertEquals(output, "aStem:aStem0", matcher.group(2));
      assertEquals(output, "aStem:aStem0", matcher.group(3));

      
    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * try get members with a slash
   * Note: for this test to work, you need this in tomcat start:
   * -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true
   * @throws Exception
   */
  public void testGetMembersSlash() throws Exception {
    // make sure group exists
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Group group = Group.saveGroup(grouperSession, "aStem:aGroup/1", null,
        "aStem:aGroup/1", "aGroup", null, null, true);

    // give permissions
    String wsUserLabel = GrouperClientUtils.propertiesValue(
        "grouperClient.webService.user.label", true);
    String wsUserString = GrouperClientUtils.propertiesValue(
        "grouperClient.webService." + wsUserLabel, true);
    Subject wsUser = SubjectFinder.findByIdOrIdentifier(wsUserString, true);

    group.grantPriv(wsUser, AccessPrivilege.READ, false);
    group.grantPriv(wsUser, AccessPrivilege.VIEW, false);
    group.grantPriv(wsUser, AccessPrivilege.ADMIN, false);

    // add some subjects
    //group.addMember(SubjectTestHelper.SUBJ0, false);

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      //add member
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
              "--operation=addMemberWs --groupName=aStem:aGroup/1 --subjectIds=test.subject.0 --outputTemplate=Index${index}:success:${resultMetadata.success}:code:${resultMetadata.resultCode}:${wsSubject.id}:${wsAddMemberResults.wsGroupAssigned.name}$newline$${index}",
              " "));
      System.out.flush();
      String output = new String(baos.toByteArray());
    
      System.setOut(systemOut);
    
      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
      //match this: Index0:success:T:code:SUCCESS:test.subject.0:aStem:aGroup/1
      Pattern pattern = Pattern
          .compile("^Index(\\d+):success:T:code:([A-Z_]+?):(.+?):(.*+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);
    
      assertTrue(outputLines[0], matcher.matches());
    
      assertEquals(outputLines[0], "0", matcher.group(1));
      assertEquals(outputLines[0], "SUCCESS", matcher.group(2));
      assertEquals(outputLines[0], "test.subject.0", matcher.group(3));
      assertEquals(outputLines[0], "aStem:aGroup/1", matcher.group(4));
    
      
      //get members
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getMembersWs --groupNames=aStem:aGroup/1",
          " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      // match: ^GroupIndex (\d+)\: success\: ([TF])\: code: ([A-Z_]+)\: group\:
      // (.*)\: subjectIndex\: (\d+)\: (.*)$
      pattern = Pattern
          .compile("^GroupIndex (\\d+)\\: success\\: ([TF])\\: code: ([A-Z_]+)\\: group\\: (.*)\\: subjectIndex\\: (\\d+)\\: (.*)$");
      matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals(outputLines[0], "0", matcher.group(1));
      assertEquals(outputLines[0], "T", matcher.group(2));
      assertEquals(outputLines[0], "SUCCESS", matcher.group(3));
      assertEquals(outputLines[0], "aStem:aGroup/1", matcher.group(4));
      assertEquals(outputLines[0], "0", matcher.group(5));
      String subjectId = matcher.group(6);
      assertTrue(outputLines[0], GrouperClientUtils.equals("test.subject.0",
          subjectId));
    } finally {
      System.setOut(systemOut);
    }
  }
  
  /**
   * @throws Exception
   */
  public void testGetMembers() throws Exception {

    // make sure group exists
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Group group = Group.saveGroup(grouperSession, "aStem:aGroup", null,
        "aStem:aGroup", "aGroup", null, null, true);
    Group group2 = Group.saveGroup(grouperSession, "aStem:aGroup2", null,
        "aStem:aGroup2", "aGroup2", null, null, true);

    // give permissions
    String wsUserLabel = GrouperClientUtils.propertiesValue(
        "grouperClient.webService.user.label", true);
    String wsUserString = GrouperClientUtils.propertiesValue(
        "grouperClient.webService." + wsUserLabel, true);
    Subject wsUser = SubjectFinder.findByIdOrIdentifier(wsUserString, true);

    group.grantPriv(wsUser, AccessPrivilege.READ, false);
    group.grantPriv(wsUser, AccessPrivilege.VIEW, false);
    group2.grantPriv(wsUser, AccessPrivilege.READ, false);
    group2.grantPriv(wsUser, AccessPrivilege.VIEW, false);

    // add some subjects
    group.addMember(SubjectTestHelper.SUBJ0, false);
    group.addMember(SubjectTestHelper.SUBJ1, false);
    group2.addMember(SubjectTestHelper.SUBJ2, false);
    group2.addMember(SubjectTestHelper.SUBJ3, false);

    PrintStream systemOut = System.out;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {

      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2",
          " "));
      System.out.flush();
      String output = new String(baos.toByteArray());

      System.setOut(systemOut);

      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      // match: ^GroupIndex (\d+)\: success\: ([TF])\: code: ([A-Z_]+)\: group\:
      // (.*)\: subjectIndex\: (\d+)\: (.*)$
      Pattern pattern = Pattern
          .compile("^GroupIndex (\\d+)\\: success\\: ([TF])\\: code: ([A-Z_]+)\\: group\\: (.*)\\: subjectIndex\\: (\\d+)\\: (.*)$");
      Matcher matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLines[0], matcher.matches());

      assertEquals(outputLines[0], "0", matcher.group(1));
      assertEquals(outputLines[0], "T", matcher.group(2));
      assertEquals(outputLines[0], "SUCCESS", matcher.group(3));
      assertEquals(outputLines[0], "aStem:aGroup", matcher.group(4));
      assertEquals(outputLines[0], "0", matcher.group(5));
      String subjectId = matcher.group(6);
      assertTrue(outputLines[0], GrouperClientUtils.equals("test.subject.0",
          subjectId)
          || GrouperClientUtils.equals("test.subject.1", subjectId));

      matcher = pattern.matcher(outputLines[1]);

      assertTrue(outputLines[1], matcher.matches());

      assertEquals(outputLines[1], "0", matcher.group(1));
      assertEquals(outputLines[1], "T", matcher.group(2));
      assertEquals(outputLines[1], "SUCCESS", matcher.group(3));
      assertEquals(outputLines[1], "aStem:aGroup", matcher.group(4));
      assertEquals(outputLines[1], "1", matcher.group(5));
      subjectId = matcher.group(6);
      assertTrue(outputLines[1], GrouperClientUtils.equals("test.subject.0",
          subjectId)
          || GrouperClientUtils.equals("test.subject.1", subjectId));

      matcher = pattern.matcher(outputLines[2]);

      assertTrue(outputLines[2], matcher.matches());

      assertEquals(outputLines[2], "1", matcher.group(1));
      assertEquals(outputLines[2], "T", matcher.group(2));
      assertEquals(outputLines[2], "SUCCESS", matcher.group(3));
      assertEquals(outputLines[2], "aStem:aGroup2", matcher.group(4));
      assertEquals(outputLines[2], "0", matcher.group(5));
      subjectId = matcher.group(6);
      assertTrue(outputLines[2], GrouperClientUtils.equals("test.subject.2",
          subjectId)
          || GrouperClientUtils.equals("test.subject.3", subjectId));

      matcher = pattern.matcher(outputLines[3]);

      assertTrue(outputLines[3], matcher.matches());

      assertEquals(outputLines[3], "1", matcher.group(1));
      assertEquals(outputLines[3], "T", matcher.group(2));
      assertEquals(outputLines[3], "SUCCESS", matcher.group(3));
      assertEquals(outputLines[3], "aStem:aGroup2", matcher.group(4));
      assertEquals(outputLines[3], "1", matcher.group(5));
      subjectId = matcher.group(6);
      assertTrue(outputLines[3], GrouperClientUtils.equals("test.subject.2",
          subjectId)
          || GrouperClientUtils.equals("test.subject.3", subjectId));

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      // ######################################################
      // Try a sourceId

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --sourceIds=jdbc,g:gsa",
          " "));

      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertEquals(4, GrouperUtil.length(outputLines));
      
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --sourceIds=g:gsa",
          " "));

      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertEquals(0, GrouperUtil.length(outputLines));
      
      
      // ######################################################
      // Try uuid

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      Group aGroup = GroupFinder.findByName(grouperSession, "aStem:aGroup", true);
      Group aGroup2 = GroupFinder.findByName(grouperSession, "aStem:aGroup2", true);
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getMembersWs --groupUuids=" + aGroup.getUuid() + "," + aGroup2.getUuid(),
          " "));

      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertEquals(4, GrouperUtil.length(outputLines));
      
      
      // ######################################################
      // Try a subject attribute name with custom template

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --subjectAttributeNames=a,name --outputTemplate=${wsSubject.getAttributeValue(1)}$newline$",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      outputLines = GrouperClientUtils.splitTrim(output, "\n");

      Arrays.sort(outputLines);

      assertEquals(outputLines[0], "my name is test.subject.0");
      assertEquals(outputLines[1], "my name is test.subject.1");
      assertEquals(outputLines[2], "my name is test.subject.2");
      assertEquals(outputLines[3], "my name is test.subject.3");

      // #######################################################
      // try member filter

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --memberFilter=Effective",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      // #######################################################
      // try member filter nonimmediate

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --memberFilter=NonImmediate",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      // #######################################################
      // try includeGroupDetail

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --includeGroupDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      // #######################################################
      // try includeSubjectDetail

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --includeSubjectDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      // #######################################################
      // try subjectAttributeNames

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --subjectAttributeNames=name",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      // #######################################################
      // try params

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --paramName0=someParam --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("someParam")
              && GrouperClientWs.mostRecentRequest.toLowerCase().contains(
                  "params")
              && GrouperClientWs.mostRecentRequest.contains("someValue"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      // #######################################################
      // try fieldName

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --fieldName=members",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("fieldName")
              && GrouperClientWs.mostRecentRequest.contains("members"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      // #######################################################
      // try actAsSubject

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --actAsSubjectId=GrouperSystem",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("actAsSubject")
              && GrouperClientWs.mostRecentRequest.contains("GrouperSystem"));

      // #######################################################
      // try actAsSubject but with alias

      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembersWs --groupNames=aStem:aGroup,aStem:aGroup2 --actAsPennId=GrouperSystem",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());

      System.setOut(systemOut);

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("actAsSubject")
              && GrouperClientWs.mostRecentRequest.contains("GrouperSystem"));

    } finally {
      System.setOut(systemOut);
    }

  }

  /**
   * @throws Exception
   */
  public void testGetGroups2() throws Exception {
  
    // make sure group exists
    @SuppressWarnings("unused")
    GrouperSession grouperSession = GrouperSession.startRootSession();
    
    Subject         subj  = SubjectTestHelper.SUBJ0;
    Subject         subj1  = SubjectTestHelper.SUBJ1;
    GrouperSession  s     = SessionHelper.getRootSession();
    Stem            root  = StemFinder.findRootStem(s);
    Stem            edu   = root.addChildStem("edu", "edu");
    Stem            eduSub   = edu.addChildStem("eduSub", "eduSub");
    Stem            edu2   = root.addChildStem("edu2", "edu2");
    Group           i2    = edu.addChildGroup("i2", "i2");
    Group           i2sub    = eduSub.addChildGroup("i2sub", "i2sub");
    Group           edu2i2sub    = edu2.addChildGroup("edu2i2sub", "edu2i2sub");
    Group           comp1    = edu.addChildGroup("comp1", "comp1");
    Group           compLeft    = edu.addChildGroup("compLeft", "compRight");
    Group           compRight    = edu.addChildGroup("compRight", "compRight");
    
    comp1.addCompositeMember(CompositeType.INTERSECTION, compLeft, compRight);
    
    compLeft.addMember(subj);  
    compRight.addMember(subj);  
    
    Group           uofc  = edu.addChildGroup("uofc", "uofc");
    GroupHelper.addMember(uofc, subj, "members");
    GroupHelper.addMember(i2, uofc.toSubject(), "members");
    
    i2sub.addMember(subj1);
    edu2i2sub.addMember(subj1);
  
    // give permissions
    String wsUserLabel = GrouperClientUtils.propertiesValue(
        "grouperClient.webService.user.label", true);
    String wsUserString = GrouperClientUtils.propertiesValue(
        "grouperClient.webService." + wsUserLabel, true);
    Subject wsUser = SubjectFinder.findByIdOrIdentifier(wsUserString, true);
  
    i2.grantPriv(wsUser, AccessPrivilege.READ, false);
    i2sub.grantPriv(wsUser, AccessPrivilege.READ, false);
    edu2i2sub.grantPriv(wsUser, AccessPrivilege.READ, false);
    comp1.grantPriv(wsUser, AccessPrivilege.READ, false);
    compLeft.grantPriv(wsUser, AccessPrivilege.READ, false);
    compRight.grantPriv(wsUser, AccessPrivilege.READ, false);
    uofc.grantPriv(wsUser, AccessPrivilege.READ, false);
  
  
    PrintStream systemOut = System.out;
  
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));
  
    //Set<Group> groups = member.getImmediateGroups();
    //assertEquals(3, groups.size());
    //assertTrue(groups.contains(compLeft));
    //assertTrue(groups.contains(compRight));
    //assertTrue(groups.contains(uofc));

    GrouperClient.main(GrouperClientUtils.splitTrim(
        "--operation=getGroupsWs --subjectIds=test.subject.0 --memberFilter=Immediate --sortString=name",
        " "));
    System.out.flush();
    String output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);

    String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(3, outputLines.length);
    
    Pattern pattern = Pattern
        .compile("^SubjectIndex (\\d+): success: T: code: ([A-Z_]+): subject: (.*): groupIndex: (\\d+): (.*+)$");
    Matcher matcher = pattern.matcher(outputLines[0]);

    assertTrue(outputLines[0], matcher.matches());

    assertEquals("0", matcher.group(1));
    assertEquals("SUCCESS", matcher.group(2));
    assertEquals("test.subject.0", matcher.group(3));
    assertEquals("0", matcher.group(4));
    assertEquals(matcher.group(5), compLeft.getName(), matcher.group(5));

    matcher = pattern.matcher(outputLines[1]);

    assertTrue(outputLines[1], matcher.matches());

    assertEquals("0", matcher.group(1));
    assertEquals("SUCCESS", matcher.group(2));
    assertEquals("test.subject.0", matcher.group(3));
    assertEquals("1", matcher.group(4));
    assertEquals(matcher.group(5), compRight.getName(), matcher.group(5));

    matcher = pattern.matcher(outputLines[2]);
    
    assertTrue(outputLines[2], matcher.matches());

    assertEquals("0", matcher.group(1));
    assertEquals("SUCCESS", matcher.group(2));
    assertEquals("test.subject.0", matcher.group(3));
    assertEquals("2", matcher.group(4));
    assertEquals(matcher.group(5), uofc.getName(), matcher.group(5));


    // #####################################################
    //groups = member.getNonImmediateGroups();
    //assertEquals(2, groups.size());
    //assertTrue(groups.contains(comp1));
    //assertTrue(groups.contains(i2));
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    GrouperClient
        .main(GrouperClientUtils
            .splitTrim(
                "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.0 --memberFilter=NonImmediate --sortString=name",
                " "));

    System.out.flush();

    output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);
    
    outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(2, outputLines.length);
    
    pattern = Pattern
        .compile("^SubjectIndex (\\d+): success: T: code: ([A-Z_]+): subject: (.*): groupIndex: (\\d+): (.*+)$");
    matcher = pattern.matcher(outputLines[0]);

    assertTrue(outputLines[0], matcher.matches());

    assertEquals("0", matcher.group(1));
    assertEquals("SUCCESS", matcher.group(2));
    assertEquals("test.subject.0", matcher.group(3));
    assertEquals("0", matcher.group(4));
    assertEquals(matcher.group(5), comp1.getName(), matcher.group(5));

    matcher = pattern.matcher(outputLines[1]);

    assertTrue(outputLines[1], matcher.matches());

    assertEquals("0", matcher.group(1));
    assertEquals("SUCCESS", matcher.group(2));
    assertEquals("test.subject.0", matcher.group(3));
    assertEquals("1", matcher.group(4));
    assertEquals(matcher.group(5), i2.getName(), matcher.group(5));
  
    // #####################################################
    //groups = member1.getEffectiveGroups();
    //assertEquals(0, groups.size());    
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    GrouperClient
        .main(GrouperClientUtils
            .splitTrim(
                "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.1 --memberFilter=Effective",
                " "));

    System.out.flush();

    output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);
    
    outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(0, GrouperClientUtils.length(outputLines));
    
    // #####################################################
    //groups = member1.getImmediateGroups(Group.getDefaultList(), "whatever", null, null, null, true);
    //assertEquals(0, groups.size());
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    GrouperClient
        .main(GrouperClientUtils
            .splitTrim(
                "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.1 --memberFilter=Immediate --fieldName=members --enabled=T --scope=whatever",
                " "));

    System.out.flush();

    output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);
    
    outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(0, GrouperClientUtils.length(outputLines));
    
    // #####################################################
    //groups = member1.getImmediateGroups(Group.getDefaultList(), "edu:eduSub", null, null, null, true);
    //assertEquals(1, groups.size());
    //assertEquals("edu:eduSub:i2sub", ((Group)GrouperUtil.get(groups, 0)).getName());
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    GrouperClient
        .main(GrouperClientUtils
            .splitTrim(
                "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.1 --memberFilter=Immediate --scope=edu:eduSub",
                " "));

    System.out.flush();

    output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);
    
    outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(1, outputLines.length);
    
    pattern = Pattern
        .compile("^SubjectIndex (\\d+): success: T: code: ([A-Z_]+): subject: (.*): groupIndex: (\\d+): (.*+)$");
    matcher = pattern.matcher(outputLines[0]);

    assertTrue(outputLines[0], matcher.matches());

    assertEquals("0", matcher.group(1));
    assertEquals("SUCCESS", matcher.group(2));
    assertEquals("test.subject.1", matcher.group(3));
    assertEquals("0", matcher.group(4));
    assertEquals(matcher.group(5), i2sub.getName(), matcher.group(5));

    // #####################################################
    //groups = member1.getImmediateGroups(Group.getDefaultList(), "edu2", null, null, null, true);
    //assertEquals(1, groups.size());
    //assertEquals("edu2:edu2i2sub", ((Group)GrouperUtil.get(groups, 0)).getName());
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    GrouperClient
        .main(GrouperClientUtils
            .splitTrim(
                "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.1 --memberFilter=Immediate --scope=edu2",
                " "));

    System.out.flush();

    output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);
    
    outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(1, outputLines.length);
    
    pattern = Pattern
        .compile("^SubjectIndex (\\d+): success: T: code: ([A-Z_]+): subject: (.*): groupIndex: (\\d+): (.*+)$");
    matcher = pattern.matcher(outputLines[0]);

    assertTrue(outputLines[0], matcher.matches());

    assertEquals("0", matcher.group(1));
    assertEquals("SUCCESS", matcher.group(2));
    assertEquals("test.subject.1", matcher.group(3));
    assertEquals("0", matcher.group(4));
    assertEquals(matcher.group(5), edu2i2sub.getName(), matcher.group(5));

    // #####################################################
    //groups = member1.getImmediateGroups(Group.getDefaultList(), "edu2", null, null, null, false);
    //assertEquals(0, groups.size());
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    GrouperClient
        .main(GrouperClientUtils
            .splitTrim(
                "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.1 --memberFilter=Immediate --fieldName=members --enabled=F --scope=edu2",
                " "));

    System.out.flush();

    output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);
    
    outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(0, GrouperClientUtils.length(outputLines));
    

    // #####################################################
    //try {
    //  groups = member1.getImmediateGroups(Group.getDefaultList(), null, edu, null, null, true);
    //  fail("Need stemScope");
    //} catch (Exception e) {
    //  //good
    //}
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    try {
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.1 --memberFilter=Immediate --stem=" + edu.getName(),
                  " "));
      fail("Need stemScope");
    } catch (Exception e) {
      //good
    } finally {
      System.out.flush();
  
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      System.out.println(output);
    }
    
    // #####################################################
    //groups = member1.getImmediateGroups(Group.getDefaultList(), null, edu, Scope.ONE, null, true);
    //assertEquals(0, groups.size());
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    GrouperClient
        .main(GrouperClientUtils
            .splitTrim(
                "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.1 --memberFilter=Immediate --stemName=" + edu.getName() + " --stemScope=ONE_LEVEL",
                " "));

    System.out.flush();

    output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);
    
    outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(0, GrouperClientUtils.length(outputLines));

    // #####################################################
    //groups = member1.getImmediateGroups(Group.getDefaultList(), null, edu2, Scope.ONE, null, true);
    //assertEquals(1, groups.size());
    //assertEquals("edu2:edu2i2sub", ((Group)GrouperUtil.get(groups, 0)).getName());
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    GrouperClient
        .main(GrouperClientUtils
            .splitTrim(
                "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.1 --memberFilter=Immediate --stemName=" + edu2.getName() + " --stemScope=ONE_LEVEL",
                " "));

    System.out.flush();

    output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);
    
    outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(1, outputLines.length);
    
    pattern = Pattern
        .compile("^SubjectIndex (\\d+): success: T: code: ([A-Z_]+): subject: (.*): groupIndex: (\\d+): (.*+)$");
    matcher = pattern.matcher(outputLines[0]);

    assertTrue(outputLines[0], matcher.matches());

    assertEquals("0", matcher.group(1));
    assertEquals("SUCCESS", matcher.group(2));
    assertEquals("test.subject.1", matcher.group(3));
    assertEquals("0", matcher.group(4));
    assertEquals(matcher.group(5), edu2i2sub.getName(), matcher.group(5));
    
    // #####################################################
    //groups = member1.getImmediateGroups(Group.getDefaultList(), null, edu, Scope.SUB, null, true);
    //assertEquals(1, groups.size());
    //assertEquals("edu:eduSub:i2sub", ((Group)GrouperUtil.get(groups, 0)).getName());
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    GrouperClient
        .main(GrouperClientUtils
            .splitTrim(
                "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.1 --memberFilter=Immediate --stemName=" + edu.getName() + " --stemScope=ALL_IN_SUBTREE",
                " "));

    System.out.flush();

    output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);
    
    outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(1, outputLines.length);
    
    pattern = Pattern
        .compile("^SubjectIndex (\\d+): success: T: code: ([A-Z_]+): subject: (.*): groupIndex: (\\d+): (.*+)$");
    matcher = pattern.matcher(outputLines[0]);

    assertTrue(outputLines[0], matcher.matches());

    assertEquals("0", matcher.group(1));
    assertEquals("SUCCESS", matcher.group(2));
    assertEquals("test.subject.1", matcher.group(3));
    assertEquals("0", matcher.group(4));
    assertEquals(matcher.group(5), i2sub.getName(), matcher.group(5));

    // #####################################################
    //groups = member1.getImmediateGroups(Group.getDefaultList(), null, edu2, Scope.SUB, null, true);
    //assertEquals(1, groups.size());
    //assertEquals("edu2:edu2i2sub", ((Group)GrouperUtil.get(groups, 0)).getName());
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    GrouperClient
        .main(GrouperClientUtils
            .splitTrim(
                "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.1 --memberFilter=Immediate --stemName=" + edu2.getName() + " --stemScope=ALL_IN_SUBTREE",
                " "));

    System.out.flush();

    output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);
    
    outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(1, outputLines.length);
    
    pattern = Pattern
        .compile("^SubjectIndex (\\d+): success: T: code: ([A-Z_]+): subject: (.*): groupIndex: (\\d+): (.*+)$");
    matcher = pattern.matcher(outputLines[0]);

    assertTrue(outputLines[0], matcher.matches());

    assertEquals("0", matcher.group(1));
    assertEquals("SUCCESS", matcher.group(2));
    assertEquals("test.subject.1", matcher.group(3));
    assertEquals("0", matcher.group(4));
    assertEquals(matcher.group(5), edu2i2sub.getName(), matcher.group(5));

    // #####################################################
    //QueryOptions queryOptions = new QueryOptions().paging(1, 1, true).sortAsc("name");
    //groups = member.getImmediateGroups(Group.getDefaultList(), null, null, null, queryOptions, true);
    //assertEquals(1, groups.size());
    //assertEquals("edu:compLeft", ((Group)GrouperUtil.get(groups, 0)).getName());
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    // test a command line template
    GrouperClient
        .main(GrouperClientUtils
            .splitTrim(
                "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.0 --memberFilter=Immediate --pageSize=1 --pageNumber=1 --sortString=name",
                " "));

    System.out.flush();

    output = new String(baos.toByteArray());

    System.setOut(systemOut);

    System.out.println(output);
    
    outputLines = GrouperClientUtils.splitTrim(output, "\n");
    
    assertEquals(1, outputLines.length);
    
    pattern = Pattern
        .compile("^SubjectIndex (\\d+): success: T: code: ([A-Z_]+): subject: (.*): groupIndex: (\\d+): (.*+)$");
    matcher = pattern.matcher(outputLines[0]);

    assertTrue(outputLines[0], matcher.matches());

    assertEquals("0", matcher.group(1));
    assertEquals("SUCCESS", matcher.group(2));
    assertEquals("test.subject.0", matcher.group(3));
    assertEquals("0", matcher.group(4));
    assertEquals(matcher.group(5), compLeft.getName(), matcher.group(5));


    // #####################################################
    //queryOptions = new QueryOptions().paging(1, 1, true).sortAsc("non existent column");
    //try {
    //  groups = member.getImmediateGroups(Group.getDefaultList(), null, null, null, queryOptions, true);
    //  fail("Column doesnt exist");
    //} catch (Exception e) {
    //  //good
    //}
    
    baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));

    try {
      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getGroupsWs --subjectIdentifiers=id.test.subject.0 --memberFilter=Immediate --pageSize=1 --pageNumber=1 --sortString=doesntExist",
                  " "));
      fail("Column doesnt exist");
    } catch (Exception e) {
      //good
    } finally {
      System.out.flush();
  
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      System.out.println(output);
    }    
  
  }

  /**
   * @throws Exception
   */
  public void testGetMemberships() throws Exception {
  
    // make sure group exists
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Group group = Group.saveGroup(grouperSession, "aStem:aGroup", null,
        "aStem:aGroup", "aGroup", null, null, true);
    Group group2 = Group.saveGroup(grouperSession, "aStem:aGroup2", null,
        "aStem:aGroup2", "aGroup2", null, null, true);
  
    // give permissions
    String wsUserLabel = GrouperClientUtils.propertiesValue(
        "grouperClient.webService.user.label", true);
    String wsUserString = GrouperClientUtils.propertiesValue(
        "grouperClient.webService." + wsUserLabel, true);
    Subject wsUser = SubjectFinder.findByIdOrIdentifier(wsUserString, true);
  
    group.grantPriv(wsUser, AccessPrivilege.READ, false);
    group.grantPriv(wsUser, AccessPrivilege.VIEW, false);
    group2.grantPriv(wsUser, AccessPrivilege.READ, false);
    group2.grantPriv(wsUser, AccessPrivilege.VIEW, false);
  
    // add some subjects
    group.addMember(SubjectTestHelper.SUBJ0, false);
    group.addMember(SubjectTestHelper.SUBJ1, false);
    group2.addMember(SubjectTestHelper.SUBJ2, false);
    group2.addMember(SubjectTestHelper.SUBJ3, false);
  
    PrintStream systemOut = System.out;
  
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));
  
    try {
  
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2",
          " "));
      System.out.flush();
      String output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      // match: Index: 0: group: aStem:aGroup, subject: GrouperSystem, list: members, type: immediate, enabled: T
      // match: ^Index: (\d+)\: group\: (.+), subject\: (.+), list: (.+), type\: (.+), enabled\: (T|F)$
      Pattern pattern = Pattern
          .compile("^Index: (\\d+)\\: group\\: (.+), subject\\: (.+), list: (.+), type\\: (.+), enabled\\: (T|F)$");
      String outputLine = outputLines[0];

      Matcher matcher = pattern.matcher(outputLines[0]);

      assertTrue(outputLine, matcher.matches());
      assertEquals(outputLine, "0", matcher.group(1));
      assertEquals(outputLine, "aStem:aGroup", matcher.group(2));
      assertEquals(outputLine, "test.subject.0", matcher.group(3));
      assertEquals(outputLine, "members", matcher.group(4));
      assertEquals(outputLine, "immediate", matcher.group(5));
      assertEquals(outputLine, "T", matcher.group(6));
      
      outputLine = outputLines[1];

      matcher = pattern.matcher(outputLine);
  
      assertTrue(outputLine, matcher.matches());
  
      assertEquals(outputLine, "1", matcher.group(1));
      assertEquals(outputLine, "aStem:aGroup", matcher.group(2));
      assertEquals(outputLine, "test.subject.1", matcher.group(3));
      assertEquals(outputLine, "members", matcher.group(4));
      assertEquals(outputLine, "immediate", matcher.group(5));
      assertEquals(outputLine, "T", matcher.group(6));
  
      outputLine = outputLines[2];

      matcher = pattern.matcher(outputLine);
  
      assertTrue(outputLine, matcher.matches());
  
      assertEquals(outputLine, "2", matcher.group(1));
      assertEquals(outputLine, "aStem:aGroup2", matcher.group(2));
      assertEquals(outputLine, "test.subject.2", matcher.group(3));
      assertEquals(outputLine, "members", matcher.group(4));
      assertEquals(outputLine, "immediate", matcher.group(5));
      assertEquals(outputLine, "T", matcher.group(6));

      outputLine = outputLines[3];

      matcher = pattern.matcher(outputLine);
  
      assertTrue(outputLine, matcher.matches());
  
      assertEquals(outputLine, "3", matcher.group(1));
      assertEquals(outputLine, "aStem:aGroup2", matcher.group(2));
      assertEquals(outputLine, "test.subject.3", matcher.group(3));
      assertEquals(outputLine, "members", matcher.group(4));
      assertEquals(outputLine, "immediate", matcher.group(5));
      assertEquals(outputLine, "T", matcher.group(6));

      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // ######################################################
      // Try a sourceId
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --sourceIds=jdbc,g:gsa",
          " "));
  
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(4, GrouperUtil.length(outputLines));

      //##########################################################
      //Try a source id with no results
      
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --sourceIds=g:gsa",
          " "));
  
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(0, GrouperUtil.length(outputLines));
      
      
      // ######################################################
      // Try uuid
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      Group aGroup = GroupFinder.findByName(grouperSession, "aStem:aGroup", true);
      Group aGroup2 = GroupFinder.findByName(grouperSession, "aStem:aGroup2", true);
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getMembershipsWs --groupUuids=" + aGroup.getUuid() + "," + aGroup2.getUuid(),
          " "));
  
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(4, GrouperUtil.length(outputLines));
      
      
      // ######################################################
      // Try a subject attribute name with custom template
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --subjectAttributeNames=a,name --outputTemplate=${wsSubject.getAttributeValue(1)}$newline$",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      Arrays.sort(outputLines);
  
      assertEquals(outputLines[0], "my name is test.subject.0");
      assertEquals(outputLines[1], "my name is test.subject.1");
      assertEquals(outputLines[2], "my name is test.subject.2");
      assertEquals(outputLines[3], "my name is test.subject.3");
  
      // #######################################################
      // try member filter
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --memberFilter=Effective",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(0, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try member filter nonimmediate
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --memberFilter=NonImmediate",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(0, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try includeGroupDetail
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --includeGroupDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(4, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try includeSubjectDetail
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --includeSubjectDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(4, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try subjectAttributeNames
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --subjectAttributeNames=name",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(4, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try params
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --paramName0=someParam --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(4, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("someParam")
              && GrouperClientWs.mostRecentRequest.toLowerCase().contains(
                  "params")
              && GrouperClientWs.mostRecentRequest.contains("someValue"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try fieldName
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --fieldName=members",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(4, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("fieldName")
              && GrouperClientWs.mostRecentRequest.contains("members"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try actAsSubject
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --actAsSubjectId=GrouperSystem",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(4, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("actAsSubject")
              && GrouperClientWs.mostRecentRequest.contains("GrouperSystem"));
  
      // #######################################################
      // try actAsSubject but with alias
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --actAsPennId=GrouperSystem",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(4, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("actAsSubject")
              && GrouperClientWs.mostRecentRequest.contains("GrouperSystem"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("enabled"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("scope"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("sourceIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsStemLookup"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("stemScope"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsSubjectLookup"));

      
      // #######################################################
      // try enabled
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --enabled=F",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(0, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("enabled"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("scope"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("sourceIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsStemLookup"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("stemScope"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsSubjectLookup"));

      // #######################################################
      // try membership ids
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --membershipIds=123",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(0, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("enabled"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("scope"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("sourceIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsStemLookup"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("stemScope"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsSubjectLookup"));

      // #######################################################
      // try scope
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --scope=abc",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(0, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("enabled"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("scope"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("sourceIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsStemLookup"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("stemScope"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsSubjectLookup"));

      // #######################################################
      // try subjectSources
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --sourceIds=g:gsa",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(0, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("enabled"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("scope"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("sourceIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsStemLookup"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("stemScope"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsSubjectLookup"));

      // #######################################################
      // try one level stem
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --stemName=aStem --stemScope=ONE_LEVEL",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(4, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("enabled"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("scope"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("sourceIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("wsStemLookup"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("stemScope"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("ONE_LEVEL"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsSubjectLookup"));

      // #######################################################
      // try all in subtree
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --groupNames=aStem:aGroup,aStem:aGroup2 --stemName=aStem --stemScope=ALL_IN_SUBTREE",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(4, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("enabled"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("scope"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("sourceIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("wsStemLookup"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("stemScope"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("ALL_IN_SUBTREE"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsSubjectLookup"));

      // #######################################################
      // try all in subtree
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getMembershipsWs --subjectIds=test.subject.0,test.subject.1",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(2, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("enabled"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("scope"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("sourceIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("membershipIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsStemLookup"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("stemScope"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("ALL_IN_SUBTREE"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("wsSubjectLookup"));

      
    } finally {
      System.setOut(systemOut);
    }
  
  }

  /**
   * @throws Exception
   */
  public void testGetSubjects() throws Exception {
  
    // make sure group exists
    GrouperSession grouperSession = GrouperSession.startRootSession();
    Group group = Group.saveGroup(grouperSession, "aStem:aGroup", null,
        "aStem:aGroup", "aGroup", null, null, true);
    Group group2 = Group.saveGroup(grouperSession, "aStem:aGroup2", null,
        "aStem:aGroup2", "aGroup2", null, null, true);
  
    // give permissions
    String wsUserLabel = GrouperClientUtils.propertiesValue(
        "grouperClient.webService.user.label", true);
    String wsUserString = GrouperClientUtils.propertiesValue(
        "grouperClient.webService." + wsUserLabel, true);
    Subject wsUser = SubjectFinder.findByIdOrIdentifier(wsUserString, true);
  
    group.grantPriv(wsUser, AccessPrivilege.READ, false);
    group.grantPriv(wsUser, AccessPrivilege.VIEW, false);
    group2.grantPriv(wsUser, AccessPrivilege.READ, false);
    group2.grantPriv(wsUser, AccessPrivilege.VIEW, false);
  
    // add some subjects
    group.addMember(SubjectTestHelper.SUBJ0, false);
    group.addMember(SubjectTestHelper.SUBJ1, false);
    group2.addMember(SubjectTestHelper.SUBJ2, false);
    group2.addMember(SubjectTestHelper.SUBJ3, false);
  
    PrintStream systemOut = System.out;
  
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));
  
    try {
  
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getSubjectsWs --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "," + SubjectTestHelper.SUBJ1_ID,
          " "));
      System.out.flush();
      String output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");

      assertEquals(2, outputLines.length);
      
      // match: Index: 0: subject: GrouperSystem
      // match: ^Index: (\d+)\: subject\: (.+)$
      Pattern pattern = Pattern
          .compile("^Index: (\\d+)\\: success: (T|F), code: (.+), subject\\: (.+)$");

      String outputLine = outputLines[0];
  
      Matcher matcher = pattern.matcher(outputLines[0]);
  
      assertTrue(outputLine, matcher.matches());
      assertEquals(outputLine, "0", matcher.group(1));
      assertEquals(outputLine, "T", matcher.group(2));
      assertEquals(outputLine, "SUCCESS", matcher.group(3));
      assertEquals(outputLine, "test.subject.0", matcher.group(4));
      
      outputLine = outputLines[1];
  
      matcher = pattern.matcher(outputLine);
  
      assertTrue(outputLine, matcher.matches());
  
      assertEquals(outputLine, "1", matcher.group(1));
      assertEquals(outputLine, "T", matcher.group(2));
      assertEquals(outputLine, "SUCCESS", matcher.group(3));
      assertEquals(outputLine, "test.subject.1", matcher.group(4));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // ######################################################
      // Try sourceIds
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getSubjectsWs --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "," + SubjectTestHelper.SUBJ1_ID + " --subjectSources=jdbc,g:gsa",
          " "));
  
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(output, 2, GrouperUtil.length(outputLines));

      outputLine = outputLines[0];
      
      matcher = pattern.matcher(outputLines[0]);
  
      assertTrue(outputLine, matcher.matches());
      //since this is a different source, it is first (g:gsa)
      assertEquals(outputLine, "0", matcher.group(1));
      assertEquals(outputLine, "F", matcher.group(2));
      assertEquals(outputLine, "SUBJECT_NOT_FOUND", matcher.group(3));
      assertEquals(outputLine, "test.subject.1", matcher.group(4));
      
      outputLine = outputLines[1];
  
      matcher = pattern.matcher(outputLine);
  
      assertTrue(outputLine, matcher.matches());
  
      assertEquals(outputLine, "1", matcher.group(1));
      assertEquals(output, "T", matcher.group(2));
      assertEquals(outputLine, "SUCCESS", matcher.group(3));
      assertEquals(outputLine, "test.subject.0", matcher.group(4));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      // ######################################################
      // Try sourceId
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getSubjectsWs --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "," + SubjectTestHelper.SUBJ1_ID + " --subjectSources=jdbc",
          " "));
  
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(output, 2, GrouperUtil.length(outputLines));

      outputLine = outputLines[0];
      
      matcher = pattern.matcher(outputLines[0]);
  
      assertTrue(outputLine, matcher.matches());
      assertEquals(outputLine, "0", matcher.group(1));
      assertEquals(outputLine, "T", matcher.group(2));
      assertEquals(outputLine, "SUCCESS", matcher.group(3));
      assertEquals(outputLine, "test.subject.0", matcher.group(4));
      
      outputLine = outputLines[1];
  
      matcher = pattern.matcher(outputLine);
  
      assertTrue(outputLine, matcher.matches());
  
      assertEquals(outputLine, "1", matcher.group(1));
      assertEquals(outputLine, "T", matcher.group(2));
      assertEquals(outputLine, "SUCCESS", matcher.group(3));
      assertEquals(outputLine, "test.subject.1", matcher.group(4));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      // ######################################################
      // Try sourceId not found
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getSubjectsWs --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "," + SubjectTestHelper.SUBJ1_ID + " --subjectSources=g:gsa",
          " "));
  
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(output, 2, GrouperUtil.length(outputLines));

      outputLine = outputLines[0];
      
      matcher = pattern.matcher(outputLines[0]);
  
      assertTrue(outputLine, matcher.matches());
      assertEquals(outputLine, "0", matcher.group(1));
      assertEquals(outputLine, "F", matcher.group(2));
      assertEquals(outputLine, "SUBJECT_NOT_FOUND", matcher.group(3));
      assertEquals(outputLine, "test.subject.0", matcher.group(4));
      
      outputLine = outputLines[1];
  
      matcher = pattern.matcher(outputLine);
  
      assertTrue(outputLine, matcher.matches());
  
      assertEquals(outputLine, "1", matcher.group(1));
      assertEquals(outputLine, "F", matcher.group(2));
      assertEquals(outputLine, "SUBJECT_NOT_FOUND", matcher.group(3));
      assertEquals(outputLine, "test.subject.1", matcher.group(4));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      
      //##########################################################
      //Try a source id with no results
      
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getSubjectsWs --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "," + SubjectTestHelper.SUBJ1_ID + " --subjectSources=g:gsa,g:gsa",
          " "));
  
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(output, 2, GrouperUtil.length(outputLines));

      outputLine = outputLines[0];
      
      matcher = pattern.matcher(outputLines[0]);
  
      assertTrue(outputLine, matcher.matches());
      assertEquals(outputLine, "0", matcher.group(1));
      assertEquals(outputLine, "F", matcher.group(2));
      assertEquals(outputLine, "SUBJECT_NOT_FOUND", matcher.group(3));
      assertEquals(outputLine, "test.subject.0", matcher.group(4));
      
      outputLine = outputLines[1];
  
      matcher = pattern.matcher(outputLine);
  
      assertTrue(outputLine, matcher.matches());
  
      assertEquals(outputLine, "1", matcher.group(1));
      assertEquals(outputLine, "F", matcher.group(2));
      assertEquals(outputLine, "SUBJECT_NOT_FOUND", matcher.group(3));
      assertEquals(outputLine, "test.subject.1", matcher.group(4));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      
      //##########################################################
      //Try a identifier
      
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getSubjectsWs --subjectIdentifiers=id.test.subject.0,id.test.subject.1",
          " "));
  
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(output, 2, GrouperUtil.length(outputLines));

      outputLine = outputLines[0];
      
      matcher = pattern.matcher(outputLines[0]);
  
      assertTrue(outputLine, matcher.matches());
      assertEquals(outputLine, "0", matcher.group(1));
      assertEquals(outputLine, "T", matcher.group(2));
      assertEquals(outputLine, "SUCCESS", matcher.group(3));
      assertEquals(outputLine, "test.subject.0", matcher.group(4));
      
      outputLine = outputLines[1];
  
      matcher = pattern.matcher(outputLine);
  
      assertTrue(outputLine, matcher.matches());
  
      assertEquals(outputLine, "1", matcher.group(1));
      assertEquals(outputLine, "T", matcher.group(2));
      assertEquals(outputLine, "SUCCESS", matcher.group(3));
      assertEquals(outputLine, "test.subject.1", matcher.group(4));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));

      
      // ######################################################
      // Try filter by group
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      Group aGroup = GroupFinder.findByName(grouperSession, "aStem:aGroup", true);
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getSubjectsWs --groupUuid=" + aGroup.getUuid() + " --subjectIds=" + SubjectTestHelper.SUBJ5_ID + "," + SubjectTestHelper.SUBJ6_ID,
          " "));
  
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(output, 0, GrouperUtil.length(outputLines));
      
      // ######################################################
      // Try filter by group, success
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getSubjectsWs --groupUuid=" + aGroup.getUuid() + " --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "," + SubjectTestHelper.SUBJ7_ID,
          " "));
  
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(1, GrouperUtil.length(outputLines));
      
      outputLine = outputLines[0];
      
      matcher = pattern.matcher(outputLines[0]);
  
      assertTrue(outputLine, matcher.matches());
      assertEquals(outputLine, "0", matcher.group(1));
      assertEquals(outputLine, "T", matcher.group(2));
      assertEquals(outputLine, "SUCCESS", matcher.group(3));
      assertEquals(outputLine, "test.subject.0", matcher.group(4));
      
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("<wsGroupLookup><uuid>"));
      
      // ######################################################
      // Try filter by group, success
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
      
      GrouperClient.main(GrouperClientUtils.splitTrim(
          "--operation=getSubjectsWs --groupName=" + aGroup.getName() + " --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "," + SubjectTestHelper.SUBJ7_ID,
          " "));
  
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(1, GrouperUtil.length(outputLines));
      
      outputLine = outputLines[0];
      
      matcher = pattern.matcher(outputLines[0]);
  
      assertTrue(outputLine, matcher.matches());
      assertEquals(outputLine, "0", matcher.group(1));
      assertEquals(outputLine, "T", matcher.group(2));
      assertEquals(outputLine, "SUCCESS", matcher.group(3));
      assertEquals(outputLine, "test.subject.0", matcher.group(4));
      
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("<wsGroupLookup><groupName>"));
      
      // ######################################################
      // Try a subject attribute name with custom template
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --subjectIds=" + SubjectTestHelper.SUBJ0_ID + " --subjectAttributeNames=a,name --outputTemplate=${wsSubject.getAttributeValue(1)}$newline$",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
      
      assertEquals(1, outputLines.length);
      assertEquals(outputLines[0], "my name is test.subject.0");
  
      // #######################################################
      // try member filter
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --groupName=" + aGroup.getName() + " --subjectIds=" + SubjectTestHelper.SUBJ0_ID + " --memberFilter=Effective",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(0, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try member filter immediate
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --groupName=" + aGroup.getName() + " --subjectIds=" + SubjectTestHelper.SUBJ0_ID + " --memberFilter=Immediate",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(1, GrouperUtil.length(outputLines));
  
      outputLine = outputLines[0];
      
      matcher = pattern.matcher(outputLines[0]);
  
      assertTrue(outputLine, matcher.matches());
      assertEquals(outputLine, "0", matcher.group(1));
      assertEquals(outputLine, "T", matcher.group(2));
      assertEquals(outputLine, "SUCCESS", matcher.group(3));
      assertEquals(outputLine, "test.subject.0", matcher.group(4));

      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try includeGroupDetail
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --groupName=" + aGroup.getName() + " --subjectIds=" + SubjectTestHelper.SUBJ0_ID + " --includeGroupDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(1, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try includeSubjectDetail
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "  --includeSubjectDetail=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(1, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try subjectAttributeNames
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "  --subjectAttributeNames=name",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(1, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try params
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "  --paramName0=someParam --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(1, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("someParam")
              && GrouperClientWs.mostRecentRequest.toLowerCase().contains(
                  "params")
              && GrouperClientWs.mostRecentRequest.contains("someValue"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try fieldName
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --groupName=aStem:aGroup --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "  --fieldName=members",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(1, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("fieldName")
              && GrouperClientWs.mostRecentRequest.contains("members"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
  
      // #######################################################
      // try actAsSubject
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "  --actAsSubjectId=GrouperSystem",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(1, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("actAsSubject")
              && GrouperClientWs.mostRecentRequest.contains("GrouperSystem"));
  
      // #######################################################
      // try actAsSubject but with alias
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --subjectIds=" + SubjectTestHelper.SUBJ0_ID + "  --actAsPennId=GrouperSystem",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(1, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("actAsSubject")
              && GrouperClientWs.mostRecentRequest.contains("GrouperSystem"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("sourceIds"));
  
      // #######################################################
      // try searchString
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --searchString=test",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertTrue(output, 8 < GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("sourceIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsSubjectLookup"));
  
      
      // #######################################################
      // try searchString
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --searchString=test --sourceIds=jdbc",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertTrue(output, 8 < GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("sourceIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsSubjectLookup"));
  
      
      // #######################################################
      // try subjectSources
  
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=getSubjectsWs --searchString=test --sourceIds=g:gsa",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
      
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertEquals(output, 0, GrouperUtil.length(outputLines));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("memberFilter"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeGroupDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("subjectAttributeNames"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("params"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("fieldName"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("actAsSubject"));
      //subjectSources
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("sourceIds"));
      assertTrue(GrouperClientWs.mostRecentRequest,
          !GrouperClientWs.mostRecentRequest.contains("wsSubjectLookup"));
  
  
      
    } finally {
      System.setOut(systemOut);
    }
  
  }

  /**
   * @throws Exception
   */
  public void testAssignGrouperPrivileges() throws Exception {
  
    PrintStream systemOut = System.out;
  
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    System.setOut(new PrintStream(baos));
  
    try {
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesWs --groupName=aStem:aGroup --subjectIds=test.subject.0 --privilegeNames=optin --allowed=true",
                  " "));
      System.out.flush();
      String output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      String[] outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      Pattern pattern = Pattern
          .compile("^Index: ([0-9]+), success: (T|F), code: (.+), (group|stem): (.*), subject: (.+), (.+): (.+)$");
      Matcher matcher = pattern.matcher(outputLines[0]);
  
      assertEquals(output, 1, GrouperClientUtils.length(outputLines));
      assertTrue(outputLines[0], matcher.matches());
  
      assertEquals("0", matcher.group(1));
      assertEquals("T", matcher.group(2));
      assertEquals("SUCCESS_ALLOWED", matcher.group(3));
      assertEquals("group", matcher.group(4));
      assertEquals("aStem:aGroup", matcher.group(5));
      assertEquals("test.subject.0", matcher.group(6));
      assertEquals("access", matcher.group(7));
      assertEquals("optin", matcher.group(8));
  
      // #####################################################
      // run again with subject identifier, and privilege type
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesWs --groupName=aStem:aGroup --subjectIdentifiers=id.test.subject.0 --privilegeType=access --privilegeNames=optin --allowed=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      matcher = pattern.matcher(outputLines[0]);
  
      assertEquals(GrouperClientUtils.length(outputLines), 1);
      assertTrue(outputLines[0], matcher.matches());
  
      assertEquals("0", matcher.group(1));
      assertEquals("T", matcher.group(2));
      assertEquals("SUCCESS_ALLOWED_ALREADY_EXISTED", matcher.group(3));
      assertEquals("group", matcher.group(4));
      assertEquals("aStem:aGroup", matcher.group(5));
      assertEquals("test.subject.0", matcher.group(6));
      assertEquals("access", matcher.group(7));
      assertEquals("optin", matcher.group(8));
  
      assertTrue(GrouperClientWs.mostRecentRequest,
          GrouperClientWs.mostRecentRequest.contains("access")
              && GrouperClientWs.mostRecentRequest.contains("privilegeType")
              && GrouperClientWs.mostRecentRequest
                  .contains("id.test.subject.0"));
  
      // #####################################################
      // run with invalid args
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      // test a command line template
      try {
        GrouperClient
            .main(GrouperClientUtils
                .splitTrim(
                    "--operation=assignGrouperPrivilegesWs --groupName=aStem:aGroup --subjectIds=test.subject.0 --privilegeNames=optin --allowed=true --ousdfsdfate=${index}",
                    " "));
      } catch (Exception e) {
        assertTrue(e.getMessage(), e.getMessage().contains("ousdfsdfate"));
      }
      System.out.flush();
  
      System.setOut(systemOut);
  
      // #####################################################
      // run with custom template
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      // test a command line template
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesWs --groupName=aStem:aGroup --pennKeys=id.test.subject.0 --privilegeNames=optin --allowed=true --outputTemplate=${wsSubject.identifierLookup}",
                  " "));
  
      System.out.flush();
  
      output = new String(baos.toByteArray());
  
      assertEquals("id.test.subject.0", output);
  
      System.setOut(systemOut);
  
      // #####################################################
      // run again, with stem
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesWs --stemName=aStem --pennKeys=id.test.subject.0 --privilegeNames=stem --allowed=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      matcher = pattern.matcher(outputLines[0]);
  
      assertEquals(GrouperClientUtils.length(outputLines), 1);
      assertTrue(outputLines[0], matcher.matches());
  
      assertEquals("0", matcher.group(1));
      assertEquals("T", matcher.group(2));
      assertEquals("SUCCESS_ALLOWED_ALREADY_EXISTED", matcher.group(3));
      assertEquals("stem", matcher.group(4));
      assertEquals("aStem", matcher.group(5));
      assertEquals("test.subject.0", matcher.group(6));
      assertEquals("naming", matcher.group(7));
      assertEquals("stem", matcher.group(8));
  
      // #####################################################
      // run again, with includeGroupDetail and includeSubjectDetail
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesWs --groupName=aStem:aGroup --subjectIds=test.subject.0 --includeGroupDetail=true --includeSubjectDetail=true --privilegeNames=optin --allowed=false",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      matcher = pattern.matcher(outputLines[0]);
  
      assertEquals(GrouperClientUtils.length(outputLines), 1);
      assertTrue(outputLines[0], matcher.matches());
  
      assertEquals("0", matcher.group(1));
      assertEquals("T", matcher.group(2));
      assertEquals("SUCCESS_NOT_ALLOWED", matcher.group(3));
      assertEquals("group", matcher.group(4));
      assertEquals("aStem:aGroup", matcher.group(5));
      assertEquals("test.subject.0", matcher.group(6));
      assertEquals("access", matcher.group(7));
      assertEquals("optin", matcher.group(8));

      assertTrue(GrouperClientWs.mostRecentRequest
          .contains("includeGroupDetail")
          && GrouperClientWs.mostRecentRequest.contains("includeSubjectDetail"));
  
      // #####################################################
      // run again, with subject attributes
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesWs --groupName=aStem:aGroup --subjectIds=test.subject.0 --subjectAttributeNames=name --privilegeNames=optin --allowed=false --outputTemplate=${index}:$space$${wsSubject.getAttributeValue(0)}$newline$",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      assertTrue(outputLines[0], outputLines[0]
          .contains("my name is test.subject.0"));
  
      assertTrue(GrouperClientWs.mostRecentRequest.contains(">name<"));
      assertTrue(GrouperClientWs.mostRecentResponse
          .contains("my name is test.subject.0"));
  
      // #####################################################
      // run again, with params
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));
  
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesWs --groupName=aStem:aGroup --privilegeNames=optin --allowed=false --subjectIds=test.subject.0 --paramName0=whatever --paramValue0=someValue",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      matcher = pattern.matcher(outputLines[0]);
  
      assertEquals(GrouperClientUtils.length(outputLines), 1);
      assertTrue(outputLines[0], matcher.matches());
  
      assertEquals("0", matcher.group(1));
      assertEquals("T", matcher.group(2));
      assertEquals("SUCCESS_NOT_ALLOWED_DIDNT_EXIST", matcher.group(3));
      assertEquals("group", matcher.group(4));
      assertEquals("aStem:aGroup", matcher.group(5));
      assertEquals("test.subject.0", matcher.group(6));
      assertEquals("access", matcher.group(7));
      assertEquals("optin", matcher.group(8));

      assertTrue(GrouperClientWs.mostRecentRequest.contains("whatever")
          && GrouperClientWs.mostRecentRequest.contains("someValue"));
  
      // #####################################################
      // run again, replace existing
      baos = new ByteArrayOutputStream();
      System.setOut(new PrintStream(baos));

      GrouperSession grouperSession = GrouperSession.startRootSession();
      
      Group replaceGroup = new GroupSave(grouperSession).assignGroupNameToEdit("aStem:replaceExisting")
        .assignName("aStem:replaceExisting").assignCreateParentStemsIfNotExist(true).save();
      
      Set<Subject> subjects = grouperSession.getAccessResolver().getSubjectsWithPrivilege(replaceGroup, AccessPrivilege.UPDATE);

      assertEquals(0, GrouperUtil.length(subjects));
      
      replaceGroup.grantPriv(SubjectTestHelper.SUBJ0, AccessPrivilege.UPDATE);
      
      GrouperClient
          .main(GrouperClientUtils
              .splitTrim(
                  "--operation=assignGrouperPrivilegesWs --groupName=aStem:replaceExisting --privilegeNames=update --allowed=true --subjectIds=test.subject.4 --replaceAllExisting=true",
                  " "));
      System.out.flush();
      output = new String(baos.toByteArray());
  
      System.setOut(systemOut);
  
      outputLines = GrouperClientUtils.splitTrim(output, "\n");
  
      matcher = pattern.matcher(outputLines[0]);
  
      assertEquals(GrouperClientUtils.length(outputLines), 1);
      assertTrue(outputLines[0], matcher.matches());
  
      assertEquals("0", matcher.group(1));
      assertEquals("T", matcher.group(2));
      assertEquals("SUCCESS_ALLOWED", matcher.group(3));
      assertEquals("group", matcher.group(4));
      assertEquals("aStem:replaceExisting", matcher.group(5));
      assertEquals("test.subject.4", matcher.group(6));
      assertEquals("access", matcher.group(7));
      assertEquals("update", matcher.group(8));

      subjects = grouperSession.getAccessResolver().getSubjectsWithPrivilege(replaceGroup, AccessPrivilege.UPDATE);

      assertEquals(1, GrouperUtil.length(subjects));
      assertEquals(SubjectTestHelper.SUBJ4_ID, subjects.iterator().next().getId());
    } finally {
      System.setOut(systemOut);
    }
  
  }

}
