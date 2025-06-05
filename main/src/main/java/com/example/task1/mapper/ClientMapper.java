package com.example.task1.mapper;

import com.example.task1.dto.ClientDto;
import com.example.task1.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientDto toDto(Client client);

    @Mapping(target="id", ignore=true)
    Client toEntity(ClientDto clientDto);
}
