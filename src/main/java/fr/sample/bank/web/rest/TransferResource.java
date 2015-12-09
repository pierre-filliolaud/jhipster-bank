package fr.sample.bank.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.sample.bank.domain.Transfer;
import fr.sample.bank.repository.TransferRepository;
import fr.sample.bank.repository.search.TransferSearchRepository;
import fr.sample.bank.web.rest.util.HeaderUtil;
import fr.sample.bank.web.rest.util.PaginationUtil;
import fr.sample.bank.web.rest.dto.TransferDTO;
import fr.sample.bank.web.rest.mapper.TransferMapper;
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
 * REST controller for managing Transfer.
 */
@RestController
@RequestMapping("/api")
public class TransferResource {

    private final Logger log = LoggerFactory.getLogger(TransferResource.class);

    @Inject
    private TransferRepository transferRepository;

    @Inject
    private TransferMapper transferMapper;

    @Inject
    private TransferSearchRepository transferSearchRepository;

    /**
     * POST  /transfers -> Create a new transfer.
     */
    @RequestMapping(value = "/transfers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TransferDTO> createTransfer(@RequestBody TransferDTO transferDTO) throws URISyntaxException {
        log.debug("REST request to save Transfer : {}", transferDTO);
        if (transferDTO.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new transfer cannot already have an ID").body(null);
        }
        Transfer transfer = transferMapper.transferDTOToTransfer(transferDTO);
        Transfer result = transferRepository.save(transfer);
        transferSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/transfers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("transfer", result.getId().toString()))
            .body(transferMapper.transferToTransferDTO(result));
    }

    /**
     * PUT  /transfers -> Updates an existing transfer.
     */
    @RequestMapping(value = "/transfers",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TransferDTO> updateTransfer(@RequestBody TransferDTO transferDTO) throws URISyntaxException {
        log.debug("REST request to update Transfer : {}", transferDTO);
        if (transferDTO.getId() == null) {
            return createTransfer(transferDTO);
        }
        Transfer transfer = transferMapper.transferDTOToTransfer(transferDTO);
        Transfer result = transferRepository.save(transfer);
        transferSearchRepository.save(transfer);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("transfer", transferDTO.getId().toString()))
            .body(transferMapper.transferToTransferDTO(result));
    }

    /**
     * GET  /transfers -> get all the transfers.
     */
    @RequestMapping(value = "/transfers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<TransferDTO>> getAllTransfers(Pageable pageable)
        throws URISyntaxException {
        Page<Transfer> page = transferRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/transfers");
        return new ResponseEntity<>(page.getContent().stream()
            .map(transferMapper::transferToTransferDTO)
            .collect(Collectors.toCollection(LinkedList::new)), headers, HttpStatus.OK);
    }

    /**
     * GET  /transfers/:id -> get the "id" transfer.
     */
    @RequestMapping(value = "/transfers/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TransferDTO> getTransfer(@PathVariable Long id) {
        log.debug("REST request to get Transfer : {}", id);
        return Optional.ofNullable(transferRepository.findOne(id))
            .map(transferMapper::transferToTransferDTO)
            .map(transferDTO -> new ResponseEntity<>(
                transferDTO,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /transfers/:id -> delete the "id" transfer.
     */
    @RequestMapping(value = "/transfers/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTransfer(@PathVariable Long id) {
        log.debug("REST request to delete Transfer : {}", id);
        transferRepository.delete(id);
        transferSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("transfer", id.toString())).build();
    }

    /**
     * SEARCH  /_search/transfers/:query -> search for the transfer corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/transfers/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TransferDTO> searchTransfers(@PathVariable String query) {
        return StreamSupport
            .stream(transferSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(transferMapper::transferToTransferDTO)
            .collect(Collectors.toList());
    }
}
