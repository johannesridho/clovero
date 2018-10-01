package clovero.category;

import clovero.jpa.Jpa8Repository;

import java.util.Optional;

public interface CategoryRepository extends Jpa8Repository<Category, Integer> {
    Optional<Category> findByName(String name);
}
