package com.example.agriweb.services;


import com.example.agriweb.models.Category;
import com.example.agriweb.repositories.CategoryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@ExtendWith( MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @MockBean
    private CategoryRepo categoryRepo;
    @InjectMocks
    private CategoryService categoryService;

    @Test
    /* Test succeeds if the category name already exists in the database*/
    public void testCheckUniqueInNewModelReturnDuplicateName(){
         Long idCategory = null;
         String name = "Meat";
         String alias = "meat";

        Category category = new Category(idCategory, name, alias);

        Mockito.when(categoryRepo.findByName(name)).thenReturn(category);
        Mockito.when(categoryRepo.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(idCategory, name, alias);
       assertThat(result).isEqualTo("DuplicateName");
    }



    @Test
    /* Test succeeds if the category alias already exists in the database*/
    public void testCheckUniqueInNewModelReturnDuplicateAlias(){
        Long idCategory = null;
        String name = "Meta";
        String alias = "meat";

        Category category = new Category(idCategory, name, alias);

        Mockito.when(categoryRepo.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepo.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(idCategory, name, alias);
        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    /* Test succeeds if the category alias already exists in the database*/
    public void testCheckUniqueInNewModelReturnDuplicateOK(){
        Long idCategory = null;
        String name = "Meta";
        String alias = "meat";

        Mockito.when(categoryRepo.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepo.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(idCategory, name, alias);
        assertThat(result).isEqualTo("OK");
    }


    @Test
    /* Test succeeds if the category name already exists in the database*/
    public void testCheckUniqueInEditModelReturnDuplicateName(){
        Long idCategory = Long.valueOf(1);
        String name = "Meat";
        String alias = "meat";

        Category category = new Category((long) 2, name, alias);

        Mockito.when(categoryRepo.findByName(name)).thenReturn(category);
        Mockito.when(categoryRepo.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUnique(idCategory, name, alias);
        assertThat(result).isEqualTo("DuplicateName");
    }


    @Test
    /* Test succeeds if the category alias already exists in the database*/
    public void testCheckUniqueIneditModelReturnDuplicateAlias(){
        Long idCategory = Long.valueOf(1);
        String name = "Meta";
        String alias = "meat";

        Category category = new Category((long) 2, name, alias);

        Mockito.when(categoryRepo.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepo.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(idCategory, name, alias);
        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    /* Test succeeds if the category alias already exists in the database*/
    public void testCheckUniqueInEditModelReturnDuplicateOK(){
        Long idCategory = Long.valueOf(1);
        String name = "Meta";
        String alias = "meat";

        Category category = new Category(idCategory, name, alias);

        Mockito.when(categoryRepo.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepo.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUnique(idCategory, name, alias);
        assertThat(result).isEqualTo("OK");
    }
}
