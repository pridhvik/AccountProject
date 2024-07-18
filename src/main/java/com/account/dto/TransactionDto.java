package com.account.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

	@Nullable
	private String transactionId;

	private String description;

	private Boolean isCredited;

	private Double transactionAmount;

	private String accountNumber;

	@Nullable
	private String userName;

}
