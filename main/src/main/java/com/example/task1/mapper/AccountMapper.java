package com.example.task1.mapper;

import com.example.task1.dto.AccountDto;
import com.example.task1.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "accountType", expression = "java(account.getAccountType().toString())")
    AccountDto toDto(Account account);

    @Mapping(target = "id", ignore=true)
    @Mapping(target = "accountType", expression = "java(AccountType.valueOf(accountDto.accountType()))")
    Account toEntity(AccountDto accountDto);
}
