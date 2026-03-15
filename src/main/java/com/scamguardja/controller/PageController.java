package com.scamguardja.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageController {

    @GetMapping(value = "/", produces = "text/html")
    public String homePage() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>ScamGuard JA - Sign In</title>
            <meta name="viewport" content="width=device-width, initial-scale=1">
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

                .nav {
                    position: absolute;
                    top: 20px;
                    right: 30px;
                }

                .nav a {
                    margin-left: 18px;
                    text-decoration: none;
                    color: #7dd3fc;
                    font-weight: bold;
                }

                .card {
                    background: #162744;
                    padding: 30px;
                    border-radius: 16px;
                    max-width: 520px;
                    box-shadow: 0 8px 24px rgba(0,0,0,0.25);
                }

                h1 {
                    margin-top: 0;
                }

                p {
                    color: #c7d3ea;
                    line-height: 1.6;
                }

                button {
                    margin-top: 18px;
                    padding: 12px 16px;
                    border: none;
                    border-radius: 10px;
                    background: #2f6df6;
                    color: white;
                    font-weight: bold;
                    cursor: pointer;
                }

                label {
                    display: block;
                    margin-top: 16px;
                }

                a {
                    color: #7dd3fc;
                }
            </style>
        </head>

        <body>

            <div class="nav">
                <a href="/gmail/dashboard">Dashboard</a>
                <a href="/about">About</a>
                <a href="/help">Help</a>
                <a href="/contact">Contact</a>
                <a href="/security">Security</a>
                <a href="/privacy">Privacy</a>
                <a href="/auth/logout" style="color:#f87171;">Sign Out</a>
            </div>

            <div class="card">
                <h1>🛡 ScamGuard JA</h1>

                <p style="color:#7dd3fc;font-weight:bold;">
                    Welcome to ScamGuard JA — your phishing detection assistant.
                </p>

                <p>
                    ScamGuard JA uses read-only Gmail access to scan your inbox for phishing threats.
                    By continuing, you agree that email metadata such as sender, subject, and preview text
                    may be analyzed for security purposes.
                </p>

                <p>
                    High-risk emails may be flagged or reported as part of ScamGuard JA’s phishing detection process.
                </p>

                <label>
                    <input type="checkbox" id="agree">
                    I agree to the <a href="/terms" target="_blank">Terms & Conditions</a>
                </label>

                <button onclick="continueWithGoogle()">Continue with Gmail</button>

                <p style="font-size:12px;color:#9ca3af;margin-top:20px;">
                    © 2026 ScamGuard JA. All rights reserved. ScamGuard JA™ is a trademark of its developer.
                </p>
            </div>

            <script>
                function continueWithGoogle() {
                    const agree = document.getElementById('agree').checked;

                    if (!agree) {
                        alert("You must agree to the Terms & Conditions first.");
                        return;
                    }

                    window.location.href = "/auth/google";
                }
            </script>

        </body>
        </html>
        """;
    }

    @GetMapping(value = "/terms", produces = "text/html")
    public String termsPage() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>ScamGuard JA - Terms & Conditions</title>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background: #08152f;
                    color: white;
                    margin: 0;
                    padding: 40px 20px;
                }
                <div class="nav">
                    <a href="/">Home</a>
                    <a href="/gmail/dashboard">Dashboard</a>
                    <a href="/about">About</a>
                    <a href="/help">Help</a>
                    <a href="/contact">Contact</a>
                </div>
                .container {
                    max-width: 900px;
                    margin: auto;
                    background: #162744;
                    padding: 30px;
                    border-radius: 16px;
                    box-shadow: 0 8px 24px rgba(0,0,0,0.25);
                }
                h1 {
                    margin-top: 0;
                }
                h2 {
                    margin-top: 24px;
                }
                p {
                    color: #c7d3ea;
                    line-height: 1.7;
                    margin-bottom: 16px;
                }
                .back {
                    display: inline-block;
                    margin-top: 20px;
                    color: #7dd3fc;
                    text-decoration: none;
                    font-weight: bold;
                    .nav {
                        position: absolute;
                        top: 20px;
                        right: 30px;
                    }

                    .nav a {
                        margin-left: 18px;
                        text-decoration: none;
                        color: #7dd3fc;
                        font-weight: bold;
                    }

                    .nav a:hover {
                        color: #ffffff;
                    }
                }

                .back-links {
                    display: flex;
                    gap: 16px;
                    flex-wrap: wrap;
                    margin-top: 20px;
                }

                .back {
                    display: inline-block;
                    color: #7dd3fc;
                    text-decoration: none;
                    font-weight: bold;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>ScamGuard JA Terms & Conditions</h1>

                <h2>1. Gmail Access</h2>
                <p>
                    ScamGuard JA uses read-only Gmail access to analyze sender information, email subjects,
                    and preview text for phishing detection. The system does not require permission to send,
                    delete, or modify your emails.
                </p>

                <h2>2. Threat Detection</h2>
                <p>
                    ScamGuard JA scans email metadata and limited message previews for suspicious indicators such as
                    urgency, credential requests, suspicious links, login prompts, and account-related threats.
                </p>

                <h2>3. Reporting</h2>
                <p>
                    Emails identified as high risk may be flagged or automatically reported to ScamGuard Security
                    for monitoring and investigation. Users may also manually report suspicious emails through the platform.
                </p>

                <h2>4. Data Usage</h2>
                <p>
                    ScamGuard JA may store risk scores, threat indicators, case IDs, and phishing report records
                    for security monitoring, analytics, and incident tracking. Full email contents are not intended
                    to be permanently stored as part of the standard phishing detection process.
                </p>

                <h2>5. No Guarantee</h2>
                <p>
                    ScamGuard JA is designed to improve phishing detection and user awareness, but it does not guarantee
                    that all malicious emails will be detected or that all flagged emails are malicious.
                </p>

                <h2>6. User Consent</h2>
                <p>
                    By continuing to sign in with Gmail, you acknowledge that you have read and agreed to these
                    Terms & Conditions and consent to the use of Gmail read-only access for phishing detection purposes.
                </p>

                <div class="back-links">
                    <a class="back" href="/gmail/dashboard">← Back to Dashboard</a>
                    <a class="back" href="/">Back to Sign In</a>
                </div>
            </div>
        </body>
        </html>
        """;
    }

    @GetMapping(value = "/about", produces = "text/html")
        public String aboutPage() {
            return """
        <!DOCTYPE html>
        <html>
        <head>
        <title>About ScamGuard JA</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        
        <style>
        
        body{
            font-family:Arial,sans-serif;
            background:#08152f;
            color:white;
            margin:0;
            padding:40px 20px;
        }
        
        .nav{
            position:absolute;
            top:20px;
            right:30px;
        }
        
        .nav a{
            margin-left:18px;
            text-decoration:none;
            color:#7dd3fc;
            font-weight:bold;
        }
        
        .nav a:hover{
            color:white;
        }
        
        .container{
            max-width:900px;
            margin:auto;
            background:#162744;
            padding:30px;
            border-radius:16px;
            box-shadow:0 8px 24px rgba(0,0,0,0.25);
        }
        
        p{
            color:#c7d3ea;
            line-height:1.7;
        }
        
        .back{
            display:inline-block;
            margin-top:20px;
            color:#7dd3fc;
            font-weight:bold;
        }

        .back-links {
            display: flex;
            gap: 16px;
            flex-wrap: wrap;
            margin-top: 20px;
        }

        .back {
            display: inline-block;
            color: #7dd3fc;
            text-decoration: none;
            font-weight: bold;
        }
        
        </style>
        </head>
        
        <body>
        
        <div class="nav">
            <a href="/">Home</a>
            <a href="/gmail/dashboard">Dashboard</a>
            <a href="/about">About</a>
            <a href="/help">Help</a>
            <a href="/contact">Contact</a>
        </div>
        
        <div class="container">
        
        <h1>About ScamGuard JA</h1>
        
        <p>
        ScamGuard JA is a phishing detection platform designed to help users identify suspicious emails using Gmail read-only access.
        </p>
        
        <p>
        The system analyzes sender information, subject lines, preview text and threat indicators such as urgency, login prompts and credential requests.
        </p>
        
        <p>
        ScamGuard JA generates risk scores and incident reports to help users detect phishing attempts earlier and stay safer online.
        </p>
        
        <div class="back-links">
            <a class="back" href="/gmail/dashboard">← Back to Dashboard</a>
            <a class="back" href="/">Back to Sign In</a>
        </div>
        
        </div>
        </body>
        </html>
        """;
        }

    @GetMapping(value = "/help", produces = "text/html")
    public String helpPage() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>ScamGuard JA - Help</title>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background: #08152f;
                    color: white;
                    margin: 0;
                    padding: 40px 20px;
                }
                .container {
                    max-width: 900px;
                    margin: auto;
                    background: #162744;
                    padding: 30px;
                    border-radius: 16px;
                    box-shadow: 0 8px 24px rgba(0,0,0,0.25);
                }
                h1 {
                    margin-top: 0;
                }
                h2 {
                    margin-top: 24px;
                }
                p {
                    color: #c7d3ea;
                    line-height: 1.7;
                    margin-bottom: 16px;
                }
                .back {
                    display: inline-block;
                    margin-top: 20px;
                    color: #7dd3fc;
                    text-decoration: none;
                    font-weight: bold;
                .nav {
                    position: absolute;
                    top: 20px;
                    right: 30px;
                }

                .nav a {
                    margin-left: 18px;
                    text-decoration: none;
                    color: #7dd3fc;
                    font-weight: bold;
                }
                .nav a:hover {
                    color: #ffffff;
                }
                }

                .back-links {
                    display: flex;
                    gap: 16px;
                    flex-wrap: wrap;
                    margin-top: 20px;
                }

                .back {
                    display: inline-block;
                    color: #7dd3fc;
                    text-decoration: none;
                    font-weight: bold;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>Help Center</h1>

                <h2>How does ScamGuard JA work?</h2>
                <p>
                    ScamGuard JA scans Gmail sender information, subjects, and preview text for phishing indicators such as
                    urgency, account alerts, suspicious links, and credential requests.
                </p>

                <h2>What does the risk score mean?</h2>
                <p>
                    Higher risk scores indicate stronger phishing indicators. Emails with low scores are likely safe,
                    while higher scores may trigger warnings or automatic reports.
                </p>

                <h2>What happens when I report an email?</h2>
                <p>
                    Reported emails are turned into incident cases within ScamGuard JA and may be reviewed for security monitoring.
                </p>

                <h2>Does ScamGuard JA read all my emails?</h2>
                <p>
                    ScamGuard JA uses read-only Gmail access and focuses on sender information, subject lines, and preview text
                    for phishing detection.
                </p>

                <div class="back-links">
                    <a class="back" href="/gmail/dashboard">← Back to Dashboard</a>
                    <a class="back" href="/">Back to Sign In</a>
                </div>
            </div>
        </body>
        </html>
        """;
    }

    @GetMapping(value = "/contact", produces = "text/html")
    public String contactPage() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Contact ScamGuard JA</title>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background: #08152f;
                    color: white;
                    margin: 0;
                    padding: 40px 20px;
                }
                .container {
                    max-width: 900px;
                    margin: auto;
                    background: #162744;
                    padding: 30px;
                    border-radius: 16px;
                    box-shadow: 0 8px 24px rgba(0,0,0,0.25);
                }
                h1 {
                    margin-top: 0;
                }
                p {
                    color: #c7d3ea;
                    line-height: 1.7;
                    margin-bottom: 16px;
                }
                .back {
                    display: inline-block;
                    margin-top: 20px;
                    color: #7dd3fc;
                    text-decoration: none;
                    font-weight: bold;
                }
                .nav {
                    position: absolute;
                    top: 20px;
                    right: 30px;
                }

                .nav a {
                    margin-left: 18px;
                    text-decoration: none;
                    color: #7dd3fc;
                    font-weight: bold;
                }
                .nav a:hover {
                    color: #ffffff;
                }

                .back-links {
                    display: flex;
                    gap: 16px;
                    flex-wrap: wrap;
                    margin-top: 20px;
                }

                .back {
                    display: inline-block;
                    color: #7dd3fc;
                    text-decoration: none;
                    font-weight: bold;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>Contact Us</h1>

                <p>
                    For support, security concerns, or general questions about ScamGuard JA, reach out to our team.
                </p>

                <p><strong>Support Email:</strong> support@scamguardja.com</p>
                <p><strong>Security Reports:</strong> security@scamguardja.com</p>
                <p><strong>Response Hours:</strong> Monday to Friday, 9:00 AM - 5:00 PM</p>

                <div class="back-links">
                    <a class="back" href="/gmail/dashboard">← Back to Dashboard</a>
                    <a class="back" href="/">Back to Sign In</a>
                </div>
            </div>
        </body>
        </html>
        """;
    }

    @GetMapping(value = "/security", produces = "text/html")
        public String securityPage() {
            return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>ScamGuard JA - Security</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background: #08152f;
                        color: white;
                        margin: 0;
                        padding: 40px 20px;
                    }
                    .nav {
                        position: absolute;
                        top: 20px;
                        right: 30px;
                    }
                    .nav a {
                        margin-left: 18px;
                        text-decoration: none;
                        color: #7dd3fc;
                        font-weight: bold;
                    }
                    .container {
                        max-width: 900px;
                        margin: 60px auto 0;
                        background: #162744;
                        padding: 30px;
                        border-radius: 16px;
                        box-shadow: 0 8px 24px rgba(0,0,0,0.25);
                    }
                    h1 { margin-top: 0; }
                    p {
                        color: #c7d3ea;
                        line-height: 1.7;
                        margin-bottom: 16px;
                    }
                    .back {
                        display: inline-block;
                        margin-top: 20px;
                        color: #7dd3fc;
                        text-decoration: none;
                        font-weight: bold;
                    }
                    .footer {
                        max-width: 900px;
                        margin: 20px auto 0;
                        padding: 20px;
                        border-top: 1px solid #22365f;
                        color: #9ca3af;
                        display: flex;
                        justify-content: space-between;
                        flex-wrap: wrap;
                    }
                    .footer a {
                        color: #7dd3fc;
                        text-decoration: none;
                        margin-left: 14px;
                    }

                    .back-links {
                        display: flex;
                        gap: 16px;
                        flex-wrap: wrap;
                        margin-top: 20px;
                    }

                    .back {
                        display: inline-block;
                        color: #7dd3fc;
                        text-decoration: none;
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <div class="nav">
                    <a href="/">Home</a>
                    <a href="/gmail/dashboard">Dashboard</a>
                    <a href="/about">About</a>
                    <a href="/help">Help</a>
                    <a href="/contact">Contact</a>
                    <a href="/security">Security</a>
                </div>

                <div class="container">
                    <h1>Security</h1>

                    <p>ScamGuard JA uses Gmail read-only access, which means the platform is designed to analyze email threat indicators without modifying or sending emails on the user's behalf.</p>
                    <p>The platform generates phishing risk scores, incident reports, and threat indicators to help users identify suspicious email activity earlier.</p>
                    <p>Suspicious emails may be flagged or reported for incident tracking and security analysis. ScamGuard JA is intended as a phishing detection aid and awareness platform.</p>

                    <div class="back-links">
                        <a class="back" href="/gmail/dashboard">← Back to Dashboard</a>
                        <a class="back" href="/">Back to Sign In</a>
                    </div>
                </div>

                <footer class="footer">
                    <div>© 2026 ScamGuard JA</div>
                    <div>
                        <a href="/privacy">Privacy Policy</a>
                        <a href="/terms">Terms</a>
                        <a href="/security">Security</a>
                    </div>
                </footer>
            </body>
            </html>
            """;
        }

        @GetMapping(value = "/privacy", produces = "text/html")
        public String privacyPage() {
            return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>ScamGuard JA - Privacy Policy</title>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background: #08152f;
                        color: white;
                        margin: 0;
                        padding: 40px 20px;
                    }
                    .nav {
                        position: absolute;
                        top: 20px;
                        right: 30px;
                    }
                    .nav a {
                        margin-left: 18px;
                        text-decoration: none;
                        color: #7dd3fc;
                        font-weight: bold;
                    }
                    .container {
                        max-width: 900px;
                        margin: 60px auto 0;
                        background: #162744;
                        padding: 30px;
                        border-radius: 16px;
                        box-shadow: 0 8px 24px rgba(0,0,0,0.25);
                    }
                    p {
                        color: #c7d3ea;
                        line-height: 1.7;
                        margin-bottom: 16px;
                    }

                    .back-links {
                        display: flex;
                        gap: 16px;
                        flex-wrap: wrap;
                        margin-top: 20px;
                    }

                    .back {
                        display: inline-block;
                        color: #7dd3fc;
                        text-decoration: none;
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <div class="nav">
                    <a href="/">Home</a>
                    <a href="/gmail/dashboard">Dashboard</a>
                    <a href="/about">About</a>
                    <a href="/help">Help</a>
                    <a href="/contact">Contact</a>
                    <a href="/security">Security</a>
                </div>

                <div class="container">
                    <h1>Privacy Policy</h1>
                    <p>ScamGuard JA uses Gmail read-only access to analyze sender information, subject lines, and preview text for phishing detection.</p>
                    <p>The platform may store phishing indicators, case IDs, risk scores, and report records for security monitoring and analytics.</p>
                    <p>ScamGuard JA is designed to minimize unnecessary data collection and focuses on threat detection and reporting rather than storing full message bodies as part of standard operation.</p>
                    <p>User data is processed for phishing detection, security monitoring, and incident analysis purposes only.</p>
                </div>
            </body>
            </html>
            """;
        }
}