package com.toolScheduler.ToolSchedulerApplication.repository;

import com.toolScheduler.ToolSchedulerApplication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, User.CredentialId> {
    User findByOwnerAndRepo(String owner, String repo);
}