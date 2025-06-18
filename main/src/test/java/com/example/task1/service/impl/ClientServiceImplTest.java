package com.example.task1.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.task1.dto.ClientDto;
import com.example.task1.entity.Client;
import com.example.task1.entity.enums.ClientStatus;
import com.example.task1.mapper.ClientMapper;
import com.example.task1.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    private final Long TEST_CLIENT_ID = 1L;
    private final ClientDto TEST_CLIENT_DTO = new ClientDto(
            TEST_CLIENT_ID,
            "John",
            "Doe",
            "Middle",
            12345L
    );
    private final Client TEST_CLIENT_ENTITY = Client.builder()
            .id(TEST_CLIENT_ID)
            .firstName("John")
            .lastName("Doe")
            .middleName("Middle")
            .clientStatus(ClientStatus.ACTIVE)
            .clientId(12345L)
            .build();

    @Test
    void registerClient_ShouldSuccessfullyRegisterClient() {
        when(clientMapper.toEntity(TEST_CLIENT_DTO)).thenReturn(TEST_CLIENT_ENTITY);
        when(clientRepository.save(TEST_CLIENT_ENTITY)).thenReturn(TEST_CLIENT_ENTITY);
        when(clientMapper.toDto(TEST_CLIENT_ENTITY)).thenReturn(TEST_CLIENT_DTO);

        ClientDto result = clientService.registerClient(TEST_CLIENT_DTO);

        assertNotNull(result);
        assertEquals(TEST_CLIENT_DTO, result);
        verify(clientRepository).save(TEST_CLIENT_ENTITY);
    }

    @Test
    void getClientById_WhenClientExists_ShouldReturnClient() {
        when(clientRepository.findById(TEST_CLIENT_ID)).thenReturn(Optional.of(TEST_CLIENT_ENTITY));
        when(clientMapper.toDto(TEST_CLIENT_ENTITY)).thenReturn(TEST_CLIENT_DTO);

        ClientDto result = clientService.getClientById(TEST_CLIENT_ID);

        assertNotNull(result);
        assertEquals(TEST_CLIENT_DTO, result);
        verify(clientRepository).findById(TEST_CLIENT_ID);
    }

    @Test
    void getClientById_WhenClientNotExists_ShouldThrowException() {
        when(clientRepository.findById(TEST_CLIENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> clientService.getClientById(TEST_CLIENT_ID));
    }

    @Test
    void updateClientById_WhenClientExists_ShouldUpdateClient() {
        ClientDto updateDto = new ClientDto(
                TEST_CLIENT_ID,
                "Updated",
                "Name",
                null,
                null
        );

        Client updatedClient = Client.builder()
                .id(TEST_CLIENT_ID)
                .firstName("Updated")
                .lastName("Name")
                .middleName("Middle")
                .clientStatus(ClientStatus.ACTIVE)
                .clientId(12345L)
                .build();

        ClientDto expectedDto = new ClientDto(
                TEST_CLIENT_ID,
                "Updated",
                "Name",
                "Middle",
                12345L
        );

        when(clientRepository.findById(TEST_CLIENT_ID)).thenReturn(Optional.of(TEST_CLIENT_ENTITY));
        when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);
        when(clientMapper.toDto(updatedClient)).thenReturn(expectedDto);

        ClientDto result = clientService.updateClientById(TEST_CLIENT_ID, updateDto);

        assertNotNull(result);
        assertEquals("Updated", result.firstName());
        assertEquals("Name", result.lastName());
        assertEquals("Middle", result.middleName());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void updateClientById_WhenClientNotExists_ShouldThrowException() {
        when(clientRepository.findById(TEST_CLIENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> clientService.updateClientById(TEST_CLIENT_ID, TEST_CLIENT_DTO));
    }

    @Test
    void deleteClientById_WhenClientExists_ShouldDeleteClient() {
        when(clientRepository.existsById(TEST_CLIENT_ID)).thenReturn(true);

        clientService.deleteClientById(TEST_CLIENT_ID);

        verify(clientRepository).deleteById(TEST_CLIENT_ID);
    }

    @Test
    void deleteClientById_WhenClientNotExists_ShouldThrowException() {
        when(clientRepository.existsById(TEST_CLIENT_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> clientService.deleteClientById(TEST_CLIENT_ID));
    }

    @Test
    void updateClientById_ShouldOnlyUpdateNonNullFields() {
        ClientDto partialUpdateDto = new ClientDto(
                TEST_CLIENT_ID,
                null,
                "UpdatedLastName",
                null,
                null
        );

        Client updatedClient = Client.builder()
                .id(TEST_CLIENT_ID)
                .firstName("John")
                .lastName("UpdatedLastName")
                .middleName("Middle")
                .clientStatus(ClientStatus.ACTIVE)
                .clientId(12345L)
                .build();

        ClientDto expectedDto = new ClientDto(
                TEST_CLIENT_ID,
                "John",
                "UpdatedLastName",
                "Middle",
                12345L
        );

        when(clientRepository.findById(TEST_CLIENT_ID)).thenReturn(Optional.of(TEST_CLIENT_ENTITY));
        when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);
        when(clientMapper.toDto(updatedClient)).thenReturn(expectedDto);

        ClientDto result = clientService.updateClientById(TEST_CLIENT_ID, partialUpdateDto);

        assertEquals("John", result.firstName());
        assertEquals("UpdatedLastName", result.lastName());
        verify(clientRepository).save(any(Client.class));
    }
}