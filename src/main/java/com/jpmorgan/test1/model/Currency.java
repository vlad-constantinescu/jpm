package com.jpmorgan.test1.model;

/**
 * @author Vlad Constantinescu
 */
public enum Currency {

    AED,
    EUR,
    GBP,
    SAR,
    SGP,
    USD;

    public boolean isSundayToThursdayWorkWeek(){

        return this == AED || this == SAR;
    }
}
