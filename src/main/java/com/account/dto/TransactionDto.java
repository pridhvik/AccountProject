package com.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
	
	private String description;

	private String accountNumber;

	private Boolean isCredited;

	private Double transactionAmount;

	private Boolean isActive;

	private Integer accountId;

}
