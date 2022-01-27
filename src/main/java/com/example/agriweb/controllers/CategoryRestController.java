package com.example.agriweb.controllers;

import com.example.agriweb.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/categories/check_unique")
    public String checkUnique(@Param("idCategory") Long idCategory, @Param("name") String name,
                              @Param("alias") String alias){
        return categoryService.checkUnique(idCategory, name, alias);
    }
}
