package ru.otus.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
@NamedEntityGraph(
        name = "phones_address_entity_graph",
        attributeNodes = {
            @NamedAttributeNode("address"),
            @NamedAttributeNode("phones"),
        })
public class Client {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Phone> phones;

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
