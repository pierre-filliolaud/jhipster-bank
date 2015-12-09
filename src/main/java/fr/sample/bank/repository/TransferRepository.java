package fr.sample.bank.repository;

import fr.sample.bank.domain.Transfer;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Transfer entity.
 */
public interface TransferRepository extends JpaRepository<Transfer,Long> {

}
