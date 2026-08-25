package com.ticketapp.booking.service;

import com.ticketapp.booking.dto.UserRequestDTO;
import com.ticketapp.booking.dto.UserResponseDTO;
import com.ticketapp.booking.entity.User;
import com.ticketapp.booking.exception.DuplicateException;
import com.ticketapp.booking.exception.NotFoundException;
import com.ticketapp.booking.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final ModelMapper modelMapper;

    public UserResponseDTO createUser (UserRequestDTO userRequestDTO){
        boolean isExists = userRepo.existsByEmail(userRequestDTO.getEmail());
        if(isExists){
            throw new DuplicateException("User already exists with email : " + userRequestDTO.getEmail());
        }
        User user = modelMapper.map(userRequestDTO, User.class);
        userRepo.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public List<UserResponseDTO> getAllUsers(){
        List<User> users = userRepo.findAll();

        return modelMapper.map(users,new TypeToken<List<UserResponseDTO>>(){}.getType());
    }

    public UserResponseDTO getUserById(Long userId){
        User user = userRepo.findById(userId).
                orElseThrow(()->new NotFoundException("User not found"));
        return modelMapper.map(user,UserResponseDTO.class);
    }




}