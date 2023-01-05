package com.example.kbs_project.repository;

import com.example.kbs_project.entity.Law;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LawRepository extends JpaRepository<Law, Integer> {
     Law getById(int id);
}