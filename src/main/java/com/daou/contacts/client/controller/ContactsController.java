package com.daou.contacts.client.controller;

import com.daou.contacts.client.ContactFilter;
import com.daou.contacts.client.SearchType;
import com.daou.contacts.client.domain.Contact;
import com.daou.contacts.client.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
public class ContactsController {

	@Autowired
	private ContactService contactService;

	@GetMapping("/")
	public String index() {
		return "redirect:/list";
	}

	@GetMapping({"/list", "/list/{id}"})
	public String getContacts(Model model, @PageableDefault(sort = "name", size = 100) Pageable pageable,
							  @PathVariable(name = "id", required = false) String id, ContactFilter filter, HttpServletRequest req) {
		prepareModel(req, model, pageable, filter, id);
		return "contacts";
	}

	@GetMapping("/duplicate")
	public String getDuplicate(Model model, @PageableDefault(sort = "name", size = 100) Pageable pageable, HttpServletRequest req) {

		return "contacts";
	}

	@PostMapping("/save")
	public String saveAndUpdateContact(Model model,
									   @PageableDefault(sort = "name", size = 100) Pageable pageable,
									   @Valid @ModelAttribute("contact") Contact contact, BindingResult result,
									   RedirectAttributes redirectAttr, HttpServletRequest req, ContactFilter filter) {

		if (!result.hasErrors()) {
			Contact saved;
			if (contact.getId() == null) {
				saved = contactService.save(contact);
			} else {
				saved = contactService.update(contact);
			}

			return "redirect:/list/" + saved.getId();
		}

		redirectAttr.addFlashAttribute("alertMessage", "주소록 추가에 실패하였습니다.");

		prepareModel(req, model, pageable, filter, null);
		return "contacts";
	}

	@PostMapping("/delete")
	public String deleteContact(@RequestParam("ids") List<String> ids, RedirectAttributes redirectAttr) {
		try {
			int cnt = contactService.deleteAllByIds(ids);
			redirectAttr.addFlashAttribute("alertMessage", String.format("%d건의 데이터를 삭제하였습니다.", cnt));
		} catch (Exception e) {
			redirectAttr.addFlashAttribute("alertMessage", "데이터 삭제에 실패하였습니다.");
		}

		return "redirect:/list";
	}

	private void prepareModel(HttpServletRequest req, Model model, Pageable pageable, ContactFilter filter, String contactId) {
		Page<Contact> contactPage = contactService.findAll(pageable, req);

		Contact contact = contactPage.stream().filter(c -> c.getId().equals(contactId)).findAny().orElse(new Contact());
		model.addAttribute("contact", contact);

		model.addAttribute("searchTypes", SearchType.values());
		model.addAttribute("filter", filter);
		model.addAttribute("contacts", contactPage);
	}
}
