package com.jpmorgan.test1.service;

import com.google.common.collect.Lists;
import com.jpmorgan.test1.model.Instruction;
import com.jpmorgan.test1.model.Operation;
import com.jpmorgan.test1.model.Ranking;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service used to generate the daily text incoming/outgoing report
 *
 * @author Vlad Constantinescu
 */

public class ReportService {

    private static final String TOTAL_INCOMING = "Total incoming value: %f";
    private static final String TOTAL_OUTGOING = "Total outgoing value: %f";
    private static final String DAILY_RANK = "%s is rank %d (total %f)";

    /**
     * For a given list of {@link com.jpmorgan.test1.model.Instruction}s it generates the report per days
     *
     * @param instructions
     *      the list of instructions for which the report has to be generated
     *
     * @return the report as a list of strings
     */
    public List<String> generateReport(List<Instruction> instructions) {

        List<String> report = Lists.newArrayList();

        //partition the instructions based on their settlementDate
        Map<DateTime, List<Instruction>> instructionsBySettlementDate = instructions.stream().collect(Collectors.groupingBy(Instruction::getSettlementDate));
        //sort the partition keys so the report would be in chronological order
        SortedSet<DateTime> keys = new TreeSet<>(instructionsBySettlementDate.keySet());

        for (DateTime key : keys) {
            report.add("Processing date: " + key.toString("YYYY-MM-dd"));
            List<Instruction> dailyInstructions = instructionsBySettlementDate.get(key);

            report.addAll(generateDailyReport(dailyInstructions));
        }

        return report;
    }

    /**
     * For a list of reports it calculates the total incoming and outgoing values, as well as ranking each entity based on its total value
     *
     * @param dailyInstructions
     *          the list containing the instructions on which the report will be generated
     */
    private List<String> generateDailyReport(List<Instruction> dailyInstructions) {

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

        //extract and sort in descending order based on totalValue for each Instruction entity
        List<Ranking> orderedIncoming = incomingRanking.values().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
        List<Ranking> orderedOutgoing = outgoingRanking.values().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());

        List<String> report = Lists.newArrayList();

        report.addAll(formatReport(incomingValue, orderedIncoming, Operation.BUY));
        report.addAll(formatReport(outgoingValue, orderedOutgoing, Operation.SELL));

        return report;
    }

    /**
     * Prints at the console the total daily value and the rankings for that day
     * @param value
     *          the total daily value
     * @param rankings
     *          the ordered rankings list
     * @param operation
     *          the type of {@link com.jpmorgan.test1.model.Operation}
     */
    private List<String> formatReport(BigDecimal value, List<Ranking> rankings, Operation operation) {

        List<String> report = Lists.newArrayList();

        switch (operation) {
            case BUY:
                report.add(String.format(TOTAL_INCOMING, value.doubleValue()));
                break;

            case SELL:
                report.add(String.format(TOTAL_OUTGOING, value.doubleValue()));
                break;
        }

        for (int rank = 0; rank < rankings.size(); rank ++) {

            report.add(String.format(DAILY_RANK, rankings.get(rank).getEntity(), rank+1, rankings.get(rank).getTotalValue().doubleValue()));
        }

        return report;
    }

    /**
     * Processes the given Instructions, adding it's valueIsUSD to the totalValue, as well as inserting (or updating) the rankingMap
     *
     * @param instruction
     *          the {@link com.jpmorgan.test1.model.Instruction} to be processed
     * @param rankingMap
     *          the map containing the {@link Ranking} objects calculated based on the previously processed Instructions
     * @param totalValue
     *          the current totalValue
     *
     * @return the new totalValue
     */
    private BigDecimal processInstruction(Instruction instruction, Map<String, Ranking> rankingMap, BigDecimal totalValue) {

        BigDecimal currentValue = BigDecimal.ZERO;

        //this is used in case in the same day we have more than 1 instruction for the same entity (e.g. we have 2 BUY operations for FOO entity)
        if (rankingMap.containsKey(instruction.getEntity())) {
            currentValue = rankingMap.get(instruction.getEntity()).getTotalValue();
        }

        //put or update the rankingMap with the new Ranking
        rankingMap.put(instruction.getEntity(), new Ranking(instruction.getEntity(), instruction.getValueInUSD().add(currentValue)));

        //return the newTotalValue
        return totalValue.add(instruction.getValueInUSD());
    }

}
