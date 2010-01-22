/**
 *
 */
package edu.internet2.middleware.grouper.webservicesClient;

import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import edu.internet2.middleware.grouper.webservicesClient.util.GeneratedClientSettings;
import edu.internet2.middleware.grouper.ws.samples.types.WsSampleGenerated;
import edu.internet2.middleware.grouper.ws.samples.types.WsSampleGeneratedType;
import edu.internet2.middleware.grouper.ws.soap.xsd.AssignGrouperPrivilegesLite;
import edu.internet2.middleware.grouper.ws.soap.xsd.WsAssignGrouperPrivilegesLiteResult;


/**
 *
 * @author mchyzer
 *
 */
public class WsSampleAssignGrouperPrivilegesLite implements WsSampleGenerated {
    /**
     * @see edu.internet2.middleware.grouper.ws.samples.types.WsSampleGenerated#executeSample(edu.internet2.middleware.grouper.ws.samples.types.WsSampleGeneratedType)
     */
    public void executeSample(WsSampleGeneratedType wsSampleGeneratedType) {
        wsAssignGrouperPrivilegesLite(wsSampleGeneratedType);
    }

    /**
     * @param wsSampleGeneratedType can run as soap or xml/http
     */
    @SuppressWarnings("deprecation")
    public static void wsAssignGrouperPrivilegesLite(
        WsSampleGeneratedType wsSampleGeneratedType) {
        try {
            //URL, e.g. http://localhost:8091/grouper-ws/services/GrouperService
            GrouperServiceStub stub = new GrouperServiceStub(GeneratedClientSettings.URL);
            
            HttpClientParams.getDefaultParams().setParameter(
                HttpClientParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
            
            Options options = stub._getServiceClient().getOptions();
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(GeneratedClientSettings.USER);
            auth.setPassword(GeneratedClientSettings.PASS);
            auth.setPreemptiveAuthentication(true);

            options.setProperty(HTTPConstants.AUTHENTICATE, auth);
            options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(3600000));
            options.setProperty(HTTPConstants.CONNECTION_TIMEOUT,
                new Integer(3600000));

            AssignGrouperPrivilegesLite assignGrouperPrivilegesLite = AssignGrouperPrivilegesLite.class.newInstance();

            //version, e.g. v1_3_000
            assignGrouperPrivilegesLite.setClientVersion(GeneratedClientSettings.VERSION);

            // set the act as id
            assignGrouperPrivilegesLite.setActAsSubjectId("GrouperSystem");
            assignGrouperPrivilegesLite.setActAsSubjectSourceId("");
            assignGrouperPrivilegesLite.setActAsSubjectIdentifier("");

            assignGrouperPrivilegesLite.setSubjectId("test.subject.0");
            assignGrouperPrivilegesLite.setSubjectSourceId("");
            assignGrouperPrivilegesLite.setSubjectIdentifier("");
            
            assignGrouperPrivilegesLite.setGroupName("aStem:aGroup");
            assignGrouperPrivilegesLite.setGroupUuid("");
            
            assignGrouperPrivilegesLite.setStemName("");
            assignGrouperPrivilegesLite.setStemUuid("");
            
            assignGrouperPrivilegesLite.setPrivilegeType("access");
            assignGrouperPrivilegesLite.setPrivilegeName("optin");
            
            assignGrouperPrivilegesLite.setIncludeSubjectDetail("T");
            assignGrouperPrivilegesLite.setSubjectAttributeNames("loginid,description");
            assignGrouperPrivilegesLite.setIncludeGroupDetail("F");

            assignGrouperPrivilegesLite.setAllowed("T");

            WsAssignGrouperPrivilegesLiteResult wsAssignGrouperPrivilegesLiteResult = 
              stub.assignGrouperPrivilegesLite(assignGrouperPrivilegesLite).get_return();
            
            System.out.println(ToStringBuilder.reflectionToString(
                wsAssignGrouperPrivilegesLiteResult));

            if (!StringUtils.equals("T", wsAssignGrouperPrivilegesLiteResult.getResultMetadata().getSuccess())) {
              throw new RuntimeException("Didnt work");
            }
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        wsAssignGrouperPrivilegesLite(WsSampleGeneratedType.soap);
    }
}
