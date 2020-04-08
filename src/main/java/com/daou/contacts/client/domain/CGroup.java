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
public class CGroup implements Serializable {

    public CGroup(Integer id) {
        this.id = id;
    }

    private Integer id;
    private String name;

    private User user;
}
