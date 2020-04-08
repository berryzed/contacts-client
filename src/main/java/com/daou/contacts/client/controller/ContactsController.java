package com.daou.contacts.client.controller;

import com.daou.contacts.client.ContactFilter;
import com.daou.contacts.client.DuplicateType;
import com.daou.contacts.client.SearchType;
import com.daou.contacts.client.domain.CGroup;
import com.daou.contacts.client.domain.Contact;
import com.daou.contacts.client.domain.User;
import com.daou.contacts.client.service.CGroupService;
import com.daou.contacts.client.service.ContactService;
import com.daou.contacts.client.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/contacts")
@Controller
public class ContactsController {

	@Autowired
	private UserService userService;

	@Autowired
	private ContactService contactService;

	@Autowired
	private CGroupService cGroupService;

	@GetMapping({"/list", "/list/{id}"})
	public String getAll(Model model, @PageableDefault(sort = "name", size = 100) Pageable pageable,
							  @PathVariable(name = "id", required = false) String id, ContactFilter filter, HttpServletRequest req) {

		Contact contact = (id != null) ? contactService.findById(id) : new Contact();
		model.addAttribute("contact", contact);

		prepareModel(req, model, pageable, filter);
		return "contacts";
	}

	@GetMapping("/duplicate")
	public String getDuplicate(
			Model model, @PageableDefault(sort = "name", size = 100) Pageable pageable, HttpServletRequest req) {
		Page<Contact> contactPage = contactService.getDuplicate(pageable, req);

		model.addAttribute("contacts", contactPage);
		return "duplicate";
	}

	@PostMapping("/save")
	public String saveAndUpdate(Model model,
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

			return "redirect:/contacts/list/" + saved.getId();
		}

		redirectAttr.addFlashAttribute("alertMessage", "주소록 추가에 실패하였습니다.");

		prepareModel(req, model, pageable, filter);
		return "contacts";
	}

	@PostMapping("/delete")
	public String delete(@RequestParam("ids") List<String> ids, RedirectAttributes redirectAttr) {
		try {
			long cnt = contactService.deleteAllByIds(ids);
			redirectAttr.addFlashAttribute("alertMessage", String.format("%d건의 데이터를 삭제하였습니다.", cnt));
		} catch (Exception e) {
			redirectAttr.addFlashAttribute("alertMessage", "데이터 삭제에 실패하였습니다.");
		}

		return "redirect:/contacts/list";
	}

	private void prepareModel(HttpServletRequest req, Model model, Pageable pageable, ContactFilter filter) {
		Page<Contact> contactPage = contactService.findAll(pageable, req);
		model.addAttribute("contacts", contactPage);

		List<CGroup> cGroups = cGroupService.findAll();
		if (cGroups.isEmpty()) {
			User user = userService.getLoggedUser();
			CGroup cGroup = new CGroup();
			cGroup.setName("기본그룹");
			cGroup.setUser(user);
			CGroup savedCGroup = cGroupService.save(cGroup);
			cGroups.add(savedCGroup);
		}
		model.addAttribute("cGroups", cGroups);

		model.addAttribute("searchTypes", SearchType.values());
		model.addAttribute("filter", filter);
	}
}
