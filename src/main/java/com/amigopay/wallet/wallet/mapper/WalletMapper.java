package com.amigopay.wallet.wallet.mapper;

import com.amigopay.wallet.wallet.dto.WalletResponse;
import com.amigopay.wallet.wallet.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(source = "id", target = "walletId")
    WalletResponse toResponse(Wallet entity);
}