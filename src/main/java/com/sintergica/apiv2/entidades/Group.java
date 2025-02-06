package com.sintergica.apiv2.entidades;

import jakarta.persistence.*;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "listGroup")
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO) //
  private UUID id;

  @Column(name = "name")
  private String name;

  @ManyToOne
  @JoinColumn(name = "id_company")
  private Company company;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "group_grants",
      joinColumns = @JoinColumn(name = "group_id"),
      inverseJoinColumns = @JoinColumn(name = "grant_id"))
  private Set<Grant> grant;

  @ManyToMany
  @JoinTable(
      name = "user_group",
      joinColumns = @JoinColumn(name = "group_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<User> user;
}
