package com.daou.contacts.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactFilter {

	private SearchType fSearchType;
	private String fKeyword = "";
}
