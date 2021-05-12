package com.example.demo.repo;

import com.example.demo.modem.Human;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HumanRepo extends JpaRepository<Human, Long> {

}
