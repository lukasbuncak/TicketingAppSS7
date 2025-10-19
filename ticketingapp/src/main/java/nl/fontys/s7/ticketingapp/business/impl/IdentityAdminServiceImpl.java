package nl.fontys.s7.ticketingapp.business.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nl.fontys.s7.ticketingapp.business.IdentityAdminService;
import nl.fontys.s7.ticketingapp.config.hashing.PasswordUtil;
import nl.fontys.s7.ticketingapp.domain.dto.AdminUserResponse;
import nl.fontys.s7.ticketingapp.domain.dto.CreateUserAccountRequest;
import nl.fontys.s7.ticketingapp.domain.dto.UpdateUserStatusRequest;
import nl.fontys.s7.ticketingapp.domain.enumerations.UserStatus;
import nl.fontys.s7.ticketingapp.persistance.entities.LoginCredentialEntity;
import nl.fontys.s7.ticketingapp.persistance.entities.UserEntity;
import nl.fontys.s7.ticketingapp.persistance.repository.LoginCredentialRepository;
import nl.fontys.s7.ticketingapp.persistance.repository.UserRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IdentityAdminServiceImpl implements IdentityAdminService {
    private final UserRepository users;
    private final LoginCredentialRepository creds;
    private final Argon2PasswordEncoder argon2;

    @Override
    @Transactional
    public AdminUserResponse createUserAccount ( CreateUserAccountRequest req ) {
        if (users.existsByPersonalEmail(req.personalEmail())) {
            throw new IllegalArgumentException("personalEmail already exists");
        }


        UserEntity user = new UserEntity();
        user.setPersonalEmail(req.personalEmail());
        user.setDisplayName(req.displayName());
        user = users.save(user);


        // 1) generate a temporary password
        String tempPassword = PasswordUtil.generate();

        // 2) hash it with Argon2 (salt is generated per call; included in the hash)
        String hash = argon2.encode(tempPassword);

        // 3) Create credentials bound to same PK (MapsId)
        LoginCredentialEntity cred = new LoginCredentialEntity();
        cred.setUser(user); // @MapsId will copy user.id into user_id
        cred.setPasswordHash(hash);
        creds.save(cred);

        return new AdminUserResponse (
                user.getSchoolEmail(),
                user.getDisplayName(),
                user.getStatus().name()
        );

        //password should be hashed and generated via argon2 and not by us
        // later we will send out email to change it together with the base password. (not yet)

    }

    @Override
    public AdminUserResponse setUserStatus ( UpdateUserStatusRequest UserStatus ) {
        return null;
    }

    @Override
    public void resendInvite ( Integer userId, Integer adminActorId ) {

    }
}
