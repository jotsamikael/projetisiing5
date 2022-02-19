package com.example.agriweb.controllers;

import com.example.agriweb.models.Category;
import com.example.agriweb.models.Product;
import com.example.agriweb.models.User;
import com.example.agriweb.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
    public String ListFirstPage(Model model){
        return listByPage(1,model, "nameProduct","asc", null);
      /* List<Product> listProducts =  productService.listAll();
       model.addAttribute("listProducts", listProducts);
        return "products/products";*/

    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String  sortDir,
                             @Param("keyword") String keyword
    ){

        System.out.println("sort field:"+ sortField);
        System.out.println("sort order:"+ sortDir);

        Page<Product> page = productService.listByPage(pageNum, sortField, sortDir,keyword);
        List<Product> listProducts = page.getContent();
        long startCount = (pageNum - 1) * productService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount +  productService.PRODUCTS_PER_PAGE - 1;
        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }
        String reverseSortDir = sortDir.equals("asc") ? "desc": "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listProducts);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);


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
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,@RequestParam("fileImage") MultipartFile mainImageMultipartFile,
                              @RequestParam("extraImage") MultipartFile[] extraImageMultiparts) throws IOException {

        setMainImageName(mainImageMultipartFile, product);
        setExtraImageNames(extraImageMultiparts, product);

            Product savedProduct = productService.saveProduct(product);

            saveUploadedImages(mainImageMultipartFile, extraImageMultiparts, savedProduct);

            redirectAttributes.addFlashAttribute("message","The product was send successfully.");

            return "redirect:/products";
    }

    private void saveUploadedImages(MultipartFile mainImageMultipartFile, MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {

        if(!mainImageMultipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());

            String uploadDir = "../product-images/" + savedProduct.getIdProduct();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipartFile);
        }

        if(extraImageMultiparts.length > 0 ){
            String uploadDir = "../product-images/" + savedProduct.getIdProduct() + "/extras";

            for(MultipartFile multipartFile : extraImageMultiparts ) {
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            }
        }


    }

    private void setExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
     if(extraImageMultiparts.length > 0 ){
          for(MultipartFile multipartFile : extraImageMultiparts ){
              if(!multipartFile.isEmpty()){
                  String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                   product.addExtraImage(fileName);
              }
          }
     }
    }

    private void setMainImageName(MultipartFile mainImageMultipartFile, Product product){

        if(!mainImageMultipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
            product.setMainImage(fileName);
        }

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
            String productExtraImagesDir = "../product-images/" + idProduct + "/extras";
            String productImagesDir = "../product-images/" + idProduct;


            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImagesDir);



            //String categoryDir = "../category-image" +idProduct;
           // FileUploadUtil.removeDir(categoryDir);

            redirectAttributes.addFlashAttribute("message", "The product with id: " + idProduct + " has been deleted successfully");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/products/edit/{idProduct}")
    public String editProduct(@PathVariable("idProduct") Long idProduct, Model model, RedirectAttributes redirectAttributes){
        try{
            Product product = productService.get(idProduct);
            List<Category> listCategories = categoryService.listcategoryUsedInForm();

            model.addAttribute("product", product);
            model.addAttribute("listCategories", listCategories);

            model.addAttribute("pageTitle", "Edit Product with id: " +idProduct);

            return "products/product_form";

        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect/products";
        }

    }


    @GetMapping("/products/detail/{idProduct}")
    public String viewProductDetails(@PathVariable("idProduct") Long idProduct, Model model, RedirectAttributes redirectAttributes){
        try{
            Product product = productService.get(idProduct);
            List<Category> listCategories = categoryService.listcategoryUsedInForm();

            model.addAttribute("product", product);
            model.addAttribute("listCategories", listCategories);

        //    model.addAttribute("pageTitle", "Edit Product with id: " +idProduct);

            return "products/product_detail_modal";

        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect/products";
        }

    }



}
