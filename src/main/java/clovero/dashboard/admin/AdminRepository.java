package clovero.dashboard.admin;

import clovero.jpa.Jpa8Repository;

import java.util.Optional;

public interface AdminRepository extends Jpa8Repository<Admin, Integer> {

    Optional<Admin> findByUsername(String username);
}
