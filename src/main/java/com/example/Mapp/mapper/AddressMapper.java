package com.example.Mapp.mapper;

import com.example.Mapp.DTO.AddressDTO;
import com.example.Mapp.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address DtoToEntity(AddressDTO dto){
        Address entity = new Address();
        entity.setCity(dto.getCity());
        entity.setCountry(dto.getCountry());
        entity.setStreetName(dto.getStreetName());
        entity.setStreetNumber(dto.getStreetNumber());
        return entity;
    }

    public AddressDTO EntityToDto(Address entity){
        AddressDTO dto = new AddressDTO();
        dto.setCity(entity.getCity());
        dto.setCountry(entity.getCountry());
        dto.setStreetName(entity.getStreetName());
        dto.setStreetNumber(entity.getStreetNumber());
        return dto;
    }
}
