package org.koreait.reommend.repositories;

import org.koreait.reommend.entities.Types;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypeRepository extends JpaRepository<Types, Long> {
}
