package com.fitness.ai.messages;

import com.fitness.ai.activity.Activity;
import com.fitness.ai.gemini.ActivityAIService;
import com.fitness.ai.recommendation.Recommendation;
import com.fitness.ai.recommendation.RecommendationRepository;
import com.fitness.ai.recommendation.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListener {

    private final ActivityAIService activityAIService;
        private final RecommendationRepository recommendationRepository;

    @RabbitListener(queues = "activity.queue")
    public void processActivityMessage(Activity activity) {
        log.info("Received activity for processing: {}", activity.toString());
//        log.info("Generated Recommendations: {}", activityAIService.generateRecommendations(activity));
        Recommendation recommendation = activityAIService.generateRecommendations(activity);
        recommendationRepository.save(recommendation);
    }

}
