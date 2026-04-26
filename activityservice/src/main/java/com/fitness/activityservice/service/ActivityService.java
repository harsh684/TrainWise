package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;

    public ActivityResponse trackActivity(ActivityRequest request){

        System.out.println(request.toString());
        boolean isValidUser = userValidationService.validateUser(request.getUserId());
        if(!isValidUser){
            throw new RuntimeException("Invalid User: "+ request.getUserId());
        }

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getActivityType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();

        log.info("Saving Activity: " + activity.toString());
        Activity savedActivity = activityRepository.save(activity);

        return mapToResponse(savedActivity);
    }

    private ActivityResponse mapToResponse(Activity savedActivity){
        log.info("Mapping activity to respone DTO");
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setId(savedActivity.getId());
        activityResponse.setUserId(savedActivity.getUserId());
        activityResponse.setActivityName(savedActivity.getActivityName());
        activityResponse.setType(savedActivity.getType());
        activityResponse.setDuration(savedActivity.getDuration());
        activityResponse.setCaloriesBurned(savedActivity.getCaloriesBurned());
        activityResponse.setStartTime(savedActivity.getStartTime());
        activityResponse.setAdditionalMetrics(savedActivity.getAdditionalMetrics());
        activityResponse.setCreatedAt(savedActivity.getCreatedAt());
        activityResponse.setUpdatedAt(savedActivity.getUpdatedAt());
        return activityResponse;
    }

    public List<ActivityResponse> getUserActivities(String userId) {
        List<Activity> activities = activityRepository.findByUserId(userId);
        log.info("Getting activities from list");
        return activities.stream().map(
                this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(String activityId) {
        log.info("Getting activity by id: " + activityId);
        return activityRepository.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(()-> new RuntimeException("Activity not found"));
    }
}
