/*
 * @author mchyzer $Id: GrouperRestHttpMethod.java,v 1.5 2008-03-29 10:50:43 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.ws.rest.method;

import java.util.List;

import edu.internet2.middleware.grouper.ws.GrouperWsVersion;
import edu.internet2.middleware.grouper.ws.rest.GrouperRestInvalidRequest;
import edu.internet2.middleware.grouper.ws.rest.WsRequestBean;
import edu.internet2.middleware.grouper.ws.rest.WsResponseBean;
import edu.internet2.middleware.grouper.ws.util.GrouperServiceUtils;

/**
 * types of http methods accepted by grouper rest
 */
public enum GrouperRestHttpMethod {

  /** GET */
  GET {

    /**
     * handle the incoming request based on HTTP method
     * @param clientVersion version of client, e.g. v1_3_000
     * @param urlStrings not including the app name or servlet.  
     * for http://localhost/grouper-ws/servicesRest/xhtml/v3_0_000/groups/a:b
     * the urlStrings would be size two: {"group", "a:b"}
     * @param requestObject is the request body converted to object
     * @return the resultObject
     */
    @Override
    public WsResponseBean service(
        GrouperWsVersion clientVersion, List<String> urlStrings,
        WsRequestBean requestObject) {

      String firstResource = GrouperServiceUtils.popUrlString(urlStrings);

      //validate and get the first resource
      GrouperWsRestGet grouperWsRestGet = GrouperWsRestGet.valueOfIgnoreCase(
          firstResource, true);

      return grouperWsRestGet.service(clientVersion, urlStrings, requestObject);
    }

  },

  /** POST */
  POST {

    /**
     * handle the incoming request based on HTTP method
     * @param clientVersion version of client, e.g. v1_3_000
     * @param urlStrings not including the app name or servlet.  for http://localhost/grouper-ws/servicesRest/groups/a:b
     * the urlStrings would be size two: {"group", "a:b"}
     * @param requestObject is the request body converted to object
     * @return the resultObject
     */
    @Override
    public WsResponseBean service(
        GrouperWsVersion clientVersion, List<String> urlStrings,
        WsRequestBean requestObject) {
      throw new RuntimeException("Invalid POST request");
    }

  },

  /** PUT */
  PUT {

    /**
     * handle the incoming request based on HTTP method
     * @param clientVersion version of client, e.g. v1_3_000
     * @param urlStrings not including the app name or servlet.  for http://localhost/grouper-ws/servicesRest/groups/a:b
     * the urlStrings would be size two: {"group", "a:b"}
     * @param requestObject is the request body converted to object
     * @return the resultObject
     */
    @Override
    public WsResponseBean service(
        GrouperWsVersion clientVersion, List<String> urlStrings,
        WsRequestBean requestObject) {
      
      String firstResource = GrouperServiceUtils.popUrlString(urlStrings);
      
      //validate and get the first resource
      GrouperWsRestPut grouperWsRestPut = GrouperWsRestPut.valueOfIgnoreCase(
          firstResource, true);

      return grouperWsRestPut.service(
          clientVersion, urlStrings, requestObject);
    }

  },

  /** DELETE */
  DELETE {

    /**
     * handle the incoming request based on HTTP method
     * @param clientVersion version of client, e.g. v1_3_000
     * @param urlStrings not including the app name or servlet.  for http://localhost/grouper-ws/servicesRest/groups/a:b
     * the urlStrings would be size two: {"group", "a:b"}
     * @param requestObject is the request body converted to object
     * @return the resultObject
     */
    @Override
    public WsResponseBean service(
        GrouperWsVersion clientVersion, List<String> urlStrings,
        WsRequestBean requestObject) {

      String firstResource = GrouperServiceUtils.popUrlString(urlStrings);

      //validate and get the first resource
      GrouperWsRestDelete grouperWsRestDelete = GrouperWsRestDelete.valueOfIgnoreCase(
          firstResource, true);

      return grouperWsRestDelete.service(
          clientVersion, urlStrings, requestObject);
    }

  };

  /**
   * handle the incoming request based on HTTP method
   * @param clientVersion version of client, e.g. v1_3_000
   * @param urlStrings not including the app name or servlet.  for http://localhost/grouper-ws/servicesRest/groups/a:b
   * the urlStrings would be size two: {"group", "a:b"}
   * @param requestObject is the request body converted to object
   * @return the resultObject
   */
  public abstract WsResponseBean service(
      GrouperWsVersion clientVersion, List<String> urlStrings, WsRequestBean requestObject);

  /**
   * do a case-insensitive matching
   * 
   * @param string
   * @param exceptionOnNotFound true to throw exception if method not found
   * @return the enum or null or exception if not found
   * @throws GrouperRestInvalidRequest if there is a problem
   */
  public static GrouperRestHttpMethod valueOfIgnoreCase(String string,
      boolean exceptionOnNotFound) throws GrouperRestInvalidRequest {
    return GrouperServiceUtils.enumValueOfIgnoreCase(GrouperRestHttpMethod.class, 
        string, exceptionOnNotFound);
  }
}
