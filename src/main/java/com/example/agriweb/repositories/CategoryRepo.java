package com.example.agriweb.repositories;

import com.example.agriweb.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends PagingAndSortingRepository<Category, Long> {
   @Query("SELECT c FROM Category c WHERE c.parent.idCategory is NULL")
  public List<Category> findRootCategory(Sort sort);

    @Query("SELECT c FROM Category c WHERE c.parent.idCategory is NULL")
    public Page<Category> findRootCategory(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
    public Page<Category> search(String keyword,Pageable pageable);

   public Long countByIdCategory(Long idCategory);
   public Category findByName(String name);

   public Category findByAlias(String alias);

   @Query("UPDATE Category  c SET c.enabled = ?2 WHERE  c.idCategory = ?1")
   @Modifying
    public void updateEnabledStatus(Long idCategory, boolean enabled);

   @Query("SELECT c FROM Category c WHERE c.enabled = true ORDER BY c.name ASC ")
   public List<Category> findAllEnabled();


   @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.alias = ?1 ")
   public Category findByAliasEnabled(String alias);

}
