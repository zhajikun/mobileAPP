package com.appspdeveloperblogapp.ws.service.impl;

import com.appspdeveloperblogapp.ws.exceptions.UserServiceException;
import com.appspdeveloperblogapp.ws.io.entity.UserEntity;
import com.appspdeveloperblogapp.ws.io.repositories.UserRepository;
import com.appspdeveloperblogapp.ws.service.UserService;
import com.appspdeveloperblogapp.ws.shared.Utils;
import com.appspdeveloperblogapp.ws.shared.dto.AddressDTO;
import com.appspdeveloperblogapp.ws.shared.dto.UserDto;
import com.appspdeveloperblogapp.ws.ui.model.response.ErrorMessages;
import org.apache.tomcat.jni.Address;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

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

		 for(int i = 0; i< user.getAddresses().size(); i++){
			 AddressDTO address = user.getAddresses().get(i);
			 address.setUserDetails(user);
			 address.setAddressId(utils.generateAddressId(30));
			 user.getAddresses().set(i, address);
		 }

		//BeanUtils.copyProperties(user, userEntity);
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity  = modelMapper.map(user, UserEntity.class);


		String publicUserId = utils.generateUserId(30);

		userEntity.setUserId(publicUserId);
		userEntity.setEncrptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(false);
		UserEntity storedUserDetailes = userRepository.save(userEntity);
		UserDto returnValue = modelMapper.map(storedUserDetailes, UserDto.class);

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

		// return new User(userEntity.getEmail(), userEntity.getEncrptedPassword(), new ArrayList<>());

	    return new User(userEntity.getEmail(),
                        userEntity.getEncrptedPassword(),
                         userEntity.getEmailVerificationStatus(),
                         true,
                         true,
                         true,
                           new ArrayList<>());
	}


	@Override
	public UserDto getUserByUserId(String userId) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);

		if(null == userEntity) throw new UsernameNotFoundException("User with ID: " + userId + "not found!");

		BeanUtils.copyProperties(userEntity, returnValue);

		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto user) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if(null == userEntity) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		UserEntity updateUserDetails = userRepository.save(userEntity);
		BeanUtils.copyProperties(updateUserDetails, returnValue);
		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (null == userEntity) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		userRepository.delete(userEntity);

	}
	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<>();
		Pageable pageableRequest = PageRequest.of(page, limit);

		Page<UserEntity> userPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = userPage.getContent();

		for(UserEntity userEntity : users){
          UserDto userDto = new UserDto();
          BeanUtils.copyProperties(userEntity, userDto);
          returnValue.add(userDto);
		}


		return returnValue;
	}


	@Override
	public boolean verifyEmailToken(String token) {
		boolean returnValue = false;

		UserEntity userEntity = userRepository.finUserByEmailVerificationToken(token);
		if(userEntity != null){
			boolean hastokenExpried = Utils.hasTokenExpired(token);
			if(!hastokenExpried){
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				returnValue = true;
			}
		}
		return returnValue;
	}
}
