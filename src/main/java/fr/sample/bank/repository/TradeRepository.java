package fr.sample.bank.repository;

import fr.sample.bank.domain.Trade;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Trade entity.
 */
public interface TradeRepository extends JpaRepository<Trade,Long> {

}
