package com.example.agriweb.services;

import com.example.agriweb.models.Category;

public class CategoryNotFoundException extends Exception {

      public CategoryNotFoundException(String message){
            super(message);

      }
}
