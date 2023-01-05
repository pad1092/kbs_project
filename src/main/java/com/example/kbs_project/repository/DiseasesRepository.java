package com.example.kbs_project.repository;

import com.example.kbs_project.entity.Diseases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiseasesRepository extends JpaRepository<Diseases, Integer> {
}