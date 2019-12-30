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
import edu.internet2.middleware.grouperClient.jdbc.GcSqlAssignPrimaryKey;
import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;
import edu.internet2.middleware.grouperClientExt.org.apache.commons.lang3.builder.ToStringBuilder;
import edu.internet2.middleware.grouperClientExt.org.apache.commons.logging.Log;


/**
 * Status of all jobs for the sync.  one record for full, one for incremental, etc
 */
@GcPersistableClass(tableName="grouper_sync_job", defaultFieldPersist=GcPersist.doPersist)
public class GcGrouperSyncJob implements GcSqlAssignPrimaryKey {

  /**
   * 
   */
  private static Log LOG = GrouperClientUtils.retrieveLog(GcGrouperSyncJob.class);

  /**
   * 
   * @param connectionName
   */
  public void store() {
    try {
      this.lastUpdated = new Timestamp(System.currentTimeMillis());
      this.connectionName = GcGrouperSync.defaultConnectionName(this.connectionName);
      new GcDbAccess().connectionName(this.connectionName).storeToDatabase(this);
    } catch (RuntimeException e) {
      LOG.info("GrouperSyncJob uuid potential mismatch: " + this.grouperSyncId + ", " + this.syncType, e);
      // maybe a different uuid is there
      GcGrouperSyncJob gcGrouperSyncJob = this.grouperSync.retrieveJobBySyncType(this.syncType);
      if (gcGrouperSyncJob != null) {
        this.id = gcGrouperSyncJob.getId();
        new GcDbAccess().connectionName(connectionName).storeToDatabase(this);
        LOG.warn("GrouperSyncJob uuid mismatch corrected: " + this.grouperSyncId + ", " + this.syncType);
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
    
    for (GcGrouperSyncJob theGcGrouperSyncJob : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncJob.class)) {
      System.out.println(theGcGrouperSyncJob.toString());
    }
    
    // foreign key
    GcGrouperSync gcGrouperSync = new GcGrouperSync();
    gcGrouperSync.setSyncEngine("temp");
    gcGrouperSync.setProvisionerName("myJob");
    gcGrouperSync.store();

    
    GcGrouperSyncJob gcGrouperSyncJob = new GcGrouperSyncJob();
    gcGrouperSyncJob.setGrouperSync(gcGrouperSync);
    gcGrouperSyncJob.setJobState("success");
    gcGrouperSyncJob.setLastSyncIndexOrMillis(135L);
    gcGrouperSyncJob.setLastTimeWorkWasDone(new Timestamp(System.currentTimeMillis() + 2000));
    gcGrouperSyncJob.setSyncType("testSyncType");
    gcGrouperSyncJob.store();
    
    System.out.println("stored");
    
    gcGrouperSyncJob = gcGrouperSync.retrieveJobBySyncType("testSyncType");
    System.out.println(gcGrouperSyncJob);
    
    gcGrouperSyncJob.setJobState("success1");
    gcGrouperSyncJob.store();

    System.out.println("updated");

    for (GcGrouperSyncJob theGcGrouperSyncStatus : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncJob.class)) {
      System.out.println(theGcGrouperSyncStatus.toString());
    }

    gcGrouperSyncJob.delete();
    gcGrouperSync.delete();
    
    System.out.println("deleted");

    for (GcGrouperSyncJob theGcGrouperSyncStatus : new GcDbAccess().connectionName("grouper").selectList(GcGrouperSyncJob.class)) {
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
        .append("grouperSyncId", this.grouperSyncId)
        .append("syncType", this.syncType)
        .append("lastUpdated", this.lastUpdated)
        .append("jobState", this.jobState)
        .append("lastSyncIndexOrMillis", this.lastSyncIndexOrMillis)
        .append("lastTimeWorkWasDone", this.lastTimeWorkWasDone).build();
  }


  
  /**
   * 
   */
  public GcGrouperSyncJob() {
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
   * type of sync, e.g. for sql sync this is the job subtype
   */
  private String syncType;
  
  /**
   * type of sync, e.g. for sql sync this is the job subtype
   * @return sync type
   */
  public String getSyncType() {
    return this.syncType;
  }

  /**
   * type of sync, e.g. for sql sync this is the job subtype
   * @param syncType
   */
  public void setSyncType(String syncType) {
    this.syncType = syncType;
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
    if (this.grouperSync == null || !GrouperClientUtils.equals(this.grouperSync.getId(), grouperSyncId1)) {
      this.grouperSync = null;
    }
    this.grouperSyncId = grouperSyncId1;
  }
  
  /**
   * running, waitingForAnotherJobToFinish (if waiting for another job to finish), notRunning
   */
  private String jobState;
  
  
  /**
   * running, waitingForAnotherJobToFinish (if waiting for another job to finish), notRunning
   * @return the jobState
   */
  public String getJobState() {
    return this.jobState;
  }

  
  /**
   * running, waitingForAnotherJobToFinish (if waiting for another job to finish), notRunning
   * @param jobState1 the jobState to set
   */
  public void setJobState(String jobState1) {
    this.jobState = jobState1;
  }

  /**
   * either an int of last record checked, or an int of millis since 1970 of last record processed
   */
  private Long lastSyncIndexOrMillis;
  
  
  /**
   * either an int of last record checked, or an int of millis since 1970 of last record processed
   * @return the lastSyncIndexOrMillis
   */
  public Long getLastSyncIndexOrMillis() {
    return this.lastSyncIndexOrMillis;
  }

  
  /**
   * either an int of last record checked, or an int of millis since 1970 of last record processed
   * @param lastSyncIndexOrMillis1 the lastSyncIndexOrMillis to set
   */
  public void setLastSyncIndexOrMillis(Long lastSyncIndexOrMillis1) {
    this.lastSyncIndexOrMillis = lastSyncIndexOrMillis1;
  }

  /**
   * last time a record was processed
   */
  private Timestamp lastTimeWorkWasDone;
  

  /**
   * last time a record was processed
   * @return the lastTimeWorkWasDone
   */
  public Timestamp getLastTimeWorkWasDone() {
    return this.lastTimeWorkWasDone;
  }

  
  /**
   * last time a record was processed
   * @param lastTimeWorkWasDone1 the lastTimeWorkWasDone to set
   */
  public void setLastTimeWorkWasDone(Timestamp lastTimeWorkWasDone1) {
    this.lastTimeWorkWasDone = lastTimeWorkWasDone1;
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
   * @param lastUpdated1 the lastUpdated to set
   */
  public void setLastUpdated(Timestamp lastUpdated1) {
    this.lastUpdated = lastUpdated1;
  }

  /**
   * 
   */
  @Override
  public void gcSqlAssignNewPrimaryKeyForInsert() {
    this.id = GrouperClientUtils.uuid();
  }

}
