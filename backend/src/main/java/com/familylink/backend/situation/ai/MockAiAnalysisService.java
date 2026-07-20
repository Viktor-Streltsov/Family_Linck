package com.familylink.backend.situation.ai;

import com.familylink.backend.family.FamilyMember;
import com.familylink.backend.family.FamilyMemberRepository;
import com.familylink.backend.family.FamilyRole;
import com.familylink.backend.situation.Situation;
import com.familylink.backend.situation.SituationParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MockAiAnalysisService implements AiAnalysisService {

    private final FamilyMemberRepository familyMemberRepository;

    // Триггеры для safety-фильтра
    private static final Set<String> SAFETY_TRIGGERS = Set.of(
            "суицид", "убить себя", "покончить", "самоубий",
            "избивает", "бьёт", "бьет", "насилие",
            "изнасилов", "домогатель"
    );

    @Override
    public AiAnalysisResult generateRecommendation(Situation situation) {
        // 1. Собираем описания всех, кто дал согласие
        StringBuilder combined = new StringBuilder();
        for (SituationParticipant p : situation.getParticipants()) {
            if (p.isConsentedToAi() && p.getDescription() != null) {
                String role = detectRole(situation, p);
                combined.append(role).append(": ").append(p.getDescription()).append("\n\n");
            }
        }

        String allText = combined.toString().toLowerCase();

        // 2. Safety check — если обнаружены триггеры, не даём совет, показываем ресурсы
        if (containsSafetyTrigger(allText)) {
            return new AiAnalysisResult(
                    true,
                    "Похоже, ситуация серьёзная. Мы не будем давать здесь совет — важнее " +
                            "получить поддержку от специалиста. Позвоните на линию помощи, " +
                            "разговор бесплатный и конфиденциальный.",
                    buildCrisisResources(),
                    "safety-filter-v1",
                    true,
                    null
            );
        }

        // 3. Заглушка: генерируем «типовой» совет на основе категории
        String recommendation = generateRecommendationText(situation);
        String resources = generateResources(situation);

        return new AiAnalysisResult(
                true,
                recommendation,
                resources,
                "mock-v1",
                false,
                null
        );
    }

    private String detectRole(Situation situation, SituationParticipant participant) {
        Optional<FamilyMember> memberOpt = familyMemberRepository
                .findByFamilyAndUser(situation.getFamily(), participant.getUser());
        if (memberOpt.isEmpty()) return "Участник";
        FamilyRole role = memberOpt.get().getRole();
        return switch (role) {
            case PARENT -> "Родитель";
            case CHILD -> "Ребёнок";
            case GUARDIAN -> "Опекун";
            case OTHER -> "Родственник";
        };
    }

    private boolean containsSafetyTrigger(String text) {
        return SAFETY_TRIGGERS.stream().anyMatch(text::contains);
    }

    private String buildCrisisResources() {
        return """
                Экстренные контакты в Кыргызской Республике:
                • 111 — Телефон доверия для детей и подростков (бесплатно, круглосуточно)
                • 112 — Единый экстренный номер
                • Кризисный центр «Сезим»: +996 (312) 511-101
                """;
    }

    private String generateRecommendationText(Situation situation) {
        String baseAdvice = switch (situation.getCategory()) {
            case STUDY -> """
                    Учёба часто становится точкой напряжения между родителями и детьми.
                    
                    Родителю: попробуйте отделить оценки от отношений. Спросите не «почему тройка?»,
                    а «что было сложнее всего на этой неделе?». Ребёнок должен чувствовать, что вы
                    на его стороне, а не против него.
                    
                    Ребёнку: расскажите родителям, какая помощь нужна — репетитор, тишина в комнате,
                    больше отдыха. Родители часто не понимают, что именно сложно.
                    
                    Возможный компромисс: договориться о регулярном коротком разговоре (15 минут в
                    неделю) без телефонов, где обсуждается не «как оценки», а «как ты».
                    """;
            case GADGETS -> """
                    Гаджеты — одна из самых частых причин конфликтов в современных семьях.
                    
                    Родителю: запреты редко работают долго. Лучше обсудить, для чего ребёнок
                    использует телефон — общение, игры, учёба, отдых. Часть этих потребностей важна.
                    
                    Ребёнку: попробуйте понять беспокойство родителей — они видят, что вы устаёте от
                    экрана, и переживают за ваше здоровье.
                    
                    Возможный компромисс: договориться о времени без экрана (например, за ужином,
                    за час до сна) для всей семьи, включая родителей.
                    """;
            case CONFLICTS -> """
                    В любом конфликте важнее не «кто прав», а «что нужно каждому».
                    
                    Обеим сторонам: попробуйте назвать не претензии, а чувства. Не «ты меня не
                    слышишь», а «мне обидно, когда меня перебивают».
                    
                    Правило пяти минут: каждый говорит без прерываний, второй только слушает.
                    Потом меняются.
                    """;
            case EMOTIONS -> """
                    Эмоциональные разговоры сложны, потому что мы боимся быть уязвимыми.
                    
                    Начните с малого: «Я хочу рассказать, что чувствую, но мне сложно. Можешь
                    просто послушать без совета?»
                    
                    Иногда достаточно, чтобы вас услышали. Совет не всегда нужен.
                    """;
            case DAILY_ROUTINE -> """
                    Режим дня — это про предсказуемость и границы.
                    
                    Родителю: жёсткие правила без объяснения вызывают протест. Расскажите, почему
                    важен сон, приёмы пищи, отдых.
                    
                    Ребёнку: попробуйте предложить свой вариант расписания. Часто родители готовы
                    к компромиссу, если видят, что вы серьёзно подходите.
                    """;
            case FRIENDS -> """
                    Друзья — важная часть жизни ребёнка, и вмешательство родителей воспринимается
                    как посягательство на «свою территорию».
                    
                    Родителю: интересуйтесь друзьями без осуждения. Пригласите их домой, познакомьтесь.
                    Так вы будете знать окружение, а ребёнок не будет прятать эту часть жизни.
                    
                    Ребёнку: расскажите родителям о друзьях сами, до того как они начнут спрашивать.
                    Это снимает подозрительность.
                    """;
            case HEALTH -> """
                    Разговоры о здоровье лучше вести спокойно, без нагнетания.
                    
                    Если беспокойство серьёзное — обратитесь к специалисту. AI не заменяет врача.
                    """;
            case OTHER -> """
                    Каждая ситуация уникальна. Общий принцип: начните разговор без обвинений.
                    Используйте «я-сообщения» вместо «ты-сообщений».
                    
                    Вместо «ты никогда меня не слушаешь» — «я чувствую, что меня не слышат».
                    """;
        };

        return baseAdvice + "\n\n" +
                "⚠️ Это автоматически сгенерированные рекомендации, не заменяющие " +
                "консультацию специалиста. Если ситуация серьёзная — обратитесь к психологу.";
    }

    private String generateResources(Situation situation) {
        return """
                Полезные материалы:
                • Юлия Гиппенрейтер «Общаться с ребёнком. Как?» — базовая книга о семейной коммуникации
                • Людмила Петрановская «Тайная опора: привязанность в жизни ребёнка»
                • Адель Фабер, Элейн Мазлиш «Как говорить, чтобы дети слушали»
                
                Если нужна помощь специалиста:
                • Ассоциация психологов Кыргызстана: [ссылка добавляется в конфиге]
                • Онлайн-платформы для семейной терапии: b17.ru, yasno.live
                """;
    }
}