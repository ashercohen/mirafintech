package com.mirafintech.prototype.tests.util;


public interface EntityProvider {

    /**
     * Entity types shared among multiple test configurations
     *
     * @return entity types
     */
    Class<?>[] entities();
}
