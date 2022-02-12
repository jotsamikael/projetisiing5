package com.example.agriweb;


import com.example.agriweb.models.Category;
import com.example.agriweb.models.Product;
import com.example.agriweb.repositories.ProductRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ProductRepoTest {

    @Autowired
    private ProductRepo  productRepo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void CreateProduct(){
        Category category = entityManager.find(Category.class, (long) 14);
        Product product = new Product();
        product.setNameProduct("cabbage");
        product.setShortDescription(" fresh cabbage ");
        product.setFullDescription("This is excellent for stews or entries eaten fresh");

        product.setCategory(category);
        product.setCost(2000);
        product.setAlias("cabbage");
        product.setCretatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepo.save(product);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getIdProduct()).isGreaterThan(0);

    }

    @Test
    public void testListAllProducts(){
        Iterable<Product> productIterable = productRepo.findAll();
    productIterable.forEach(System.out :: println);
    }

    @Test
    public void testGetProduct(){
        Long id = (long) 2;
        Optional<Product> product = productRepo.findById(id);
        System.out.println(product);

        assertThat(product).isNotNull();
    }

    @Test
    public void testUpdateProduct(){
           Long id = (long) 2;
       Product product =    productRepo.findById(id).get();
       product.setCost(3500);
       productRepo.save(product);

        Product updatedProduct = entityManager.find(Product.class, id);
         assertThat(updatedProduct.getCost()).isEqualTo(3500);

    }

    @Test
    public void testDeleteById(){
        Long id = (long) 3;
       productRepo.deleteById(id);
     Optional<Product> result = productRepo.findById(id);

       assertThat(!result.isPresent());


    }

    @Test
    public void testSaveProductWithImages(){
        Long idProduct = (long) 8;
        Product product = productRepo.findById(idProduct).get();

        product.setMainImage("main image.jpg");
        product.addExtraImage("extra1.png");
        product.addExtraImage("extra2.png");
        product.addExtraImage("extra3.png");

       Product savedProduct = productRepo.save(product);
       assertThat(savedProduct.getImages().size()).isEqualTo(3);


    }
}
