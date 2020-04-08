package com.daou.contacts.client.service;

import com.daou.contacts.client.domain.CGroup;
import com.daou.contacts.client.dto.RestPageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
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
public class CGroupService {

    @Value("${app.api.url}")
    private String apiUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    // pageable을 풀어쓸지 현재 방식으로 갈지
    public Page<CGroup> findAll(Pageable pageable, HttpServletRequest req) {
        String queryString = req.getQueryString() != null ? "?" + req.getQueryString() : "";
        queryString = UriUtils.decode(queryString, StandardCharsets.UTF_8.toString()); // 인코딩이 2번 되는 경우

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/cgroups" + queryString);
        log.info("url: {}", builder.build().toUri());

        ResponseEntity<RestPageResponse<CGroup>> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(userService.getAuthHeaders()), RestPageResponse.CGroupClass);

        if (response.getStatusCode().isError() || response.getBody() == null) {
            throw new RuntimeException("데이터를 가져 올 수 없습니다.");
        }

        RestPageResponse<CGroup> restPageResponse = response.getBody();

        return new PageImpl<>(restPageResponse.getContent(), restPageResponse.getPageable(), restPageResponse.getTotalElements());
    }

    public List<CGroup> findAll() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/cgroups");
        log.info("url: {}", builder.build().toUri());

        ResponseEntity<RestPageResponse<CGroup>> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(userService.getAuthHeaders()), RestPageResponse.CGroupClass);

        if (response.getStatusCode().isError() || response.getBody() == null) {
            throw new RuntimeException("데이터를 가져 올 수 없습니다.");
        }

        RestPageResponse<CGroup> restPageResponse = response.getBody();

        return restPageResponse.getContent();
    }

    public CGroup findById(String id) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/cgroups/{id}");
        log.info("url: {}", builder.buildAndExpand(id).toUri());

        ResponseEntity<CGroup> response = restTemplate.exchange(builder.buildAndExpand(id).toUri(), HttpMethod.GET, new HttpEntity<>(userService.getAuthHeaders()), CGroup.class);

        if (response.getStatusCode().isError() || response.getBody() == null) {
            throw new RuntimeException("데이터를 가져 올 수 없습니다.");
        }

        return response.getBody();
    }

    public CGroup save(CGroup cGroup) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/cgroups");
        log.info("url: {}", builder.build().toUri());

        ResponseEntity<CGroup> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, new HttpEntity<>(cGroup, userService.getAuthHeaders()), CGroup.class);

        if (response.getStatusCode().isError() || response.getBody() == null) {
            throw new RuntimeException("데이터를 가져 올 수 없습니다.");
        }

        return response.getBody();
    }

    public CGroup update(CGroup cGroup) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/cgroups/{id}");
        log.info("url: {}", builder.buildAndExpand(cGroup.getId()).toUri());

        ResponseEntity<CGroup> response = restTemplate.exchange(builder.buildAndExpand(cGroup.getId()).toUri(), HttpMethod.PUT, new HttpEntity<>(cGroup, userService.getAuthHeaders()), CGroup.class);

        if (response.getStatusCode().isError() || response.getBody() == null) {
            throw new RuntimeException("데이터를 가져 올 수 없습니다.");
        }

        return response.getBody();
    }

    public long deleteById(String id) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + "/v1.0/cgroups/" + id);
        log.info("url: {}", builder.build().toUri());

        ResponseEntity<Map<String, Long>> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.DELETE, new HttpEntity<>(userService.getAuthHeaders()), new ParameterizedTypeReference<Map<String, Long>>() {
        });

        if (response.getStatusCode().isError() || response.getBody() == null) {
            throw new RuntimeException("데이터를 가져 올 수 없습니다.");
        }

        return response.getBody().get("count");
    }
}
