package com.scamguardja.controller;

import com.scamguardja.service.GmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final GmailService gmailService;

    @Value("${gmail.redirect-uri}")
    private String redirectUri;

    public AuthController(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    @GetMapping(value = "/auth/google", produces = "text/html")
    public String connectGoogle() throws Exception {
        String authUrl = gmailService.getAuthorizationUrl(redirectUri);
    
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta http-equiv="refresh" content="0;url=%s">
            <title>Redirecting...</title>
        </head>
        <body>
            <p>Redirecting to Google sign-in...</p>
            <p><a href="%s">Click here if you are not redirected</a></p>
        </body>
        </html>
        """.formatted(authUrl, authUrl);
    }

    @GetMapping(value = "/auth/google/callback", produces = "text/html")
    public String callback(@RequestParam("code") String code) throws Exception {
        gmailService.exchangeCodeForTokens(code, redirectUri, "default-user");

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>ScamGuard JA</title>
            <meta http-equiv="refresh" content="2;url=/gmail/dashboard">
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background: #08152f;
                    color: white;
                    margin: 0;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    min-height: 100vh;
                }
                .card {
                    background: #162744;
                    padding: 30px;
                    border-radius: 16px;
                    box-shadow: 0 8px 24px rgba(0,0,0,0.25);
                    text-align: center;
                    max-width: 500px;
                }
                h1 {
                    margin-top: 0;
                }
                p {
                    color: #c7d3ea;
                    line-height: 1.6;
                }
                a {
                    color: #7dd3fc;
                }
            </style>
        </head>
        <body>
            <div class="card">
                <h1>✅ Gmail Connected</h1>
                <p>Your Gmail account has been connected successfully.</p>
                <p>Redirecting you to the ScamGuard JA dashboard...</p>
                <p><a href="/gmail/dashboard">Click here if you are not redirected</a></p>
            </div>
        </body>
        </html>
        """;
    }

    @GetMapping(value = "/auth/logout", produces = "text/html")
        public String logout() {
            try {
                java.nio.file.Path tokenPath =
                        java.nio.file.Paths.get("tokens", "StoredCredential");

                java.nio.file.Files.deleteIfExists(tokenPath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta http-equiv="refresh" content="0;url=/">
                <title>Signing Out...</title>
            </head>
            <body>
                <p>Signing out...</p>
                <p><a href="/">Click here if you are not redirected</a></p>
            </body>
            </html>
            """;
        }
}