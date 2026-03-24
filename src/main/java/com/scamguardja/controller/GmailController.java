package com.scamguardja.controller;

import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.scamguardja.model.AiAnalysisResult;
import com.scamguardja.service.AiDetectionService;
import com.scamguardja.service.GmailService;
import com.scamguardja.service.ReportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.net.URI;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class GmailController {

    private final ReportService reportService;
    private final GmailService gmailService;
    private final AiDetectionService aiDetectionService;

    @Value("${official.domains.ncb:}")
    private String ncbDomains;

    @Value("${official.domains.scotiabank:}")
    private String scotiabankDomains;

    @Value("${official.domains.jmmb:}")
    private String jmmbDomains;

    @Value("${official.domains.sagicor:}")
    private String sagicorDomains;

    @Value("${official.domains.firstglobal:}")
    private String firstglobalDomains;

    @Value("${official.domains.vmbs:}")
    private String vmbsDomains;

    @Value("${official.domains.jnbank:}")
    private String jnbankDomains;

    public GmailController(ReportService reportService,
        GmailService gmailService,
        AiDetectionService aiDetectionService) {
            this.reportService = reportService;
            this.gmailService = gmailService;
            this.aiDetectionService = aiDetectionService;
        }

    @GetMapping(value = "/gmail/dashboard", produces = "text/html")
    public String dashboardPage() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>ScamGuard JA Dashboard</title>
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 0;
                    background: #08152f;
                    color: white;
                }

                .nav {
                    position: sticky;
                    top: 0;
                    z-index: 1000;
                    background: rgba(8, 21, 47, 0.96);
                    backdrop-filter: blur(8px);
                    border-bottom: 1px solid #22365f;
                    padding: 16px 24px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    gap: 16px;
                    flex-wrap: wrap;
                }

                .brand {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    font-weight: bold;
                    font-size: 20px;
                    color: #ffffff;
                }

                .brand-badge {
                    background: #162744;
                    border: 1px solid #22365f;
                    padding: 6px 10px;
                    border-radius: 999px;
                    color: #7dd3fc;
                    font-size: 13px;
                }

                .nav-links a {
                    margin-left: 16px;
                    text-decoration: none;
                    color: #7dd3fc;
                    font-weight: bold;
                }

                .nav-links a:hover {
                    color: #ffffff;
                }

                .page-wrap {
                    padding-top: 10px;
                }

                .container {
                    max-width: 1100px;
                    margin: auto;
                    padding: 30px 20px;
                }

                .hero-panel {
                    background: linear-gradient(135deg, #162744, #0f213f);
                    padding: 28px;
                    border-radius: 18px;
                    box-shadow: 0 8px 24px rgba(0,0,0,0.22);
                    margin-bottom: 24px;
                }

                .hero-panel h1 {
                    margin: 0 0 10px 0;
                }

                .hero-panel p {
                    color: #c7d3ea;
                    line-height: 1.7;
                    margin: 0;
                }

                .alert-banner {
                    background: linear-gradient(90deg, #7f1d1d, #b91c1c);
                    color: white;
                    padding: 16px;
                    border-radius: 12px;
                    margin-bottom: 24px;
                    font-weight: bold;
                    box-shadow: 0 6px 20px rgba(0,0,0,0.25);
                }

                .alert-banner.safe {
                    background: linear-gradient(90deg, #14532d, #16a34a);
                }

                .legend {
                    margin-top: 20px;
                    background: #162744;
                    padding: 16px;
                    border-radius: 12px;
                    margin-bottom: 24px;
                    box-shadow: 0 6px 20px rgba(0,0,0,0.2);
                }

                .summary {
                    display: flex;
                    gap: 16px;
                    flex-wrap: wrap;
                    margin-bottom: 24px;
                }

                .summary-card {
                    background: #162744;
                    padding: 18px;
                    border-radius: 14px;
                    min-width: 220px;
                    box-shadow: 0 6px 20px rgba(0,0,0,0.2);
                }

                .emails {
                    display: flex;
                    flex-direction: column;
                    gap: 16px;
                }

                .email-card {
                    background: #162744;
                    border-radius: 14px;
                    padding: 18px;
                    box-shadow: 0 6px 20px rgba(0,0,0,0.2);
                    border-left: 8px solid #4ade80;
                }

                .email-card.suspicious {
                    border-left-color: #ef4444;
                }

                .muted {
                    color: #c7d3ea;
                    font-size: 14px;
                }

                .status-safe {
                    color: #4ade80;
                    font-weight: bold;
                }

                .status-danger {
                    color: #ff6b6b;
                    font-weight: bold;
                }

                button {
                    margin-top: 12px;
                    padding: 10px 14px;
                    border: none;
                    border-radius: 10px;
                    cursor: pointer;
                    background: #2f6df6;
                    color: white;
                    font-weight: bold;
                    margin-right: 8px;
                }

                button.report {
                    background: #ef4444;
                }

                .tag {
                    display: inline-block;
                    margin-top: 8px;
                    margin-right: 8px;
                    font-size: 13px;
                    color: #e5e7eb;
                    background: #22365f;
                    padding: 6px 10px;
                    border-radius: 999px;
                }

                .auto-report {
                    color: #ffd166;
                    font-weight: bold;
                    margin-top: 8px;
                }

                .reported {
                    color: #7dd3fc;
                    font-weight: bold;
                    margin-top: 8px;
                }

                .info-box {
                    background: #162744;
                    padding: 18px;
                    border-radius: 14px;
                    margin-bottom: 24px;
                    box-shadow: 0 6px 20px rgba(0,0,0,0.2);
                }

                .section-card {
                    background: #162744;
                    padding: 24px;
                    border-radius: 16px;
                    box-shadow: 0 6px 20px rgba(0,0,0,0.2);
                    margin-top: 28px;
                }

                .section-card h2 {
                    margin-top: 0;
                }

                .section-card p {
                    color: #c7d3ea;
                    line-height: 1.7;
                }

                .footer {
                    max-width: 1100px;
                    margin: 40px auto 0;
                    padding: 20px;
                    border-top: 1px solid #22365f;
                    color: #9ca3af;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    gap: 12px;
                    flex-wrap: wrap;
                }

                .footer a {
                    color: #7dd3fc;
                    text-decoration: none;
                    margin-left: 14px;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                    background: #162744;
                    border-radius: 12px;
                    overflow: hidden;
                    margin-top: 12px;
                }

                th, td {
                    padding: 12px;
                    border-bottom: 1px solid #2b3e63;
                    text-align: left;
                }

                th {
                    background: #0f213f;
                }

                tr:hover {
                    background: #1e3359;
                }

                .ai-box {
                    margin-top: 12px;
                    padding: 12px;
                    background: #0f213f;
                    border-radius: 10px;
                }

                .ai-high {
                    color: #ff6b6b;
                    font-weight: bold;
                }

                .ai-medium {
                    color: #ffd166;
                    font-weight: bold;
                }

                .ai-low {
                    color: #4ade80;
                    font-weight: bold;
                }

                @media (max-width: 900px) {
                    .nav {
                        flex-direction: column;
                        align-items: flex-start;
                    }

                    .nav-links a {
                        margin-left: 0;
                        margin-right: 14px;
                        display: inline-block;
                        margin-top: 8px;
                    }
                }
            </style>
        </head>
        <body>

            <div class="nav" id="home">
                <div class="brand">
                    <span>🛡 ScamGuard JA</span>
                    <span class="brand-badge">Cybersecurity Dashboard</span>
                </div>

                <div class="nav-links">
                    <a href="/">Home</a>
                    <a href="/about">About</a>
                    <a href="/help">Help</a>
                    <a href="/contact">Contact</a>
                    <a href="/security">Security</a>
                    <a href="/privacy">Privacy</a>
                    <a href="/terms">Terms</a>
                </div>
            </div>

            <div class="page-wrap">
                <div class="container">

                    <div class="hero-panel">
                        <h1>Live Gmail Threat Monitoring</h1>
                        <p>
                            ScamGuard JA continuously scans your Gmail environment for phishing indicators,
                            suspicious login prompts, credential theft attempts, malicious links, and other
                            email-based threats.
                        </p>
                    </div>

                    <div id="alertBanner"></div>

                    <div id="reportsSection" style="display:none; margin-bottom:30px;">
                        <h2>🚨 Incident Reports</h2>

                        <table>
                            <thead>
                                <tr>
                                    <th>Case ID</th>
                                    <th>Sender</th>
                                    <th>Subject</th>
                                    <th>Risk</th>
                                    <th>Auto</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody id="reports"></tbody>
                        </table>
                    </div>

                    <div class="legend">
                        <h3>Legend</h3>

                        <div style="margin-top:10px">
                            <span style="color:#4ade80;font-weight:bold">✅ Safe Email</span>
                            <div class="muted">No suspicious indicators detected.</div>
                        </div>

                        <div style="margin-top:10px">
                            <span style="color:#ff6b6b;font-weight:bold">⚠️ Phishing Risk Detected</span>
                            <div class="muted">Email contains phishing indicators such as urgency, login prompts, credential requests, suspicious links, or domain mismatch.</div>
                        </div>

                        <div style="margin-top:10px">
                            <span style="color:#ffd166;font-weight:bold">🚨 Auto-Reported</span>
                            <div class="muted">High-risk emails are automatically reported to ScamGuard Security for investigation.</div>
                        </div>

                        <div style="margin-top:10px">
                            <span style="font-weight:bold">📨 Community Reports</span>
                            <div class="muted">Number of users who have reported the email as suspicious.</div>
                        </div>

                        <div style="margin-top:10px">
                            <span style="font-weight:bold">Risk Score</span>
                            <div class="muted">
                                0-2 → Safe<br>
                                3-5 → Suspicious<br>
                                6+ → High Risk (Auto-Report Triggered)
                            </div>
                        </div>
                    </div>

                    <div class="summary" id="summary"></div>

                    <div class="section-card">
                        <h2>🧪 Demo Mode</h2>
                        <p class="muted">Paste a suspicious email or scam message below to test ScamGuard JA instantly.</p>
                        <textarea id="demoMessage" style="width:100%; min-height:120px; border:none; border-radius:12px; padding:14px; margin-top:10px;">URGENT: Your NCB account has been locked. Verify now at https://ncbelink.com to avoid suspension.</textarea>
                        <div style="margin-top:12px;">
                            <button onclick="runDemoScan()">Run Demo Scan</button>
                        </div>
                        <div id="demoResult" class="info-box" style="display:none; margin-top:16px;"></div>
                    </div>

                    <div class="emails" id="emails"></div>

                    <div id="about-section" class="section-card">
                        <h2>About Us</h2>
                        <p>
                            ScamGuard JA is a phishing detection platform built to help users identify suspicious emails
                            using Gmail read-only access. It analyzes sender information, subject lines, preview text,
                            threat indicators, domain legitimacy, and AI-assisted email classification.
                        </p>
                    </div>

                    <div id="help-section" class="section-card">
                        <h2>Help</h2>
                        <p><strong>How does ScamGuard JA work?</strong><br>
                        ScamGuard JA scans Gmail sender information, subjects, and preview text for phishing indicators such as urgency, suspicious links, login prompts, credential requests, domain mismatch, and institution impersonation.</p>

                        <p><strong>What does the risk score mean?</strong><br>
                        Higher risk scores indicate stronger phishing indicators. Emails with low scores are likely safe, while higher scores may trigger warnings or automatic incident reports.</p>

                        <p><strong>What does AI do?</strong><br>
                        ScamGuard JA also runs AI-assisted analysis on each email to classify likely scam intent, confidence level, and recommended response.</p>
                    </div>

                    <div id="contact-section" class="section-card">
                        <h2>Contact Us</h2>
                        <p><strong>Support Email:</strong> support@scamguardja.com</p>
                        <p><strong>Security Reports:</strong> security@scamguardja.com</p>
                        <p><strong>Response Hours:</strong> Monday to Friday, 9:00 AM - 5:00 PM</p>
                    </div>

                </div>

                <footer class="footer">
                    <div>© 2026 ScamGuard JA. All rights reserved. ScamGuard JA™ is a trademark of its developer.</div>
                    <div>
                        <a href="/privacy">Privacy Policy</a>
                        <a href="/terms">Terms</a>
                        <a href="/security">Security</a>
                    </div>
                </footer>
            </div>

            <script>
                let firstLoad = true;
                let isRefreshing = false;

                async function loadData(showLoader = false) {
                    if (isRefreshing) return;
                    isRefreshing = true;

                    try {
                        if (showLoader && firstLoad) {
                            document.getElementById('summary').innerHTML = `
                                <div class="summary-card"><h3>Loading...</h3><p>Scanning inbox</p></div>
                            `;
                            document.getElementById('emails').innerHTML = `
                                <div class="info-box"><div class="muted">Loading emails...</div></div>
                            `;
                        }

                        const res = await fetch('/gmail/data');

                        if (!res.ok) {
                            throw new Error('Failed to load /gmail/data');
                        }

                        const data = await res.json();
                        if (data.error) {
                            throw new Error(data.message || "Backend returned an error");
                        }

                        if (data.suspicious > 0) {
                            document.getElementById('alertBanner').innerHTML = `
                                <div class="alert-banner">
                                    ⚠️ ACTIVE PHISHING DETECTED — ${data.suspicious} suspicious email(s), ${data.autoReported} auto-reported.
                                </div>
                            `;
                        } else {
                            document.getElementById('alertBanner').innerHTML = `
                                <div class="alert-banner safe">
                                    ✅ No active phishing threats detected in the current scan.
                                </div>
                            `;
                        }

                        document.getElementById('summary').innerHTML = `
                            <div class="summary-card"><h3>Total Emails</h3><p>${data.total}</p></div>
                            <div class="summary-card"><h3>Safe Emails</h3><p>${data.safe}</p></div>
                            <div class="summary-card"><h3>Suspicious Emails</h3><p>${data.suspicious}</p></div>
                            <div class="summary-card"><h3>Auto-Reported</h3><p>${data.autoReported}</p></div>
                            <div class="summary-card"><h3>Inbox Scanned</h3><p>${data.scannedCount}</p></div>
                            <div class="summary-card"><h3>Scan Time</h3><p>${data.scanTimeSeconds}s</p></div>
                            <div class="summary-card"><h3>Top Threat Sender</h3><p>${data.topThreatSender}</p></div>
                            <div class="summary-card"><h3>Top Attack Pattern</h3><p>${data.topAttackPattern}</p></div>
                            <div class="summary-card"><h3>Top Indicator</h3><p>${data.topIndicator}</p></div>
                        `;

                        const emailsHtml = data.emails.map(email => `
                            <div class="email-card ${email.suspicious ? 'suspicious' : ''}">
                                <div><strong>Sender:</strong> ${email.sender}</div>
                                <div><strong>Subject:</strong> ${email.subject}</div>
                                <div class="muted" style="margin-top:8px;"><strong>Preview:</strong> ${email.preview}</div>

                                <div style="margin-top:10px;">
                                    ${email.hasLink ? `<span class="tag">🔗 Contains external link</span>` : ``}
                                    ${email.detectedDomain ? `<span class="tag">🌐 ${email.detectedDomain}</span>` : ``}
                                    ${email.claimedInstitution ? `<span class="tag">🏦 ${email.claimedInstitution}</span>` : ``}
                                    <span class="tag">${
                                        email.trustBadge === 'Verified Domain' ? '✅ Verified Domain' :
                                        email.trustBadge === 'Impersonation Detected' ? '🚨 Impersonation Detected' :
                                        email.trustBadge === 'No Link Found' ? 'ℹ️ No Link Found' :
                                        '⚠️ Unverified Domain'
                                    }</span>
                                    ${email.reasons.map(r => `<span class="tag">${r}</span>`).join('')}
                                </div>
                                <div style="margin-top:12px;" class="${email.suspicious ? 'status-danger' : 'status-safe'}">
                                    ${email.suspicious ? '⚠️ Phishing Risk Detected' : '✅ Looks Safe'}
                                </div>

                                <div class="muted" style="margin-top:6px;">Risk score: ${email.riskScore}</div>
                                    <div class="muted">Threat level: ${email.threatLevel}</div>
                                    <div style="margin-top:8px; background:#22365f; border-radius:999px; height:10px; overflow:hidden;">
                                        <div style="width:${email.threatPercent}%; height:100%; background:${
                                            email.threatLevel === 'HIGH' ? '#ef4444' :
                                            email.threatLevel === 'MEDIUM' ? '#f59e0b' :
                                            '#22c55e'
                                        };"></div>
                                </div>
                                <div class="muted" style="margin-top:8px;">Community reports: ${email.communityReports}</div>

                                <div class="ai-box">
                                    <div>
                                        <strong>AI Verdict:</strong>
                                        <span class="${
                                            email.aiVerdict === 'HIGH' ? 'ai-high' :
                                            email.aiVerdict === 'MEDIUM' ? 'ai-medium' : 'ai-low'
                                        }">${email.aiVerdict}</span>
                                    </div>
                                    <div><strong>AI Confidence:</strong> ${email.aiConfidence}%</div>
                                    <div><strong>AI Category:</strong> ${email.aiCategory}</div>
                                    <div style="margin-top:8px;"><strong>AI Summary:</strong> ${email.aiSummary}</div>
                                    <div class="muted" style="margin-top:6px;"><strong>AI Explanation:</strong> ${email.aiExplanation}</div>
                                    <div class="muted" style="margin-top:6px;"><strong>AI Recommendation:</strong> ${email.aiRecommendation}</div>
                                    <div style="margin-top:8px;">
                                        ${email.aiIndicators.map(i => `<span class="tag">${i}</span>`).join('')}
                                    </div>
                                </div>
                                ${email.wouldBlock ? `<div class="auto-report">🚫 This email would be blocked by ScamGuard JA</div>` : ``}

                                ${email.autoReported ? `<div class="auto-report">🚨 Auto-reported to ScamGuard Security</div>` : ``}
                                ${email.reported ? `<div class="reported">📨 Already reported</div>` : ``}

                                <button class="report" onclick="reportEmail('${email.id}')">Report</button>
                            </div>
                        `).join('');

                        document.getElementById('emails').innerHTML = emailsHtml || `
                            <div class="info-box"><div class="muted">No emails found.</div></div>
                        `;

                        await loadReports();

                        firstLoad = false;
                    } catch (error) {
                        console.error(error);

                        document.getElementById('summary').innerHTML = `
                            <div class="summary-card"><h3>Error</h3><p>Could not load email data</p></div>
                        `;
                        document.getElementById('emails').innerHTML = `
                            <div class="info-box">
                                <div class="muted">Failed to load emails.</div>
                                <div class="muted" style="margin-top:8px;">${error.message}</div>
                            </div>
                        `;
                    } finally {
                        isRefreshing = false;
                    }
                }

                async function loadReports() {
                    const reportsSection = document.getElementById('reportsSection');
                    if (!reportsSection) return;

                    const res = await fetch('/gmail/reports');

                    if (!res.ok) {
                        throw new Error('Failed to load /gmail/reports');
                    }

                    const data = await res.json();

                    if (!data || data.length === 0) {
                        reportsSection.style.display = 'none';
                        document.getElementById('reports').innerHTML = '';
                        return;
                    }

                    const rows = data.map(r => `
                        <tr>
                            <td>${r.caseId}</td>
                            <td>${r.sender}</td>
                            <td>${r.subject}</td>
                            <td>${r.riskScore}</td>
                            <td>${r.autoReported ? "Yes" : "No"}</td>
                            <td>${r.status}</td>
                        </tr>
                    `).join('');

                    document.getElementById('reports').innerHTML = rows;
                    reportsSection.style.display = 'block';
                }

                async function reportEmail(id) {
                    const res = await fetch('/gmail/report', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify({ id: id })
                    });

                    const data = await res.json();
                    alert("📨 " + data.message + "\\nCase ID: " + data.caseId + "\\nStatus: Submitted to ScamGuard JA Security");                    loadData(false);
                }

                async function runDemoScan() {
                    const message = document.getElementById('demoMessage').value.trim();

                    if (!message) {
                        alert('Paste a message first.');
                        return;
                    }

                    try {
                        const res = await fetch('/scan/ai', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ message })
                        });

                        const data = await res.json();

                        document.getElementById('demoResult').style.display = 'block';
                        document.getElementById('demoResult').innerHTML = `
                            <h3>Demo Result</h3>
                            <div><strong>AI Verdict:</strong> ${data.verdict}</div>
                            <div><strong>Confidence:</strong> ${data.confidence}%</div>
                            <div><strong>Category:</strong> ${data.category}</div>
                            <div class="muted" style="margin-top:8px;"><strong>Explanation:</strong> ${data.explanation}</div>
                            <div class="muted" style="margin-top:8px;"><strong>Recommendation:</strong> ${data.recommendation}</div>
                            <div style="margin-top:10px;">
                                ${data.indicators.map(i => `<span class="tag">${i}</span>`).join('')}
                            </div>
                        `;
                    } catch (error) {
                        console.error(error);
                        alert('Demo scan failed.');
                    }
                }

                loadData(true);
                setInterval(() => loadData(false), 5000);
            </script>
        </body>
        </html>
        """;
    }

    @GetMapping("/gmail/data")
    public Map<String, Object> getInboxData(HttpSession session) {
        try {
            long startTime = System.currentTimeMillis();

            String userId = (String) session.getAttribute("gmailUserId");
            System.out.println("Session gmailUserId in /gmail/data = " + userId);
            System.out.println("Data session id = " + session.getId());

        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "No Gmail session found. Please sign in first.");
            return error;
        }

        var gmail = gmailService.buildGmailClient(userId);

            List<Message> messages = new ArrayList<>();
            String pageToken = null;
            int pageCount = 0;

            do {
                ListMessagesResponse response = gmail.users().messages()
                        .list("me")
                        .setMaxResults(10L)
                        .setPageToken(pageToken)
                        .execute();

                if (response.getMessages() != null) {
                    messages.addAll(response.getMessages());
                }

                pageToken = response.getNextPageToken();
                pageCount++;

            } while (pageToken != null && pageCount < 1);

            List<Map<String, Object>> emailResults = new ArrayList<>();

            int safe = 0;
            int suspicious = 0;

            Map<String, Integer> senderThreatCounts = new HashMap<>();
            Map<String, Integer> patternCounts = new HashMap<>();
            Map<String, Integer> keywordCounts = new HashMap<>();

            for (Message message : messages) {
                try {
                    Message fullMessage = gmail.users().messages()
                            .get("me", message.getId())
                            .execute();

                    String snippet = fullMessage.getSnippet();
                    String subject = "(Unknown)";
                    String sender = "(Unknown)";

                    if (snippet == null) {
                        snippet = "(No preview available)";
                    }

                    if (fullMessage.getPayload() != null && fullMessage.getPayload().getHeaders() != null) {
                        var headers = fullMessage.getPayload().getHeaders();
                        for (var header : headers) {
                            if (header.getName().equalsIgnoreCase("Subject")) {
                                subject = header.getValue();
                            }
                            if (header.getName().equalsIgnoreCase("From")) {
                                sender = header.getValue();
                            }
                        }
                    }

                    String originalText = subject + " " + snippet;
                    String combinedText = normalizeText(originalText);
                    String extractedDomain = extractDomainFromText(originalText);
                    String claimedInstitution = detectClaimedInstitution(originalText);

                    boolean hasLink =
                            combinedText.contains("http://")
                            || combinedText.contains("https://")
                            || combinedText.contains("www.");

                    int riskScore = 0;
                    List<String> reasons = new ArrayList<>();

                    if (containsAny(combinedText, "urgent", "urgently", "immediately", "asap")) {
                        riskScore += 2;
                        reasons.add("Urgency");
                        incrementCount(keywordCounts, "Urgency");
                    }

                    if (containsAny(combinedText, "verify", "verification", "confirm", "confirmation")) {
                        riskScore += 2;
                        reasons.add("Verification request");
                        incrementCount(keywordCounts, "Verification");
                    }

                    if (containsAny(combinedText, "password", "passcode", "pin", "otp", "code")) {
                        riskScore += 3;
                        reasons.add("Credential request");
                        incrementCount(keywordCounts, "Credential request");
                    }

                    if (containsAny(combinedText,
                            "login", "log in", "signin", "sign in", "sign in attempt",
                            "unusual sign in", "signin attempt", "sign in alert")) {
                        riskScore += 2;
                        reasons.add("Login alert");
                        incrementCount(keywordCounts, "Login alert");
                    }

                    if (containsAny(combinedText,
                            "unrecognized device", "unknown device", "new device")) {
                        riskScore += 2;
                        reasons.add("Device alert");
                        incrementCount(keywordCounts, "Device alert");
                    }

                    if (containsAny(combinedText,
                            "bank", "account suspended", "suspended", "security alert",
                            "locked account", "account locked")) {
                        riskScore += 2;
                        reasons.add("Account/security threat");
                        incrementCount(keywordCounts, "Account threat");
                    }

                    if (containsAny(combinedText, "click here", "tap here")) {
                        riskScore += 3;
                        reasons.add("Click bait");
                        incrementCount(keywordCounts, "Click bait");
                    }

                    if (containsAny(combinedText,
                            "free gift", "gift", "reward", "claim now", "claim", "winner",
                            "won", "prize", "signup", "sign up", "promotion")) {
                        riskScore += 2;
                        reasons.add("Promotional scam bait");
                        incrementCount(keywordCounts, "Scam bait");
                    }

                    String suspiciousDomain = extractSuspiciousDomain(combinedText);
                    if (suspiciousDomain != null) {
                        riskScore += 3;
                        reasons.add("Suspicious domain: " + suspiciousDomain);
                        incrementCount(keywordCounts, "Suspicious domain");
                    }

                    if (hasLink) {
                        riskScore += 2;
                        reasons.add("External link");
                        incrementCount(keywordCounts, "External link");
                    }

                    if (isDomainMismatch(originalText, extractedDomain)) {
                        riskScore += 4;
                        reasons.add("Domain does not match claimed institution");
                        incrementCount(keywordCounts, "Domain mismatch");
                    }

                    if (looksLikeBrandImpersonation(extractedDomain)) {
                        riskScore += 4;
                        reasons.add("Brand-like domain does not match official institution domain");
                        incrementCount(keywordCounts, "Brand impersonation");
                    }

                    

                    boolean isSuspicious = riskScore >= 3;

                    if (isSuspicious) {
                        suspicious++;
                        incrementCount(senderThreatCounts, sender);

                        String attackPattern = classifyAttackPattern(reasons);
                        incrementCount(patternCounts, attackPattern);
                    } else {
                        safe++;
                    }

                    if (riskScore >= 6 && !reportService.hasReport(message.getId())) {
                        reportService.reportEmail(
                                message.getId(),
                                sender,
                                subject,
                                snippet,
                                riskScore,
                                reasons,
                                true
                        );
                    }

                    AiAnalysisResult aiResult = aiDetectionService.analyze(originalText);

                    Map<String, Object> email = new HashMap<>();
                    email.put("id", message.getId());
                    email.put("sender", sender);
                    email.put("subject", subject);
                    email.put("preview", snippet);
                    email.put("suspicious", isSuspicious);
                    email.put("hasLink", hasLink);
                    email.put("riskScore", riskScore);
                    email.put("reasons", reasons);
                    email.put("reported", reportService.hasReport(message.getId()));
                    email.put("communityReports", reportService.getCommunityCount(message.getId()));
                    email.put("autoReported", reportService.wasAutoReported(message.getId()));
                    email.put("trustBadge", getTrustBadge(claimedInstitution, extractedDomain, reasons));
                    email.put("detectedDomain", extractedDomain);
                    email.put("claimedInstitution", claimedInstitution);
                    email.put("wouldBlock", shouldSimulateBlock(riskScore, aiResult.getVerdict()));
                    email.put("threatLevel", getThreatLevelLabel(riskScore));
                    email.put("threatPercent", Math.min(riskScore * 12, 100));
                    email.put("aiVerdict", aiResult.getVerdict());
                    email.put("aiConfidence", aiResult.getConfidence());
                    email.put("aiCategory", aiResult.getCategory());
                    email.put("aiExplanation", aiResult.getExplanation());
                    email.put("aiRecommendation", aiResult.getRecommendation());
                    email.put("aiSummary", buildAiSummary(aiResult.getVerdict(), aiResult.getCategory(), reasons));
                    email.put("aiIndicators", aiResult.getIndicators());

                    emailResults.add(email);

                } catch (Exception e) {
                    System.out.println("Skipping email due to error: " + e.getMessage());
                }
            }

            long endTime = System.currentTimeMillis();
            double scanTimeSeconds = (endTime - startTime) / 1000.0;

            Map<String, Object> result = new HashMap<>();
            result.put("total", emailResults.size());
            result.put("safe", safe);
            result.put("suspicious", suspicious);
            result.put("autoReported", reportService.getAutoReportCount());
            result.put("scannedCount", emailResults.size());
            result.put("scanTimeSeconds", scanTimeSeconds);
            result.put("emails", emailResults);
            result.put("topThreatSender", getTopKey(senderThreatCounts));
            result.put("topAttackPattern", getTopKey(patternCounts));
            result.put("topIndicator", getTopKey(keywordCounts));

            return result;

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", e.getMessage());
            return error;
        }
    }

    @PostMapping("/gmail/report")
    public Map<String, String> reportEmail(@RequestBody Map<String, String> body, HttpSession session) throws Exception {
        String userId = (String) session.getAttribute("gmailUserId");

        if (userId == null) {
            Map<String, String> result = new HashMap<>();
            result.put("message", "No Gmail session found. Please sign in first.");
            result.put("caseId", "N/A");
            return result;
        }

        var gmail = gmailService.buildGmailClient(userId);
        
        String id = body.get("id");

        Message fullMessage = gmail.users().messages()
                .get("me", id)
                .execute();

        String snippet = fullMessage.getSnippet();
        String subject = "(Unknown)";
        String sender = "(Unknown)";

        if (snippet == null) {
            snippet = "(No preview available)";
        }

        if (fullMessage.getPayload() != null && fullMessage.getPayload().getHeaders() != null) {
            var headers = fullMessage.getPayload().getHeaders();
            for (var header : headers) {
                if (header.getName().equalsIgnoreCase("Subject")) {
                    subject = header.getValue();
                }
                if (header.getName().equalsIgnoreCase("From")) {
                    sender = header.getValue();
                }
            }
        }

        String originalText = subject + " " + snippet;
        String combinedText = normalizeText(originalText);
        String extractedDomain = extractDomainFromText(originalText);

        boolean hasLink =
                combinedText.contains("http://")
                || combinedText.contains("https://")
                || combinedText.contains("www.");

        int riskScore = 0;
        List<String> reasons = new ArrayList<>();

        if (containsAny(combinedText, "urgent", "urgently", "immediately", "asap")) {
            riskScore += 2;
            reasons.add("Urgency");
        }
        if (containsAny(combinedText, "verify", "verification", "confirm", "confirmation")) {
            riskScore += 2;
            reasons.add("Verification request");
        }
        if (containsAny(combinedText, "password", "passcode", "pin", "otp", "code")) {
            riskScore += 3;
            reasons.add("Credential request");
        }
        if (containsAny(combinedText,
                "login", "log in", "signin", "sign in", "sign in attempt",
                "unusual sign in", "signin attempt", "sign in alert")) {
            riskScore += 2;
            reasons.add("Login alert");
        }
        if (containsAny(combinedText,
                "unrecognized device", "unknown device", "new device")) {
            riskScore += 2;
            reasons.add("Device alert");
        }
        if (containsAny(combinedText,
                "bank", "account suspended", "suspended", "security alert",
                "locked account", "account locked")) {
            riskScore += 2;
            reasons.add("Account/security threat");
        }
        if (containsAny(combinedText, "click here", "tap here")) {
            riskScore += 3;
            reasons.add("Click bait");
        }

        String suspiciousDomain = extractSuspiciousDomain(combinedText);
        if (suspiciousDomain != null) {
            riskScore += 3;
            reasons.add("Suspicious domain: " + suspiciousDomain);
        }

        if (hasLink) {
            riskScore += 2;
            reasons.add("External link");
        }

        if (isDomainMismatch(originalText, extractedDomain)) {
            riskScore += 4;
            reasons.add("Domain does not match claimed institution");
        }

        if (looksLikeBrandImpersonation(extractedDomain)) {
            riskScore += 4;
            reasons.add("Brand-like domain does not match official institution domain");
        }

        Map<String, Object> report = reportService.reportEmail(
                id, sender, subject, snippet, riskScore, reasons, false
        );

        Map<String, String> result = new HashMap<>();
        result.put("message", "Email reported to ScamGuard Security.");
        result.put("caseId", report.get("caseId").toString());
        return result;
    }

    @GetMapping("/gmail/reports")
    public List<Map<String, Object>> getReports() {
        return new ArrayList<>(reportService.getAllReports());
    }

    @GetMapping("/gmail/profile")
    public Map<String, Object> getProfile(HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        try {
            String userId = (String) session.getAttribute("gmailUserId");
            if (userId == null) {
                throw new RuntimeException("No Gmail session found. Please sign in first.");
            }
            
            var userInfo = gmailService.getUserInfo(userId);            
            result.put("name", userInfo.getName());
            result.put("email", userInfo.getEmail());
            result.put("picture", userInfo.getPicture());
            result.put("signedIn", true);
        } catch (Exception e) {
            result.put("name", "Guest User");
            result.put("email", "Not signed in");
            result.put("picture", "");
            result.put("signedIn", false);
        }

        return result;
    }

    private void incrementCount(Map<String, Integer> map, String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        map.put(key, map.getOrDefault(key, 0) + 1);
    }

    private String getTopKey(Map<String, Integer> map) {
        String topKey = "None";
        int max = 0;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                topKey = entry.getKey();
            }
        }

        return topKey;
    }

    private String classifyAttackPattern(List<String> reasons) {
        if (reasons.contains("Domain does not match claimed institution")
                || reasons.contains("Brand-like domain does not match official institution domain")) {
            return "Institution Impersonation";
        }
        if (reasons.contains("Credential request") || reasons.contains("Verification request")) {
            return "Credential Theft Attempt";
        }
        if (reasons.contains("Login alert") || reasons.contains("Device alert")) {
            return "Account Compromise Alert";
        }
        if (reasons.contains("Click bait") || reasons.contains("External link")) {
            return "Phishing Link Delivery";
        }
        if (reasons.contains("Promotional scam bait")) {
            return "Prize / Reward Scam";
        }
        if (reasons.contains("Urgency")) {
            return "Urgency-Based Social Engineering";
        }
        return "General Phishing Activity";
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKC).toLowerCase();

        normalized = normalized
                .replace('-', ' ')
                .replace('_', ' ')
                .replace('/', ' ')
                .replaceAll("[^a-z0-9:.\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return normalized;
    }

    private boolean containsAny(String text, String... patterns) {
        for (String pattern : patterns) {
            if (text.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    private String extractDomainFromText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        Pattern pattern = Pattern.compile("(https?://[^\\s]+|www\\.[^\\s]+)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String url = matcher.group();

            try {
                if (url.startsWith("www.")) {
                    url = "https://" + url;
                }

                URI uri = new URI(url);
                String host = uri.getHost();

                if (host != null && host.startsWith("www.")) {
                    host = host.substring(4);
                }

                return host == null ? null : host.toLowerCase();
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    private Set<String> parseDomains(String domains) {
        Set<String> result = new HashSet<>();

        if (domains == null || domains.isBlank()) {
            return result;
        }

        Arrays.stream(domains.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isBlank())
                .forEach(result::add);

        return result;
    }

    private boolean domainMatchesAny(String actualDomain, Set<String> allowedDomains) {
        if (actualDomain == null || allowedDomains.isEmpty()) {
            return true;
        }

        for (String allowed : allowedDomains) {
            if (actualDomain.equals(allowed) || actualDomain.endsWith("." + allowed)) {
                return true;
            }
        }

        return false;
    }

    private String detectClaimedInstitution(String text) {
        if (text == null) {
            return null;
        }
    
        String lower = text.toLowerCase();
    
        if (lower.contains("ncb")) {
            return "NCB";
        }
        if (lower.contains("scotiabank") || lower.contains("scotia")) {
            return "Scotiabank";
        }
        if (lower.contains("jmmb")) {
            return "JMMB";
        }
        if (lower.contains("sagicor")) {
            return "Sagicor";
        }
        if (lower.contains("first global") || lower.contains("firstglobal")) {
            return "First Global";
        }
        if (lower.contains("victoria mutual") || lower.contains("vmbs") || lower.contains("vm building society")) {
            return "VMBS";
        }
        if (lower.contains("jn bank") || lower.contains("jamaica national")) {
            return "JN Bank";
        }
    
        return null;
    }

    private boolean looksLikeBrandImpersonation(String actualDomain) {
        if (actualDomain == null || actualDomain.isBlank()) {
            return false;
        }
    
        String domain = actualDomain.toLowerCase();
    
        if (domain.contains("ncb") && !domainMatchesAny(domain, parseDomains(ncbDomains))) {
            return true;
        }
    
        if ((domain.contains("scotia") || domain.contains("scotiabank"))
                && !domainMatchesAny(domain, parseDomains(scotiabankDomains))) {
            return true;
        }
    
        if (domain.contains("jmmb") && !domainMatchesAny(domain, parseDomains(jmmbDomains))) {
            return true;
        }
    
        if (domain.contains("sagicor") && !domainMatchesAny(domain, parseDomains(sagicorDomains))) {
            return true;
        }
    
        if ((domain.contains("firstglobal") || domain.contains("first-global") || domain.contains("firstglobalbank"))
                && !domainMatchesAny(domain, parseDomains(firstglobalDomains))) {
            return true;
        }

        if ((domain.contains("vmbs") || domain.contains("victoriamutual") || domain.contains("victoria-mutual"))
            && !domainMatchesAny(domain, parseDomains(vmbsDomains))) {
        return true;
        }
        
        if ((domain.contains("jnbank") || domain.contains("jamaicanational") || domain.contains("jn-bank"))
                && !domainMatchesAny(domain, parseDomains(jnbankDomains))) {
            return true;
        }
    
        return false;
    }

    private boolean isDomainMismatch(String text, String actualDomain) {
        String institution = detectClaimedInstitution(text);
    
        if (institution == null || actualDomain == null) {
            return false;
        }
    
        return switch (institution) {
            case "NCB" -> !domainMatchesAny(actualDomain, parseDomains(ncbDomains));
            case "Scotiabank" -> !domainMatchesAny(actualDomain, parseDomains(scotiabankDomains));
            case "JMMB" -> !domainMatchesAny(actualDomain, parseDomains(jmmbDomains));
            case "Sagicor" -> !domainMatchesAny(actualDomain, parseDomains(sagicorDomains));
            case "First Global" -> !domainMatchesAny(actualDomain, parseDomains(firstglobalDomains));
            case "VMBS" -> !domainMatchesAny(actualDomain, parseDomains(vmbsDomains));
            case "JN Bank" -> !domainMatchesAny(actualDomain, parseDomains(jnbankDomains));
            default -> false;
        };
    }
    
    private String extractSuspiciousDomain(String text) {
        String[] suspiciousDomains = {
                "secure-bank-login.com",
                "fake-paypal-login.xyz",
                "verify-account-now.net",
                "login-alert-security.com"
        };

        for (String domain : suspiciousDomains) {
            if (text.contains(domain)) {
                return domain;
            }
        }
        return null;
    }

    private String getTrustBadge(String claimedInstitution, String detectedDomain, List<String> reasons) {
        if (reasons.contains("Domain does not match claimed institution")
                || reasons.contains("Brand-like domain does not match official institution domain")) {
            return "Impersonation Detected";
        }
    
        if (detectedDomain == null || detectedDomain.isBlank()) {
            return "No Link Found";
        }
    
        if (claimedInstitution != null) {
            boolean matches = switch (claimedInstitution) {
                case "NCB" -> domainMatchesAny(detectedDomain, parseDomains(ncbDomains));
                case "Scotiabank" -> domainMatchesAny(detectedDomain, parseDomains(scotiabankDomains));
                case "JMMB" -> domainMatchesAny(detectedDomain, parseDomains(jmmbDomains));
                case "Sagicor" -> domainMatchesAny(detectedDomain, parseDomains(sagicorDomains));
                case "First Global" -> domainMatchesAny(detectedDomain, parseDomains(firstglobalDomains));
                case "VMBS" -> domainMatchesAny(detectedDomain, parseDomains(vmbsDomains));
                case "JN Bank" -> domainMatchesAny(detectedDomain, parseDomains(jnbankDomains));
                default -> false;
            };
    
            return matches ? "Verified Domain" : "Unverified Domain";
        }
    
        return "Unverified Domain";
    }

    private String buildAiSummary(String aiVerdict, String aiCategory, List<String> reasons) {
        if ("HIGH".equalsIgnoreCase(aiVerdict)) {
            return "This email is likely a phishing attempt and should not be trusted.";
        }
        if ("MEDIUM".equalsIgnoreCase(aiVerdict)) {
            return "This email shows suspicious scam signals and should be verified carefully.";
        }
        if (reasons.contains("Brand-like domain does not match official institution domain")
                || reasons.contains("Domain does not match claimed institution")) {
            return "This email may be impersonating a trusted institution using a misleading link.";
        }
        return "This email currently appears low risk, but should still be verified through official channels.";
    }

    private boolean shouldSimulateBlock(int riskScore, String aiVerdict) {
        return riskScore >= 6 || "HIGH".equalsIgnoreCase(aiVerdict);
    }

    private String getThreatLevelLabel(int riskScore) {
        if (riskScore >= 6) return "HIGH";
        if (riskScore >= 3) return "MEDIUM";
        return "LOW";
    }
}