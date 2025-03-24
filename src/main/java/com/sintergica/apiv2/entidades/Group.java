package com.sintergica.apiv2.entidades;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(
    name = "listGroup",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "id_company"})})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "name")
  private String name;

  @ManyToOne
  @JoinColumn(name = "id_company")
  private Company company;

  @ManyToOne
  @JoinColumn(name = "creator")
  private User userCreator;

  @Column(name = "creationDate")
  private Date creationDate;

  @Column(name = "editDate")
  private Date editDate;

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
