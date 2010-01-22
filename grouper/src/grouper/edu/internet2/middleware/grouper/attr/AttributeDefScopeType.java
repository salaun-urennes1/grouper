package edu.internet2.middleware.grouper.attr;

import edu.internet2.middleware.grouper.util.GrouperUtil;

/**
 * type of scope, like
 * @author mchyzer
 */
public enum AttributeDefScopeType {

  /** for member type attributes, filter on sourceId (none means allow all) */
  sourceId,
  
  /** this attribute can be assigned only if another attribute def name id is assigned */
  attributeDefNameIdAssigned,
  
  /** stemId of the stem the object needs to be in for this attribute to be assigned */
  inStem,
  
  /** matching generally on namepsace names, its a like string in DB.  Can be stem or substem */
  nameLike;

  /**
   * do a case-insensitive matching
   * 
   * @param string
   * @param exceptionOnNull will not allow null or blank entries
   * @return the enum or null or exception if not found
   */
  public static AttributeDefScopeType valueOfIgnoreCase(String string, boolean exceptionOnNull) {
    return GrouperUtil.enumValueOfIgnoreCase(AttributeDefScopeType.class, 
        string, exceptionOnNull);

  }

  
}
