package com.example.agriweb.services;

import com.example.agriweb.models.Product;
import com.example.agriweb.repositories.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
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
            String defaultAlias = product.getNameProduct().replaceAll(" ","-");
            product.setAlias(defaultAlias);
        } else{
            product.setAlias(product.getAlias().replaceAll(" ","-"));
        }

        product.setUpdatedTime(new Date());
        return productRepo.save(product);
    }

    public String checkUnique(Long idProduct, String name){
        boolean isCreatingNew = (idProduct == null || idProduct== 0);
        Product productByName =  productRepo.findByNameProduct(name);

        if(isCreatingNew){
                if(productByName != null) return "Duplicate";
            } else {
            if(productByName != null && productByName.getIdProduct() != idProduct){
                  return "Duplicate";
            }
        }
        return "OK";
    }

    public void updateproductEnabledStatus(Long idProduct, boolean enabled){
       productRepo.updateEnabledStatus(idProduct, enabled);
    }

    public void delete(Long idProduct) throws ProductNotFoundException{
        Long countById = productRepo.countByIdProduct(idProduct);
        if(countById == null || countById == 0){
            throw new ProductNotFoundException("could not find any product with Id: " +idProduct);
        }
        productRepo.deleteById(idProduct);
    }
}
