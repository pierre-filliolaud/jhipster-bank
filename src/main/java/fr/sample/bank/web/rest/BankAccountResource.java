package fr.sample.bank.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.sample.bank.domain.BankAccount;
import fr.sample.bank.repository.BankAccountRepository;
import fr.sample.bank.repository.search.BankAccountSearchRepository;
import fr.sample.bank.web.rest.util.HeaderUtil;
import fr.sample.bank.web.rest.util.PaginationUtil;
import fr.sample.bank.web.rest.dto.BankAccountDTO;
import fr.sample.bank.web.rest.mapper.BankAccountMapper;
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
 * REST controller for managing BankAccount.
 */
@RestController
@RequestMapping("/api")
public class BankAccountResource {

    private final Logger log = LoggerFactory.getLogger(BankAccountResource.class);

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private BankAccountMapper bankAccountMapper;

    @Inject
    private BankAccountSearchRepository bankAccountSearchRepository;

    /**
     * POST  /bankAccounts -> Create a new bankAccount.
     */
    @RequestMapping(value = "/bankAccounts",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BankAccountDTO> createBankAccount(@RequestBody BankAccountDTO bankAccountDTO) throws URISyntaxException {
        log.debug("REST request to save BankAccount : {}", bankAccountDTO);
        if (bankAccountDTO.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new bankAccount cannot already have an ID").body(null);
        }
        BankAccount bankAccount = bankAccountMapper.bankAccountDTOToBankAccount(bankAccountDTO);
        BankAccount result = bankAccountRepository.save(bankAccount);
        bankAccountSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/bankAccounts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("bankAccount", result.getId().toString()))
            .body(bankAccountMapper.bankAccountToBankAccountDTO(result));
    }

    /**
     * PUT  /bankAccounts -> Updates an existing bankAccount.
     */
    @RequestMapping(value = "/bankAccounts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BankAccountDTO> updateBankAccount(@RequestBody BankAccountDTO bankAccountDTO) throws URISyntaxException {
        log.debug("REST request to update BankAccount : {}", bankAccountDTO);
        if (bankAccountDTO.getId() == null) {
            return createBankAccount(bankAccountDTO);
        }
        BankAccount bankAccount = bankAccountMapper.bankAccountDTOToBankAccount(bankAccountDTO);
        BankAccount result = bankAccountRepository.save(bankAccount);
        bankAccountSearchRepository.save(bankAccount);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("bankAccount", bankAccountDTO.getId().toString()))
            .body(bankAccountMapper.bankAccountToBankAccountDTO(result));
    }

    /**
     * GET  /bankAccounts -> get all the bankAccounts.
     */
    @RequestMapping(value = "/bankAccounts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<BankAccountDTO>> getAllBankAccounts(Pageable pageable)
        throws URISyntaxException {
        Page<BankAccount> page = bankAccountRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bankAccounts");
        return new ResponseEntity<>(page.getContent().stream()
            .map(bankAccountMapper::bankAccountToBankAccountDTO)
            .collect(Collectors.toCollection(LinkedList::new)), headers, HttpStatus.OK);
    }

    /**
     * GET  /bankAccounts/:id -> get the "id" bankAccount.
     */
    @RequestMapping(value = "/bankAccounts/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BankAccountDTO> getBankAccount(@PathVariable Long id) {
        log.debug("REST request to get BankAccount : {}", id);
        return Optional.ofNullable(bankAccountRepository.findOne(id))
            .map(bankAccountMapper::bankAccountToBankAccountDTO)
            .map(bankAccountDTO -> new ResponseEntity<>(
                bankAccountDTO,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /bankAccounts/:id -> delete the "id" bankAccount.
     */
    @RequestMapping(value = "/bankAccounts/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBankAccount(@PathVariable Long id) {
        log.debug("REST request to delete BankAccount : {}", id);
        bankAccountRepository.delete(id);
        bankAccountSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("bankAccount", id.toString())).build();
    }

    /**
     * SEARCH  /_search/bankAccounts/:query -> search for the bankAccount corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/bankAccounts/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<BankAccountDTO> searchBankAccounts(@PathVariable String query) {
        return StreamSupport
            .stream(bankAccountSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(bankAccountMapper::bankAccountToBankAccountDTO)
            .collect(Collectors.toList());
    }
}
