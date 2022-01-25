package com.example.agriweb.repositories;

import com.example.agriweb.models.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends PagingAndSortingRepository<Category, Long> {
   @Query("SELECT c FROM Category c WHERE c.parent.idCategory is NULL")
  public List<Category> findRootCategory();

}
