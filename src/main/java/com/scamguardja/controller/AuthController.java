package com.scamguardja.controller;
import com.scamguardja.service.ReportService;

import com.scamguardja.service.GmailService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final GmailService gmailService;
    private final ReportService reportService;
    

    @Value("${gmail.redirect-uri}")
    private String redirectUri;

    public AuthController(GmailService gmailService, ReportService reportService) {
        this.gmailService = gmailService;
        this.reportService = reportService;
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

    @GetMapping("/auth/google/callback")
        public void callback(@RequestParam("code") String code,
                            HttpSession session,
                            jakarta.servlet.http.HttpServletResponse response) throws Exception {

            System.out.println("AUTH CALLBACK HIT");

            String userId = "user-" + System.currentTimeMillis();
            gmailService.exchangeCodeForTokens(code, redirectUri, userId);

            session.setAttribute("gmailUserId", userId);
            reportService.clearAllReports();
            System.out.println("New login detected -> clearing incident reports");

            System.out.println("Saved Gmail session userId = " + userId);
            System.out.println("Callback session id = " + session.getId());

            response.sendRedirect("/gmail/dashboard");
        }

    @GetMapping(value = "/auth/logout", produces = "text/html")
    public String logout(HttpSession session) {
        try {
            String userId = (String) session.getAttribute("gmailUserId");
            session.invalidate();
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