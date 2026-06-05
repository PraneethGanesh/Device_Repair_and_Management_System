package com.dms.customerservice.repository;

import com.dms.customerservice.entity.Employee;
import com.dms.customerservice.entity.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
