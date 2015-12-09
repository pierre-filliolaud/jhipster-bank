package fr.sample.bank.web.rest.mapper;

import fr.sample.bank.domain.*;
import fr.sample.bank.web.rest.dto.TransferDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Transfer and its DTO TransferDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TransferMapper {

    @Mapping(source = "toBankAccount.id", target = "toBankAccountId")
    @Mapping(source = "toBankAccount.name", target = "toBankAccountName")
    @Mapping(source = "fromBankAccount.id", target = "fromBankAccountId")
    @Mapping(source = "fromBankAccount.name", target = "fromBankAccountName")
    @Mapping(source = "trade.id", target = "tradeId")
    @Mapping(source = "trade.name", target = "tradeName")
    TransferDTO transferToTransferDTO(Transfer transfer);

    @Mapping(source = "toBankAccountId", target = "toBankAccount")
    @Mapping(source = "fromBankAccountId", target = "fromBankAccount")
    @Mapping(source = "tradeId", target = "trade")
    Transfer transferDTOToTransfer(TransferDTO transferDTO);

    default BankAccount bankAccountFromId(Long id) {
        if (id == null) {
            return null;
        }
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(id);
        return bankAccount;
    }

    default Trade tradeFromId(Long id) {
        if (id == null) {
            return null;
        }
        Trade trade = new Trade();
        trade.setId(id);
        return trade;
    }
}
