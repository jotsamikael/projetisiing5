package com.example.agriweb.services;

import com.example.agriweb.models.Product;
import com.example.agriweb.repositories.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductService {

    @Autowired private ProductRepo productRepo;

    public List<Product> listAll(){
        return (List<Product>) productRepo.findAll();
    }

    public Product saveProduct(Product product){
        if (product.getIdProduct() == null){
            product.setCretatedTime(new Date());
        }
        if (product.getAlias() == null || product.getAlias().isEmpty()){
            String defaultAlias = product.getNameProduct().replaceAll("","-");
            product.setAlias(defaultAlias);
        } else{
            product.setAlias(product.getAlias().replaceAll("","-"));
        }

        product.setUpdatedTime(new Date());
        return productRepo.save(product);
    }
}
