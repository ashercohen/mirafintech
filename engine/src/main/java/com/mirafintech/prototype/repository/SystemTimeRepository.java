package com.mirafintech.prototype.repository;

import com.mirafintech.prototype.model.SystemTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SystemTimeRepository extends JpaRepository<SystemTime, Long> {

    /**
     * get latest update made to SystemTime table (based on entity id which is auto-incremented)
     */
    SystemTime findTopByOrderByIdDesc();
}
