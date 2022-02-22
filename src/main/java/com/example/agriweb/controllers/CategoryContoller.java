package com.example.agriweb.controllers;

import com.example.agriweb.models.Category;
import com.example.agriweb.services.CategoryNotFoundException;
import com.example.agriweb.services.CategoryService;
import com.example.agriweb.services.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
    public String listFirst(@Param("sortDir") String sortDir,Model model){

        return listByPage(1,sortDir, null, model);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             @Param("sortDir") String sortDir, @Param("keyword") String keyword,Model model){
        if(sortDir == null || sortDir.isEmpty()){
            sortDir ="asc";

        }
        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> categoryList = categoryService.listAllCategoryByPage(pageInfo, pageNum, sortDir, keyword);

        long startCount = (pageNum - 1) * categoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount +  categoryService.ROOT_CATEGORIES_PER_PAGE - 1;
        if(endCount > pageInfo.getTotalElements()){
            endCount = pageInfo.getTotalElements();
        }


        String reverseSortDir = sortDir.equals("asc") ? "desc": "asc";

        model.addAttribute("totalPages",pageInfo.getTotalPages());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);



        model.addAttribute("listCategories",categoryList);
        model.addAttribute("reverseSortDir",reverseSortDir);
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


            //category.setAllParentIDs("-"+ String.valueOf(category.getIdCategory()) + "-");


            Category savedCategory = categoryService.saveCategory(category);
            //category.setAllParentIDs("-"+ String.valueOf(savedCategory.getIdCategory()) + "-");

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

    @GetMapping("/categories/{idCategory}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("idCategory") Long idCategory,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes){
        categoryService.updateCategoryEnabledStatus(idCategory, enabled);
        String status = enabled? "enabled" : "disabled";
        String message = "The category with ID:" + idCategory + "has been" + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{idCategory}")
    public String deleteCategory(@PathVariable(name = "idCategory") Long idCategory,Model model, RedirectAttributes redirectAttributes){
        try {
            categoryService.delete(idCategory);
            String categoryDir = "../category-image" +idCategory;
            FileUploadUtil.removeDir(categoryDir);

            redirectAttributes.addFlashAttribute("message", "The category with id:" + idCategory + "has been deleted successfully");
        } catch (CategoryNotFoundException e) {
           redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/categories";
    }
}
