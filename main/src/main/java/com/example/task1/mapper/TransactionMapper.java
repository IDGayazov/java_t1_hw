package com.example.task1.mapper;

import com.example.task1.dto.ProcessedTransactionDto;
import com.example.task1.dto.TransactionDto;
import com.example.task1.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target="id", ignore=true)
    @Mapping(target="time", ignore=true)
    @Mapping(target = "status", expression = "java(TransactionStatus.valueOf(transactionDto.status()))")
    Transaction toEntity(TransactionDto dto);

    @Mapping(target="accountId", expression = "java(entity.getAccount().getId())")
    @Mapping(target = "status", expression = "java(entity.getStatus().toString())")
    TransactionDto toDto(Transaction entity);
}
