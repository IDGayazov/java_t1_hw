package com.example.task1.service;

import com.example.task1.dto.ClientDto;
import com.example.task1.entity.Client;

import java.util.List;

public interface ClientService {
    ClientDto registerClient(ClientDto clientDto);
    ClientDto getClientById(Long clientId);
    ClientDto updateClientById(Long clientId, ClientDto client);
    void deleteClientById(Long clientId);
}
