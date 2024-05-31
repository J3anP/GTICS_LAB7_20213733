package org.example.lab7gticsniupay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="userid")
    private int userId;

    private String name;

    private String type;

    @Column(name="authorizedresource")
    private String authorizedResource;

    private boolean active;
}
