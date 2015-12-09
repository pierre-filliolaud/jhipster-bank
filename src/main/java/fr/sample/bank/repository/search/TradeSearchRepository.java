package fr.sample.bank.repository.search;

import fr.sample.bank.domain.Trade;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Trade entity.
 */
public interface TradeSearchRepository extends ElasticsearchRepository<Trade, Long> {
}
