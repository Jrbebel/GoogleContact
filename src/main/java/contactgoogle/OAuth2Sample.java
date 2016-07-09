
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

        int numberContact = 206;

        Credential credential = peopleApiGoogle.authorize(httpTransport, dataStoreFactory);
        List peopleResource = peopleApiGoogle.getResourcesPerson(credential, httpTransport, numberContact); //get resources of people

        int sizePeopleResource = peopleResource.size();
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

            for (int i = 1; i < divisible + 1; i++) {

                fin = i * pas;

                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------" + i);
                System.out.println("pas de debut est de " + debut + " et la fin de " + fin + "----------------------------------------------------------------------------------");

                peopleApiGoogle.getInformationPerson(credential, httpTransport, peopleResource.subList(debut, fin)); // 0 to 49 , 49 to 99 , 99 to 149

                debut += pas;
                if (fin == finModulo) {
                    System.out.println("mon modulo---------------------------------------------------------------------------------------------------------------------------------" + finModulo);
                    peopleApiGoogle.getInformationPerson(credential, httpTransport, peopleResource.subList(finModulo, sizePeopleResource)); // 0 to 49 , 49 to 99 , 99 to 149

                }
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------FIN" + i);
            }
            return;
        }


    }


}
