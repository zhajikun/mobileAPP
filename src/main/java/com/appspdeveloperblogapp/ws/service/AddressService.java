package com.appspdeveloperblogapp.ws.service;

import com.appspdeveloperblogapp.ws.shared.dto.AddressDTO;
import com.appspdeveloperblogapp.ws.ui.model.response.AddressesRest;

import java.util.List;

public interface AddressService {

    List<AddressDTO> getAddresses(String userId);
    AddressDTO getAddress(String addressId);
}
