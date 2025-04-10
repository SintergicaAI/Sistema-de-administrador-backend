package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.*;
import com.sintergica.apiv2.repositorio.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class ResetPasswordTokensService {

    private ResetPasswordTokensRepository resetPasswordTokens;

    public ResetPasswordTokensService(ResetPasswordTokensRepository resetPasswordTokens) {
        this.resetPasswordTokens = resetPasswordTokens;
    }

    public ResetPasswordTokens getResetPasswordToken(UUID uuidTokenRes) {
        return this.resetPasswordTokens.findByToken(uuidTokenRes);
    }

    public ResetPasswordTokens createResetPasswordToken(ResetPasswordTokens resetPasswordToken) {
        return this.resetPasswordTokens.save(resetPasswordToken);
    }

    public ResetPasswordTokens findByUserAndIsUsed(User user, boolean isUsed) {
        return this.resetPasswordTokens.findByUserAndIsUsed(user, isUsed);
    }

    public ResetPasswordTokens save(ResetPasswordTokens resetPasswordToken) {
        return this.resetPasswordTokens.save(resetPasswordToken);
    }

}
