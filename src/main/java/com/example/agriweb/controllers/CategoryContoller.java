package com.example.agriweb.controllers;

import com.example.agriweb.models.Category;
import com.example.agriweb.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CategoryContoller {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listAll(Model model){
        List<Category> categoryList = categoryService.listAllCategory();
        model.addAttribute("listCategories",categoryList);

        return "categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model){
        List<Category> listCategories =categoryService.listcategoryUsedInForm();
        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", listCategories);

        model.addAttribute("pageTitle", "Create new category");

        return "category_form";
    }
}
