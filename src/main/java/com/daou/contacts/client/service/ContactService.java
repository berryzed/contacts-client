package com.daou.contacts.client.service;

import com.daou.contacts.client.domain.Contact;
import com.daou.contacts.client.domain.User;
import com.daou.contacts.client.dto.RestPageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ContactService {

    @Value("${app.api.url}")
    private String apiUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    // pageable을 풀어쓸지 현재 방식으로 갈지
    public Page<Contact> findAll(Pageable pageable, HttpServletRequest req) {
        String queryString = req.getQueryString() != null ? "?" + req.getQueryString() : "";
        queryString = UriUtils.decode(queryString, StandardCharsets.UTF_8.toString()); // 인코딩이 2번 되는 경우

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/contacts" + queryString);
        log.info("url: {}", builder.build().toUri());

        ResponseEntity<RestPageResponse<Contact>> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(getAuthHeaders()), RestPageResponse.ContactClass);

        if (response.getStatusCode().isError() || response.getBody() == null) {
            throw new RuntimeException("데이터를 가져 올 수 없습니다.");
        }

        RestPageResponse<Contact> restPageResponse = response.getBody();

        return new PageImpl<>(restPageResponse.getContent(), restPageResponse.getPageable(), restPageResponse.getTotalElements());
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

    public long deleteAllByIds(List<String> ids) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/contacts");
        log.info("url: {}", builder.build().toUri());

        ResponseEntity<Map<String, Long>> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.DELETE, new HttpEntity<>(ids, getAuthHeaders()), new ParameterizedTypeReference<Map<String, Long>>() {
        });

        if (response.getStatusCode().isError() || response.getBody() == null) {
            throw new RuntimeException("데이터를 가져 올 수 없습니다.");
        }

        return response.getBody().get("count");
    }

    public Page<Contact> getDuplicate(Pageable pageable, HttpServletRequest req) {
        String queryString = req.getQueryString() != null ? "?" + req.getQueryString() : "";
        queryString = UriUtils.decode(queryString, StandardCharsets.UTF_8.toString()); // 인코딩이 2번 되는 경우

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/duplicates" + queryString);
        log.info("url: {}", builder.build().toUri());

        ResponseEntity<RestPageResponse<Contact>> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(getAuthHeaders()), RestPageResponse.ContactClass);

        if (response.getStatusCode().isError() || response.getBody() == null) {
            throw new RuntimeException("데이터를 가져 올 수 없습니다.");
        }

        RestPageResponse<Contact> restPageResponse = response.getBody();

        return new PageImpl<>(restPageResponse.getContent(), restPageResponse.getPageable(), restPageResponse.getTotalElements());
    }

    private HttpHeaders getAuthHeaders() {
        User loggedUser = userService.getLoggedUser();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + loggedUser.getAccessToken());
        return headers;
    }
}
