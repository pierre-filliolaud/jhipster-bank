package fr.sample.bank.repository;

import fr.sample.bank.domain.BankAccount;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the BankAccount entity.
 */
public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {

    @Query("select bankAccount from BankAccount bankAccount where bankAccount.owner.login = ?#{principal.username}")
    List<BankAccount> findByOwnerIsCurrentUser();

}
