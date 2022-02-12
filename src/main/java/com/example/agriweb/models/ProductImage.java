package com.example.agriweb.models;


import javax.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProductImage;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    public ProductImage() {
    }

    public ProductImage(String name, Product product) {
        this.name = name;
        this.product = product;
    }

    public Long getIdProductImage() {
        return idProductImage;
    }

    public void setIdProductImage(Long idProductImage) {
        this.idProductImage = idProductImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
