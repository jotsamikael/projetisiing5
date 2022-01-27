package com.example.agriweb.services;

import com.example.agriweb.models.Category;
import com.example.agriweb.repositories.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;


    public List<Category> listAllCategory(){
      List<Category> rootCategories =  categoryRepo.findRootCategory();
    return listHierarchicalCategories(rootCategories);
    }

    private  List<Category> listHierarchicalCategories(List<Category> rootCategories){
        List<Category> hierarchicalCategories = new ArrayList<>();
        for(Category rootCategory : rootCategories){
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = rootCategory.getChildren();
            for(Category subCategory : children){
                String name = "--" +subCategory.getName();
                 hierarchicalCategories.add(Category.copyFull(subCategory, name));
                listSubHierarchicalCategories(hierarchicalCategories,subCategory,1);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,Category parent, int subLevel){
        Set<Category> children = parent.getChildren();
        int newSubLevel = subLevel+1;

        for(Category subCategory : children) {
            String name = "";
            for(int i=0; i< newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            hierarchicalCategories.add(Category.copyFull(subCategory, name));
            listSubHierarchicalCategories(hierarchicalCategories, subCategory,newSubLevel);
        }

    }


    public Category saveCategory(Category category){
        return categoryRepo.save(category);
    }


    public List<Category> listcategoryUsedInForm(){
        List<Category> categoriesUsedInForm = new ArrayList<>();
       Iterable<Category> categoriesInDB = categoryRepo.findAll();

        for(Category category : categoriesInDB){
            if(category.getParent() == null){
             categoriesUsedInForm.add(Category.copyIdCategoryAndName(category));
                Set<Category> children = category.getChildren();

                for(Category subCategory :  children){
                    String name = "--" +subCategory.getName();
                    categoriesUsedInForm.add(Category.copyIdCategoryAndName(subCategory.getIdCategory(), name));
                    listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);

                }
            }
        }


        return  categoriesUsedInForm;
    }

    private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm,Category parent, int subLevel){
        int newSubLevel = subLevel+1;
        Set<Category> children = parent.getChildren();

        for(Category subCategory : children){
            String name = "";
            for(int i=0; i< newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdCategoryAndName(subCategory.getIdCategory(), name));
            listSubCategoriesUsedInForm(categoriesUsedInForm,subCategory, newSubLevel);

        }

    }

    public Category get(Long idCategory) throws CategoryNotFoundException{
        try{
            return categoryRepo.findById(idCategory).get();
        } catch (NoSuchElementException ex) {
            throw new CategoryNotFoundException("Could not find any category with id:" +idCategory);
        }
    }

    public String checkUnique(Long idCategory, String name, String alias){
        boolean isCreatingNew = (idCategory == null || idCategory == 0);
        Category categoryByName =  categoryRepo.findByName(name);
          if(isCreatingNew){
              if(categoryByName != null){
                  return "DuplicateName";
              } else {
                  Category categoryByAlias = categoryRepo.findByAlias(alias);
                  if(categoryByAlias != null) {
                     return  "DuplicateAlias";
                  }
              }
          } else {
              if(categoryByName != null && categoryByName.getIdCategory() != idCategory){
                  return "DuplicateName";
              }
              Category categoryByAlias = categoryRepo.findByAlias(alias);
               if(categoryByAlias != null && categoryByAlias.getIdCategory() != idCategory){
                   return "DuplicateAlias";
               }
          }

        return  "OK";

    }
}
