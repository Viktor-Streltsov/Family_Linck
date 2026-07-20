package com.familylink.backend.situation.ai;

import com.familylink.backend.situation.Situation;

public interface AiAnalysisService {

    /**
     * Генерирует рекомендацию по ситуации на основе описаний участников.
     * Отправляет обезличенные тексты в модель.
     */
    AiAnalysisResult generateRecommendation(Situation situation);
}