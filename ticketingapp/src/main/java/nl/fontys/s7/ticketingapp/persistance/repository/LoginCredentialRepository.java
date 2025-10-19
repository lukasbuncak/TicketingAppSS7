package nl.fontys.s7.ticketingapp.persistance.repository;

import nl.fontys.s7.ticketingapp.persistance.entities.LoginCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginCredentialRepository extends JpaRepository <LoginCredentialEntity, Integer> {
    // Load hash by user id (primary key)
    Optional < LoginCredentialEntity > findById( Integer userId);

    // Optional convenience alias
    Optional<LoginCredentialEntity> findByUserId(Integer userId);
}