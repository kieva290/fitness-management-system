package com.activity.activity;

import com.activity.exception.ActivityNotFoundException;
import com.activity.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;

    private final ActivityMapper activityMapper;

    private  final UserValidationService userValidationService;

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest activityRequest) {

        boolean isValidUser = userValidationService.validateUser(activityRequest.getUserId());

        if (!isValidUser) {
            throw new UserNotFoundException("Invalid user " + activityRequest.getUserId());
        }

        Activity activity = activityRepository.save(activityMapper.toActivity(activityRequest));

        // Publish to RabbitMQ for AI Processing
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, activity);
        } catch (Exception e) {
            log.error("Failed to publish activity to RabbitMQ : " ,e.getMessage());
        }

        return activityMapper.toActivityResponse(activity);

    }

    public List<ActivityResponse> retrieveUserActivities(String userId) {
        List<Activity> activities = activityRepository.findByUserId(userId);
        return activities.stream()
                .map(activityMapper::toActivityResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(String activityId) {
        return activityRepository.findById(activityId)
                .map(activityMapper::toActivityResponse)
                .orElseThrow(() -> new ActivityNotFoundException("Activity not found with id: " + activityId));
    }

}
