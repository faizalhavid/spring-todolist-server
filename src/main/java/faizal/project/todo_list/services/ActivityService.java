package faizal.project.todo_list.services;

import faizal.project.todo_list.dto.ActivityRequest;
import faizal.project.todo_list.dto.ActivityResponse;
import faizal.project.todo_list.exception.ResourceNotFoundException;
import faizal.project.todo_list.model.Activity;
import faizal.project.todo_list.model.User;
import faizal.project.todo_list.repository.ActivityRepository;
import faizal.project.todo_list.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public ActivityService(ActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    public List<ActivityResponse> getAllActivities() {
        return activityRepository.findAll().stream()
                .map(this::convertToActivityResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivityById(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id " + id));
        return convertToActivityResponse(activity);
    }

    public Activity createActivity(ActivityRequest activityRequest) {
        Activity activity = new Activity();
        activity.setName(activityRequest.getName());

        User user = userRepository.findById(activityRequest.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        activity.setUser(user);
        activity.setDescription(activityRequest.getDescription());
        activity.setStatus(activityRequest.getStatus());

        if (activityRequest.getStart_date().isAfter(activityRequest.getEnd_date())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        activity.setStart_date(activityRequest.getStart_date());
        activity.setEnd_date(activityRequest.getEnd_date());

        return activityRepository.save(activity);
    }

    public Activity updateActivity(ActivityRequest activityRequest, Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id " + id));

        User user = userRepository.findById(activityRequest.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        activity.setUser(user);
        activity.setName(activityRequest.getName());
        activity.setDescription(activityRequest.getDescription());
        activity.setStatus(activityRequest.getStatus());

        if (activityRequest.getStart_date().isAfter(activityRequest.getEnd_date())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        activity.setStart_date(activityRequest.getStart_date());
        activity.setEnd_date(activityRequest.getEnd_date());

        return activityRepository.save(activity);
    }

    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id " + id));
        activityRepository.delete(activity);
    }

    private ActivityResponse convertToActivityResponse(Activity activity) {
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUser_id(activity.getUser().getId());
        response.setName(activity.getName());
        response.setDescription(activity.getDescription());
        response.setStart_date(activity.getStart_date());
        response.setEnd_date(activity.getEnd_date());
        response.setStatus(activity.getStatus());
        response.setCreated_at(activity.getCreated_at());
        response.setUpdated_at(activity.getUpdated_at());
        return response;
    }
}