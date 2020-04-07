package com.daou.contacts.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum SearchType {

	NAME("이름"), TEL("전화번호"), EMAIL("이메일"), MEMO("메모"), GROUP("그룹");

	private String title;
}
