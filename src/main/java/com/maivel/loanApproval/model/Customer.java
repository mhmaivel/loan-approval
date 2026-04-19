package com.maivel.loanApproval.model;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer customerId;

    @Column(name = "first_name", nullable = false,  length = 32)
    private String firstName;

    @Column(name = "last_name", nullable = false,  length = 32)
    private String lastName;

    @Column(name = "id_code", nullable = false, length = 11, unique = true)
    private String idCode;

    public static Customer create(String firstName, String lastName, String idCode) {
        Customer customer = new Customer();
        customer.firstName = firstName;
        customer.lastName = lastName;
        customer.idCode = idCode;
        return customer;
    }

}
