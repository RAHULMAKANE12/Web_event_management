package com.spoorthy.ems.service;

import com.spoorthy.ems.dto.LoginRequest;
import com.spoorthy.ems.dto.RegisterRequest;
import com.spoorthy.ems.entity.User;
import com.spoorthy.ems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public User register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        if (userRepository.existsByRollNumber(request.getRollNumber())) {
            throw new IllegalArgumentException("An account with this roll number already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRollNumber(request.getRollNumber());
        user.setMobile(request.getMobile());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(request.getRole()));

        return userRepository.save(user);
    }

    public Map<String, Object> login(LoginRequest request) {
        String identifier = request.getIdentifier();

        Optional<User> userOpt = userRepository.findByEmail(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByRollNumber(identifier);
        }
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("No account found with the provided email/roll number");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password. Please try again.");
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("rollNumber", user.getRollNumber());
        userData.put("mobile", user.getMobile());
        userData.put("role", user.getRole().name());
        return userData;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
