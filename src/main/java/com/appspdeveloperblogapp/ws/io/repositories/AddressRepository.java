package com.appspdeveloperblogapp.ws.io.repositories;

import com.appspdeveloperblogapp.ws.io.entity.AddressEntity;
import com.appspdeveloperblogapp.ws.io.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

   List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
   AddressEntity findByAddressId(String addressId);
}
