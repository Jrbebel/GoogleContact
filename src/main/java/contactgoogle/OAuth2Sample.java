/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package contactgoogle;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Command-line sample for the Google OAuth2 API described at <a
 * href="http://code.google.com/apis/accounts/docs/OAuth2Login.html">Using OAuth
 * 2.0 for Login (Experimental)</a>.
 *
 * @author Yaniv Inbar
 */
public class OAuth2Sample {

    /**
     * Be sure to specify the name of your application. If the application name
     * is {@code null} or blank, the application will log a warning. Suggested
     * format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "ClientId";

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

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * OAuth 2.0 scopes.
     */
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/contacts.readonly",
            "https://www.googleapis.com/auth/userinfo.email");

    private static Oauth2 oauth2;
    private static GoogleClientSecrets clientSecrets;


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
            peopleApiGoogle.getInformationPerson(credential,httpTransport,peopleResource.subList(50,100)); //c
        }

        return;

    }

    private static void tokenInfo(String accessToken) throws IOException {
        header("Validating a token");
        Tokeninfo tokeninfo = oauth2.tokeninfo().setAccessToken(accessToken).execute();
        System.out.println(tokeninfo.toPrettyString());
        if (!tokeninfo.getAudience().equals(clientSecrets.getDetails().getClientId())) {
            System.err.println("ERROR: audience does not match our client ID!");
        }
    }

    private static void userInfo() throws IOException {
        header("Obtaining User Profile Information");
        Userinfoplus userinfo = oauth2.userinfo().get().execute();
        System.out.println(userinfo.toPrettyString());


    }

    static void header(String name) {
        System.out.println();
        System.out.println("================== " + name + " ==================");
        System.out.println();
    }


    public static void setUp() throws Exception {


        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        // Or your redirect URL for web based applications.
        String redirectUrl = "http://localhost";
        String scope = "https://www.googleapis.com/auth/contacts.readonly";


        // load client secrets
        clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(OAuth2Sample.class.getResourceAsStream("clients_secrets.json")));

        if (clientSecrets.getDetails().getClientId().startsWith("Enter") || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
                    + "into oauth2-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }
        List<String> SCO = Arrays.asList(
                " https://people.googleapis.com/v1/"

        );

        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCO).setDataStoreFactory(
                dataStoreFactory).build();

        Credential credentials = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        People peopleService = new People.Builder(httpTransport, jsonFactory, credentials).setApplicationName(APPLICATION_NAME)
                .build();
        ListConnectionsResponse response = peopleService.people().connections().list("people/list?").execute();
        List<Person> connections = response.getConnections();
        System.out.println(connections);

    }

}
