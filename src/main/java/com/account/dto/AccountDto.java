package com.account.dto;

import java.time.LocalDate;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

	@Nullable
	private Integer accId;
	
	@NotNull
	private String firstName;

	private String lastName;

	@Min(18)
	private Integer age;

	private LocalDate dob;

	private String email;

	private Long phone;

	private String password;
	
}
