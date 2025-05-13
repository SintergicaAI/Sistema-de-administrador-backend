package com.sintergica.apiv2.entidades;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "group_knowledge")
@Data
public class GroupKnowledge {
    @ManyToOne
    @JoinColumn(name = "groupId")
    private Group group;

    @Column(name = "knowledgeId")
    private UUID knowledgeId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int knowledgeKey;
}