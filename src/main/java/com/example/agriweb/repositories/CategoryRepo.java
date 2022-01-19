package com.example.agriweb.repositories;

import com.example.agriweb.models.Category;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends PagingAndSortingRepository<Category, Long> {
}
