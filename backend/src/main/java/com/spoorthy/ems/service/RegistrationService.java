package com.spoorthy.ems.service;

import com.spoorthy.ems.dto.EventRegistrationDTO;
import com.spoorthy.ems.entity.Event;
import com.spoorthy.ems.entity.EventRegistration;
import com.spoorthy.ems.entity.User;
import com.spoorthy.ems.exception.ResourceNotFoundException;
import com.spoorthy.ems.repository.EventRegistrationRepository;
import com.spoorthy.ems.repository.EventRepository;
import com.spoorthy.ems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public EventRegistrationDTO registerForEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (registrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new IllegalArgumentException("You are already registered for this event");
        }
        if (event.getStatus() != Event.Status.UPCOMING) {
            throw new IllegalArgumentException("Registration is only available for upcoming events");
        }

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);

        EventRegistration saved = registrationRepository.save(registration);

        try {
            emailService.sendRegistrationConfirmation(user, event);
        } catch (Exception e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }

        return toDTO(saved);
    }

    public List<EventRegistrationDTO> getRegistrationsByEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventRegistrationDTO> getRegistrationsByUser(Long userId) {
        return registrationRepository.findByUserId(userId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public boolean isRegistered(Long eventId, Long userId) {
        return registrationRepository.existsByEventIdAndUserId(eventId, userId);
    }

    public Long getRegistrationCount(Long eventId) {
        return registrationRepository.countByEventId(eventId);
    }

    private EventRegistrationDTO toDTO(EventRegistration reg) {
        EventRegistrationDTO dto = new EventRegistrationDTO();
        dto.setId(reg.getId());
        dto.setEventId(reg.getEvent().getId());
        dto.setEventName(reg.getEvent().getEventName());
        dto.setUserId(reg.getUser().getId());
        dto.setUserName(reg.getUser().getName());
        dto.setUserEmail(reg.getUser().getEmail());
        dto.setUserRollNumber(reg.getUser().getRollNumber());
        dto.setUserMobile(reg.getUser().getMobile());
        dto.setRegisteredAt(reg.getRegisteredAt());
        return dto;
    }
}
