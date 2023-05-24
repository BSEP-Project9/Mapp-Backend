package com.example.Mapp.service;

import com.example.Mapp.dto.AddressDTO;
import com.example.Mapp.mapper.AddressMapper;
import com.example.Mapp.model.Address;
import com.example.Mapp.repository.AddressRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    public Address create(AddressDTO addressDTO){
        Address address = addressMapper.DtoToEntity(addressDTO);
        addressRepository.save(address);
        return address;
    }

}
