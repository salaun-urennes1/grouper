/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.grouper.xml.export;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.CompactWriter;

import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.hibernate.AuditControl;
import edu.internet2.middleware.grouper.hibernate.GrouperTransactionType;
import edu.internet2.middleware.grouper.hibernate.HibUtils;
import edu.internet2.middleware.grouper.hibernate.HibernateHandler;
import edu.internet2.middleware.grouper.hibernate.HibernateHandlerBean;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.internal.dao.GrouperDAOException;
import edu.internet2.middleware.grouper.misc.GrouperVersion;
import edu.internet2.middleware.grouper.util.GrouperUtil;


/**
 *
 */
public class XmlExportStem {

  /** uuid */
  private String uuid;
  
  /** parentStem */
  private String parentStem;

  /** name */
  private String name;

  /** displayName */
  private String displayName;
  
  /** creatorId */
  private String creatorId;

  /** createTime */
  private String createTime;

  /** modifierId */
  private String modifierId;

  /** modifierTime */
  private String modifierTime;

  /** displayExtension */
  private String displayExtension;

  /** extension */
  private String extension;
  
  /** description */
  private String description;

  /** lastMembershipChange */
  private Long lastMembershipChange;
  
  /** hibernateVersionNumber */
  private long hibernateVersionNumber;

  /** contextId */
  private String contextId;

  /**
   * 
   */
  public XmlExportStem() {
    
  }

  /**
   * @param stem
   * @param grouperVersion
   */
  public XmlExportStem(GrouperVersion grouperVersion, Stem stem) {
    
    if (stem == null) {
      throw new RuntimeException();
    }
    
    if (grouperVersion == null) {
      throw new RuntimeException();
    }
    
    this.contextId = stem.getContextId();
    this.createTime = GrouperUtil.dateStringValue(stem.getCreateTime());
    this.creatorId = stem.getCreatorUuid();
    this.description = stem.getDescription();
    this.displayExtension = stem.getDisplayExtension();
    this.displayName = stem.getDisplayName();
    this.extension = stem.getExtension();
    this.hibernateVersionNumber = stem.getHibernateVersionNumber();
    //TODO make string
    this.lastMembershipChange = stem.getLastMembershipChangeDb();
    this.modifierId = stem.getModifierUuid();
    this.modifierTime = GrouperUtil.dateStringValue(stem.getModifyTime());
    this.name = stem.getName();
    this.parentStem = stem.getParentUuid();
    this.uuid = stem.getUuid();
    
  }

  /**
   * uuid
   * @return uuid
   */
  public String getUuid() {
    return this.uuid;
  }

  /**
   * uuid
   * @param uuid1
   */
  public void setUuid(String uuid1) {
    this.uuid = uuid1;
  }

  /**
   * parentStem
   * @return parentStem
   */
  public String getParentStem() {
    return this.parentStem;
  }

  /**
   * parentStem
   * @param parentStem1
   */
  public void setParentStem(String parentStem1) {
    this.parentStem = parentStem1;
  }

  /**
   * name
   * @return name
   */
  public String getName() {
    return this.name;
  }

  /**
   * name
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * display name
   * @return display name
   */
  public String getDisplayName() {
    return this.displayName;
  }

  /**
   * display name
   * @param displayName1
   */
  public void setDisplayName(String displayName1) {
    this.displayName = displayName1;
  }

  /**
   * creatorId
   * @return creatorId
   */
  public String getCreatorId() {
    return this.creatorId;
  }

  /**
   * creatorId
   * @param creatorId1
   */
  public void setCreatorId(String creatorId1) {
    this.creatorId = creatorId1;
  }

  /**
   * createTime
   * @return createTime
   */
  public String getCreateTime() {
    return this.createTime;
  }

  /**
   * createTime
   * @param createTime1
   */
  public void setCreateTime(String createTime1) {
    this.createTime = createTime1;
  }

  /**
   * modifierId
   * @return modifierId
   */
  public String getModifierId() {
    return this.modifierId;
  }

  /**
   * modifierId
   * @param modifierId1
   */
  public void setModifierId(String modifierId1) {
    this.modifierId = modifierId1;
  }

  /**
   * modifierTime
   * @return modifierTime
   */
  public String getModifierTime() {
    return this.modifierTime;
  }

  /**
   * modifierTime
   * @param modifierTime1
   */
  public void setModifierTime(String modifierTime1) {
    this.modifierTime = modifierTime1;
  }

  /**
   * displayExtension
   * @return displayExtension
   */
  public String getDisplayExtension() {
    return this.displayExtension;
  }

  /**
   * displayExtension
   * @param displayExtension1
   */
  public void setDisplayExtension(String displayExtension1) {
    this.displayExtension = displayExtension1;
  }

  /**
   * extension
   * @return extension
   */
  public String getExtension() {
    return this.extension;
  }

  /**
   * extension
   * @param extension1
   */
  public void setExtension(String extension1) {
    this.extension = extension1;
  }

  /**
   * description
   * @return description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * description
   * @param description1
   */
  public void setDescription(String description1) {
    this.description = description1;
  }

  /**
   * lastMembershipChange
   * @return lastMembershipChange
   */
  public Long getLastMembershipChange() {
    return this.lastMembershipChange;
  }

  /**
   * lastMembershipChange
   * @param lastMembershipChange1
   */
  public void setLastMembershipChange(Long lastMembershipChange1) {
    this.lastMembershipChange = lastMembershipChange1;
  }

  /**
   * hibernateVersionNumber
   * @return hibernateVersionNumber
   */
  public long getHibernateVersionNumber() {
    return this.hibernateVersionNumber;
  }

  /**
   * hibernateVersionNumber
   * @param hibernateVersionNumber1
   */
  public void setHibernateVersionNumber(long hibernateVersionNumber1) {
    this.hibernateVersionNumber = hibernateVersionNumber1;
  }

  /**
   * contextId
   * @return contextId
   */
  public String getContextId() {
    return this.contextId;
  }

  /**
   * contextId
   * @param contextId1
   */
  public void setContextId(String contextId1) {
    this.contextId = contextId1;
  }
  
  /**
   * convert to stem
   * @return the stem
   */
  public Stem toStem() {
    Stem stem = new Stem();
    
    stem.setContextId(this.contextId);
    stem.setCreateTimeLong(GrouperUtil.dateLongValue(this.createTime));
    stem.setCreatorUuid(this.creatorId);
    stem.setDescriptionDb(this.description);
    stem.setDisplayExtensionDb(this.displayExtension);
    stem.setDisplayNameDb(this.displayName);
    stem.setExtensionDb(this.extension);
    stem.setHibernateVersionNumber(this.hibernateVersionNumber);
    stem.setLastMembershipChangeDb(this.lastMembershipChange);
    stem.setModifierUuid(this.modifierId);
    stem.setModifyTimeLong(GrouperUtil.dateLongValue(this.modifierTime));
    stem.setNameDb(this.name);
    stem.setParentUuid(this.parentStem);
    stem.setUuid(this.uuid);
    
    return stem;
  }

  /**
   * @param exportVersion
   * @return the xml string
   */
  public String toXml(GrouperVersion exportVersion) {
    StringWriter stringWriter = new StringWriter();
    this.toXml(exportVersion, stringWriter);
    return stringWriter.toString();
  }

  /**
   * @param exportVersion 
   * @param writer
   */
  public void toXml(
      @SuppressWarnings("unused") GrouperVersion exportVersion, Writer writer) {
    XStream xStream = XmlExportUtils.xstream();
  
    CompactWriter compactWriter = new CompactWriter(writer);
    
    xStream.marshal(this, compactWriter);
  
  }

  /**
   * 
   * @param writer
   */
  public static void exportStems(final Writer writer) {
    //get the members
    HibernateSession.callbackHibernateSession(GrouperTransactionType.READONLY_OR_USE_EXISTING, AuditControl.WILL_NOT_AUDIT, new HibernateHandler() {
      
      public Object callback(HibernateHandlerBean hibernateHandlerBean)
          throws GrouperDAOException {
  
        Session session = hibernateHandlerBean.getHibernateSession().getSession();
  
        //select all members in order
        Query query = session.createQuery(
            "select theStem from Stem as theStem order by theStem.nameDb");
  
        GrouperVersion grouperVersion = new GrouperVersion(GrouperVersion.GROUPER_VERSION);
        try {
          writer.write("  <stems>\n");
  
          //this is an efficient low-memory way to iterate through a resultset
          ScrollableResults results = null;
          try {
            results = query.scroll();
            while(results.next()) {
              Object object = results.get(0);
              Stem stem = (Stem)object;
              XmlExportStem xmlExportStem = new XmlExportStem(grouperVersion, stem);
              writer.write("    ");
              xmlExportStem.toXml(grouperVersion, writer);
              writer.write("\n");
            }
          } finally {
            HibUtils.closeQuietly(results);
          }
          
          //end the members element 
          writer.write("  </stems>\n");
        } catch (IOException ioe) {
          throw new RuntimeException("Problem with streaming stems", ioe);
        }
        return null;
      }
    });
  }

  /**
   * take a reader (e.g. dom reader) and convert to an xml export stem
   * @param exportVersion
   * @param hierarchicalStreamReader
   * @return the bean
   */
  public static XmlExportStem fromXml(@SuppressWarnings("unused") GrouperVersion exportVersion, 
      HierarchicalStreamReader hierarchicalStreamReader) {
    XStream xStream = XmlExportUtils.xstream();
    
    XmlExportStem xmlExportStem = (XmlExportStem)xStream.unmarshal(hierarchicalStreamReader);
  
    return xmlExportStem;
  }

  /**
   * 
   * @param exportVersion
   * @param xml
   * @return the object from xml
   */
  public static XmlExportStem fromXml(
      @SuppressWarnings("unused") GrouperVersion exportVersion, String xml) {
    XStream xStream = XmlExportUtils.xstream();
    
    XmlExportStem xmlExportStem = (XmlExportStem)xStream.fromXML(xml);
  
    return xmlExportStem;
  }

}
