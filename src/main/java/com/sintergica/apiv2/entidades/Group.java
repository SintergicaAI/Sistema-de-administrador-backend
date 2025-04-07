package com.sintergica.apiv2.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Date;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  /*@Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;*/

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

  @Id
  @Column(name = "composite_key")
  private String compositeKey;

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
