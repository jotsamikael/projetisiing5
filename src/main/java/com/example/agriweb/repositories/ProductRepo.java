package com.example.agriweb.repositories;

import com.example.agriweb.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends PagingAndSortingRepository<Product, Long> {
  public  Product findByNameProduct(String name);


    @Query("UPDATE Product  p SET p.enabled = ?2 WHERE  p.idProduct = ?1")
    @Modifying
    public void updateEnabledStatus(Long idProduct, boolean enabled);

    public Long countByIdProduct(Long idProduct);

    @Query("SELECT p FROM Product p WHERE p.nameProduct LIKE %?1%" + "OR p.shortDescription LIKE %?1%"
            + "OR p.fullDescription LIKE %?1%"
            + "OR p.category.name LIKE %?1%")
    public Page<Product> findAll(String keyword, Pageable pageable);


    @Query("SELECT p FROM Product p  WHERE p.enabled = true " +
            "AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%)"
    + " ORDER BY p.nameProduct ASC")
    public Page<Product> listByCategory(Long idCategory, String categoryIDMatch, Pageable pageable);


    public Product findByAlias(String alias);

    @Query(value = "SELECT * FROM products WHERE enabled = true AND "
           +"MATCH (name_product, short_description, full_description) AGAINST (?1)" , nativeQuery = true)
    public Page<Product> search(String keyword, Pageable pageable);
}
