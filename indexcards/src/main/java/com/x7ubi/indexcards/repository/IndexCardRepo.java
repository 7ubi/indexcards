package com.x7ubi.indexcards.repository;

import com.x7ubi.indexcards.models.IndexCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexCardRepo extends JpaRepository<IndexCard, Long> {
}
