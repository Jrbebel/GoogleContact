package contactgoogle;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jrbebel on 03/07/16.
 */
public class peopleApiGoogle {

    /**
     * Be sure to specify the name of your application. If the application name
     * is {@code null} or blank, the application will log a warning. Suggested
     * format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "Autre client 1";


    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    /**
     * OAuth 2.0 scopes.
     */
    private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/contacts", "https://www.googleapis.com/auth/contacts.readonly", "https://www.googleapis.com/auth/user.emails.read");

    /**
     * Path to Resources of Contact
     */
    private static final String pathResourcesContact = "people/*";

    /***
     * List to Resources of Contact
     */
    private static List listResources;
    /***
     * List to Information of Contact
     */
    private static List listInformationPerson;

    private static GoogleClientSecrets clientSecrets;

    /*
    * Permet l'autorisation de ton application
    * */
    public static Credential authorize(HttpTransport httpTransport, FileDataStoreFactory dataStoreFactory) throws Exception {

        // load client secrets
        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(peopleApiGoogle.class.getResourceAsStream("client_secrets.json")));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter") || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
                    + "into oauth2-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }

        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(
                dataStoreFactory).build();

        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    /**
     * get Resources of person
     */
    public static List getResourcesPerson(Credential credential, HttpTransport httpTransport, int sizeContact) throws IOException {

        People peopleService = new People.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
                .build();

        ListConnectionsResponse response = peopleService.people().connections().list(pathResourcesContact).setPageSize(sizeContact).setFields("connections(resourceName)").execute();
        List<Person> connections = response.getConnections();

        listResourcesPerson(connections);
        return listResources;
    }

    /**
     * get Resources of person
     */
    public static List getInformationPerson(Credential credential, HttpTransport httpTransport, List<String> resourcesPerson) throws IOException {

        People peopleService = new People.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
                .build();

/**
 * Valid paths are: [person.addresses, person.age_range, person.biographies, person.birthdays, person.bragging_rights,
 * person.cover_photos, person.email_addresses, person.events, person.genders, person.im_clients, person.interests, person.locales,
 * person.memberships, person.metadata, person.names, person.nicknames, person.occupations, person.organizations, person.phone_numbers,
 * person.photos, person.relations, person.relationship_interests, person.relationship_statuses, person.residences, person.skills, person.taglines,
 * person.urls]."
 *
 */

        GetPeopleResponse response = peopleService.people().getBatchGet().setResourceNames(resourcesPerson).setRequestMaskIncludeField("person.names,person.email_addresses,person.addresses,person.phone_numbers," +
                "person.photos").setFields("responses(person(addresses,emailAddresses,names,phoneNumbers,photos),requestedResourceName)").execute();


        displayContactConsole(response.getResponses());
        // System.out.println("con" + response);


        return listResources;
    }
 /*   public static ListInfomationPerson() {
        listInformationPerson= new ArrayList();
        return listInformationPerson;
    }*/

    /**
     * put resources in the list
     */
    public static void listResourcesPerson(List<Person> resourcePeople) {

        listResources = new ArrayList();

        for (Person peopleResources : resourcePeople) {
            listResources.add(peopleResources.getResourceName());

        }

    }

    public static void displayContactConsole(List<PersonResponse> ResponseList) {

        int i = 0;
        Iterator<PersonResponse> people = ResponseList.listIterator();


        StringBuilder PersonFinale = new StringBuilder();

        for (int a = 0; a < 49; a++) {


            /**declaration of list than i want***/
            List<Name> GivenName = ResponseList.get(a).getPerson().getNames();
            List<PhoneNumber> phoneNumber = ResponseList.get(a).getPerson().getPhoneNumbers();
            List<Photo> photos = ResponseList.get(a).getPerson().getPhotos();
            List<EmailAddress> emails = ResponseList.get(a).getPerson().getEmailAddresses();


            /**Display information***/
            if (GivenName == null) {
                System.out.println("personne inconnu");
            } else {
                for (Name name : GivenName) {

                    System.out.println(name.getFamilyName() + "  " + name.getGivenName());

                }
            }


            if (phoneNumber == null) {

                System.out.println("Numéro de telephone non renseigné");

            } else {

                for (PhoneNumber phone : phoneNumber) {

                    System.out.println(phone.getType() + " : " + phone.getValue());

                }


            }
            if (photos == null) {

                System.out.println("Aucune photo");

            } else {

                for (Photo photo : photos) {

                    System.out.println(photo.getUrl());

                }
            }
            if (emails == null) {

                System.out.println("Aucune addresse email connu");

            } else {

                for (EmailAddress email : emails) {

                    System.out.println(email.getType() + " :" + email.getValue());

                }
            }

            System.out.println("iteration " + a);
            System.out.println("\n<------------------->\n");

        }

    }
}
