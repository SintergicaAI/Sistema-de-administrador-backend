package com.sintergica.apiv2;

import com.sintergica.apiv2.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Apiv2Application implements CommandLineRunner {

  @Autowired private CompanyRepository companyRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private RolRepository rolRepository;
  @Autowired private GrantRepository grantRepository;
  @Autowired private PackageRepository packageRepository;
  @Autowired private UserSubscriptionRepository userSubscriptionRepository;
  @Autowired private CompanySubscriptionRepository companySubscriptionRepository;

  public static void main(String[] args) {
    SpringApplication.run(Apiv2Application.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

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
    company1.setName("Almacenes denmiblue");
    company1.setRFC("RFCALMACEN");
    company1.setAddress("Av ejemplo #333");
    companyRepository.save(company1);

    Company company2 = new Company();
    company2.setName("Aero México");
    company2.setRFC("Aero3423123");
    company2.setAddress("Av ejemplo #444");
    companyRepository.save(company2);

    Company company3 = new Company();
    company3.setName("Administrador de ejemplo");
    company3.setRFC("Administrador de ejemplo");
    company3.setAddress("Av ejemplo #555");
    companyRepository.save(company3);

    User user1 = new User();
    user1.setName("Alice");
    user1.setPassword(passwordEncoder.encode("123456"));
    user1.setEmail("alice@gmail.com");
    user1.setLastName("James");
    user1.setRol(rol2);
    user1.setCompany(company1);
    userRepository.save(user1);

    User user2 = new User();
    user2.setName("Bob");
    user2.setPassword(passwordEncoder.encode("123456"));
    user2.setEmail("bob@gmail.com");
    user2.setLastName("Bob");
    user2.setRol(rol1);
    user2.setCompany(company1);
    userRepository.save(user2);

    User user3 = new User();
    user3.setName("Carlos");
    user3.setPassword(passwordEncoder.encode("123456"));
    user3.setEmail("carlos@gmail.com");
    user3.setLastName("Carlos");
    user3.setRol(rol2);
    user3.setCompany(company1);
    userRepository.save(user3);

    User user4 = new User();
    user4.setName("David");
    user4.setPassword(passwordEncoder.encode("123456"));
    user4.setEmail("david@gmail.com");
    user4.setLastName("David");
    user4.setRol(rol2);
    user4.setCompany(company2); //PERTENECE A AEROMEXICO NECESITA UN GRUPO DE AEROMEXICO
    userRepository.save(user4);

    User user5 = new User();
    user5.setName("John");
    user5.setPassword(passwordEncoder.encode("123456"));
    user5.setEmail("john@gmail.com");
    user5.setLastName("John");
    user5.setRol(rol1);
    user5.setCompany(company2);
    userRepository.save(user5);

    User user6 = new User();
    user6.setName("Jane");
    user6.setPassword(passwordEncoder.encode("123456"));
    user6.setEmail("jane@gmail.com");
    user6.setLastName("Jane");
    user6.setRol(rol2);
    user6.setCompany(company2);
    userRepository.save(user6);

    User user7 = new User();
    user7.setName("Jack");
    user7.setPassword(passwordEncoder.encode("123456"));
    user7.setEmail("jack@gmail.com");
    user7.setLastName("Jack");
    user7.setRol(rol1);
    user7.setCompany(company3);
    userRepository.save(user7);

    User user8 = new User();
    user8.setName("Joe");
    user8.setPassword(passwordEncoder.encode("123456"));
    user8.setEmail("joe@gmail.com");
    user8.setLastName("Joe");
    user8.setRol(rol2);
    user8.setCompany(company3);
    userRepository.save(user8);

    Group group1 = new Group();
    group1.setName("Almacen denmiblue administrador");
    group1.setCompany(company1);
    group1.setGrant(new HashSet<>(){{
            add(grant1);
            add(grant2);
            add(grant3);
            add(grant4);
    }});
    group1.setUser(new HashSet<>(){{
        add(user1);
    }});
    groupRepository.save(group1);

    Group group2 = new Group();
    group2.setName("Almacen denmiblue user");
    group2.setCompany(company1);
    group2.setGrant(new HashSet<>(){{
        add(grant1);
        add(grant2);
    }});

    group2.setUser(new HashSet<>(){{
        add(user1);
        add(user2);
        add(user3);
    }});
    groupRepository.save(group2);

    Group group3 = new Group();
    group3.setName("Almacen denmiblue guest");
    group3.setCompany(company1);
    group3.setGrant(new HashSet<>(){{
        add(grant1);
        add(grant2);
    }});
    groupRepository.save(group3);

    Group group4 = new Group();
    group4.setName("Aeromexico administrador");
    group4.setCompany(company2);
    group4.setGrant(new HashSet<>(){{
        add(grant1);
        add(grant2);
        add(grant3);
        add(grant4);
    }});

    group4.setUser(new HashSet<>(){{
        add(user4);
        add(user5);
        add(user6);
    }});

    groupRepository.save(group4);

    Package pack1 = new Package();
    pack1.setNombre("PAQUETE 1");
    pack1.setPrice(23.40);
    pack1.setUsers(10);
    pack1.setQuerys("10");
    pack1.setTokens("10");
    pack1.setKnowledge("10");
    pack1.setModels("aa");
    pack1.setAssistants("a");
    packageRepository.save(pack1);

    Package pack2 = new Package();
    pack2.setNombre("PAQUETE 2");
    pack2.setPrice(23.40);
    pack2.setUsers(10);
    pack2.setQuerys("10");
    pack2.setTokens("10");
    pack2.setKnowledge("10");
    pack2.setModels("aa");
    pack2.setAssistants("a");
    packageRepository.save(pack2);

    Package pack3 = new Package();
    pack3.setNombre("PAQUETE 3");
    pack3.setPrice(23.40);
    pack3.setUsers(10);
    pack3.setQuerys("10");
    pack3.setTokens("10");
    pack3.setKnowledge("10");
    pack3.setModels("aa");
    pack3.setAssistants("a");
    packageRepository.save(pack3);

    UserSubscription userSubscription = new UserSubscription();
    userSubscription.setPackageId(pack1);
    userSubscription.setSubscriptionId(user7);
    userSubscription.setStartDate(new Date());
    userSubscriptionRepository.save(userSubscription);

    UserSubscription userSubscription2 = new UserSubscription();
    userSubscription2.setPackageId(pack2);
    userSubscription2.setSubscriptionId(user8);
    userSubscription2.setStartDate(new Date());
    userSubscriptionRepository.save(userSubscription2);

    CompanySubscription companySubscription = new CompanySubscription();
    companySubscription.setPackageId(pack3);
    companySubscription.setSubscriptionId(company1);
    companySubscription.setStartDate(new Date());
    companySubscriptionRepository.save(companySubscription);*/

  }
}
