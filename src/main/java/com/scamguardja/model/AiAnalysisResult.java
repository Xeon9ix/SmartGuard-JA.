package com.scamguardja.model;

import java.util.List;

public class AiAnalysisResult {
    private String verdict;
    private int confidence;
    private String category;
    private String explanation;
    private String recommendation;
    private List<String> indicators;

    public AiAnalysisResult() {
    }

    public AiAnalysisResult(String verdict, int confidence, String category,
                            String explanation, String recommendation, List<String> indicators) {
        this.verdict = verdict;
        this.confidence = confidence;
        this.category = category;
        this.explanation = explanation;
        this.recommendation = recommendation;
        this.indicators = indicators;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public List<String> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<String> indicators) {
        this.indicators = indicators;
    }
}