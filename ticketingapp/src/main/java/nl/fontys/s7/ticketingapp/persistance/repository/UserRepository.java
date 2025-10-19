package nl.fontys.s7.ticketingapp.persistance.repository;

import nl.fontys.s7.ticketingapp.persistance.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <UserEntity, Integer> {
    boolean existsByPersonalEmail(String personalEmail);
    boolean existsBySchoolEmail(String schoolEmail);
    Optional <UserEntity> findByPersonalEmail( String personalEmail);
    Optional< UserEntity > findBySchoolEmail( String schoolEmail);
}