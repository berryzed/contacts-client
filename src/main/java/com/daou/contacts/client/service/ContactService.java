package com.daou.contacts.client.service;

import com.daou.contacts.client.domain.Contact;
import com.daou.contacts.client.domain.User;
import com.daou.contacts.client.dto.ContactResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Service
public class ContactService {

	@Value("${app.api.url}")
	private String apiUrl;

	@Autowired
	private UserService userService;

	@Autowired
	private RestTemplate restTemplate;

	public Page<Contact> findAll(Pageable pageable, HttpServletRequest req) {
		String queryString = req.getQueryString() != null ? "?" + req.getQueryString() : "";

		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/contacts" + queryString);
		log.info("url: {}", builder.build().toUri());

		ResponseEntity<ContactResponse> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(getAuthHeaders()), ContactResponse.class);

		if (response.getStatusCode().isError() || response.getBody() == null) {
			throw new RuntimeException("데이터를 가져 올 수 없습니다.");
		}

		ContactResponse contactResponse = response.getBody();

		return PageableExecutionUtils.getPage(contactResponse.getContactList(), pageable, contactResponse::getTotalElements);
	}

	public Contact findById(String id) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/contacts/{id}");
		log.info("url: {}", builder.buildAndExpand(id).toUri());

		ResponseEntity<Contact> response = restTemplate.exchange(builder.buildAndExpand(id).toUri(), HttpMethod.GET, new HttpEntity<>(getAuthHeaders()), Contact.class);

		if (response.getStatusCode().isError() || response.getBody() == null) {
			throw new RuntimeException("데이터를 가져 올 수 없습니다.");
		}

		return response.getBody();
	}

	public Contact save(Contact contact) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/contacts");
		log.info("url: {}", builder.build().toUri());

		ResponseEntity<Contact> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, new HttpEntity<>(contact, getAuthHeaders()), Contact.class);

		if (response.getStatusCode().isError() || response.getBody() == null) {
			throw new RuntimeException("데이터를 가져 올 수 없습니다.");
		}

		return response.getBody();
	}

	public Contact update(Contact contact) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/contacts/{id}");
		log.info("url: {}", builder.buildAndExpand(contact.getId()).toUri());

		ResponseEntity<Contact> response = restTemplate.exchange(builder.buildAndExpand(contact.getId()).toUri(), HttpMethod.PUT, new HttpEntity<>(contact, getAuthHeaders()), Contact.class);

		if (response.getStatusCode().isError() || response.getBody() == null) {
			throw new RuntimeException("데이터를 가져 올 수 없습니다.");
		}

		return response.getBody();
	}

	public int deleteAllByIds(List<String> ids) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/contacts");
		log.info("url: {}", builder.build().toUri());

		ResponseEntity<ContactResponse> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.DELETE, new HttpEntity<>(ids, getAuthHeaders()), ContactResponse.class);

		if (response.getStatusCode().isError() || response.getBody() == null) {
			throw new RuntimeException("데이터를 가져 올 수 없습니다.");
		}

		ContactResponse contactResponse = response.getBody();

		return (int) contactResponse.getTotalElements();
	}

	public Page<Contact> getDuplicate(Pageable pageable) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/duplicate");
		log.info("url: {}", builder.build().toUri());

		ResponseEntity<ContactResponse> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(getAuthHeaders()), ContactResponse.class);

		if (response.getStatusCode().isError() || response.getBody() == null) {
			throw new RuntimeException("데이터를 가져 올 수 없습니다.");
		}

		ContactResponse contactResponse = response.getBody();
		return PageableExecutionUtils.getPage(contactResponse.getContactList(), pageable, contactResponse::getTotalElements);
	}

	private HttpHeaders getAuthHeaders() {
		User loggedUser = userService.getLoggedUser();
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + loggedUser.getAccessToken());
		return headers;
	}
}
