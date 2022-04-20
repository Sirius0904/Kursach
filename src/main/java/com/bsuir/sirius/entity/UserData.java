package com.bsuir.sirius.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USER_DATA")
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private BigDecimal balance;
    @OneToMany
    private Set<Image> images = new HashSet<>();
    @OneToMany(mappedBy = "owner")
    private Set<ImageCollection> collections = new HashSet<>();
    @OneToMany
    private Set<TransactionHistory> transactions = new HashSet<>();
}
