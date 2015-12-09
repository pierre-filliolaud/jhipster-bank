package fr.sample.bank.repository.search;

import fr.sample.bank.domain.BankAccount;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the BankAccount entity.
 */
public interface BankAccountSearchRepository extends ElasticsearchRepository<BankAccount, Long> {
}
