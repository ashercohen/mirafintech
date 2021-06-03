package com.mirafintech.prototype.model;

/**
 * TODO:
 * we might need to create an hierarchy of events since each events operates differently on
 * the tranche and contains different data. for example:
 * - loan added/removed affects balance
 * - payment_received: balance, create additional charges
 * - missed payment: charges,
 * - etc...
 * we might be able to make a "TrancheEventMetadata" table/entity that contains all the a.m. data that only
 * relevant fields will be populated. we can also add this here
 */

public enum TrancheEventType {
    LOAN_ADDED,
    LOAN_REMOVED,
    PAYMENT_RECEIVED,
    PAYMENT_MISSED
    // TODO: add here more types of events/operations on tranche
}
