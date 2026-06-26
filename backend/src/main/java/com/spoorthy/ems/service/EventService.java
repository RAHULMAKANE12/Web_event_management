package com.spoorthy.ems.service;

import com.spoorthy.ems.dto.EventDTO;
import com.spoorthy.ems.entity.Club;
import com.spoorthy.ems.entity.Event;
import com.spoorthy.ems.entity.User;
import com.spoorthy.ems.exception.ResourceNotFoundException;
import com.spoorthy.ems.repository.ClubRepository;
import com.spoorthy.ems.repository.EventRegistrationRepository;
import com.spoorthy.ems.repository.EventRepository;
import com.spoorthy.ems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRegistrationRepository registrationRepository;

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventDTO> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(Event.Status.UPCOMING, LocalDate.now()).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByClub(Long clubId) {
        return eventRepository.findByClubIdOrderByEventDateAsc(clubId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByClubAndStatus(Long clubId, String status) {
        Event.Status eventStatus = Event.Status.valueOf(status.toUpperCase());
        return eventRepository.findByClubIdAndStatusOrderByEventDateAsc(clubId, eventStatus).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return toDTO(event);
    }

    @Transactional
    public EventDTO createEvent(EventDTO dto, Long createdById) {
        Club club = clubRepository.findById(dto.getClubId())
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + dto.getClubId()));

        Event event = new Event();
        event.setEventName(dto.getEventName());
        event.setClub(club);
        event.setEventDate(dto.getEventDate());
        event.setEventTime(dto.getEventTime());
        event.setLocation(dto.getLocation());
        event.setDescription(dto.getDescription());
        event.setGuestSpeaker(dto.getGuestSpeaker());
        event.setRegistrationLink(dto.getRegistrationLink());
        event.setPrizeAllocation(dto.getPrizeAllocation());
        event.setStatus(Event.Status.UPCOMING);

        if (createdById != null) {
            User creator = userRepository.findById(createdById)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            event.setCreatedBy(creator);
        }

        Event saved = eventRepository.save(event);
        return toDTO(saved);
    }

    @Transactional
    public EventDTO updateEvent(Long id, EventDTO dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        if (dto.getEventName() != null) event.setEventName(dto.getEventName());
        if (dto.getClubId() != null) {
            Club club = clubRepository.findById(dto.getClubId())
                    .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
            event.setClub(club);
        }
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getEventTime() != null) event.setEventTime(dto.getEventTime());
        if (dto.getLocation() != null) event.setLocation(dto.getLocation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getGuestSpeaker() != null) event.setGuestSpeaker(dto.getGuestSpeaker());
        if (dto.getRegistrationLink() != null) event.setRegistrationLink(dto.getRegistrationLink());
        if (dto.getPrizeAllocation() != null) event.setPrizeAllocation(dto.getPrizeAllocation());
        if (dto.getStatus() != null) event.setStatus(Event.Status.valueOf(dto.getStatus().toUpperCase()));

        Event saved = eventRepository.save(event);
        return toDTO(saved);
    }

    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }

    private EventDTO toDTO(Event event) {
        Long regCount = registrationRepository.countByEventId(event.getId());

        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setEventName(event.getEventName());
        dto.setClubId(event.getClub().getId());
        dto.setClubName(event.getClub().getClubName());
        dto.setEventDate(event.getEventDate());
        dto.setEventTime(event.getEventTime());
        dto.setLocation(event.getLocation());
        dto.setDescription(event.getDescription());
        dto.setGuestSpeaker(event.getGuestSpeaker());
        dto.setRegistrationLink(event.getRegistrationLink());
        dto.setPrizeAllocation(event.getPrizeAllocation());
        dto.setStatus(event.getStatus().name());
        dto.setCreatedById(event.getCreatedBy() != null ? event.getCreatedBy().getId() : null);
        dto.setCreatedByName(event.getCreatedBy() != null ? event.getCreatedBy().getName() : null);
        dto.setRegistrationCount(regCount);
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        return dto;
    }
}
