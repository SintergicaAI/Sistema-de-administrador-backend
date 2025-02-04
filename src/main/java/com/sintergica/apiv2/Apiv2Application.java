package com.sintergica.apiv2;

import com.sintergica.apiv2.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class Apiv2Application implements CommandLineRunner {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private GrantRepository grantRepository;

    public static void main(String[] args) {
        SpringApplication.run(Apiv2Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        /*
        Company company = new Company();
        company.setName("Almacenes denimblue");
        company.setAddress("Av almacenes");

        Group group = new Group();
        group.setName("administradores del almacenes");
        group.setCompany(company);

        companyRepository.save(company);
        groupRepository.save(group);

        companyRepository.findByName("Almacenes den").getGroupList().stream().forEach(e->{
            System.out.println(e.getName());
        });*/

        /*Grant grant1 = new Grant();
        grant1.setName("ALLOW FILE UPLOAD");
        grantRepository.save(grant1);

        Grant grant2 = new Grant();
        grant2.setName("ALLOW CHAT DELETE");
        grantRepository.save(grant2);

        Grant grant3 = new Grant();
        grant3.setName("ALLOW CHAT EDIT");
        grantRepository.save(grant3);

        Grant grant4 = new Grant();
        grant4.setName("ALLOW TEMPORARY CHAT");
        grantRepository.save(grant4);

        Rol rol1 = new Rol();
        rol1.setName("ADMIN");
        Rol rol2 = new Rol();
        rol2.setName("USER");
        Rol rol3 = new Rol();
        rol3.setName("GUEST");
        rolRepository.save(rol1);
        rolRepository.save(rol2);
        rolRepository.save(rol3);

        Company company1 = new Company();
        Company company2 = new Company();
        Company company3 = new Company();
        Company company4 = new Company();
        company1.setName("Almacenenes denimblue");
        company1.setAddress("Av ejemplo1");
        company2.setName("AeroMéxico");
        company2.setAddress("Av ejemplo2");
        company3.setName("Restaurante bienvenido");
        company3.setAddress("Av ejemplo3");
        company4.setName("Otra empresa");
        company4.setAddress("Av ejemplo4");
        companyRepository.save(company1);
        companyRepository.save(company2);
        companyRepository.save(company3);
        companyRepository.save(company4);

        User user1 = new User();
        user1.setName("Javier Alberto");
        user1.setEmail("javieralberto@gmail.com");
        user1.setPassword(passwordEncoder.encode("123456"));
        user1.setRol(rol1);
        user1.setFirstName("Palacios");
        user1.setLastName("López");
        user1.setCompany(company1);
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Juan");
        user2.setEmail("juan@gmail.com");
        user2.setPassword(passwordEncoder.encode("123456"));
        user2.setRol(rol2);
        user2.setFirstName("Gomez");
        user2.setLastName("Gomez");
        user2.setCompany(company1);
        userRepository.save(user2);

        User user3 = new User();
        user3.setName("Simon");
        user3.setEmail("Simon@gmail.com");
        user3.setPassword(passwordEncoder.encode("123456"));
        user3.setRol(rol2);
        user3.setFirstName("Blanco");
        user3.setLastName("Negrete");
        user3.setCompany(company2);
        userRepository.save(user3);

        User user4 = new User();
        user4.setName("Maria");
        user4.setEmail("maria@gmail.com");
        user4.setPassword(passwordEncoder.encode("123456"));
        user4.setRol(rol2);
        user4.setFirstName("carbajal");
        user4.setLastName("carbajal");
        user4.setCompany(company2);
        userRepository.save(user4);

        User user5 = new User();
        user5.setName("Daniela");
        user5.setEmail("Daniela@gmail.com");
        user5.setPassword(passwordEncoder.encode("123456"));
        user5.setRol(rol2);
        user5.setFirstName("Leon");
        user5.setLastName("Leon");
        user5.setCompany(company3);

        userRepository.save(user5);

        User user6 = new User();
        user6.setName("Minerva");
        user6.setEmail("minerva@gmail.com");
        user6.setPassword(passwordEncoder.encode("123456"));
        user6.setRol(rol2);
        user6.setFirstName("Perez");
        user6.setLastName("Perez");
        user6.setCompany(company4);
        userRepository.save(user6);

        Group group1 = new Group();
        group1.setName("Administrador almacenes");
        Set<Grant> listGrant1 = new HashSet<>();
        listGrant1.add(grant1);
        listGrant1.add(grant2);
        listGrant1.add(grant3);
        listGrant1.add(grant4);

        group1.setGrant(listGrant1);
        group1.setCompany(company1);

        Set<User> userSet1 = new HashSet<>();
        userSet1.add(user1);
        userSet1.add(user2);
        group1.setUser(userSet1);
        groupRepository.save(group1);

        Group group2 = new Group();
        group2.setName("empleados almacenes");
        Set<Grant> listGrant2 = new HashSet<>();
        listGrant2.add(grant1);
        listGrant2.add(grant2);
        group2.setGrant(listGrant2);
        group2.setCompany(company1);
        group2.setUser(userSet1);

        groupRepository.save(group2);
        */

    }

}
