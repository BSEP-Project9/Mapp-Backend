package com.example.Mapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    private boolean isActivated;

    @ManyToOne
    private Role role;

    @OneToOne
    private Address address;

    @OneToMany
    private List<Skill> skills;

    @OneToMany(mappedBy = "worker", fetch = FetchType.LAZY)
    private List<Contribution> projects;

    @Column
    private LocalDate startOfEmployment;

}
