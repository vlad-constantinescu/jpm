package com.jpmorgan.test1.model;

import org.joda.time.DateTime;
import org.junit.Test;

import java.math.BigDecimal;

import static com.jpmorgan.test1.helper.InstructionHelper.generateInstruction;
import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link com.jpmorgan.test1.model.Instruction}
 *
 * @author Vlad Constantinescu
 */

public class InstructionTest {

    @Test
    public void givenInstructionWithAEDCurrencyThenSettlementDateCorrect() {

        //setup
        Instruction fridayIns = generateInstruction(Currency.AED, new DateTime("2018-06-22"));
        Instruction saturdayIns = generateInstruction(Currency.AED, new DateTime("2018-06-23"));
        Instruction sundayIns = generateInstruction(Currency.AED, new DateTime("2018-06-24"));

        //verify
        //the next working day is Sunday, 24th June 2018
        String sunday = "2018-06-24";
        assertEquals(sunday, fridayIns.getSettlementDate().toString("YYYY-MM-dd"));
        assertEquals(sunday, saturdayIns.getSettlementDate().toString("YYYY-MM-dd"));
        assertEquals(sunday, sundayIns.getSettlementDate().toString("YYYY-MM-dd"));
    }

    @Test
    public void givenInstructionWithUSDCurrencyThenSettlementDateCorrect() {

        //setup
        Instruction saturdayIns = generateInstruction(Currency.USD, new DateTime("2018-06-23"));
        Instruction sundayIns = generateInstruction(Currency.USD, new DateTime("2018-06-24"));
        Instruction mondayIns = generateInstruction(Currency.USD, new DateTime("2018-06-25"));

        //verify
        //the next working day is Sunday, 24th June 2018
        String monday = "2018-06-25";
        assertEquals(monday, saturdayIns.getSettlementDate().toString("YYYY-MM-dd"));
        assertEquals(monday, sundayIns.getSettlementDate().toString("YYYY-MM-dd"));
        assertEquals(monday, mondayIns.getSettlementDate().toString("YYYY-MM-dd"));
    }

    @Test
    public void givenInstructionThenValueInUSDCalculatedCorrectly(){

        //setup
        Instruction instruction = generateInstruction(BigDecimal.valueOf(1.5f), BigDecimal.valueOf(50f), 100);

        // 100 * 50 * 1.5 = 7500
        double result = 7500f;
        assertEquals(result, instruction.getValueInUSD().doubleValue(), 0);
    }

}
