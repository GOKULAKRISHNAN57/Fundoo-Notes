package com.fundoonotes.service.impl;

import com.fundoonotes.dto.request.LoginRequestDto;
import com.fundoonotes.dto.request.UserRegisterRequestDto;
import com.fundoonotes.dto.response.LoginResponseDto;
import com.fundoonotes.dto.response.UserResponseDto;
import com.fundoonotes.entity.User;
import com.fundoonotes.exception.InvalidCredentialsException;
import com.fundoonotes.exception.UserAlreadyExistsException;
import com.fundoonotes.exception.UserNotFoundException;
import com.fundoonotes.repository.UserRepository;
import com.fundoonotes.service.UserService;
import com.fundoonotes.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           TokenUtil tokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public UserResponseDto register(UserRegisterRequestDto requestDto) {
        log.info("Registering new user with email: {}", requestDto.getEmail());

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + requestDto.getEmail());
        }

        User user = new User();
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        return mapToUserResponse(savedUser);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto requestDto) {
        log.info("Login attempt for email: {}", requestDto.getEmail());

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + requestDto.getEmail()));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = tokenUtil.generateToken(user.getId());
        log.info("Login successful for user id: {}", user.getId());

        return new LoginResponseDto(token, "Login successful");
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return mapToUserResponse(user);
    }

    // Helper: map User entity to response DTO
    private UserResponseDto mapToUserResponse(User user) {
        return new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
