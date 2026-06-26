package com.spoorthy.ems.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventDTO {

    private Long id;
    private String eventName;
    private Long clubId;
    private String clubName;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String location;
    private String description;
    private String guestSpeaker;
    private String registrationLink;
    private String prizeAllocation;
    private String status;
    private Long createdById;
    private String createdByName;
    private Long registrationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EventDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public LocalTime getEventTime() { return eventTime; }
    public void setEventTime(LocalTime eventTime) { this.eventTime = eventTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGuestSpeaker() { return guestSpeaker; }
    public void setGuestSpeaker(String guestSpeaker) { this.guestSpeaker = guestSpeaker; }

    public String getRegistrationLink() { return registrationLink; }
    public void setRegistrationLink(String registrationLink) { this.registrationLink = registrationLink; }

    public String getPrizeAllocation() { return prizeAllocation; }
    public void setPrizeAllocation(String prizeAllocation) { this.prizeAllocation = prizeAllocation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public Long getRegistrationCount() { return registrationCount; }
    public void setRegistrationCount(Long registrationCount) { this.registrationCount = registrationCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
