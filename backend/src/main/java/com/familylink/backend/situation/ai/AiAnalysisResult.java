package com.familylink.backend.situation.ai;

public record AiAnalysisResult(
        boolean success,
        String content,
        String resources,
        String modelVersion,
        boolean safetyFlag,
        String failureReason
) {}