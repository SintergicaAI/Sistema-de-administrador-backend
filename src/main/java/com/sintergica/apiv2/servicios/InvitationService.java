package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.Invitation;
import com.sintergica.apiv2.entidades.User;
import com.sintergica.apiv2.repositorio.InvitationRepository;
import com.sintergica.apiv2.utilidades.InvitationTokenUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;

    public InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    /**
     * @param email The email associated with the token
     * @param signInToken The token sent to the user
     */
    public Pair<Boolean,String> validateInvitation(String email, UUID signInToken){
        Optional<Invitation> invitation = invitationRepository.findById(signInToken);

        if (invitation.isEmpty()) {
            return new Pair<>(false, "Invalid SignIn Token");
        }

        Pair<Boolean,String> validateToken =
                InvitationTokenUtils.validateToken(invitation.get(), email);

        if (!validateToken.a) {
            return new Pair<>(false, validateToken.b);
        }

        invitation.get().setActive(false);
        invitationRepository.save(invitation.get());
        return new Pair<>(true, validateToken.b);
    }

}
