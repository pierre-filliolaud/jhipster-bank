package fr.sample.bank.web.rest.mapper;

import fr.sample.bank.domain.*;
import fr.sample.bank.web.rest.dto.BankAccountDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity BankAccount and its DTO BankAccountDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BankAccountMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.login", target = "ownerLogin")
    BankAccountDTO bankAccountToBankAccountDTO(BankAccount bankAccount);

    @Mapping(target = "incomingTransferss", ignore = true)
    @Mapping(target = "outgoingTransfers", ignore = true)
    @Mapping(source = "ownerId", target = "owner")
    BankAccount bankAccountDTOToBankAccount(BankAccountDTO bankAccountDTO);

    default User userFromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
