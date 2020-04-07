package com.daou.contacts.client.dto;

import com.daou.contacts.client.domain.Contact;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ContactResponse {

	private List<Contact> contactList;
	private long totalElements;
}
