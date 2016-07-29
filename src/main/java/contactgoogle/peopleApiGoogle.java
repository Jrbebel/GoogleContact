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
import connexionBD.Connexion;
import connexionBD.SavetoBD;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.*;

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
    private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/contacts", "https://www.googleapis.com/auth/contacts.readonly");

    /**
     * Path to Resources of Contact
     */
    private static final String pathResourcesContact = "people/*";

    /***
     * List to Resources of Contact
     */
    private static List<Person> listResources;
    /***
     * List to Information of Contact
     */
    private static List listInformationPerson;
    private static List<String> listresournames;

    private static GoogleClientSecrets clientSecrets;

    private static Map<String, String> stringMap;
    private static Map<String, String> stringMapPhone;
    private static int iterator = 0;
    private static int IteraorEror = 0;

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

        ListConnectionsResponse response = peopleService.people().connections().list(pathResourcesContact)
                .setPageSize(sizeContact)
                .setFields("connections").execute();
        List<Person> connections = response.getConnections();

        listResourcesPerson(connections);
        return listResources;
    }

    /**
     * get Resources of person
     */
    public static List getInformationPerson(Credential credential, HttpTransport httpTransport, List<String> resourcesPerson) throws IOException, ClassNotFoundException {

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

        GetPeopleResponse response = peopleService.people().getBatchGet().setResourceNames(resourcesPerson).execute();


        return listResources;
    }


    /**
     * put resources in the list
     */
    public static void listResourcesPerson(List<Person> resourcePeople) {

        listResources = new ArrayList();
        listresournames = new ArrayList<String>();

        for (Person peopleResources : resourcePeople) {

            listResources.add(peopleResources);
            if (!listresournames.contains(peopleResources.getResourceName())) {
                listresournames.add(peopleResources.getResourceName());

            }


        }

    }


    public static void traitementContact(GetPeopleResponse response, int fin) throws IOException {

        int indicator = 0;
        Connection Cnx = null;
        try {
            Cnx = new SavetoBD().MyConnected();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        for (int a = 0; a < fin; a++) {

            stringMap = new HashMap<String, String>();
            stringMapPhone = new HashMap<String, String>();


            /**declaration of list than i want***/
            List<Name> GivenName = response.getResponses().get(a).getPerson().getNames();
            List<PhoneNumber> phoneNumber = response.getResponses().get(a).getPerson().getPhoneNumbers();
            List<Photo> photos = response.getResponses().get(a).getPerson().getPhotos();
            List<EmailAddress> emails = response.getResponses().get(a).getPerson().getEmailAddresses();


            /**Display information***/
            if (GivenName == null) {

                System.out.println("Personne inconnu");

                stringMap.put("last_name", "John");
                stringMap.put("first_name", "DOE");

            } else {

                for (Name name : GivenName) {

                    if (name.getMetadata().getSource().getType().equals("CONTACT")) {

                        stringMap.put("last_name", name.getFamilyName());
                        stringMap.put("first_name", name.getGivenName());

                        System.out.println(name.getFamilyName().toUpperCase() + " " + name.getGivenName().toLowerCase());
                    }


                }


            }

            if (phoneNumber == null) {

                System.out.println("Numéro de telephone non renseigné");

                stringMapPhone.put("tel", "");

            } else {
                for (PhoneNumber phone : phoneNumber) {

                    if (phone.getType().equals("mobile")) {
                        stringMapPhone.put("tel", phone.getValue());
                        System.out.println(phone.getType() + " " + phone.getValue());

                    }

                }
            }
            if (!stringMapPhone.containsKey("tel")) {
                stringMapPhone.put("tel", null);
            }
            if (photos == null) {

                System.out.println("Aucune photo");

                stringMap.put("pathImg", null);

            } else {

                for (Photo photo : photos) {


                    /**Get only picture of type contact **/
                    if (photo.getMetadata().getSource().getType().equals("CONTACT")) {

                        System.out.println(photo.getUrl());
                        stringMap.put("pathImg", photo.getUrl());

                    }

                }
            }
            if (!stringMap.containsKey("pathImg")) {
                stringMap.put("pathImg", null);
            }

            if (emails == null) {

                System.out.println("Aucune addresse email connu");

                stringMap.put("email", null);

            } else {

                for (EmailAddress email : emails) {
                    //System.out.println(email);
                    if (email.getType().equals("home"))

                        stringMap.put("email", email.getValue());

                    System.out.println(email.getType() + " :" + email.getValue());

                }

            }


            System.out.println("Commencement de l'enregistrement du user ....");
            System.out.println("taille de " + stringMap.size());

            boolean last_name = stringMap.containsKey("last_name");
            boolean first_name = stringMap.containsKey("first_name");
            boolean phone = stringMapPhone.containsKey("tel");
            boolean email = stringMap.containsKey("email");
            System.out.println(last_name + "-" + first_name + "- " + phone + " - " + email);

            if (last_name && first_name && phone && email) {

                stringMapPhone.put("user_id", String.valueOf(iterator + 1));
                InsertiontoBd(Cnx, stringMap, stringMapPhone);
                iterator++;
            } else {

                IteraorEror++;
            }


            System.out.println("------------------------------SAUVEGARDE ---------------------------------------------------------------->>> " + iterator);


        }
        System.out.println("nombre d'erreur" + IteraorEror);

    }


    public static void Remplissage(Credential credential, HttpTransport httpTransport, int debut, int fin) throws IOException {

        People peopleService = new People.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
                .build();
        System.out.println("debut " + debut + " fin : " + fin + " dans le remplissage");
        GetPeopleResponse response = peopleService.people().getBatchGet().setResourceNames(listresournames.subList(debut, fin)).execute();
        System.out.println("size " + listresournames.size());
        traitementContact(response, fin);
        listresournames.subList(debut, fin).clear();
    }

    public static void InsertiontoBd(Connection Cnx, Map stringMap, Map stringMapPhone) {

        String psTableMember = "member";
        SavetoBD.insert(Cnx, psTableMember, stringMap);
        String psTablePhone = "phone";
        SavetoBD.insert(Cnx, psTablePhone, stringMapPhone);
        Connexion.valider();
    }
}
