package com.appspdeveloperblogapp.ws.service.impl;

import com.appspdeveloperblogapp.ws.io.entity.UserEntity;
import com.appspdeveloperblogapp.ws.io.repositories.UserRepository;
import com.appspdeveloperblogapp.ws.shared.dto.UserDto;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception{

        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetUser() {

        UserEntity userEntity = new UserEntity();

        userEntity.setId(1L);
        userEntity.setFirstName("Sergey");
        userEntity.setUserId("asde33f");
        userEntity.setEncrptedPassword("sdf234fgh67");

        when(userRepository.findByEmail( anyString() ) ).thenReturn(userEntity);

        UserDto userDto = userService.getUser("test@test.com");

        assertNotNull(userDto);

        assertEquals("Sergey", userDto.getFirstName());


    }
}