package com.sintergica.apiv2.dto;

import com.sintergica.apiv2.entidades.User;

import java.util.*;

public record GetGroupDTO(String group_id, String name, GetUserDTO userCreator, Date creationDate, Date editDate, Set<GetUserDTO> users) {
}
