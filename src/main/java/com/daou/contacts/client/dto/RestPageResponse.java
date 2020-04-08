package com.daou.contacts.client.dto;

import com.daou.contacts.client.domain.CGroup;
import com.daou.contacts.client.domain.Contact;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestPageResponse<T> implements Serializable {

    public static final ParameterizedTypeReference<RestPageResponse<Contact>> ContactClass = new ParameterizedTypeReference<RestPageResponse<Contact>>() {
    };

    public static final ParameterizedTypeReference<RestPageResponse<CGroup>> CGroupClass = new ParameterizedTypeReference<RestPageResponse<CGroup>>() {
    };

    private List<T> content;
    private Long totalElements;
    private int size;
    private int number;

    private List<Order> orders;

    public Pageable getPageable() {
        return PageRequest.of(this.number, this.size, Sort.by(this.orders.stream().map(Order::toSortOrder).collect(Collectors.toList())));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Order {

        private Sort.Direction direction;
        private String property;
        private Sort.NullHandling nullHandling;

        public Sort.Order toSortOrder() {
            return new Sort.Order(this.direction, this.property, this.nullHandling);
        }
    }
}
