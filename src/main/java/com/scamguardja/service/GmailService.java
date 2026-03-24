package com.scamguardja.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

@Service
public class GmailService {

    private static final String APPLICATION_NAME = "ScamGuard JA";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private GoogleAuthorizationCodeFlow flow;

    public GoogleAuthorizationCodeFlow buildFlow() throws Exception {
        if (flow != null) {
            return flow;
        }
    
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    
        InputStream credentialsStream;

        File renderSecretFile = new File("/etc/secrets/credentials.json");
        if (renderSecretFile.exists()) {
            credentialsStream = new FileInputStream(renderSecretFile);
            System.out.println("Using Render secret file for credentials.json");
        } else {
            credentialsStream = Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream("credentials.json")
            );
            System.out.println("Using local bundled credentials.json");
        }
        
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                new InputStreamReader(credentialsStream)
        );
    
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                JSON_FACTORY,
                clientSecrets,
                Arrays.asList(
                        "https://www.googleapis.com/auth/gmail.readonly",
                        "https://www.googleapis.com/auth/userinfo.email",
                        "https://www.googleapis.com/auth/userinfo.profile",
                        "openid"
                )
        )
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("/tmp/tokens")))
                .setAccessType("offline")
                .build();
    
        return flow;
    }

    public String getAuthorizationUrl(String redirectUri) throws Exception {
        return buildFlow()
        .newAuthorizationUrl()
        .setRedirectUri(redirectUri)
        .setAccessType("offline")   
        .setApprovalPrompt("force")
        .build();
    }

    public void exchangeCodeForTokens(String code, String redirectUri, String userId) throws Exception {
        GoogleTokenResponse tokenResponse = buildFlow()
                .newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();
    
        Credential credential = buildFlow().createAndStoreCredential(tokenResponse, userId);
    
        System.out.println("Stored credential for userId = " + userId);
        System.out.println("Access token present = " + (credential.getAccessToken() != null));
        System.out.println("Refresh token present = " + (credential.getRefreshToken() != null));
    }

    public Credential loadCredential(String userId) throws Exception {
        Credential credential = buildFlow().loadCredential(userId);
    
        System.out.println("Loading credential for userId = " + userId);
        System.out.println("Credential found = " + (credential != null));
    
        if (credential != null) {
            System.out.println("Loaded access token present = " + (credential.getAccessToken() != null));
            System.out.println("Loaded refresh token present = " + (credential.getRefreshToken() != null));
        }
    
        return credential;
    }

    public Gmail buildGmailClient(String userId) throws Exception {
        Credential credential = loadCredential(userId);
    
        if (credential == null) {
            throw new RuntimeException("No Gmail credential found for user: " + userId);
        }
    
        if ((credential.getAccessToken() == null || credential.getAccessToken().isBlank())
                && credential.getRefreshToken() != null) {
            boolean refreshed = credential.refreshToken();
            System.out.println("Credential refresh result = " + refreshed);
        }
    
        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential
        ).setApplicationName(APPLICATION_NAME).build();
    }

    public void deleteStoredCredential(String userId) {
        try {
            java.nio.file.Path tokenPath = java.nio.file.Paths.get("tokens", userId);
            java.nio.file.Files.deleteIfExists(tokenPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Userinfo getUserInfo(String userId) throws Exception {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = loadCredential(userId);

        if (credential == null) {
            throw new RuntimeException("No Google credential found. Please sign in again.");
        }

        if (credential.getAccessToken() == null && credential.getRefreshToken() != null) {
            credential.refreshToken();
        }

        Oauth2 oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        return oauth2.userinfo().get().execute();
    }
}