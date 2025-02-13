package com.toolScheduler.ToolSchedulerApplication.repository;

import com.toolScheduler.ToolSchedulerApplication.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    // If you need custom methods, add them here
    Optional<Tenant> findByTenantId(String tenantId);
}
