package com.mirafintech.prototype.tests.association;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.EntityBase;
import com.mirafintech.prototype.tests.util.AbstractTest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.Test;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ManyToOneUniDeleteNotCascadedTest extends AbstractTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
                RiskLevel.class,
                Tranche.class
        };
    }

    @Override
    public void destroy() {
        // comment this line in order not to drop the tables
        super.destroy();
    }

    @Override
    protected void afterInit() {
        doInJPA(entityManager -> {
            RiskLevel riskLevel1 = new RiskLevel(10L, 95, 100);
            RiskLevel riskLevel2 = new RiskLevel(5L, 50, 70);
            /**
             * same RiskLevel, multiple Tranches
             * make sure only one RiskLevel entity (with 10, 95, 100)  is persisted
             */
            Tranche tranche1 = new Tranche(1L, BigDecimal.TEN, LocalDateTime.now(), riskLevel1);
            Tranche tranche2 = new Tranche(2L, BigDecimal.ONE, LocalDateTime.now(), riskLevel1);
            /**
             * another RiskLevel entity
             */
            Tranche tranche3 = new Tranche(3L, BigDecimal.ZERO, LocalDateTime.now(), riskLevel2);
            entityManager.persist(tranche1);
            entityManager.persist(tranche2);
            entityManager.persist(tranche3);
        });
    }

    @Test
    public void testLifecycle() {

        doInJPA(entityManager -> {
            List<Tranche> tranches = entityManager.createQuery("select t from Tranche t", Tranche.class).getResultList();
            assertNotNull(tranches);
            assertEquals(3, tranches.size());
        });

        doInJPA(entityManager -> {
            List<RiskLevel> riskLevels = entityManager.createQuery("select rl from RiskLevel rl", RiskLevel.class).getResultList();
            assertNotNull(riskLevels);
            assertEquals(2, riskLevels.size());
        });
    }

    @Test
    public void testLazyFetch() {
        QueryCountHolder.clear();

        doInJPA(entityManager -> {

            Tranche tranche = entityManager.createQuery("select t from Tranche t where t.id=1", Tranche.class).getSingleResult();
            /**
             * lazy association => RiskLevel table is not queried
             */
            assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());

            /**
             * RiskLevel (= association target) is not accessed - table not queried
             */
            RiskLevel riskLevel = tranche.getRiskLevel();
            assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());

            /**
             * once the association target is accessed - RiskLevel table is queried
             */
            int lowerBound = riskLevel.getLowerBound();
            assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
        });
    }

    @Test
    public void testDeleteNotCascaded() {

        doInJPA(entityManager -> {
            List<RiskLevel> riskLevelsBefore = entityManager.createQuery("select rl from RiskLevel rl", RiskLevel.class).getResultList();
            assertNotNull(riskLevelsBefore);
            assertEquals(2, riskLevelsBefore.size());
            Tranche tranche = entityManager.createQuery("select t from Tranche t where t.id=1", Tranche.class).getSingleResult();
            entityManager.remove(tranche);
        });

        doInJPA(entityManager -> {
            List<RiskLevel> riskLevelsAfter = entityManager.createQuery("select rl from RiskLevel rl", RiskLevel.class).getResultList();
            assertNotNull(riskLevelsAfter);
            assertEquals(2, riskLevelsAfter.size());
        });
    }

    @Entity(name = "RiskLevel")
    @Table(name = "RISK_LEVEL")
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RiskLevel extends EntityBase<RiskLevel> {

        @Id
        private Long id;
        private int lowerBound;
        private int upperBound;

        public RiskLevel(Long id, int lowerBound, int upperBound) {
            this.id = id;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }
    }

    @Entity(name = "Tranche")
    @Table(name = "TRANCHE")
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Tranche extends EntityBase<Tranche> {

        @Id
        private Long id;

        private BigDecimal initialValue;
        private LocalDateTime creationDate;


        @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
        @JoinColumn(name = "risklevel_fk")
        private RiskLevel riskLevel;

        public Tranche(Long id, BigDecimal initialValue, LocalDateTime creationDate, RiskLevel riskLevel) {
            this.id = id;
            this.initialValue = initialValue;
            this.creationDate = creationDate;
            this.riskLevel = riskLevel;
        }
    }
}
