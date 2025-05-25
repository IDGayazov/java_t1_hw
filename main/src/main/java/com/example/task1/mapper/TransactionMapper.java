package com.example.task1.mapper;

import com.example.task1.dto.TransactionDto;
import com.example.task1.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target="id", ignore=true)
    @Mapping(target="time", ignore=true)
    Transaction toEntity(TransactionDto dto);

    TransactionDto toDto(Transaction entity);
}
