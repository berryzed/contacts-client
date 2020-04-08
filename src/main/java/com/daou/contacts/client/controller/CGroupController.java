package com.daou.contacts.client.controller;

import com.daou.contacts.client.domain.CGroup;
import com.daou.contacts.client.service.CGroupService;
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

@Slf4j
@RequestMapping("/cgroups")
@Controller
public class CGroupController {

    @Autowired
    private CGroupService cGroupService;

    @GetMapping({"/list", "/list/{id}"})
    public String getAll(Model model, @PageableDefault(sort = "name", size = 100) Pageable pageable,
                         @PathVariable(name = "id", required = false) String id, HttpServletRequest req) {

        CGroup cGroup = (id != null) ? cGroupService.findById(id) : new CGroup();
        model.addAttribute("cGroup", cGroup);

        prepareModel(req, model, pageable);
        return "cgroups";
    }

    @PostMapping("/save")
    public String saveAndUpdate(Model model,
                                @PageableDefault(sort = "name", size = 100) Pageable pageable,
                                @Valid @ModelAttribute("contact") CGroup cGroup, BindingResult result,
                                RedirectAttributes redirectAttr, HttpServletRequest req) {

        if (!result.hasErrors()) {
            CGroup saved;
            if (cGroup.getId() == null) {
                saved = cGroupService.save(cGroup);
            } else {
                saved = cGroupService.update(cGroup);
            }

            return "redirect:/cgroups/list/" + saved.getId();
        }

        redirectAttr.addFlashAttribute("alertMessage", "주소록 추가에 실패하였습니다.");

        prepareModel(req, model, pageable);
        return "cgroups";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id, RedirectAttributes redirectAttr) {
        try {
            long cnt = cGroupService.deleteById(id);
            redirectAttr.addFlashAttribute("alertMessage", String.format("%d건의 데이터를 삭제하였습니다.", cnt));
        } catch (Exception e) {
            redirectAttr.addFlashAttribute("alertMessage", "데이터 삭제에 실패하였습니다.");
        }

        return "redirect:/cgroups/list";
    }

    private void prepareModel(HttpServletRequest req, Model model, Pageable pageable) {
        Page<CGroup> cGroupPage = cGroupService.findAll(pageable, req);

        model.addAttribute("cgroups", cGroupPage);
    }
}
