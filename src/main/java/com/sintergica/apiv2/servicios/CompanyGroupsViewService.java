package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.views.*;
import com.sintergica.apiv2.repositorio.views.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class CompanyGroupsViewService {

    private final CompanyGroupsViewRepository companyGroupsViewRepository;

    public CompanyGroupsViewService(CompanyGroupsViewRepository companyGroupsViewRepository) {
        this.companyGroupsViewRepository = companyGroupsViewRepository;
    }

    public CompanyGroupsView findByIdCompanyAndCombinedName(UUID uuidUserLogged, String combinedName) {
        return companyGroupsViewRepository.findByIdCompanyAndCombinedName(uuidUserLogged, combinedName);
    }

    public List<CompanyGroupsView> findByIdCompanyAndCombinedNameIn(UUID uuidUserLogged, Collection<String> combinedNames) {
        System.out.println(this.companyGroupsViewRepository.findAll().toString());
        System.out.println(uuidUserLogged);
        System.out.println(combinedNames.toString());
        return this.companyGroupsViewRepository.findByIdCompanyAndCombinedNameIn(uuidUserLogged, combinedNames);
    }

}
