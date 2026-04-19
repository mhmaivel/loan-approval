package com.maivel.loanApproval.repository;

import com.maivel.loanApproval.model.SystemParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SystemParameterRepository extends JpaRepository<SystemParameter, Integer> {

    Optional<SystemParameter> findByParameterKey(String parameterKey);
}
