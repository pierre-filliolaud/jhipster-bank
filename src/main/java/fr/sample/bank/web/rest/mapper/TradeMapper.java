package fr.sample.bank.web.rest.mapper;

import fr.sample.bank.domain.*;
import fr.sample.bank.web.rest.dto.TradeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Trade and its DTO TradeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TradeMapper {

    TradeDTO tradeToTradeDTO(Trade trade);

    @Mapping(target = "transfer", ignore = true)
    Trade tradeDTOToTrade(TradeDTO tradeDTO);
}
