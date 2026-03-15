package com.scamguardja.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/scan")
@CrossOrigin(origins = "*")
public class EmailController {

    private final List<String> suspiciousWords = Arrays.asList(
            "urgent",
            "verify",
            "suspended",
            "locked",
            "click here",
            "immediately",
            "password",
            "otp",
            "pin",
            "security alert",
            "confirm now",
            "update account",
            "restricted",
            "unusual activity"
    );

    private final List<String> banks = Arrays.asList(
            "ncb",
            "scotiabank",
            "jmmb",
            "sagicor",
            "first global",
            "jn bank"
    );

    private final List<String> trustedDomains = Arrays.asList(
            "ncb.co.jm",
            "scotiabank.com.jm",
            "jmmb.com",
            "sagicor.com",
            "jnbank.com"
    );

    @PostMapping
    public Map<String, Object> scanEmail(@RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "").toLowerCase();

        int score = 0;
        List<String> reasons = new ArrayList<>();

        for (String word : suspiciousWords) {
            if (message.contains(word)) {
                score += 12;
                reasons.add("Suspicious phrase detected: " + word);
            }
        }

        for (String bank : banks) {
            if (message.contains(bank)) {
                score += 18;
                reasons.add("Possible bank impersonation: " + bank.toUpperCase());
            }
        }

        List<String> links = extractLinks(message);

        if (!links.isEmpty()) {
            score += 15;
            reasons.add("Message contains one or more links");
        }

        for (String link : links) {
            String domain = extractDomain(link);
            if (domain == null) continue;

            boolean trusted = false;
            for (String trustedDomain : trustedDomains) {
                if (domain.equals(trustedDomain) || domain.endsWith("." + trustedDomain)) {
                    trusted = true;
                    break;
                }
            }

            if (trusted) {
                reasons.add("Trusted domain detected: " + domain);
                score -= 10;
            } else {
                score += 25;
                reasons.add("Unrecognized or suspicious domain detected: " + domain);
            }
        }

        if (message.contains("password") || message.contains("otp") || message.contains("pin")) {
            score += 20;
            reasons.add("Sensitive credential request detected");
        }

        if (score < 0) {
            score = 0;
        }
        if (score > 100) {
            score = 100;
        }
        

        String risk = "LOW";
        if (score >= 60) {
            risk = "HIGH";
        } else if (score >= 25) {
            risk = "MEDIUM";
        }

        String recommendation;
        switch (risk) {
            case "HIGH":
                recommendation = "Do NOT click links. Contact the institution through its official website or app.";
                break;
            case "MEDIUM":
                recommendation = "Be cautious. Verify the sender independently before taking action.";
                break;
            default:
                recommendation = "Risk appears low, but always verify through official channels.";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("risk", risk);
        result.put("score", score);
        result.put("reasons", reasons);
        result.put("recommendation", recommendation);

        return result;
    }

    private List<String> extractLinks(String text) {
        List<String> links = new ArrayList<>();
        String[] parts = text.split("\\s+");

        for (String part : parts) {
            if (part.startsWith("http://") || part.startsWith("https://") || part.startsWith("www.")) {
                links.add(part);
            }
        }

        return links;
    }

    private String extractDomain(String url) {
        try {
            String cleanUrl = url;

            if (cleanUrl.startsWith("www.")) {
                cleanUrl = "https://" + cleanUrl;
            }

            java.net.URI uri = new java.net.URI(cleanUrl);
            String host = uri.getHost();

            if (host == null) return null;

            if (host.startsWith("www.")) {
                host = host.substring(4);
            }

            return host;
        } catch (Exception e) {
            return null;
        }
    }
}
