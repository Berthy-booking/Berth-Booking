package app.database.repository;

import app.database.entity.Account;
import app.database.entity.BerthApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BerthApplicationRepository extends JpaRepository<BerthApplication, Long>, JpaSpecificationExecutor<BerthApplication> {

    List<BerthApplication> findAllByApplicant(Account account);
}