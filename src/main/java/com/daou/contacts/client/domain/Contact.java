package com.daou.contacts.client.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact implements Serializable {

    public Contact(Integer id) {
        this.id = id;
    }

    private Integer id;
    private String name;
    private String tel;
    private String email;
    private String memo;

    private CGroup cGroup;
}
