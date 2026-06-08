package com.example.customer_service.repository;

import com.example.customer_service.entity.Employee;
import com.example.customer_service.entity.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    List<Employee> findByCompanyId(UUID companyId);

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Employee> findByCompanyIdAndInviteStatus(UUID companyId, InviteStatus inviteStatus);

    @Query("select count(employee) > 0 from Employee employee where employee.id = :employeeId and employee.company.id = :companyId")
    boolean existsByIdAndCompanyId(@Param("employeeId") UUID employeeId, @Param("companyId") UUID companyId);
}
