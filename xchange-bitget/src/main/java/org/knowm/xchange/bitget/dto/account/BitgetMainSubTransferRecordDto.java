package org.knowm.xchange.bitget.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.knowm.xchange.bitget.config.converter.StringToCurrencyConverter;
import org.knowm.xchange.currency.Currency;

@Data
@Builder
@Jacksonized
public class BitgetMainSubTransferRecordDto {

  @JsonProperty("clientOid")
  private String clientOid;

  @JsonProperty("transferId")
  private String transferId;

  @JsonProperty("coin")
  @JsonDeserialize(converter = StringToCurrencyConverter.class)
  private Currency currency;

  @JsonProperty("status")
  private Status status;

  @JsonProperty("toType")
  private BitgetAccountType toAccountType;

  @JsonProperty("fromType")
  private BitgetAccountType fromAccountType;

  @JsonProperty("size")
  private BigDecimal size;

  @JsonProperty("ts")
  private Instant timestamp;

  @JsonProperty("fromUserId")
  private String fromUserId;

  @JsonProperty("toUserId")
  private String toUserId;

  public static enum Status {
    @JsonProperty("Successful")
    SUCCESSFUL,

    @JsonProperty("Processing")
    PROCESSING,

    @JsonProperty("Failed")
    FAILED
  }
}