package com.example.agriweb.controllers;

import com.example.agriweb.models.Category;
import com.example.agriweb.services.CategoryNotFoundException;
import com.example.agriweb.services.CategoryService;
import com.example.agriweb.services.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile
    , RedirectAttributes redirectAttributes) throws IOException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);

            Category savedCategory = categoryService.saveCategory(category);
            String uploadDir = "../category-image/" + savedCategory.getIdCategory();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            categoryService.saveCategory(category);
        }

        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully.");
         return "redirect:/categories";

    }

    @GetMapping("/categories/edit/{idCategory}")
    public String editCategory(@PathVariable(name = "idCategory") Long idCategory, Model model, RedirectAttributes ra){
        try{
            Category category = categoryService.get(idCategory);
            List<Category> listCategories = categoryService.listcategoryUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Category (ID: "+ idCategory + ")");
        return "category_form";
        }
        catch (CategoryNotFoundException ex){
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/categories";
        }
    }
}
