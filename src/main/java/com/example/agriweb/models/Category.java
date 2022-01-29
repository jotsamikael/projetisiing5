package com.example.agriweb.models;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategory;

    @Column(length = 128, nullable = false, unique = true)
    private String name;

    @Column(length = 64, nullable = false, unique = true)
    private String alias;

    @Column(length = 128, nullable = false)
    private String image;

    private boolean enabled;


    @OneToOne
    @JoinColumn(name= "parent_id")
    private Category parent;

    @OneToMany(mappedBy ="parent" )
    private Set<Category> children = new HashSet<>();

    public Category() {
    }

    public static Category copyIdCategoryAndName(Category category) {

        Category copyCategory = new Category();
        copyCategory.setIdCategory(category.getIdCategory());
        copyCategory.setName(category.getName());
        return copyCategory;
    }


    public static Category copyIdCategoryAndName(Long idCategory, String name) {

        Category copyCategory = new Category();
        copyCategory.setIdCategory(idCategory);
        copyCategory.setName(name);
        return copyCategory;
    }

    public static Category copyFull(Category category){
        Category copyCategory = new Category();
        copyCategory.setIdCategory(category.getIdCategory());
        copyCategory.setName(category.getName());
        copyCategory.setImage(category.getImage());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setEnabled(category.isEnabled());
        copyCategory.setHasChildren(category.getChildren().size() > 0);

        return copyCategory;
    }

    public static Category copyFull(Category category, String name) {
        Category copyCategory = Category.copyFull(category);
        copyCategory.setName(name);
        return copyCategory;
    }


    public Category(Long idCategory) {
        this.idCategory = idCategory;
    }

    public Category(String name) {
        this.name = name;
        this.alias = name;
        this.image = "default.png";

    }

    public Category(String name, Category parent) {
        this(name);
        this.parent = parent;
    }

    public Category(Long idCategory, String name, String alias) {
        super();
        this.idCategory = idCategory;
        this.name = name;
        this.alias = alias;
    }

    public Long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Long idCategory) {
        this.idCategory = idCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }

    @Transient
    public String getImagePath(){
        if(this.idCategory == null) return  "/img/image-preview.jpg";
         return "/category-image/" +this.idCategory + "/" +this.image;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    @Transient
    private boolean hasChildren;
}
