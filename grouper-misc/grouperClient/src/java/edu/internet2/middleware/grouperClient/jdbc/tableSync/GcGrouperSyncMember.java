/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.grouperClient.jdbc.tableSync;

import java.sql.Timestamp;

import edu.internet2.middleware.grouperClient.jdbc.GcDbAccess;
import edu.internet2.middleware.grouperClient.jdbc.GcPersist;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableClass;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableField;
import edu.internet2.middleware.grouperClient.jdbc.GcPersistableHelper;
import edu.internet2.middleware.grouperClient.jdbc.GcSqlAssignPrimaryKey;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;
import edu.internet2.middleware.grouperClientExt.org.apache.commons.lang3.builder.ToStringBuilder;
import edu.internet2.middleware.grouperClientExt.org.apache.commons.logging.Log;


/**
 * if doing user level syncs, this is the metadata
 */
@GcPersistableClass(tableName="grouper_sync_member", defaultFieldPersist=GcPersist.doPersist)
public class GcGrouperSyncMember implements GcSqlAssignPrimaryKey {


  /**
   * delete all data if table is here
   */
  public static void reset() {
    
    try {
      // if its not there forget about it... TODO remove this in 2.5+
      new GcDbAccess().connectionName("grouper").sql("select * from " + GcPersistableHelper.tableName(GcGrouperSyncMember.class) + " where 1 != 1").select(Integer.class);
    } catch (Exception e) {
      return;
    }

    new GcDbAccess().connectionName("grouper").sql("delete from " + GcPersistableHelper.tableName(GcGrouperSyncMember.class)).executeSql();
  }

  /**
   * select grouper sync member by sync id and member id
   * @param theConnectionName
   * @param grouperSyncId
   * @param memberId
   * @return the sync
   */
  public static GcGrouperSyncMember retrieveBySyncIdAndMemberId(String theConnectionName, String grouperSyncId, String memberId) {
    theConnectionName = GcGrouperSync.defaultConnectionName(theConnectionName);
    GcGrouperSyncMember gcGrouperSyncMember = new GcDbAccess().connectionName(theConnectionName)
        .sql("select * from grouper_sync_member where grouper_sync_id = ? and member_id = ?").addBindVar(grouperSyncId).addBindVar(memberId).select(GcGrouperSyncMember.class);
    if (gcGrouperSyncMember != null) {
      gcGrouperSyncMember.connectionName = theConnectionName;
    }
    return gcGrouperSyncMember;
  }

  /**
   * select grouper sync member by id
   * @param theConnectionName
   * @param id
   * @return the sync
   */
  public static GcGrouperSyncMember retrieveById(String theConnectionName, String id) {
    theConnectionName = GcGrouperSync.defaultConnectionName(theConnectionName);
    GcGrouperSyncMember gcGrouperSyncMember = new GcDbAccess().connectionName(theConnectionName)
        .sql("select * from grouper_sync_member where id = ?").addBindVar(id).select(GcGrouperSyncMember.class);
    if (gcGrouperSyncMember != null) {
      gcGrouperSyncMember.connectionName = theConnectionName;
    }
    return gcGrouperSyncMember;
  }

  /**
   * foreign key to the members sync table, though not a real foreign key
   */
  private String memberId;
  
  /**
   * foreign key to the members sync table, though not a real foreign key
   * @return member id
   */
  public String getMemberId() {
    return this.memberId;
  }

  /**
   * foreign key to the members sync table, though not a real foreign key
   * @param memberId1
   */
  public void setMemberId(String memberId1) {
    this.memberId = memberId1;
  }

  /**
   * subject identifier for this sync
   */
  private String subjectIdentifier;
  
  /**
   * subject identifier for this sync
   * @return subject identifier for this sync
   */
  public String getSubjectIdentifier() {
    return this.subjectIdentifier;
  }

  /**
   * subject identifier for this sync
   * @param subjectIdentifier1
   */
  public void setSubjectIdentifier(String subjectIdentifier1) {
    this.subjectIdentifier = subjectIdentifier1;
  }



  /**
   * subject source id
   */
  private String sourceId;
  
  /**
   * subject source id
   * @return subject source id
   */
  public String getSourceId() {
    return this.sourceId;
  }

  /**
   * subject source id
   * @param sourceId1
   */
  public void setSourceId(String sourceId1) {
    this.sourceId = sourceId1;
  }

  /**
   * subject id
   */
  private String subjectId;

  /**
   * subject id
   * @return subject id
   */
  public String getSubjectId() {
    return this.subjectId;
  }

  /**
   * subject id
   * @param subjectId1
   */
  public void setSubjectId(String subjectId1) {
    this.subjectId = subjectId1;
  }



  /**
   * 
   */
  private static Log LOG = GrouperClientUtils.retrieveLog(GcGrouperSyncMember.class);

  /**
   * 
   * @param connectionName
   */
  public void store() {
    try {
      this.lastUpdated = new Timestamp(System.currentTimeMillis());
      this.connectionName = GcGrouperSync.defaultConnectionName(this.connectionName);
      new GcDbAccess().connectionName(this.connectionName).storeToDatabase(this);
    } catch (RuntimeException re) {
      
      LOG.info("GrouperSyncUser uuid potential mismatch: " + this.grouperSyncId + ", " + this.memberId, re);

      // maybe a different uuid is there
      GcGrouperSyncMember gcGrouperSyncUser = this.grouperSync.retrieveMemberByMemberId(this.memberId);
      if (gcGrouperSyncUser != null) {
        this.id = gcGrouperSyncUser.getId();
        new GcDbAccess().connectionName(connectionName).storeToDatabase(this);
        LOG.warn("GrouperSyncUser uuid mismatch corrected: " + this.grouperSyncId + ", " + this.memberId);
      } else {
        throw re;
      }
    }
  }

  /**
   * 
   * @return sync
   */
  public GcGrouperSync retrieveGrouperSync() {
    if (this.grouperSync == null && this.grouperSyncId != null) {
      this.grouperSync = GcGrouperSync.retrieveById(this.connectionName, this.grouperSyncId);
    }
    return this.grouperSync;
  }
  
  /**
   * 
   */
  @GcPersistableField(persist=GcPersist.dontPersist)
  private GcGrouperSync grouperSync;
  
  /**
   * 
   * @return gc grouper sync
   */
  public GcGrouperSync getGrouperSync() {
    return this.grouperSync;
  }
  
  /**
   * 
   * @param gcGrouperSync
   */
  public void setGrouperSync(GcGrouperSync gcGrouperSync) {
    this.grouperSync = gcGrouperSync;
    this.grouperSyncId = gcGrouperSync == null ? null : gcGrouperSync.getId();
  }
  
  /**
   * connection name or null for default
   */
  @GcPersistableField(persist=GcPersist.dontPersist)
  private String connectionName;

  /**
   * connection name or null for default
   * @return connection name
   */
  public String getConnectionName() {
    return this.connectionName;
  }

  /**
   * connection name or null for default
   * @param connectionName1
   */
  public void setConnectionName(String connectionName1) {
    this.connectionName = connectionName1;
  }

  /**
   * 
   * @param connectionName
   */
  public void delete() {
    this.connectionName = GcGrouperSync.defaultConnectionName(this.connectionName);
    new GcDbAccess().connectionName(this.connectionName).deleteFromDatabase(this);
  }
  
  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    
    System.out.println("none");
    
    for (GcGrouperSyncGroup theGcGrouperSyncGroup : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncGroup.class)) {
      System.out.println(theGcGrouperSyncGroup.toString());
    }
    
    // foreign key
    GcGrouperSync gcGrouperSync = new GcGrouperSync();
    gcGrouperSync.setSyncEngine("temp");
    gcGrouperSync.setProvisionerName("myJob");
    gcGrouperSync.store();
    
    GcGrouperSyncMember gcGrouperSyncMember = new GcGrouperSyncMember();
    gcGrouperSyncMember.setGrouperSync(gcGrouperSync);
    gcGrouperSyncMember.setLastTimeWorkWasDone(new Timestamp(System.currentTimeMillis() + 2000));
    gcGrouperSyncMember.memberId = "memId";
    gcGrouperSyncMember.sourceId = "sourceId";
    gcGrouperSyncMember.subjectId = "subjectId";
    gcGrouperSyncMember.subjectIdentifier = "subjectIdentifier";
    gcGrouperSyncMember.memberFromId2 = "from2";
    gcGrouperSyncMember.memberFromId3 = "from3";
    gcGrouperSyncMember.memberToId2 = "toId2";
    gcGrouperSyncMember.memberToId3 = "toId3";
    gcGrouperSyncMember.inTargetDb = "T";
    gcGrouperSyncMember.inTargetInsertOrExistsDb = "T";
    gcGrouperSyncMember.inTargetEnd = new Timestamp(123L);
    gcGrouperSyncMember.inTargetStart = new Timestamp(234L);
    gcGrouperSyncMember.lastTimeWorkWasDone = new Timestamp(345L);
    gcGrouperSyncMember.provisionableDb = "T";
    gcGrouperSyncMember.provisionableEnd = new Timestamp(456L);
    gcGrouperSyncMember.provisionableStart = new Timestamp(567L);
    gcGrouperSyncMember.store();
    
    System.out.println("stored");
    
    gcGrouperSyncMember = gcGrouperSync.retrieveMemberByMemberId("memId");
    System.out.println(gcGrouperSyncMember);
    
    gcGrouperSyncMember.setMemberToId2("from2a");
    gcGrouperSyncMember.store();

    System.out.println("updated");

    for (GcGrouperSyncMember theGcGrouperSyncMember : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncMember.class)) {
      System.out.println(theGcGrouperSyncMember.toString());
    }

    gcGrouperSyncMember.delete();
    gcGrouperSync.delete();
    
    System.out.println("deleted");

    for (GcGrouperSyncGroup theGcGrouperSyncStatus : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncGroup.class)) {
      System.out.println(theGcGrouperSyncStatus.toString());
    }
  }
  
  /**
   * 
   */
  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", this.id)
        .append("connectionName", this.connectionName)
        .append("memberId", this.memberId)
        .append("sourceId", this.sourceId)
        .append("subjectId", this.subjectId)
        .append("subjectIdentifier", this.subjectIdentifier)
        .append("grouperSyncId", this.grouperSyncId)
        .append("memberFromId2", this.memberFromId2)
        .append("memberFromId3", this.memberFromId3)
        .append("memberToId2", this.memberToId2)
        .append("memberToId3", this.memberFromId3)
        .append("inTarget", this.isInTarget())
        .append("inTargetInsertOrExists", this.isInTargetInsertOrExists())
        .append("inTargetStart", this.getInTargetStart())
        .append("inTargetEnd", this.getInTargetEnd())
        .append("provisionable", this.isProvisionable())
        .append("provisionableStart", this.getProvisionableStart())
        .append("provisionableEnd", this.getProvisionableEnd())
        .append("lastUpdated", this.lastUpdated)
        .append("lastTimeWorkWasDone", this.lastTimeWorkWasDone).build();
  }

  /**
   * last time a record was processed
   */
  private Timestamp lastTimeWorkWasDone;
  
  /**
   * last time a record was processe
   * @return last time a record was processed
   */
  public Timestamp getLastTimeWorkWasDone() {
    return this.lastTimeWorkWasDone;
  }

  /**
   * last time a record was processe
   * @param lastTimeWorkWasDone1
   */
  public void setLastTimeWorkWasDone(Timestamp lastTimeWorkWasDone1) {
    this.lastTimeWorkWasDone = lastTimeWorkWasDone1;
  }


  /**
   * 
   */
  public GcGrouperSyncMember() {
  }
  
  /**
   * uuid of this record in this table
   */
  @GcPersistableField(primaryKey=true, primaryKeyManuallyAssigned=true)
  private String id;

  
  /**
   * uuid of this record in this table
   * @return the id
   */
  public String getId() {
    return this.id;
  }

  
  /**
   * uuid of this record in this table
   * @param id1 the id to set
   */
  public void setId(String id1) {
    this.id = id1;
  }

  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   */
  @GcPersistableField(columnName="in_target_insert_or_exists")
  private String inTargetInsertOrExistsDb;

  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @return true or false
   */
  public String getInTargetInsertOrExistsDb() {
    return this.inTargetInsertOrExistsDb;
  }

  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @param inTargetInsertOrExistsDb1
   */
  public void setInTargetInsertOrExistsDb(String inTargetInsertOrExistsDb1) {
    this.inTargetInsertOrExistsDb = inTargetInsertOrExistsDb1;
  }

  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @return true or false
   */
  public boolean isInTargetInsertOrExists() {
    return GrouperClientUtils.booleanValue(this.inTargetInsertOrExistsDb, false);
  }
  
  /**
   * T if inserted on the in_target_start date, or F if it existed then and not sure when inserted
   * @param inTargetInsertOrExists
   */
  public void setInTargetInsertOrExists(boolean inTargetInsertOrExists) {
    this.inTargetInsertOrExistsDb = inTargetInsertOrExists ? "T" : "F";
  }
  
  /**
   * if this group exists in the target/destination
   */
  @GcPersistableField(columnName="in_target")
  private String inTargetDb;
  
  /**
   * if this group exists in the target/destination
   * @return if in target
   */
  public String getInTargetDb() {
    return this.inTargetDb;
  }

  /**
   * if this group exists in the target/destination
   * @param inTargetDb1
   */
  public void setInTargetDb(String inTargetDb1) {
    this.inTargetDb = inTargetDb1;
  }

  /**
   * if this group exists in the target/destination
   * @return if is target
   */
  public Boolean getInTarget() {
    return GrouperClientUtils.booleanObjectValue(this.inTargetDb);
  }
  
  /**
   * if this group exists in the target/destination
   * @param inTarget
   */
  public void setInTarget(Boolean inTarget) {
    this.inTargetDb = inTarget ? "T" : "F";
  }
  
  /**
   * uuid of the job in grouper_sync
   */
  private String grouperSyncId;
  
  /**
   * uuid of the job in grouper_sync
   * @return uuid of the job in grouper_sync
   */ 
  public String getGrouperSyncId() {
    return this.grouperSyncId;
  }

  /**
   * uuid of the job in grouper_sync
   * @param grouperSyncId1
   */
  public void setGrouperSyncId(String grouperSyncId1) {
    this.grouperSyncId = grouperSyncId1;
    if (this.grouperSync == null || !GrouperClientUtils.equals(this.grouperSync.getId(), grouperSyncId1)) {
      this.grouperSync = null;
    }
  }

  /**
   * when this record was last updated
   */
  private Timestamp lastUpdated;
  
  /**
   * when this record was last updated
   * @return the lastUpdated
   */
  public Timestamp getLastUpdated() {
    return this.lastUpdated;
  }

  /**
   * when this record was last updated
   * @param lastUpdated1
   */
  public void setLastUpdated(Timestamp lastUpdated1) {
    this.lastUpdated = lastUpdated1;
  }

  /**
   * T if provisionable and F is not
   */
  @GcPersistableField(columnName="provisionable")
  private String provisionableDb;
  
  /**
   * T if provisionable and F is not
   * @return if provisionable
   */
  public String getProvisionableDb() {
    return this.provisionableDb;
  }

  /**
   * T if provisionable and F is not
   * @param provisionableDb1
   */
  public void setProvisionableDb(String provisionableDb1) {
    this.provisionableDb = provisionableDb1;
  }

  /**
   * if provisionable
   * @return if provisionable
   */
  public boolean isProvisionable() {
    return GrouperClientUtils.booleanValue(this.provisionableDb, false);
  }

  /**
   * if provisionable
   * @param provisionable
   */
  public void setProvisionable(boolean provisionable) {
    this.provisionableDb = provisionable ? "T" : "F";
  }
  
  /**
   * millis since 1970 that this group ended being provisionable
   */
  private Timestamp provisionableEnd;

  /**
   * millis since 1970 that this group ended being provisionable
   * @return millis
   */
  public Timestamp getProvisionableEnd() {
    return this.provisionableEnd;
  }

  /**
   * millis since 1970 that this group ended being provisionable
   * @param provisionableEndMillis1
   */
  public void setProvisionableEnd(Timestamp provisionableEndMillis1) {
    this.provisionableEnd = provisionableEndMillis1;
  }

  /**
   * millis since 1970 that this group started to be provisionable
   */
  private Timestamp provisionableStart;
    
  /**
   * millis since 1970 that this group started to be provisionable
   * @return millis
   */
  public Timestamp getProvisionableStart() {
    return this.provisionableStart;
  }

  /**
   * millis since 1970 that this group started to be provisionable
   * @param provisionableStartMillis1
   */
  public void setProvisionableStart(Timestamp provisionableStartMillis1) {
    this.provisionableStart = provisionableStartMillis1;
  }

  /**
   * for users this is the group idIndex
   */
  private String memberFromId2;

  /**
   * for users this is the group idIndex
   * @return group from id 2
   */
  public String getMemberFromId2() {
    return this.memberFromId2;
  }

  /**
   * for users this is the group idIndex
   * @param groupFromId2_1
   */
  public void setMemberFromId2(String groupFromId2_1) {
    this.memberFromId2 = groupFromId2_1;
  }

  /**
   * other metadata on users
   */
  private String memberFromId3;

  /**
   * other metadata on users
   * @return id3
   */
  public String getMemberFromId3() {
    return this.memberFromId3;
  }

  /**
   * other metadata on users
   * @param groupFromId3_1
   */
  public void setMemberFromId3(String groupFromId3_1) {
    this.memberFromId3 = groupFromId3_1;
  }

  /**
   * other metadata on users
   */
  private String memberToId2;
  
  /**
   * other metadata on users
   * @return metadata
   */
  public String getMemberToId2() {
    return this.memberToId2;
  }

  /**
   * other metadata on users
   * @param groupToId2_1
   */
  public void setMemberToId2(String groupToId2_1) {
    this.memberToId2 = groupToId2_1;
  }

  /**
   * other metadata on users
   */
  private String memberToId3;
  /**
   * when this group was removed from target
   */
  private Timestamp inTargetEnd;
  /**
   * when this group was provisioned to target
   */
  private Timestamp inTargetStart;

  /**
   * other metadata on users
   * @return group id
   */
  public String getMemberToId3() {
    return this.memberToId3;
  }

  /**
   * other metadata on users
   * @param groupToId3_1
   */
  public void setMemberToId3(String groupToId3_1) {
    this.memberToId3 = groupToId3_1;
  }

  /**
   * 
   */
  @Override
  public void gcSqlAssignNewPrimaryKeyForInsert() {
    this.id = GrouperClientUtils.uuid();
  }

  /**
   * when this group was provisioned to target
   * @return when
   */
  public Timestamp getInTargetEnd() {
    return this.inTargetEnd;
  }

  /**
   * when this group was provisioned to target
   * @return when
   */
  public Timestamp getInTargetStart() {
    return this.inTargetStart;
  }

  /**
   * when this group was provisioned to target
   * @param inTargetEnd1
   */
  public void setInTargetEnd(Timestamp inTargetEnd1) {
    this.inTargetEnd = inTargetEnd1;
  }

  /**
   * when this group was provisioned to target
   * @param inTargetStart1
   */
  public void setInTargetStart(Timestamp inTargetStart1) {
    this.inTargetStart = inTargetStart1;
  }

  /**
   * if in target
   * @return if in target
   */
  public boolean isInTarget() {
    return GrouperClientUtils.booleanValue(this.inTargetDb, false);
  }

  /**
   * if in target
   * @param in target
   */
  public void setInTarget(boolean inTarget) {
    this.inTargetDb = inTarget ? "T" : "F";
  }

}
