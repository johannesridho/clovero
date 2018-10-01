package clovero.cache;

import clovero.jpa.Jpa8Repository;

import java.util.Optional;

public interface CacheRepository extends Jpa8Repository<Cache, Integer> {

    Optional<Cache> findByKey(String key);
}
