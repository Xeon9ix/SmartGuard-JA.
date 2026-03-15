package com.scamguardja.service;

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

import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
public class GmailService {

    private static final String APPLICATION_NAME = "ScamGuard JA";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public GoogleAuthorizationCodeFlow buildFlow() throws Exception {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("credentials.json")
                )
        );

        return new GoogleAuthorizationCodeFlow.Builder(
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
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("offline")
                .build();
    }

    public String getAuthorizationUrl(String redirectUri) throws Exception {
        return buildFlow()
                .newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .build();
    }

    public void exchangeCodeForTokens(String code, String redirectUri, String userId) throws Exception {
        GoogleTokenResponse tokenResponse = buildFlow()
                .newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();

        buildFlow().createAndStoreCredential(tokenResponse, userId);
    }

    public Gmail buildGmailClient(String userId) throws Exception {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var credential = buildFlow().loadCredential(userId);

        if (credential == null) {
            throw new RuntimeException("No Gmail credential found. Please sign in again.");
        }

        if (credential.getAccessToken() == null && credential.getRefreshToken() != null) {
            credential.refreshToken();
        }

        if (credential.getAccessToken() == null) {
            throw new RuntimeException("Gmail access token is missing or expired. Please reconnect Gmail.");
        }

        return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Userinfo getUserInfo(String userId) throws Exception {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var credential = buildFlow().loadCredential(userId);
    
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