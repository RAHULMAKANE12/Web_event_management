package com.spoorthy.ems.controller;

import com.spoorthy.ems.dto.ApiResponse;
import com.spoorthy.ems.dto.ClubDTO;
import com.spoorthy.ems.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired
    private ClubService clubService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClubDTO>>> getAllClubs() {
        return ResponseEntity.ok(ApiResponse.success(clubService.getAllClubs()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClubDTO>> getClubById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(clubService.getClubById(id)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ClubDTO>> createClub(
            @RequestParam("clubName") String clubName,
            @RequestParam("description") String description,
            @RequestParam("mentorName") String mentorName,
            @RequestParam("mentorEmail") String mentorEmail,
            @RequestParam(value = "createdById", required = false) Long createdById,
            @RequestParam(value = "logo", required = false) MultipartFile logo) {

        String logoUrl = null;
        if (logo != null && !logo.isEmpty()) logoUrl = saveFile(logo);

        ClubDTO created = clubService.createClub(clubName, description, mentorName, mentorEmail, logoUrl, createdById);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Club created!", created));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ClubDTO>> updateClub(
            @PathVariable Long id,
            @RequestParam(value = "clubName", required = false) String clubName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "mentorName", required = false) String mentorName,
            @RequestParam(value = "mentorEmail", required = false) String mentorEmail,
            @RequestParam(value = "logo", required = false) MultipartFile logo) {

        String logoUrl = null;
        if (logo != null && !logo.isEmpty()) logoUrl = saveFile(logo);

        ClubDTO updated = clubService.updateClub(id, clubName, description, mentorName, mentorEmail, logoUrl);
        return ResponseEntity.ok(ApiResponse.success("Club updated!", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return ResponseEntity.ok(ApiResponse.success("Club deleted!", null));
    }

    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) ext = original.substring(original.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + ext;
            Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "/api/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }
}
