package com.fitness.ai.recommendation;

import com.fitness.ai.exception.RecommendationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public List<Recommendation> retrieveUserRecommendations(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    public Recommendation retrieveActivityRecommendation(String activityId) {
        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow(() -> new RecommendationNotFoundException("No recommendation found for this activity: {} " + activityId));
    }

}
