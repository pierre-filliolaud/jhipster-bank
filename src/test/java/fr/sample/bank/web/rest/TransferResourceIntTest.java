package fr.sample.bank.web.rest;

import fr.sample.bank.Application;
import fr.sample.bank.domain.Transfer;
import fr.sample.bank.repository.TransferRepository;
import fr.sample.bank.repository.search.TransferSearchRepository;
import fr.sample.bank.web.rest.dto.TransferDTO;
import fr.sample.bank.web.rest.mapper.TransferMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TransferResource REST controller.
 *
 * @see TransferResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TransferResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_CREATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_CREATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_CREATION_DATE_STR = dateTimeFormatter.format(DEFAULT_CREATION_DATE);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private TransferRepository transferRepository;

    @Inject
    private TransferMapper transferMapper;

    @Inject
    private TransferSearchRepository transferSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTransferMockMvc;

    private Transfer transfer;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TransferResource transferResource = new TransferResource();
        ReflectionTestUtils.setField(transferResource, "transferRepository", transferRepository);
        ReflectionTestUtils.setField(transferResource, "transferMapper", transferMapper);
        ReflectionTestUtils.setField(transferResource, "transferSearchRepository", transferSearchRepository);
        this.restTransferMockMvc = MockMvcBuilders.standaloneSetup(transferResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        transfer = new Transfer();
        transfer.setCreationDate(DEFAULT_CREATION_DATE);
        transfer.setAmount(DEFAULT_AMOUNT);
        transfer.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createTransfer() throws Exception {
        int databaseSizeBeforeCreate = transferRepository.findAll().size();

        // Create the Transfer
        TransferDTO transferDTO = transferMapper.transferToTransferDTO(transfer);

        restTransferMockMvc.perform(post("/api/transfers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(transferDTO)))
                .andExpect(status().isCreated());

        // Validate the Transfer in the database
        List<Transfer> transfers = transferRepository.findAll();
        assertThat(transfers).hasSize(databaseSizeBeforeCreate + 1);
        Transfer testTransfer = transfers.get(transfers.size() - 1);
        assertThat(testTransfer.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testTransfer.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testTransfer.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllTransfers() throws Exception {
        // Initialize the database
        transferRepository.saveAndFlush(transfer);

        // Get all the transfers
        restTransferMockMvc.perform(get("/api/transfers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(transfer.getId().intValue())))
                .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE_STR)))
                .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getTransfer() throws Exception {
        // Initialize the database
        transferRepository.saveAndFlush(transfer);

        // Get the transfer
        restTransferMockMvc.perform(get("/api/transfers/{id}", transfer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(transfer.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE_STR))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTransfer() throws Exception {
        // Get the transfer
        restTransferMockMvc.perform(get("/api/transfers/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTransfer() throws Exception {
        // Initialize the database
        transferRepository.saveAndFlush(transfer);

		int databaseSizeBeforeUpdate = transferRepository.findAll().size();

        // Update the transfer
        transfer.setCreationDate(UPDATED_CREATION_DATE);
        transfer.setAmount(UPDATED_AMOUNT);
        transfer.setDescription(UPDATED_DESCRIPTION);
        TransferDTO transferDTO = transferMapper.transferToTransferDTO(transfer);

        restTransferMockMvc.perform(put("/api/transfers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(transferDTO)))
                .andExpect(status().isOk());

        // Validate the Transfer in the database
        List<Transfer> transfers = transferRepository.findAll();
        assertThat(transfers).hasSize(databaseSizeBeforeUpdate);
        Transfer testTransfer = transfers.get(transfers.size() - 1);
        assertThat(testTransfer.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testTransfer.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testTransfer.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteTransfer() throws Exception {
        // Initialize the database
        transferRepository.saveAndFlush(transfer);

		int databaseSizeBeforeDelete = transferRepository.findAll().size();

        // Get the transfer
        restTransferMockMvc.perform(delete("/api/transfers/{id}", transfer.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Transfer> transfers = transferRepository.findAll();
        assertThat(transfers).hasSize(databaseSizeBeforeDelete - 1);
    }
}
