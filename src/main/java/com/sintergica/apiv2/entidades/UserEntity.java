package com.sintergica.apiv2.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class UserEntity {
  @Id
  @Size(max = 255)
  @NotNull
  @Column(name = "id", nullable = false)
  private String id;

  @Size(max = 255)
  @NotNull
  @Column(name = "name", nullable = false)
  private String name;

  @Size(max = 255)
  @NotNull
  @Column(name = "email", nullable = false)
  private String email;

  @Size(max = 255)
  @NotNull
  @Column(name = "role", nullable = false)
  private String role;

  @NotNull
  @Column(name = "profile_image_url", nullable = false, length = Integer.MAX_VALUE)
  private String profileImageUrl;

  @Size(max = 255)
  @Column(name = "api_key")
  private String apiKey;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private Long createdAt;

  @NotNull
  @Column(name = "updated_at", nullable = false)
  private Long updatedAt;

  @NotNull
  @Column(name = "last_active_at", nullable = false)
  private Long lastActiveAt;

  @Column(name = "settings", length = Integer.MAX_VALUE)
  private String settings;

  @Column(name = "info", length = Integer.MAX_VALUE)
  private String info;

  @Column(name = "oauth_sub", length = Integer.MAX_VALUE)
  private String oauthSub;
}
