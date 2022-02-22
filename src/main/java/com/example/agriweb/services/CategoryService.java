package com.example.agriweb.services;

import com.example.agriweb.controllers.CategoryPageInfo;
import com.example.agriweb.models.Category;
import com.example.agriweb.repositories.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CategoryService {
    public static final int ROOT_CATEGORIES_PER_PAGE = 3;

    @Autowired
    private CategoryRepo categoryRepo;

    public List<Category> getAllCategories(){
      List<Category> listCategory = (List<Category>) categoryRepo.findAll();
      return listCategory;
    }

    public List<Category> listAllCategoryByPage(CategoryPageInfo categoryPageInfo, int pageNum, String sortDir
    , String keyword){
       Sort sort= Sort.by("name");

       if(sortDir.equals("asc")){
           sort = sort.ascending();
       } else if(sortDir.equals("desc")){
          sort= sort.descending();
       }
        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories = null;

       if(keyword != null && !keyword.isEmpty()){
             pageCategories =  categoryRepo.search(keyword, pageable);
        } else {

            pageCategories = categoryRepo.findRootCategory(pageable);
       }
        List<Category>  rootCategories=  pageCategories.getContent();

        categoryPageInfo.setTotalElements(pageCategories.getTotalElements());
        categoryPageInfo.setTotalPages(pageCategories.getTotalPages());

        if(keyword != null && !keyword.isEmpty()){
            List<Category>  searchResult =  pageCategories.getContent();
            for(Category category : searchResult){
                category.setHasChildren(category.getChildren().size() > 0);
            }
            return searchResult;

        } else {

            return listHierarchicalCategories(rootCategories, sortDir);
        }
    }

    private  List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir){
        List<Category> hierarchicalCategories = new ArrayList<>();
        for(Category rootCategory : rootCategories){
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);
            for(Category subCategory : children){
                String name = "--" +subCategory.getName();
                 hierarchicalCategories.add(Category.copyFull(subCategory, name));
                listSubHierarchicalCategories(hierarchicalCategories,subCategory,1, sortDir);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,Category parent, int subLevel, String sortDir){
        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int newSubLevel = subLevel+1;

        for(Category subCategory : children) {
            String name = "";
            for(int i=0; i< newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();
            hierarchicalCategories.add(Category.copyFull(subCategory, name));
            listSubHierarchicalCategories(hierarchicalCategories, subCategory,newSubLevel, sortDir);
        }

    }


    public Category saveCategory(Category category){
        //category.setAllParentIDs("-"+ String.valueOf(category.getIdCategory()) + "-");

        return categoryRepo.save(category);
    }


    public List<Category> listcategoryUsedInForm(){
        List<Category> categoriesUsedInForm = new ArrayList<>();
       Iterable<Category> categoriesInDB = categoryRepo.findRootCategory(Sort.by("name").ascending());

        for(Category category : categoriesInDB){
            if(category.getParent() == null){
             categoriesUsedInForm.add(Category.copyIdCategoryAndName(category));
                Set<Category> children = sortSubCategories(category.getChildren());

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
        Set<Category> children = sortSubCategories(parent.getChildren());

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
    private SortedSet<Category> sortSubCategories(Set<Category> children) {
         return sortSubCategories(children, "asc");
    }
        //used to sort sub Categories
    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir){
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if(sortDir.equals("asc")){
                    return cat1.getName().compareTo(cat2.getName());
                }
                else{
                    return cat2.getName().compareTo(cat1.getName());

                }
            }
        });

        sortedChildren.addAll(children);
        return sortedChildren;
    }

    public void updateCategoryEnabledStatus(Long idCategory, boolean enabled){
        categoryRepo.updateEnabledStatus(idCategory, enabled);
    }

    public void delete(Long idCategory) throws CategoryNotFoundException {
        Long countById = categoryRepo.countByIdCategory(idCategory);
        if(countById == null || countById == 0){
            throw new CategoryNotFoundException("could not find category with id:" +idCategory);

        }
        categoryRepo.deleteById(idCategory);
    }

    public List<Category> listNoChildrenCategories(){
        List<Category> listNoChildrenCategories = new ArrayList<>();
        List<Category> listEnabledCategories = categoryRepo.findAllEnabled();
       listEnabledCategories.forEach(category -> {
           Set<Category> children = category.getChildren();
           if(children == null || children.size() == 0){
              listNoChildrenCategories.add(category);
           }
       });

    return listNoChildrenCategories;
    }

    public Category getCategory(String alias) throws CategoryNotFoundException {
        Category category = categoryRepo.findByAliasEnabled(alias);

       if(category == null){
           throw new CategoryNotFoundException("Could not find category with alias: " +alias);
       }
        return category;
    }


    public List<Category> getCtegoryParents(Category child){
        List<Category> listParents = new ArrayList<>();

         Category parent = child.getParent();

         while(parent != null){
             listParents.add(0, parent);
             parent = parent.getParent();
         }

         listParents.add(child);

        return listParents;
    }
}
