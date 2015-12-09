package fr.sample.bank.web.rest;

import fr.sample.bank.Application;
import fr.sample.bank.domain.Trade;
import fr.sample.bank.repository.TradeRepository;
import fr.sample.bank.repository.search.TradeSearchRepository;
import fr.sample.bank.web.rest.dto.TradeDTO;
import fr.sample.bank.web.rest.mapper.TradeMapper;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TradeResource REST controller.
 *
 * @see TradeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TradeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private TradeRepository tradeRepository;

    @Inject
    private TradeMapper tradeMapper;

    @Inject
    private TradeSearchRepository tradeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTradeMockMvc;

    private Trade trade;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TradeResource tradeResource = new TradeResource();
        ReflectionTestUtils.setField(tradeResource, "tradeRepository", tradeRepository);
        ReflectionTestUtils.setField(tradeResource, "tradeMapper", tradeMapper);
        ReflectionTestUtils.setField(tradeResource, "tradeSearchRepository", tradeSearchRepository);
        this.restTradeMockMvc = MockMvcBuilders.standaloneSetup(tradeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        trade = new Trade();
        trade.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createTrade() throws Exception {
        int databaseSizeBeforeCreate = tradeRepository.findAll().size();

        // Create the Trade
        TradeDTO tradeDTO = tradeMapper.tradeToTradeDTO(trade);

        restTradeMockMvc.perform(post("/api/trades")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tradeDTO)))
                .andExpect(status().isCreated());

        // Validate the Trade in the database
        List<Trade> trades = tradeRepository.findAll();
        assertThat(trades).hasSize(databaseSizeBeforeCreate + 1);
        Trade testTrade = trades.get(trades.size() - 1);
        assertThat(testTrade.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void getAllTrades() throws Exception {
        // Initialize the database
        tradeRepository.saveAndFlush(trade);

        // Get all the trades
        restTradeMockMvc.perform(get("/api/trades"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(trade.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getTrade() throws Exception {
        // Initialize the database
        tradeRepository.saveAndFlush(trade);

        // Get the trade
        restTradeMockMvc.perform(get("/api/trades/{id}", trade.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(trade.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTrade() throws Exception {
        // Get the trade
        restTradeMockMvc.perform(get("/api/trades/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTrade() throws Exception {
        // Initialize the database
        tradeRepository.saveAndFlush(trade);

		int databaseSizeBeforeUpdate = tradeRepository.findAll().size();

        // Update the trade
        trade.setName(UPDATED_NAME);
        TradeDTO tradeDTO = tradeMapper.tradeToTradeDTO(trade);

        restTradeMockMvc.perform(put("/api/trades")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tradeDTO)))
                .andExpect(status().isOk());

        // Validate the Trade in the database
        List<Trade> trades = tradeRepository.findAll();
        assertThat(trades).hasSize(databaseSizeBeforeUpdate);
        Trade testTrade = trades.get(trades.size() - 1);
        assertThat(testTrade.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteTrade() throws Exception {
        // Initialize the database
        tradeRepository.saveAndFlush(trade);

		int databaseSizeBeforeDelete = tradeRepository.findAll().size();

        // Get the trade
        restTradeMockMvc.perform(delete("/api/trades/{id}", trade.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Trade> trades = tradeRepository.findAll();
        assertThat(trades).hasSize(databaseSizeBeforeDelete - 1);
    }
}
