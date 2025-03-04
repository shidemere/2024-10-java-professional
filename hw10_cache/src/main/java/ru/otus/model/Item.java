package ru.otus.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Table(name = "item", schema = "public", catalog = "postgres")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "customer")
@NamedEntityGraph(
        name = "item_customer",
        attributeNodes = {@NamedAttributeNode("customer")})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
