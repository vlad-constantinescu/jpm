package com.jpmorgan.test1.service;

import com.google.common.collect.Lists;
import com.jpmorgan.test1.model.Currency;
import com.jpmorgan.test1.model.Instruction;
import com.jpmorgan.test1.model.Operation;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.jpmorgan.test1.helper.InstructionHelper.generateInstruction;
import static org.junit.Assert.assertEquals;

/**
 * Test class for {@link com.jpmorgan.test1.service.ReportService}
 *
 * @author Vlad Constantinescu
 */
public class ReportServiceTest {

    private ReportService service;

    @Before
    public void setUp(){

        service = new ReportService();
    }

    @Test
    public void givenInstructionWhenGenerateReportThenReportCorrect() {

        //setup
        Instruction instruction = generateInstruction("buy_entity", Operation.BUY, Currency.AED, new DateTime("2018-06-23"), BigDecimal.valueOf(1.5f), BigDecimal.valueOf(50), 100);

        //execute
        List<String> report = service.generateReport(Lists.newArrayList(instruction));

        //verify
        assertEquals(4, report.size());
        assertEquals("Processing date: 2018-06-24", report.get(0));
        assertEquals("Total incoming value: 7500.000000", report.get(1));
        assertEquals("buy_entity is rank 1 (total 7500.000000)", report.get(2));
        assertEquals("Total outgoing value: 0.000000", report.get(3));
    }

    @Test
    public void given2InstructionsWithSameEntityWhenGenerateReportThenReportCorrect(){

        //setup
        Instruction instruction1 = generateInstruction("buy_entity", Operation.BUY, Currency.AED, new DateTime("2018-06-23"), BigDecimal.valueOf(1.5f), BigDecimal.valueOf(50), 100);
        Instruction instruction2 = generateInstruction("buy_entity", Operation.BUY, Currency.AED, new DateTime("2018-06-23"), BigDecimal.valueOf(1.5f), BigDecimal.valueOf(50), 200);

        //execute
        List<String> report = service.generateReport(Lists.newArrayList(instruction1, instruction2));

        //verify
        assertEquals(4, report.size());
        assertEquals("Processing date: 2018-06-24", report.get(0));
        assertEquals("Total incoming value: 22500.000000", report.get(1));
        assertEquals("buy_entity is rank 1 (total 22500.000000)", report.get(2));
        assertEquals("Total outgoing value: 0.000000", report.get(3));
    }

    @Test
    public void given2InstructionsWithDifferentEntityWhenGenerateReportThenReportCorrect() {

        //setup
        Instruction instruction1 = generateInstruction("buy_entity1", Operation.BUY, Currency.AED, new DateTime("2018-06-23"), BigDecimal.valueOf(1.5f), BigDecimal.valueOf(50), 100);
        Instruction instruction2 = generateInstruction("buy_entity2", Operation.BUY, Currency.AED, new DateTime("2018-06-23"), BigDecimal.valueOf(1.5f), BigDecimal.valueOf(50), 200);

        //execute
        List<String> report = service.generateReport(Lists.newArrayList(instruction1, instruction2));

        //verify
        assertEquals(5, report.size());
        assertEquals("Processing date: 2018-06-24", report.get(0));
        assertEquals("Total incoming value: 22500.000000", report.get(1));
        assertEquals("buy_entity2 is rank 1 (total 15000.000000)", report.get(2));
        assertEquals("buy_entity1 is rank 2 (total 7500.000000)", report.get(3));
        assertEquals("Total outgoing value: 0.000000", report.get(4));
    }

    @Test
    public void givenDifferentDaysInstructionsWhenGenerateReportThenReportCorrect(){

        //setup
        Instruction instruction1 = generateInstruction("BUY_E", Operation.BUY, Currency.AED, new DateTime("2018-06-22"), BigDecimal.valueOf(1.5f), BigDecimal.valueOf(50), 100);
        Instruction instruction2 = generateInstruction("SEL_E", Operation.SELL, Currency.USD, new DateTime("2018-06-22"), BigDecimal.valueOf(1.5f), BigDecimal.valueOf(50), 200);

        //execute
        List<String> report = service.generateReport(Lists.newArrayList(instruction1, instruction2));

        //verify
        assertEquals(8, report.size());
        assertEquals("Processing date: 2018-06-22", report.get(0));
        assertEquals("Total incoming value: 0.000000", report.get(1));
        assertEquals("Total outgoing value: 15000.000000", report.get(2));
        assertEquals("SEL_E is rank 1 (total 15000.000000)", report.get(3));

        assertEquals("Processing date: 2018-06-24", report.get(4));
        assertEquals("Total incoming value: 7500.000000", report.get(5));
        assertEquals("BUY_E is rank 1 (total 7500.000000)", report.get(6));
        assertEquals("Total outgoing value: 0.000000", report.get(7));
    }
}
