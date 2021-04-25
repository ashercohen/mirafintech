package com.mirafintech.prototype.repository;

import com.mirafintech.prototype.model.UCICreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UCICreditCardRepository extends JpaRepository<UCICreditCard, Integer> {
}
