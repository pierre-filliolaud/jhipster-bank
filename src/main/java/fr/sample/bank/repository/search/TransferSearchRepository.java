package fr.sample.bank.repository.search;

import fr.sample.bank.domain.Transfer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Transfer entity.
 */
public interface TransferSearchRepository extends ElasticsearchRepository<Transfer, Long> {
}
