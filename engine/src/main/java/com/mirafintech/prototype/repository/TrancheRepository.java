package com.mirafintech.prototype.repository;

import com.mirafintech.prototype.model.Tranche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TrancheRepository extends JpaRepository<Tranche, Long> {
}
