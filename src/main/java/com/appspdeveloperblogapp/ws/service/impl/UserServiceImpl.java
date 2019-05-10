package com.appspdeveloperblogapp.ws.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appspdeveloperblogapp.ws.io.repositories.UserRepository;
import com.appspdeveloperblogapp.ws.io.entity.UserEntity;
import com.appspdeveloperblogapp.ws.service.UserService;
import com.appspdeveloperblogapp.ws.shared.Utils;
import com.appspdeveloperblogapp.ws.shared.dto.UserDto;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public UserDto createUser(UserDto user) {
		UserEntity storedUserDetails = userRepository.findByEmail(user.getEmail());

		
		 if(storedUserDetails != null ) { throw new
		    RuntimeException("Record already exists");
		 }
		 

		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);

		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncrptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		UserEntity storedUserDetailes = userRepository.save(userEntity);
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(storedUserDetailes, returnValue);
		return returnValue;
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);

		if(null == userEntity){
			throw new UsernameNotFoundException(email);
		}
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);
		if(null == userEntity){
			throw new UsernameNotFoundException(email);
		}


		return new User(userEntity.getEmail(), userEntity.getEncrptedPassword(), new ArrayList<>());
	}


	@Override
	public UserDto getUserByUserId(String userId) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);

		if(null == userEntity) throw new UsernameNotFoundException(userId);

		BeanUtils.copyProperties(userEntity, returnValue);

		return returnValue;
	}
}
