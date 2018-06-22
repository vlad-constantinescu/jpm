package com.jpmorgan.test1.service;

import com.jpmorgan.test1.model.Instruction;
import com.jpmorgan.test1.model.Operation;
import com.jpmorgan.test1.model.Ranking;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Vlad Constantinescu
 */

public class ReportService {

    private static final String TOTAL_INCOMING = "Total incoming value: %f";
    private static final String TOTAL_OUTGOING = "Total outgoing value: %f";
    private static final String DAILY_RANK = "%s is rank %d (total %f)";

    public void generateReport(List<Instruction> instructions) {

        Map<DateTime, List<Instruction>> instructionsBySettlementDate = instructions.stream().collect(Collectors.groupingBy(Instruction::getSettlementDate));
        SortedSet<DateTime> keys = new TreeSet<>(instructionsBySettlementDate.keySet());

        for (DateTime key : keys) {
            System.out.println("****");
            System.out.println("Processing date: " + key.toString("YYYY-MM-dd"));
            List<Instruction> dailyInstructions = instructionsBySettlementDate.get(key);

            generateDailyReport(dailyInstructions);
        }
    }

    private void generateDailyReport(List<Instruction> dailyInstructions) {

        BigDecimal incomingValue = BigDecimal.ZERO;
        BigDecimal outgoingValue = BigDecimal.ZERO;
        Map<String, Ranking> incomingRanking = new HashMap<>();
        Map<String, Ranking> outgoingRanking = new HashMap<>();

        for (Instruction ins : dailyInstructions) {

            if (ins.getOperation() == Operation.BUY) {
                incomingValue = processInstruction(ins, incomingRanking, incomingValue);
            }

            if (ins.getOperation() == Operation.SELL) {
                outgoingValue = processInstruction(ins, outgoingRanking, outgoingValue);
            }
        }

        List<Ranking> orderedIncoming = incomingRanking.values().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
        List<Ranking> orderedOutgoing = outgoingRanking.values().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());

        printReport(incomingValue, orderedIncoming, Operation.BUY);
        printReport(outgoingValue, orderedOutgoing, Operation.SELL);
    }

    private void printReport(BigDecimal value, List<Ranking> rankings, Operation operation) {

        switch (operation) {
            case BUY:
                System.out.println("INCOMING RANKINGS:");
                System.out.println(String.format(TOTAL_INCOMING, value.doubleValue()));
                break;

            case SELL:
                System.out.println("OUTGOING RANKINGS:");
                System.out.println(String.format(TOTAL_OUTGOING, value.doubleValue()));
                break;
        }

        for (int rank = 0; rank < rankings.size(); rank ++) {

            System.out.println(String.format(DAILY_RANK, rankings.get(rank).getEntity(), rank+1, rankings.get(rank).getTotalValue().doubleValue()));
        }
    }

    private BigDecimal processInstruction(Instruction ins, Map<String, Ranking> incomingRanking, BigDecimal incomingValue) {

        BigDecimal currentValue = BigDecimal.ZERO;
        if (incomingRanking.containsKey(ins.getEntity())) {
            currentValue = incomingRanking.get(ins.getEntity()).getTotalValue();
        }

        incomingRanking.put(ins.getEntity(), new Ranking(ins.getEntity(), ins.getValueInUSD()));

        return incomingValue.add(ins.getValueInUSD());
    }

}
