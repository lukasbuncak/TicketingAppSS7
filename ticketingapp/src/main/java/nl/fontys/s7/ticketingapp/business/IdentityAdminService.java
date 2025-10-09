package nl.fontys.s7.ticketingapp.business;

import nl.fontys.s7.ticketingapp.domain.dto.CreateUserAccountRequest;
import nl.fontys.s7.ticketingapp.domain.dto.UpdateUserStatusRequest;
import nl.fontys.s7.ticketingapp.domain.dto.UserResponse;

public interface IdentityAdminService {
    UserResponse createUserAccount( CreateUserAccountRequest request);
    UserResponse setUserStatus( UpdateUserStatusRequest UserStatus );
    void resendInvite(Integer userId, Integer adminActorId);
}
