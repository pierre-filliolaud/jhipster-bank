package fr.sample.bank.web.rest.dto;

import java.time.ZonedDateTime;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


/**
 * A DTO for the Transfer entity.
 */
public class TransferDTO implements Serializable {

    private Long id;

    private ZonedDateTime creationDate;

    private BigDecimal amount;

    private String description;

    private Long toBankAccountId;

    private String toBankAccountName;

    private Long fromBankAccountId;

    private String fromBankAccountName;

    private Long tradeId;

    private String tradeName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getToBankAccountId() {
        return toBankAccountId;
    }

    public void setToBankAccountId(Long bankAccountId) {
        this.toBankAccountId = bankAccountId;
    }

    public String getToBankAccountName() {
        return toBankAccountName;
    }

    public void setToBankAccountName(String bankAccountName) {
        this.toBankAccountName = bankAccountName;
    }

    public Long getFromBankAccountId() {
        return fromBankAccountId;
    }

    public void setFromBankAccountId(Long bankAccountId) {
        this.fromBankAccountId = bankAccountId;
    }

    public String getFromBankAccountName() {
        return fromBankAccountName;
    }

    public void setFromBankAccountName(String bankAccountName) {
        this.fromBankAccountName = bankAccountName;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TransferDTO transferDTO = (TransferDTO) o;

        if ( ! Objects.equals(id, transferDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TransferDTO{" +
            "id=" + id +
            ", creationDate='" + creationDate + "'" +
            ", amount='" + amount + "'" +
            ", description='" + description + "'" +
            '}';
    }
}
