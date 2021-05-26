package com.mirafintech.prototype.tests.association;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

public class UnidiManyToMany {

/**
 * Uni-directional ManyToMany
 * - a third table is used to hold the association
 *
 * based on: https://thorben-janssen.com/ultimate-guide-association-mappings-jpa-hibernate/#Unidirectional_Many-to-Many_Associations
 */

//@Entity
class Store {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany
    @JoinTable(name = "store_product",
            joinColumns = { @JoinColumn(name = "store_id") },
            inverseJoinColumns = { @JoinColumn(name = "product_id") })
    private Set<Product> products = new HashSet<Product>();

}

//@Entity
class Product {

    @Id
    @GeneratedValue
    private Long id;
}
}
