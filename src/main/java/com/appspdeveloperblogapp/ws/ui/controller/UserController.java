package com.appspdeveloperblogapp.ws.ui.controller;


import com.appspdeveloperblogapp.ws.service.AddressService;
import com.appspdeveloperblogapp.ws.service.UserService;
import com.appspdeveloperblogapp.ws.shared.dto.AddressDTO;
import com.appspdeveloperblogapp.ws.shared.dto.UserDto;
import com.appspdeveloperblogapp.ws.ui.model.request.RequestOperationName;
import com.appspdeveloperblogapp.ws.ui.model.request.UserDetailsRequestModel;
import com.appspdeveloperblogapp.ws.ui.model.response.AddressesRest;
import com.appspdeveloperblogapp.ws.ui.model.response.OperationStatusModel;
import com.appspdeveloperblogapp.ws.ui.model.response.RequestOperationStatus;
import com.appspdeveloperblogapp.ws.ui.model.response.UserRest;
import org.apache.tomcat.jni.Address;
import org.apache.tomcat.jni.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users") //http://localhost:8080/users
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	AddressService addressService;

	@Autowired
	AddressService addressesService;

	@GetMapping(path = "/{id}",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest getUser(@PathVariable String id) {
		UserRest returnValue = new UserRest();
		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);
		return returnValue;
	}

	@PostMapping(
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
		UserRest returnValue = new UserRest();


//		UserDto userDto = new UserDto();
//		BeanUtils.copyProperties(userDetails, userDto);
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);

		returnValue = modelMapper.map(createdUser, UserRest.class);
		return returnValue;
	}

	@PutMapping(path = "/{id}",
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

		UserRest returnValue = new UserRest();

		//if(userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);

		return returnValue;
	}


	@DeleteMapping(path = "/{id}",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());

		userService.deleteUser(id);

		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

		return returnValue;
	}

	@GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "limit", defaultValue = "25") int limit) {
		List<UserRest> returnValue = new ArrayList<>();
		if (page > 0) page = page - 1;

		List<UserDto> users = userService.getUsers(page, limit);

		for (UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}

		return returnValue;
	}

	// http://localhost:8080/mobile-app-ws/users/dferrtdsf132sdf/addresses/
	@GetMapping(path = "/{id}/addresses",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})

	public Resources<AddressesRest> getUserAddresses(@PathVariable String id) {
		List<AddressesRest> addessesListRestModel = new ArrayList<>();

		List<AddressDTO> addressesDTO = addressesService.getAddresses(id);


		if (addressesDTO != null && !addressesDTO.isEmpty()) {
			Type listType = new TypeToken<List<AddressesRest>>() {
			}.getType();
            addessesListRestModel = new ModelMapper().map(addressesDTO, listType);

            for(AddressesRest addressesRest : addessesListRestModel){
                Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressesRest.getAddressId())).withSelfRel();
                addressesRest.add(addressLink);
                Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
                addressesRest.add(userLink);
            }
		}

		return new Resources<>(addessesListRestModel);
	}


   // http://localhost:8080/mobile-app-ws/users/dferrtdsf132sdf/addresses/
	@GetMapping(path = "/{userId}/addresses/{addressId}",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE,  "application/hal+json"})

    public Resource<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId){
        AddressDTO addressDto = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();

        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
        Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		AddressesRest addressRestModel = modelMapper.map(addressDto, AddressesRest.class);
		addressRestModel.add(addressLink);
		addressRestModel.add(userLink);
        addressRestModel.add(addressesLink);
		return new Resource<>(modelMapper.map(addressDto, AddressesRest.class));

	}

    // http://localhost:8080/mobile-app-ws/users/email-verification?token=sdfsdf
    @GetMapping(path = "/email-verification",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value="token") String token){
       OperationStatusModel returnValue = new OperationStatusModel();
       returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
       boolean isVerified = userService.verifyEmailToken(token);
       if(isVerified){
           returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
       } else {
           returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
       }
       return returnValue;
    }


}