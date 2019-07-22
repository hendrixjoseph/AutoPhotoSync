package hendixjoseph.autophotosync;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.auth.Credentials;
import com.google.auth.oauth2.UserCredentials;

public class CredentialBuilder {
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final File DATA_STORE_DIR = new File(CredentialBuilder.class.getResource("/").getPath(), "credentials");
	private static final int LOCAL_RECEIVER_PORT = 61984;
	
	public static Credentials getCredentials(String credentialsPath, List<String> selectedScopes) throws IOException, GeneralSecurityException {
	    GoogleClientSecrets clientSecrets =
	            GoogleClientSecrets.load(
	                JSON_FACTORY, new InputStreamReader(new FileInputStream(credentialsPath)));
	        String clientId = clientSecrets.getDetails().getClientId();
	        String clientSecret = clientSecrets.getDetails().getClientSecret();

	        GoogleAuthorizationCodeFlow flow =
	            new GoogleAuthorizationCodeFlow.Builder(
	                    GoogleNetHttpTransport.newTrustedTransport(),
	                    JSON_FACTORY,
	                    clientSecrets,
	                    selectedScopes)
	                .setDataStoreFactory(new FileDataStoreFactory(DATA_STORE_DIR))
	                .setAccessType("offline")
	                .build();
	        LocalServerReceiver receiver =
	            new LocalServerReceiver.Builder().setPort(LOCAL_RECEIVER_PORT).build();
	        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	        String refreshToken = credential.getRefreshToken();
	        System.out.println(refreshToken);
	        
	        return UserCredentials.newBuilder()
	            .setClientId(clientId)
	            .setClientSecret(clientSecret)
	            .setRefreshToken(refreshToken)
	            .build();		
	}
}
