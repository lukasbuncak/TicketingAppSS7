package nl.fontys.s7.ticketingapp.business;

import nl.fontys.s7.ticketingapp.domain.dto.*;

public interface IdentityAdminService {
    AdminUserResponse createUserAccount( CreateUserAccountRequest request);
    AdminUserResponse setUserStatus( UpdateUserStatusRequest UserStatus );
    void resendInvite(Integer userId, Integer adminActorId);
    AdminGetUser getById( Integer id);
}
