package com.scamguardja.service;

import com.scamguardja.model.AiAnalysisResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiDetectionService {

    public AiAnalysisResult analyze(String message) {
        String text = message == null ? "" : message.toLowerCase();

        int confidence = 10;
        String verdict = "LOW";
        String category = "Safe / Low Risk";
        String explanation = "The message does not show strong scam characteristics.";
        String recommendation = "No urgent action needed, but always verify through official channels.";
        List<String> indicators = new ArrayList<>();

        if (text.contains("urgent") || text.contains("immediately") || text.contains("asap")) {
            confidence += 20;
            indicators.add("Urgency / pressure tactic");
        }

        if (text.contains("verify") || text.contains("confirm") || text.contains("login")) {
            confidence += 20;
            indicators.add("Requests verification or login");
        }

        if (text.contains("password") || text.contains("otp") || text.contains("pin") || text.contains("code")) {
            confidence += 25;
            indicators.add("Requests sensitive credentials");
        }

        if (text.contains("bank") || text.contains("account locked") || text.contains("security alert")) {
            confidence += 15;
            indicators.add("Impersonates financial/security institution");
        }

        if (text.contains("won") || text.contains("prize") || text.contains("claim now")) {
            confidence += 20;
            indicators.add("Prize / reward scam pattern");
        }

        if (text.contains("http://") || text.contains("https://") || text.contains("www.")) {
            confidence += 15;
            indicators.add("Contains external link");
        }

        if (confidence >= 75) {
            verdict = "HIGH";
            category = "Credential Phishing";
            explanation = "The message strongly resembles a phishing attempt designed to pressure the user into revealing credentials or clicking a suspicious link.";
            recommendation = "Do not click the link, do not reply, and report the message immediately.";
        } else if (confidence >= 45) {
            verdict = "MEDIUM";
            category = "Suspicious / Potential Scam";
            explanation = "The message contains several suspicious signals and may be an impersonation or social engineering attempt.";
            recommendation = "Verify the sender through an official channel before taking any action.";
        }

        return new AiAnalysisResult(
                verdict,
                Math.min(confidence, 100),
                category,
                explanation,
                recommendation,
                indicators
        );
    }
}