package com.daou.contacts.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TokenResponse implements Serializable {
	private String accessToken;
}
