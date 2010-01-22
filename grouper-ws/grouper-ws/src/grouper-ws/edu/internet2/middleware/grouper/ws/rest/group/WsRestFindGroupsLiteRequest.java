/*
 * @author mchyzer
 * $Id: WsRestFindGroupsLiteRequest.java,v 1.1 2008-03-29 10:50:43 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.ws.rest.group;

import edu.internet2.middleware.grouper.ws.GrouperServiceLogic;
import edu.internet2.middleware.grouper.ws.rest.WsRequestBean;
import edu.internet2.middleware.grouper.ws.rest.method.GrouperRestHttpMethod;



/**
 * lite bean that will be the data from rest request
 * @see GrouperServiceLogic#getGroupsLite(edu.internet2.middleware.grouper.ws.GrouperWsVersion, String, String, String, edu.internet2.middleware.grouper.ws.member.WsMemberFilter, String, String, String, boolean, boolean, String, String, String, String, String)
 * for lite method
 */
public class WsRestFindGroupsLiteRequest implements WsRequestBean {
  
  /** field */
  private String queryFilterType; 
  
  /** field */
  private String groupName; 
  
  /** field */
  private String stemName; 
  
  /** field */
  private String stemNameScope;
  
  /** field */
  private String groupUuid; 
  
  /** field */
  private String groupAttributeName; 
  
  /** field */
  private String groupAttributeValue;
  
  /** field */
  private String groupTypeName; 
  
  /** 
   * field
   */
  private String clientVersion;
  
  /** field */
  private String actAsSubjectId;
  /** field */
  private String actAsSubjectSourceId;
  
  /** field */
  private String actAsSubjectIdentifier;
  /** field */
  private String includeGroupDetail;
  /** field */
  private String paramName0;
  
  /** field */
  private String paramValue0;
  /** field */
  private String paramName1;
  /** field */
  private String paramValue1;
  
  /**
   * field
   * @return field
   */
  public String getClientVersion() {
    return this.clientVersion;
  }
  /**
   * field
   * @param clientVersion1
   */
  public void setClientVersion(String clientVersion1) {
    this.clientVersion = clientVersion1;
  }
  /**
   * field
   * @return field
   */
  public String getActAsSubjectId() {
    return this.actAsSubjectId;
  }
  
  /**
   * field
   * @param actAsSubjectId1
   */
  public void setActAsSubjectId(String actAsSubjectId1) {
    this.actAsSubjectId = actAsSubjectId1;
  }
  
  /**
   * field
   * @return field
   */
  public String getActAsSubjectSourceId() {
    return this.actAsSubjectSourceId;
  }
  
  /**
   * field
   * @param actAsSubjectSource1
   */
  public void setActAsSubjectSourceId(String actAsSubjectSource1) {
    this.actAsSubjectSourceId = actAsSubjectSource1;
  }
  
  /**
   * field
   * @return field
   */
  public String getActAsSubjectIdentifier() {
    return this.actAsSubjectIdentifier;
  }
  
  /**
   * field
   * @param actAsSubjectIdentifier1
   */
  public void setActAsSubjectIdentifier(String actAsSubjectIdentifier1) {
    this.actAsSubjectIdentifier = actAsSubjectIdentifier1;
  }
  
  /**
   * field
   * @return field
   */
  public String getIncludeGroupDetail() {
    return this.includeGroupDetail;
  }
  
  /**
   * field
   * @param includeGroupDetail1
   */
  public void setIncludeGroupDetail(String includeGroupDetail1) {
    this.includeGroupDetail = includeGroupDetail1;
  }
  
  /**
   * field
   * @return field
   */
  public String getParamName0() {
    return this.paramName0;
  }
  /**
   * field
   * @param _paramName0
   */
  public void setParamName0(String _paramName0) {
    this.paramName0 = _paramName0;
  }
  /**
   * field
   * @return field
   */
  public String getParamValue0() {
    return this.paramValue0;
  }
  /**
   * field
   * @param _paramValue0
   */
  public void setParamValue0(String _paramValue0) {
    this.paramValue0 = _paramValue0;
  }
  /**
   * field
   * @return field
   */
  public String getParamName1() {
    return this.paramName1;
  }
  /**
   * field
   * @param _paramName1
   */
  public void setParamName1(String _paramName1) {
    this.paramName1 = _paramName1;
  }
  
  /**
   * field
   * @return field
   */
  public String getParamValue1() {
    return this.paramValue1;
  }
  
  /**
   * field
   * @param _paramValue1
   */
  public void setParamValue1(String _paramValue1) {
    this.paramValue1 = _paramValue1;
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.ws.rest.WsRequestBean#retrieveRestHttpMethod()
   */
  public GrouperRestHttpMethod retrieveRestHttpMethod() {
    return GrouperRestHttpMethod.GET;
  }
  
  /**
   * field
   * @return the queryFilterType
   */
  public String getQueryFilterType() {
    return this.queryFilterType;
  }
  
  /**
   * @param queryFilterType1 the queryFilterType to set
   */
  public void setQueryFilterType(String queryFilterType1) {
    this.queryFilterType = queryFilterType1;
  }
  
  /**
   * field
   * @return the groupName
   */
  public String getGroupName() {
    return this.groupName;
  }
  
  /**
   * field
   * @param groupName1 the groupName to set
   */
  public void setGroupName(String groupName1) {
    this.groupName = groupName1;
  }
  
  /**
   * field
   * @return the stemName
   */
  public String getStemName() {
    return this.stemName;
  }
  
  /**
   * field
   * @param stemName1 the stemName to set
   */
  public void setStemName(String stemName1) {
    this.stemName = stemName1;
  }
  
  /**
   * field
   * @return the stemNameScope
   */
  public String getStemNameScope() {
    return this.stemNameScope;
  }
  
  /**
   * field
   * @param stemNameScope1 the stemNameScope to set
   */
  public void setStemNameScope(String stemNameScope1) {
    this.stemNameScope = stemNameScope1;
  }
  
  /**
   * field
   * @return the groupUuid
   */
  public String getGroupUuid() {
    return this.groupUuid;
  }
  
  /**
   * field
   * @param groupUuid1 the groupUuid to set
   */
  public void setGroupUuid(String groupUuid1) {
    this.groupUuid = groupUuid1;
  }
  
  /**
   * field
   * @return the groupAttributeName
   */
  public String getGroupAttributeName() {
    return this.groupAttributeName;
  }
  
  /**
   * field
   * @param groupAttributeName1 the groupAttributeName to set
   */
  public void setGroupAttributeName(String groupAttributeName1) {
    this.groupAttributeName = groupAttributeName1;
  }
  
  /**
   * field
   * @return the groupAttributeValue
   */
  public String getGroupAttributeValue() {
    return this.groupAttributeValue;
  }
  
  /**
   * field
   * @param groupAttributeValue1 the groupAttributeValue to set
   */
  public void setGroupAttributeValue(String groupAttributeValue1) {
    this.groupAttributeValue = groupAttributeValue1;
  }
  
  /**
   * @return the groupTypeName
   */
  public String getGroupTypeName() {
    return this.groupTypeName;
  }
  
  /**
   * field
   * @param groupTypeName1 the groupTypeName to set
   */
  public void setGroupTypeName(String groupTypeName1) {
    this.groupTypeName = groupTypeName1;
  }

}
