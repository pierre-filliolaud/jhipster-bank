package fr.sample.bank.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.sample.bank.domain.Trade;
import fr.sample.bank.repository.TradeRepository;
import fr.sample.bank.repository.search.TradeSearchRepository;
import fr.sample.bank.web.rest.util.HeaderUtil;
import fr.sample.bank.web.rest.util.PaginationUtil;
import fr.sample.bank.web.rest.dto.TradeDTO;
import fr.sample.bank.web.rest.mapper.TradeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Trade.
 */
@RestController
@RequestMapping("/api")
public class TradeResource {

    private final Logger log = LoggerFactory.getLogger(TradeResource.class);

    @Inject
    private TradeRepository tradeRepository;

    @Inject
    private TradeMapper tradeMapper;

    @Inject
    private TradeSearchRepository tradeSearchRepository;

    /**
     * POST  /trades -> Create a new trade.
     */
    @RequestMapping(value = "/trades",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TradeDTO> createTrade(@RequestBody TradeDTO tradeDTO) throws URISyntaxException {
        log.debug("REST request to save Trade : {}", tradeDTO);
        if (tradeDTO.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new trade cannot already have an ID").body(null);
        }
        Trade trade = tradeMapper.tradeDTOToTrade(tradeDTO);
        Trade result = tradeRepository.save(trade);
        tradeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/trades/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("trade", result.getId().toString()))
            .body(tradeMapper.tradeToTradeDTO(result));
    }

    /**
     * PUT  /trades -> Updates an existing trade.
     */
    @RequestMapping(value = "/trades",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TradeDTO> updateTrade(@RequestBody TradeDTO tradeDTO) throws URISyntaxException {
        log.debug("REST request to update Trade : {}", tradeDTO);
        if (tradeDTO.getId() == null) {
            return createTrade(tradeDTO);
        }
        Trade trade = tradeMapper.tradeDTOToTrade(tradeDTO);
        Trade result = tradeRepository.save(trade);
        tradeSearchRepository.save(trade);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("trade", tradeDTO.getId().toString()))
            .body(tradeMapper.tradeToTradeDTO(result));
    }

    /**
     * GET  /trades -> get all the trades.
     */
    @RequestMapping(value = "/trades",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<TradeDTO>> getAllTrades(Pageable pageable, @RequestParam(required = false) String filter)
        throws URISyntaxException {
        if ("transfer-is-null".equals(filter)) {
            log.debug("REST request to get all Trades where transfer is null");
            return new ResponseEntity<>(StreamSupport
                .stream(tradeRepository.findAll().spliterator(), false)
                .filter(trade -> trade.getTransfer() == null)
                .map(trade -> tradeMapper.tradeToTradeDTO(trade))
                .collect(Collectors.toList()), HttpStatus.OK);
        }
        
        Page<Trade> page = tradeRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/trades");
        return new ResponseEntity<>(page.getContent().stream()
            .map(tradeMapper::tradeToTradeDTO)
            .collect(Collectors.toCollection(LinkedList::new)), headers, HttpStatus.OK);
    }

    /**
     * GET  /trades/:id -> get the "id" trade.
     */
    @RequestMapping(value = "/trades/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TradeDTO> getTrade(@PathVariable Long id) {
        log.debug("REST request to get Trade : {}", id);
        return Optional.ofNullable(tradeRepository.findOne(id))
            .map(tradeMapper::tradeToTradeDTO)
            .map(tradeDTO -> new ResponseEntity<>(
                tradeDTO,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /trades/:id -> delete the "id" trade.
     */
    @RequestMapping(value = "/trades/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        log.debug("REST request to delete Trade : {}", id);
        tradeRepository.delete(id);
        tradeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("trade", id.toString())).build();
    }

    /**
     * SEARCH  /_search/trades/:query -> search for the trade corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/trades/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TradeDTO> searchTrades(@PathVariable String query) {
        return StreamSupport
            .stream(tradeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(tradeMapper::tradeToTradeDTO)
            .collect(Collectors.toList());
    }
}
