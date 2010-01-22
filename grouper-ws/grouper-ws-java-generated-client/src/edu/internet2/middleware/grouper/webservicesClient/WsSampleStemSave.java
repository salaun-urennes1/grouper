/**
 *
 */
package edu.internet2.middleware.grouper.webservicesClient;

import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import edu.internet2.middleware.grouper.webservicesClient.util.GeneratedClientSettings;
import edu.internet2.middleware.grouper.ws.samples.types.WsSampleGenerated;
import edu.internet2.middleware.grouper.ws.samples.types.WsSampleGeneratedType;
import edu.internet2.middleware.grouper.ws.soap.xsd.StemSave;
import edu.internet2.middleware.grouper.ws.soap.xsd.WsStem;
import edu.internet2.middleware.grouper.ws.soap.xsd.WsStemLookup;
import edu.internet2.middleware.grouper.ws.soap.xsd.WsStemSaveResult;
import edu.internet2.middleware.grouper.ws.soap.xsd.WsStemSaveResults;
import edu.internet2.middleware.grouper.ws.soap.xsd.WsStemToSave;
import edu.internet2.middleware.grouper.ws.soap.xsd.WsSubjectLookup;


/**
 *
 * @author mchyzer
 *
 */
public class WsSampleStemSave implements WsSampleGenerated {
    /**
     * @param args
     */
    public static void main(String[] args) {
        stemSave(WsSampleGeneratedType.soap);
    }

    /**
     * @see edu.internet2.middleware.grouper.ws.samples.types.WsSampleGenerated#executeSample(edu.internet2.middleware.grouper.ws.samples.types.WsSampleGeneratedType)
     */
    public void executeSample(WsSampleGeneratedType wsSampleGeneratedType) {
        stemSave(wsSampleGeneratedType);
    }

    /**
     * @param wsSampleGeneratedType can run as soap or xml/http
     */
    public static void stemSave(WsSampleGeneratedType wsSampleGeneratedType) {
        try {
            //URL, e.g. http://localhost:8091/grouper-ws/services/GrouperService
            GrouperServiceStub stub = new GrouperServiceStub(GeneratedClientSettings.URL);
            Options options = stub._getServiceClient().getOptions();
            HttpTransportProperties.Authenticator auth = new HttpTransportProperties.Authenticator();
            auth.setUsername(GeneratedClientSettings.USER);
            auth.setPassword(GeneratedClientSettings.PASS);
            auth.setPreemptiveAuthentication(true);

            options.setProperty(HTTPConstants.AUTHENTICATE, auth);
            options.setProperty(HTTPConstants.SO_TIMEOUT, new Integer(3600000));
            options.setProperty(HTTPConstants.CONNECTION_TIMEOUT,
                new Integer(3600000));

            StemSave stemSave = StemSave.class.newInstance();

            //version, e.g. v1_3_000
            stemSave.setClientVersion(GeneratedClientSettings.VERSION);

            // set the act as id
            WsSubjectLookup actAsSubject = WsSubjectLookup.class.newInstance();
            actAsSubject.setSubjectId("GrouperSystem");
            stemSave.setActAsSubjectLookup(actAsSubject);

            WsStemToSave wsStemToSave = WsStemToSave.class.newInstance();
            WsStem wsStem = WsStem.class.newInstance();
            wsStem.setDescription("the test stem");
            wsStem.setDisplayExtension("test stem");
            wsStem.setName("aStem:testStem");
            wsStemToSave.setSaveMode("");
            wsStem.setUuid("");
            wsStemToSave.setWsStem(wsStem);

            WsStemLookup wsStemLookup = WsStemLookup.class.newInstance();
            wsStemLookup.setStemName("aStem:testStem");
            wsStemToSave.setWsStemLookup(wsStemLookup);

            stemSave.setWsStemToSaves(new WsStemToSave[] { wsStemToSave });
            stemSave.setTxType("");

            WsStemSaveResults wsStemSaveResults = stub.stemSave(stemSave)
                                                      .get_return();
            System.out.println(ToStringBuilder.reflectionToString(
                    wsStemSaveResults));

            WsStemSaveResult[] wsStemSaveResultArray = wsStemSaveResults.getResults();

            for (WsStemSaveResult wsStemSaveResult : GeneratedClientSettings.nonNull(
                    wsStemSaveResultArray)) {
                System.out.println(ToStringBuilder.reflectionToString(
                        wsStemSaveResult));
            }
            if (!StringUtils.equals("T", 
                wsStemSaveResults.getResultMetadata().getSuccess())) {
              throw new RuntimeException("didnt get success! ");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
