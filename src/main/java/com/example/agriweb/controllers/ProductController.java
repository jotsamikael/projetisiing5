package com.example.agriweb.controllers;

import com.example.agriweb.models.Category;
import com.example.agriweb.models.Product;
import com.example.agriweb.models.ProductImage;
import com.example.agriweb.models.User;
import com.example.agriweb.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ProductController {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

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
        Integer numberOfExistingExtraImages= product.getImages().size();
        model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);



        model.addAttribute("product", product);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "create New Product");

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,@RequestParam("fileImage") MultipartFile mainImageMultipartFile,
                              @RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "imageIDs", required = false) String imageIDs[],
                              @RequestParam(name = "imageNames", required = false) String imageNames[]) throws IOException {

        setMainImageName(mainImageMultipartFile, product);
        setExistingExtraImageNames(imageIDs,imageNames, product);
        setNewExtraImageNames(extraImageMultiparts, product);

            Product savedProduct = productService.saveProduct(product);

            saveUploadedImages(mainImageMultipartFile, extraImageMultiparts, savedProduct);

            deleteExtraWereRemovedOnForm(product);

            redirectAttributes.addFlashAttribute("message","The product was send successfully.");

            return "redirect:/products";
    }

    private void deleteExtraWereRemovedOnForm(Product product) throws IOException {
        String extraImageDir = "../product-images/" + product.getIdProduct() +"/extras";
        Path dirpath = Paths.get(extraImageDir);

        try{
            Files.list(dirpath).forEach(file->{
                String filename = file.toFile().getName();
                if(!product.containsImageName(filename)){
                    try {
                        Files.delete(file);
                        LOGGER.info("Deleted extra image:"+filename);
                    }
                    catch (IOException e){
                      LOGGER.error("Could not delete extra image: " +filename);
                    }
                }
            });
        } catch (IOException e){
            LOGGER.error("Could not list directory: " +dirpath);

        }

    }

    private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if(imageIDs == null || imageIDs.length == 0) return;

        Set<ProductImage> images = new HashSet<ProductImage>();

        for(int count = 0; count< imageIDs.length; count++){
            Integer id = Integer.parseInt(imageIDs[count]);

            String name = imageNames[count];
            images.add(new ProductImage((long)id,name,product));
        }
        product.setImages(images);
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

    private void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
     if(extraImageMultiparts.length > 0 ){
          for(MultipartFile multipartFile : extraImageMultiparts ){
              if(!multipartFile.isEmpty()){
                  String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());


                  if(!product.containsImageName(fileName))
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
            Integer numberOfExistingExtraImages= product.getImages().size();


            model.addAttribute("product", product);
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

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


    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
                                     Model model){

        return viewCategoryByPage(alias,1,model);
    }



    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias,
                               @PathVariable("pageNum") int pageNum,
                               Model model){

          Category category = categoryService.getCategory(alias);
       if(category == null){
           return "error/404";
       }
          List<Category> listCategoryParents =  categoryService.getCtegoryParents(category);
                 Page<Product> pageProducts   =    productService.listByCategory(pageNum, category.getIdCategory());

                List<Product> listProducts = pageProducts.getContent();


        long startCount = (pageNum - 1) * productService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount +  productService.PRODUCTS_PER_PAGE - 1;
        if(endCount > pageProducts.getTotalElements()){
            endCount = pageProducts.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageProducts.getTotalElements());



        model.addAttribute("pageTitle", category.getName());
        model.addAttribute("listCategoryParents", listCategoryParents);
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("category", category);


        return "products_by_category";
    }


}
