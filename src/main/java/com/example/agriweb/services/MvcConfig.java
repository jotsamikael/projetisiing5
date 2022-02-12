package com.example.agriweb.services;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "user-photos";
        Path userPhotosDir = Paths.get(dirName);
        String userPhotoPath = userPhotosDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/" +dirName+ "/**").addResourceLocations("file:"+ userPhotoPath+ "/");

        String categoryImagesDirName = "../category-image";
        Path categoryImagesDir = Paths.get(categoryImagesDirName);
        String CategoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/category-image/**").addResourceLocations("file:"+ CategoryImagesPath+ "/");


        String productImagesDirName = "../product-images";
        Path productImagesDir = Paths.get(productImagesDirName);
        String ProductImagesPath = productImagesDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/product-images/**").addResourceLocations("file:"+ ProductImagesPath+ "/");

    }
}
