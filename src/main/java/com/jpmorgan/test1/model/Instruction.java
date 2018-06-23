package com.jpmorgan.test1.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.time.DayOfWeek;

/**
 * Model class used to store an Instruction
 *
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
        calculateValueInUSD();
    }

    /**
     * Using Builder pattern for an easier time creating Instructions.
     * To be noted that the Builder doesn't have SettlementDate or ValueInUSD fields, which will be calculated when creating the actual Instruction object
     */
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

    public DateTime getSettlementDate() {
        return settlementDate;
    }

    public BigDecimal getValueInUSD() {
        return valueInUSD;
    }

    /**
     * Calculates the settlement date based on the business rules
     */
    private void calculateSettlementDate(){

        if (isWorkingDay(instructionDate)){

            settlementDate = new DateTime(instructionDate);
        } else {

            settlementDate = nextWorkingDay(instructionDate);
        }
    }

    /**
     * Checks if the instructionDate parameter is a working day
     *
     * @param instructionDate
     *          the date to be verified
     *
     * @return true if it is considered working day, false otherwise
     */
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

    /**
     * Calculate the next working day based on the given parameter.
     * Note: this works correctly even for days that aren't during the weekend
     *
     * @param instructionDate
     *          the date from which the next working day is being calculated
     *
     * @return the date of the next working date
     */
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

    /**
     * Calculates the total value in USD of the instruction and saves it in the valueInUSD variable, as it only has to be calculated once, based on the business rule.
     */
    private void calculateValueInUSD() {

        valueInUSD = fx.multiply(BigDecimal.valueOf(units)).multiply(price);
    }

}
