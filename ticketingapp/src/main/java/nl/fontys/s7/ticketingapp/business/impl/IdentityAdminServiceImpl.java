package nl.fontys.s7.ticketingapp.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s7.ticketingapp.business.IdentityAdminService;
import nl.fontys.s7.ticketingapp.domain.dto.CreateUserAccountRequest;
import nl.fontys.s7.ticketingapp.domain.dto.UpdateUserStatusRequest;
import nl.fontys.s7.ticketingapp.domain.dto.UserResponse;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IdentityAdminServiceImpl implements IdentityAdminService {
    @Override
    public UserResponse createUserAccount ( CreateUserAccountRequest request ) {
        return null;
    }

    @Override
    public UserResponse setUserStatus ( UpdateUserStatusRequest UserStatus ) {
        return null;
    }

    @Override
    public void resendInvite ( Integer userId, Integer adminActorId ) {

    }
}
