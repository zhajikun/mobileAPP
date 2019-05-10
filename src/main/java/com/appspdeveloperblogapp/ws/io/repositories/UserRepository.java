package com.appspdeveloperblogapp.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.appspdeveloperblogapp.ws.io.entity.UserEntity;


@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);

}
