package com.jpmorgan.test1.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Date;

/**
 * @author Vlad Constantinescu
 */
public class Instruction {

    private String entity;
    private Operation operation;
    private Currency currency;
    private DateTime instructionDate;
    private DateTime settlementDate;
    private long units;
    private BigDecimal fx;
    private BigDecimal price;
    private BigDecimal valueInUSD;

    public Instruction(Builder builder) {
        this.entity = builder.entity;
        this.operation = builder.operation;
        this.currency = builder.currency;
        this.instructionDate = builder.instructionDate;
        this.units = builder.units;
        this.fx = builder.fx;
        this.price = builder.price;

        calculateSettlementDate();
        calculateValue();
    }

    public Instruction() {

    }


    public static class Builder {

        private String entity;
        private Operation operation;
        private Currency currency;
        private DateTime instructionDate;
        private long units;
        private BigDecimal fx;
        private BigDecimal price;

        public Builder withEntity(String entity) {
            this.entity = entity;
            return this;
        }

        public Builder withOperation(Operation operation) {
            this.operation = operation;
            return this;
        }

        public Builder withCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder withInstructionDate(DateTime instructionDate){
            this.instructionDate = new DateTime(instructionDate);
            return this;
        }

        public Builder withUnits(long units){
            this.units = units;
            return this;
        }

        public Builder withPrice(BigDecimal price){
            this.price = price;
            return this;
        }

        public Builder withFx(BigDecimal fx) {
            this.fx = fx;
            return this;
        }

        public Instruction build(){
            return new Instruction(this);
        }

    }

    public String getEntity() {
        return entity;
    }

    public Operation getOperation() {
        return operation;
    }

    public Currency getCurrency() {
        return currency;
    }

    public DateTime getInstructionDate() {
        return instructionDate;
    }

    public DateTime getSettlementDate() {
        return settlementDate;
    }

    public long getUnits() {
        return units;
    }

    public BigDecimal getFx() {
        return fx;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getValueInUSD() {
        return valueInUSD;
    }

    public void calculateSettlementDate(){

        if (isWorkingDay(instructionDate)){

            settlementDate = new DateTime(instructionDate);
        } else {

            settlementDate = nextWorkingDay(instructionDate);
        }
    }

    private boolean isWorkingDay(DateTime instructionDate) {

        DayOfWeek dayOfWeek = DayOfWeek.of(instructionDate.getDayOfWeek());

        if (currency == Currency.AED || currency == Currency.SAR){

            if (dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY){
                return false;
            }
        } else {

            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                return false;
            }
        }

        return true;
    }

    private DateTime nextWorkingDay(DateTime instructionDate) {

        DayOfWeek dayOfWeek = DayOfWeek.of(instructionDate.getDayOfWeek());
        int offset = 0;

        if (currency == Currency.AED || currency == Currency.SAR) {
            if (dayOfWeek == DayOfWeek.FRIDAY) {
                offset = 2;
            }
            if (dayOfWeek == DayOfWeek.SATURDAY) {
                offset = 1;
            }
        } else {

            if (dayOfWeek == DayOfWeek.SATURDAY) {
                offset = 2;
            }
            if (dayOfWeek == DayOfWeek.SUNDAY) {
                offset = 1;
            }
        }

        return new DateTime(instructionDate).plusDays(offset);
    }

    private void calculateValue() {

        valueInUSD = fx.multiply(BigDecimal.valueOf(units)).multiply(price);
    }

}
