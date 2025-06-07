package com.example.task1.mapper;

import com.example.task1.dto.TransactionDto;
import com.example.task1.entity.Transaction;
import com.example.task1.entity.enums.TransactionStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports={TransactionStatus.class})
public interface TransactionMapper {
    @Mapping(target="id", ignore=true)
    @Mapping(target="time", ignore=true)
    @Mapping(target = "status", expression = "java(TransactionStatus.valueOf(dto.status()))")
    Transaction toEntity(TransactionDto dto);

    @Mapping(target="accountId", expression = "java(entity.getAccount().getId())")
    @Mapping(target = "status", expression = "java(entity.getStatus().toString())")
    TransactionDto toDto(Transaction entity);
}
