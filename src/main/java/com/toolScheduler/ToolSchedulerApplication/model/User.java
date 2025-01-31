package com.toolScheduler.ToolSchedulerApplication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(User.CredentialId.class)
public class User {

    @Id
    @Column(nullable = false)
    private String owner;

    @Id
    @Column(nullable = false)
    private String repo;

    @Column(nullable = false)
    private String pat;

    public User() {
    }

    public User(String owner, String repo, String pat) {
        this.owner = owner;
        this.repo = repo;
        this.pat = pat;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getPat() {
        return pat;
    }

    public void setPat(String pat) {
        this.pat = pat;
    }

    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(repo, that.repo) &&
                Objects.equals(pat, that.pat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, repo, pat);
    }

    @Override
    public String toString() {
        return "Credential{" +
                "owner='" + owner + '\'' +
                ", repo='" + repo + '\'' +
                ", pat='" + pat + '\'' +
                '}';
    }

    /**
     * Composite primary key class for Credential.
     */
    public static class CredentialId implements Serializable {

        private String owner;
        private String repo;

        public CredentialId() {
        }

        public CredentialId(String owner, String repo) {
            this.owner = owner;
            this.repo = repo;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getRepo() {
            return repo;
        }

        public void setRepo(String repo) {
            this.repo = repo;
        }

        // equals, hashCode, and toString
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CredentialId)) return false;
            CredentialId that = (CredentialId) o;
            return Objects.equals(owner, that.owner) &&
                    Objects.equals(repo, that.repo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(owner, repo);
        }

        @Override
        public String toString() {
            return "CredentialId{" +
                    "owner='" + owner + '\'' +
                    ", repo='" + repo + '\'' +
                    '}';
        }
    }
}