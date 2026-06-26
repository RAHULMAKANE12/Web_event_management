package com.spoorthy.ems.service;

import com.spoorthy.ems.dto.ClubDTO;
import com.spoorthy.ems.entity.Club;
import com.spoorthy.ems.entity.User;
import com.spoorthy.ems.exception.ResourceNotFoundException;
import com.spoorthy.ems.repository.ClubRepository;
import com.spoorthy.ems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ClubDTO> getAllClubs() {
        return clubRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ClubDTO getClubById(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));
        return toDTO(club);
    }

    @Transactional
    public ClubDTO createClub(String clubName, String description, String mentorName,
                              String mentorEmail, String logoUrl, Long createdById) {
        if (clubRepository.existsByClubName(clubName)) {
            throw new IllegalArgumentException("A club with this name already exists");
        }

        Club club = new Club();
        club.setClubName(clubName);
        club.setDescription(description);
        club.setMentorName(mentorName);
        club.setMentorEmail(mentorEmail);
        club.setLogoUrl(logoUrl);

        if (createdById != null) {
            User creator = userRepository.findById(createdById)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            club.setCreatedBy(creator);
        }

        Club saved = clubRepository.save(club);
        return toDTO(saved);
    }

    @Transactional
    public ClubDTO updateClub(Long id, String clubName, String description,
                              String mentorName, String mentorEmail, String logoUrl) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));

        if (clubName != null && !clubName.isBlank()) {
            if (!club.getClubName().equals(clubName) && clubRepository.existsByClubName(clubName)) {
                throw new IllegalArgumentException("A club with this name already exists");
            }
            club.setClubName(clubName);
        }
        if (description != null && !description.isBlank()) club.setDescription(description);
        if (mentorName != null && !mentorName.isBlank()) club.setMentorName(mentorName);
        if (mentorEmail != null && !mentorEmail.isBlank()) club.setMentorEmail(mentorEmail);
        if (logoUrl != null) club.setLogoUrl(logoUrl);

        Club saved = clubRepository.save(club);
        return toDTO(saved);
    }

    @Transactional
    public void deleteClub(Long id) {
        if (!clubRepository.existsById(id)) {
            throw new ResourceNotFoundException("Club not found with id: " + id);
        }
        clubRepository.deleteById(id);
    }

    private ClubDTO toDTO(Club club) {
        ClubDTO dto = new ClubDTO();
        dto.setId(club.getId());
        dto.setClubName(club.getClubName());
        dto.setDescription(club.getDescription());
        dto.setMentorName(club.getMentorName());
        dto.setMentorEmail(club.getMentorEmail());
        dto.setLogoUrl(club.getLogoUrl());
        dto.setCreatedById(club.getCreatedBy() != null ? club.getCreatedBy().getId() : null);
        dto.setCreatedByName(club.getCreatedBy() != null ? club.getCreatedBy().getName() : null);
        dto.setEventCount(club.getEvents() != null ? club.getEvents().size() : 0);
        dto.setCreatedAt(club.getCreatedAt());
        dto.setUpdatedAt(club.getUpdatedAt());
        return dto;
    }
}
