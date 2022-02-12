package com.example.agriweb.controllers;

import com.example.agriweb.models.Category;
import com.example.agriweb.models.Product;
import com.example.agriweb.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,@RequestParam("fileImage") MultipartFile multipartFile) throws IOException {

        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            product.setMainImage(fileName);

            Product savedProduct = productService.saveProduct(product);
            String uploadDir = "../product-images/" + savedProduct.getIdProduct();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else{
            productService.saveProduct(product);

        }


       redirectAttributes.addFlashAttribute("message","The product was send successfully.");

        return "redirect:/products";
    }

    @GetMapping("/products/{idProduct}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("idProduct") Long idProduct,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes){
        productService.updateproductEnabledStatus(idProduct, enabled);
        String status = enabled? "enabled" : "disabled";
        String message = "The category with ID:" + idProduct + "has been" + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/products";
    }


    @GetMapping("/products/delete/{idProduct}")
    public String deleteCategory(@PathVariable(name = "idProduct") Long idProduct,Model model, RedirectAttributes redirectAttributes){
        try {
            productService.delete(idProduct);
            //String categoryDir = "../category-image" +idProduct;
           // FileUploadUtil.removeDir(categoryDir);

            redirectAttributes.addFlashAttribute("message", "The product with id: " + idProduct + " has been deleted successfully");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/products";
    }

}
