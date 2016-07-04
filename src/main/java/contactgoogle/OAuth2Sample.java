
package contactgoogle;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.util.List;

public class OAuth2Sample {


    /**
     * Directory to store user credentials.
     */
    private static final java.io.File DATA_STORE_DIR
            = new java.io.File(System.getProperty("user.home"), ".store/oauth2_sample");

    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to
     * make it a single globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport httpTransport;


    public static void main(String[] args) throws Exception {

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);


        // authorization
        Credential credential = peopleApiGoogle.authorize(httpTransport, dataStoreFactory);
        List peopleResource = peopleApiGoogle.getResourcesPerson(credential, httpTransport); //get resources of people
        /*Know if list is empty or full**/
        if (peopleResource.isEmpty()) {
            System.out.println("Error list resources");
        } else {
            System.out.println("List is full");
            peopleApiGoogle.getInformationPerson(credential, httpTransport, peopleResource.subList(50, 100)); //c
        }

        return;

    }


}
