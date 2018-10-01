package clovero.user;

import clovero.jpa.Jpa8Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Jpa8Repository<User, Integer> {

    Optional<User> findByLineId(String lineId);
    List<User> findTop10ByOrderByPointDesc();
}
