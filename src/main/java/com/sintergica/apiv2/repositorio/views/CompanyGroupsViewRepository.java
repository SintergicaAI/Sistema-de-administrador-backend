package com.sintergica.apiv2.repositorio.views;

import com.sintergica.apiv2.entidades.views.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface CompanyGroupsViewRepository extends JpaRepository<CompanyGroupsView, Long> {
    CompanyGroupsView findByIdCompanyAndCombinedName(UUID idCompany, String combinedName);
    List<CompanyGroupsView> findByIdCompanyAndCombinedNameIn(UUID idCompany, Collection<String> names);
}
