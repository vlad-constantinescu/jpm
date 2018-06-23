package com.jpmorgan.test1;

import com.google.common.collect.Lists;
import com.jpmorgan.test1.model.Currency;
import com.jpmorgan.test1.model.Instruction;
import com.jpmorgan.test1.model.Operation;
import com.jpmorgan.test1.service.ReportService;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Vlad Constantinescu
 */
public class Main {


    public static void main(String[] args) {

        ReportService reportService = new ReportService();

        Instruction i1 = new Instruction.Builder()
                .withEntity("foo")
                .withOperation(Operation.BUY)
                .withFx(new BigDecimal("0.5"))
                .withCurrency(Currency.SGP)
                .withInstructionDate(new DateTime("2016-01-02"))
                .withUnits(200)
                .withPrice(new BigDecimal("100.25"))
                .build();

        Instruction i11 = new Instruction.Builder()
                .withEntity("foo")
                .withOperation(Operation.BUY)
                .withFx(new BigDecimal("0.5"))
                .withCurrency(Currency.SGP)
                .withInstructionDate(new DateTime("2016-01-02"))
                .withUnits(100)
                .withPrice(new BigDecimal("100.25"))
                .build();

        Instruction i3 = new Instruction.Builder()
                .withEntity("boo")
                .withOperation(Operation.BUY)
                .withFx(new BigDecimal("0.5"))
                .withCurrency(Currency.SGP)
                .withInstructionDate(new DateTime("2016-01-03"))
                .withUnits(15)
                .withPrice(new BigDecimal("100.25"))
                .build();

        Instruction i2 = new Instruction.Builder()
                .withEntity("bar")
                .withOperation(Operation.SELL)
                .withFx(new BigDecimal("0.22"))
                .withCurrency(Currency.AED)
                .withInstructionDate(new DateTime("2016-01-05"))
                .withUnits(450)
                .withPrice(new BigDecimal("150.5"))
                .build();

        List<Instruction> instructions = Lists.newArrayList(i1, i2, i11, i3);

        List<String> report = reportService.generateReport(instructions);

        report.forEach(System.out::println);


    }
}
