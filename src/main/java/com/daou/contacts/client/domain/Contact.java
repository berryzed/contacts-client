package com.daou.contacts.client.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@ScriptAssert(lang = "javascript",
		script = "(_.email != null && _.email.length() > 0) || (_.tel != null && _.tel.length() > 0)",
		alias = "_", message = "이메일 혹은 전화 번호 둘 중 하나는 필수 입니다")
@Getter
@Setter
public class Contact implements Serializable {

	private String id;

	@NotBlank(message = "이름은 필수 입니다.")
	@Size(min = 1, max = 10, message = "이름은 10자리 이하로 입력해 주세요.")
	private String name;

	@Size(max = 14, message = "전화 번호는 14자리 이하로 입력해 주세요.")
	private String tel;

	@Email(message = "이메일 형식으로 입력해 주세요.")
	@Size(max = 100, message = "이메일 주소는 100자리 이하로 입력해 주세요.")
	private String email;

	@Size(max = 100, message = "메모는 100자리 이하로 입력해 주세요.")
	private String memo;

	private String userId;
}
