package com.mirafintech.prototype.repository;

import com.mirafintech.prototype.model.consumer.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
}
