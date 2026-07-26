package com.kalai.ups.service;

import com.kalai.ups.dto.UserDto;
import com.kalai.ups.entity.User;
import com.kalai.ups.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public void save(UserDto dto) {
        User user = dto.getId() != null ? findById(dto.getId()) : new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setRole(dto.getRole());
        user.setStatus(dto.getStatus());
        userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void toggleStatus(Long id) {
        User user = findById(id);
        user.setStatus(user.getStatus() == User.Status.ACTIVE ? User.Status.INACTIVE : User.Status.ACTIVE);
        userRepository.save(user);
    }
}
