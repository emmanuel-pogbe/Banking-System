package com.pogbe.bankingsystem.batch;

import com.pogbe.bankingsystem.dto.responses.BulkTransferReportResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BulkTransferReportHolder {
    private static class Report {
        BigDecimal totalPayout = BigDecimal.ZERO;
        int totalPeoplePaid = 0;
    }

    private final Map<String, Report> reports = new ConcurrentHashMap<>();

    public void addTransfer(String jobId, BigDecimal amount) {
        Report r = reports.computeIfAbsent(jobId, k -> new Report());
        if (amount != null) r.totalPayout = r.totalPayout.add(amount);
        r.totalPeoplePaid++;
    }

    public BulkTransferReportResponseDTO getReport(String jobId) {
        Report r = reports.get(jobId);
        if (r == null) return null;
        BulkTransferReportResponseDTO dto = new BulkTransferReportResponseDTO();
        dto.setMessage("Payout successful");
        dto.setTotalPayout(r.totalPayout);
        dto.setTotalPeoplePaid(r.totalPeoplePaid);
        return dto;
    }
}
