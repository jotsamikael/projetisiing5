package com.example.agriweb;

import com.example.agriweb.models.Category;
import com.example.agriweb.repositories.CategoryRepo;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CategoryRepoTesst {
    @Autowired
    private CategoryRepo categoryRepo;

    @Test
    public void testCreateRootCategory(){
        Category category = new Category("Baies");
        Category savedCategory = categoryRepo.save(category);
         assertThat(savedCategory.getIdCategory()).isGreaterThan(0);
    }

    @Test
    public void testCreateSubCategory(){
        Category parent = new Category((long) 6);
       Category citron =  new Category("mandarine", parent);
       categoryRepo.saveAll(List.of(citron));

    }

    @Test
    public void testGetCategory(){
        Category category = categoryRepo.findById((long) 2).get();
        System.out.println(category.getName());

        Set<Category> children = category.getChildren();
        for(Category subCategory : children){
            System.out.println(subCategory.getName());

        }
        assertThat(children.size()).isGreaterThan(0);
    }

    @Test
    public void testPrintHierarchicalCategories(){
        Iterable<Category> categories = categoryRepo.findAll();
        for(Category category : categories){
            if(category.getParent() == null){
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();
                for(Category subCategory :  children){
                    System.out.println("--"+subCategory.getName());
                    printChildren(subCategory, 1);

                }
            }
        }

    }

    private void printChildren(Category parent, int subLevel){
        int newSubLevel = subLevel+1;
        Set<Category> children = parent.getChildren();

        for(Category subCategory : children){
            for(int i=0; i< newSubLevel; i++) {
                System.out.print("--");
            }
            System.out.println(subCategory.getName());
            printChildren(subCategory, newSubLevel);

        }

    }

    @Test
    public void testListRootCategory(){
        List<Category> rootCategories = categoryRepo.findRootCategory();
        rootCategories.forEach(category -> System.out.println(category.getName()));
    }
}
