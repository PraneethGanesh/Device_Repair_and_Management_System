package com.example.user_service.repository;

import com.example.user_service.entity.Employee;
import com.example.user_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Employee> findByDepartment(String department);

    List<Employee> findByRole(Role role);
}
