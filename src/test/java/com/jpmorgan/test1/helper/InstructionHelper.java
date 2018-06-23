package com.jpmorgan.test1.helper;

import com.jpmorgan.test1.model.Currency;
import com.jpmorgan.test1.model.Instruction;
import com.jpmorgan.test1.model.Operation;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @author Vlad Constantinescu
 */
public class InstructionHelper {


    public static Instruction generateInstruction(Currency currency, DateTime instructionDate) {

        return new Instruction.Builder()
                .withInstructionDate(instructionDate)
                .withCurrency(currency)
                .withFx(BigDecimal.ONE)
                .withPrice(BigDecimal.ONE)
                .withUnits(100)
                .build();
    }

    public static Instruction generateInstruction(BigDecimal fx, BigDecimal price, long units) {

        return new Instruction.Builder()
                .withInstructionDate(new DateTime())
                .withCurrency(Currency.EUR)
                .withFx(fx)
                .withPrice(price)
                .withUnits(units)
                .build();
    }

    public static Instruction generateInstruction(String entity, Operation operation, Currency currency, DateTime instructionDate, BigDecimal fx, BigDecimal price, long units) {

        return new Instruction.Builder()
                .withEntity(entity)
                .withOperation(operation)
                .withInstructionDate(instructionDate)
                .withCurrency(currency)
                .withFx(fx)
                .withPrice(price)
                .withUnits(units)
                .build();
    }
}
