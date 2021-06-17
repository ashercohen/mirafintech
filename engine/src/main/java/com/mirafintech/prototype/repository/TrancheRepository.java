package com.mirafintech.prototype.repository;

import com.mirafintech.prototype.model.tranche.Tranche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TrancheRepository extends JpaRepository<Tranche, Long> {
}
