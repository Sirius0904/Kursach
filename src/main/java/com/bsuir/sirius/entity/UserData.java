package com.bsuir.sirius.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
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
    private String city;
    private String country;

    @OneToOne
    @JoinColumn(name = "wallet")
    private Wallet wallet;

    @OneToMany(mappedBy = "owner")
    private Set<Image> images = new HashSet<>();

    @OneToOne
    private ImageData profileImage;

    @OneToMany
    private Set<Image> likedImages = new HashSet<>();

    @OneToOne(mappedBy = "userData")
    private User baseUser;

    @OneToMany
    private Set<TransactionHistory> transactions = new HashSet<>();
}