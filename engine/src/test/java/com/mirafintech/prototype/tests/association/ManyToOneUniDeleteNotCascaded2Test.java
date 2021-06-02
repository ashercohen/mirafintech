package com.mirafintech.prototype.tests.association;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mirafintech.prototype.model.EntityBase;
import com.mirafintech.prototype.tests.util.AbstractTest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.ttddyy.dsproxy.QueryCountHolder;
import org.junit.Test;

import javax.persistence.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ManyToOneUniDeleteNotCascaded2Test extends AbstractTest {

    @Override
    protected Class<?>[] entities() {
        return new Class<?>[]{
                RiskLevel.class,
                RiskScore.class
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

            RiskScore score50 = new RiskScore(50);
            RiskScore score70 = new RiskScore(70);
            RiskScore score95 = new RiskScore(95);
            RiskScore score100 = new RiskScore(100);

            /**
             * two risk level that use the same scores
             * make sure only one RiskLevel entity (with 10, 95, 100)  is persisted
             */
            RiskLevel level1 = new RiskLevel(1L, score95, score100);
            RiskLevel level2 = new RiskLevel(2L, score95, score100);

            /**
             * another RiskLevel entity
             */
            RiskLevel level3 = new RiskLevel(3L, score50, score70);

            entityManager.persist(level1);
            entityManager.persist(level2);
            entityManager.persist(level3);
        });
    }

    @Test
    public void testLifecycle() {

        doInJPA(entityManager -> {
            List<RiskScore> riskScores = entityManager.createQuery("select rs from RiskScore rs", RiskScore.class).getResultList();
            assertNotNull(riskScores);
            assertEquals(4, riskScores.size());
        });

        doInJPA(entityManager -> {
            List<RiskLevel> riskLevels = entityManager.createQuery("select rl from RiskLevel rl", RiskLevel.class).getResultList();
            assertNotNull(riskLevels);
            assertEquals(3, riskLevels.size());
        });
    }

    @Test
    public void testLazyFetch() {
        QueryCountHolder.clear();

        doInJPA(entityManager -> {

            RiskLevel riskLevel = entityManager.createQuery("select rl from RiskLevel rl where rl.id=1", RiskLevel.class).getSingleResult();
            /**
             * lazy association => RiskLevel table is not queried
             */
            assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());

            /**
             * RiskLevel (= association target) is not accessed - table not queried
             */
            RiskScore lowerBound = riskLevel.getLowerBound();
            assertEquals(1, QueryCountHolder.getGrandTotal().getSelect());

            /**
             * once the association target is accessed - RiskLevel table is queried
             */
            int lowerBoundValue = lowerBound.getValue();;
            assertEquals(2, QueryCountHolder.getGrandTotal().getSelect());
        });
    }

    @Test
    public void testDeleteNotCascaded() {

        doInJPA(entityManager -> {
            List<RiskLevel> riskLevelsBefore = entityManager.createQuery("select rl from RiskLevel rl", RiskLevel.class).getResultList();
            assertNotNull(riskLevelsBefore);
            assertEquals(3, riskLevelsBefore.size());

            List<RiskScore> riskScoresBefore = entityManager.createQuery("select rs from RiskScore rs", RiskScore.class).getResultList();
            assertNotNull(riskScoresBefore);
            assertEquals(4, riskScoresBefore.size());

            RiskLevel riskLevel = riskLevelsBefore.get(2);
            String s = riskLevel.toString();

            entityManager.remove(riskLevel);
        });

        doInJPA(entityManager -> {

            List<RiskLevel> riskLevelsAfter = entityManager.createQuery("select rl from RiskLevel rl", RiskLevel.class).getResultList();
            assertNotNull(riskLevelsAfter);
            assertEquals(2, riskLevelsAfter.size());

            List<RiskScore> riskScoresAfter = entityManager.createQuery("select rs from RiskScore rs", RiskScore.class).getResultList();
            assertNotNull(riskScoresAfter);
            assertEquals(4, riskScoresAfter.size());
        });
    }

    @Entity(name = "RiskLevel")
    @Table(name = "RISK_LEVEL")
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RiskLevel extends EntityBase<RiskLevel> {

        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
        @JoinColumn(name = "lowerBound_fk")
        private RiskScore lowerBound;

        @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
        @JoinColumn(name = "upperBound_fk")
        private RiskScore upperBound;

        public RiskLevel(Long id, RiskScore lowerBound, RiskScore upperBound) {
            this.id = id;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }
    }

    @Entity(name = "RiskScore")
    @Table(name = "RISK_SCORE")
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RiskScore extends EntityBase<RiskScore> {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private int value; // [0..100]

        private RiskScore(Long id, int value) {
            this.id = id;
            this.value = value;
        }

        public RiskScore(int value) {
            this(null, value);
        }
    }
}
