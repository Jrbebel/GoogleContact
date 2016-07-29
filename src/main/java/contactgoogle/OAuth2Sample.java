
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

        int numberContact = 98;

        Credential credential = peopleApiGoogle.authorize(httpTransport, dataStoreFactory);
        List peopleResource = peopleApiGoogle.getResourcesPerson(credential, httpTransport, numberContact); //get resources of people

        int sizePeopleResource = peopleResource.size();
        System.out.println(sizePeopleResource);
        int pas = 49;
        int divisible = sizePeopleResource / pas;
        int modulo = sizePeopleResource % pas;
        int debut = 0;
        int fin = 0;
        int finModulo = divisible * pas;
        // authorization

        /*Know if list is empty or full**/
        if (peopleResource.isEmpty()) {

            System.out.println("Error list resources");

        } else {

            System.out.println("List is full avec " + sizePeopleResource + " contact");
            System.out.println("modulo " + modulo);
            System.out.println("finmodulo " + finModulo);
            System.out.println("divisible " + divisible);

            for (int i = 0; i < divisible; i++) {

                peopleApiGoogle.Remplissage(credential, httpTransport, debut, (i + 1) * pas);
                fin = i * pas;

                debut += pas;


                System.out.println("je remplis" + debut + "- fin  " + (i + 1) * pas + "-------------------------------------------------------------------------------------------------------------");
            }


            if (modulo != 0) {
                peopleApiGoogle.Remplissage(credential, httpTransport, 0, modulo);
                System.out.println("debut :" + finModulo + "fin " + (sizePeopleResource));
            }

            return;
        }


    }


}
