package com.jpmorgan.test1.model;

import java.math.BigDecimal;

/**
 * @author Vlad Constantinescu
 */
public class Ranking implements Comparable<Ranking>{

    private String entity;
    private BigDecimal totalValue;

    public Ranking(String entity, BigDecimal totalValue) {
        this.entity = entity;
        this.totalValue = totalValue;
    }

    public String getEntity() {
        return entity;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    @Override
    public int compareTo(Ranking other) {

        if (other == null) {
            return 1;
        }

        if (other == this) {
            return 0;
        }

        return this.getTotalValue().compareTo(other.getTotalValue());
    }
}
