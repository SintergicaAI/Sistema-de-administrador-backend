package com.sintergica.apiv2.controlador;

import com.sintergica.apiv2.entidades.*;
import com.sintergica.apiv2.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class Admin {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private GrantRepository grantRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addUserToGroup")
    public ResponseEntity<String> addUserToGroup(@RequestParam String email, @RequestParam String targetGroup) {
        User user = userRepository.findByEmail(email);
        Group group = groupRepository.findByName(targetGroup);

        if (user == null || group == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario o grupo no encontrado");
        }

        if (!groupRepository.existsUserInGroup(group.getId(), user.getId()) && group.getCompany().getId().equals(user.getCompany().getId())) {
            group.getUser().add(user);
            groupRepository.save(group);
            return ResponseEntity.ok("Usuario agregado al grupo");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La empresa no tiene este grupo asociado");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/deleteUserToGroup")
    public ResponseEntity<String> deleteUserToGroup(@RequestParam String email, @RequestParam String targetGroup) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Método aún no implementado");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addUserToCompany")
    public ResponseEntity<String> addUserToCompany(@RequestParam String email, @RequestParam UUID targetCompany) {
        Optional<Company> company = companyRepository.findById(targetCompany);
        User user = userRepository.findByEmail(email);

        if (user == null || company.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario o compañía no encontrados");
        }

        if (user.getCompany() == null) {
            user.setCompany(company.get());
            userRepository.save(user);
            return ResponseEntity.ok("Compañía agregada al usuario");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El usuario ya pertenece a una compañía");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listCompany")
    public ResponseEntity<List<Map<String, Object>>> listCompany() {
        List<Map<String, Object>> companies = companyRepository.findAll().stream().map(company -> {
            Map<String, Object> companyData = new LinkedHashMap<>();
            companyData.put("id", company.getId());
            companyData.put("name", company.getName());
            companyData.put("address", company.getAddress());
            companyData.put("users", company.getUserList().stream().map(User::getEmail).collect(Collectors.toList()));
            return companyData;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(companies);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllGroups")
    public ResponseEntity<List<Map<String, Object>>> getGroups() {
        List<Map<String, Object>> JSONGroup = groupRepository.findAll().stream().map(group -> {
            Map<String, Object> groupDataJSON = new LinkedHashMap<>();
            groupDataJSON.put("groupId", group.getId());
            groupDataJSON.put("groupName", group.getName());
            groupDataJSON.put("companyUUID", group.getCompany().getId());
            groupDataJSON.put("companyName", group.getCompany().getName());
            return groupDataJSON;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(JSONGroup);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/changeRolUser")
    public ResponseEntity<String> changeRolUser(@RequestParam String email, @RequestParam String newRolUser) {
        User user = userRepository.findByEmail(email);
        Rol rol = rolRepository.findByName(newRolUser);

        if (user == null || rol == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario o rol no encontrados");
        }

        user.setRol(rol);
        userRepository.save(user);
        return ResponseEntity.ok("Rol actualizado");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/seeGroupsUser")
    public ResponseEntity<List<HashMap<String, Object>>> seeGroupsUser(@RequestParam String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }

        List<HashMap<String, Object>> jsonGroupsUser = new ArrayList<>();
        Set<Group> groupUser = user.getUserGroups();

        groupUser.stream().forEach(group -> {
            HashMap<String, Object> groupDataJSON = new LinkedHashMap<>();

            groupDataJSON.put("id", group.getId());
            groupDataJSON.put("nameGroup", group.getName());
            groupDataJSON.put("nameCompany",group.getCompany().getName());

            jsonGroupsUser.add(groupDataJSON);
        });


        return ResponseEntity.ok(jsonGroupsUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addNewGroup")
    public ResponseEntity<String> addNewGroup(@RequestParam String nameGroup, @RequestParam List<String> grantList, @RequestParam UUID companyUUID) {
        Optional<Company> company = companyRepository.findById(companyUUID);
        if (company.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Compañía no encontrada");
        }

        Set<Grant> grants = grantList.stream().map(grantRepository::findByName).filter(Objects::nonNull).collect(Collectors.toSet());
        Group newGroup = new Group();
        newGroup.setName(nameGroup);
        newGroup.setCompany(company.get());
        newGroup.setGrant(grants);
        groupRepository.save(newGroup);

        return ResponseEntity.ok("Grupo creado exitosamente");
    }
}
