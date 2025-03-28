package com.sintergica.apiv2.entidades.views;

import com.sintergica.apiv2.entidades.*;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.util.*;

@Entity
@Immutable
@Table(name = "merge_view_group_company")
@Data
public class CompanyGroupsView {

    @Id
    private UUID id;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "id_company")
    private UUID idCompany;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "combined_name")
    private String combinedName;

}
