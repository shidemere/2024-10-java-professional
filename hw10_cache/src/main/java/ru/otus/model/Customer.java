package ru.otus.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Table(name = "customer", schema = "public", catalog = "postgres")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "items")
@NamedEntityGraph(
        name = "customer_items",
        attributeNodes = {@NamedAttributeNode("items")})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(
            mappedBy = "customer",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
    private List<Item> items;
}
