package com.example.agriweb.controllers;

import com.example.agriweb.models.Category;
import com.example.agriweb.models.CategoryDTO;
import com.example.agriweb.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class CategoryRestController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/categories/check_unique")
    public String checkUnique(@Param("idCategory") Long idCategory, @Param("name") String name,
                              @Param("alias") String alias){
        return categoryService.checkUnique(idCategory, name, alias);
    }

    @GetMapping("/productcategories")
    public List<CategoryDTO> listCategories(){
        List<CategoryDTO> categoryDTOList= new ArrayList<>();

             Set<Category> categories = (Set<Category>) categoryService.getAllCategories();
             for(Category category : categories) {
                 CategoryDTO dto = new CategoryDTO(category.getIdCategory(), category.getName());
                 categoryDTOList.add(dto);
             }

             return categoryDTOList;
    }
}
