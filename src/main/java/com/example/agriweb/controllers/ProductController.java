package com.example.agriweb.controllers;

import com.example.agriweb.models.Category;
import com.example.agriweb.models.Product;
import com.example.agriweb.services.CategoryService;
import com.example.agriweb.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
  private  ProductService productService;

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/products")
    public String ListAllProducts(Model model){
       List<Product> listProducts =  productService.listAll();
       model.addAttribute("listProducts", listProducts);
        return "products/products";

    }

    @GetMapping("/products/new")
    public String newProduct(Model model){
        List<Category> listCategories = categoryService.listcategoryUsedInForm();
        Product product = new Product();
        product.setEnabled(true);
        product.setInstock(false);

        model.addAttribute("product", product);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "create New Product");

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes){
       productService.saveProduct(product);
       redirectAttributes.addFlashAttribute("message","The product was send successfully.");

        return "redirect:/products";
    }
}
