package com.pogbe.bankingsystem.services.impl;

import com.opencsv.CSVWriter;
import com.pogbe.bankingsystem.constants.TransactionType;
import com.pogbe.bankingsystem.dto.requests.TransactionGenerationRequest;
import com.pogbe.bankingsystem.dto.responses.PaginatedTransactionRecordsResponse;
import com.pogbe.bankingsystem.dto.responses.TransactionRecordItemResponse;
import com.pogbe.bankingsystem.models.Account;
import com.pogbe.bankingsystem.models.TransactionRecord;
import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.repositories.AccountRepository;
import com.pogbe.bankingsystem.repositories.TransactionRecordRepository;
import com.pogbe.bankingsystem.repositories.UserModelRepository;
import com.pogbe.bankingsystem.services.interfaces.TransactionRecordGenerationService;
import io.jsonwebtoken.Claims;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Service
public class TransactionRecordGenerationServiceImpl implements TransactionRecordGenerationService {

	private final AccountRepository accountRepository;
	private final UserModelRepository userModelRepository;
	private final TransactionRecordRepository transactionRecordRepository;

	public TransactionRecordGenerationServiceImpl(
			AccountRepository accountRepository,
			UserModelRepository userModelRepository,
			TransactionRecordRepository transactionRecordRepository
	) {
		this.accountRepository = accountRepository;
		this.userModelRepository = userModelRepository;
		this.transactionRecordRepository = transactionRecordRepository;
	}

	@Override
	public PaginatedTransactionRecordsResponse getAccountRecords(
			Authentication authentication,
            TransactionGenerationRequest transactionGenerationRequest
	) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalArgumentException("Unauthenticated request");
		}
		int safePage = Math.max(transactionGenerationRequest.getPage(), 0);
		int safeSize = Math.max(transactionGenerationRequest.getSize(), 1);
		Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "date"));
		Account account = getSenderAccount(authentication);
		TransactionType transactionType = parseTransactionType(transactionGenerationRequest.getType());
		LocalDateTime startDateTime = parseStartDate(transactionGenerationRequest.getStart());
		LocalDateTime endDateTime = parseEndDate(transactionGenerationRequest.getEnd());
		if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
			throw new IllegalArgumentException("Start date cannot be after end date");
		}
		Page<TransactionRecord> records = transactionRecordRepository
				.findStatementByAccountId(account.getId(), transactionType, startDateTime, endDateTime, pageable);
		List<TransactionRecordItemResponse> items = records.getContent()
				.stream()
				.map(record -> new TransactionRecordItemResponse(
						record.getTransactionReference(),
						record.getTransactionType(),
						record.getDescription(),
						record.getAmount(),
						record.getDate()
				))
				.toList();

		return new PaginatedTransactionRecordsResponse(
				items,
				records.getNumber(),
				records.getSize(),
				records.getTotalElements(),
				records.getTotalPages(),
				records.hasNext(),
				records.hasPrevious()
		);
	}

	@Override
	public byte[] getAllAccountRecordsForExport(Authentication authentication, String type) {
		Account account = getSenderAccount(authentication);
		List<TransactionRecord> allTransactions = transactionRecordRepository.findFullStatementByAccountId(account.getId());
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			if (type!=null && type.equals("pdf")) {
				Document doc = new Document();
				PdfWriter.getInstance(doc, outputStream);
				doc.open();
				Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
				Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
				PdfPTable table = new PdfPTable(5);
				table.setWidthPercentage(100);
				String[] headers = {"Transaction Reference","Transaction Type","Description","Amount","Date"};
				for (String h : headers) {
					PdfPCell headerCell = new PdfPCell(new Phrase(h, headerFont));
					headerCell.setBackgroundColor(Color.GRAY);
					headerCell.setPadding(6);
					table.addCell(headerCell);
				}
				for (TransactionRecord transaction : allTransactions) {
					table.addCell(new PdfPCell(new Phrase(transaction.getTransactionReference(), cellFont)));
					table.addCell(new PdfPCell(new Phrase(transaction.getTransactionType() == null ? "" : transaction.getTransactionType().toString(), cellFont)));
					table.addCell(new PdfPCell(new Phrase(transaction.getDescription() == null ? "" : transaction.getDescription(), cellFont)));
					table.addCell(new PdfPCell(new Phrase(transaction.getAmount() == null ? "" : transaction.getAmount().toString(), cellFont)));
					table.addCell(new PdfPCell(new Phrase(transaction.getDate() == null ? "" : transaction.getDate().toString(), cellFont)));
				}
				doc.add(new Paragraph("Transaction report"));
				doc.add(new Paragraph("Generated on: " + LocalDate.now()));
				doc.add(table);
				doc.close();
			} else {
				try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream))) {
					String[] headers = {"Transaction Reference","Transaction Type","Description","Amount","Date"};
					csvWriter.writeNext(headers);
					for (TransactionRecord transaction : allTransactions) {
						String[] row = {transaction.getTransactionReference(),
								transaction.getTransactionType().toString(),
								transaction.getDescription(),
								transaction.getAmount().toString(),
								transaction.getDate().toString()
						};
						csvWriter.writeNext(row);
					}
					csvWriter.flush();
				}
			}

			return outputStream.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("Failed to generate transaction export file", e);
		}
		
	}

	private Account getSenderAccount(Authentication authentication) {
		Object details = authentication.getDetails();
		if (details instanceof Claims claims) {
			Long accountId = claims.get("accountId", Long.class);
			if (accountId != null) {
				return accountRepository.findById(accountId)
						.orElseThrow(() -> new IllegalArgumentException("Authenticated account not found"));
			}
		}

		String username = authentication.getName();
		UserModel user = userModelRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

		if (user.getUserAccount() == null) {
			throw new IllegalArgumentException("Authenticated user does not have an account");
		}
		return user.getUserAccount();
	}

	private TransactionType parseTransactionType(String input) {
		if (input == null || input.isBlank()) {
			return null;
		}
		try {
			return TransactionType.fromString(input.trim());
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Transaction type must be either debit or credit");
		}
	}

	private LocalDateTime parseStartDate(String input) {
		if (input == null || input.isBlank()) {
			return null;
		}
		try {
			return LocalDate.parse(input.trim()).atStartOfDay();
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException("Start date must be in yyyy-MM-dd format");
		}
	}

	private static LocalDateTime parseEndDate(String input) {
		if (input == null || input.isBlank()) {
			return null;
		}
		try {
			System.err.println(input);
			return LocalDate.parse(input.trim()).atTime(LocalTime.MAX);
		} catch (DateTimeParseException ex) {
			throw new IllegalArgumentException("End date must be in yyyy-MM-dd format");
		}
	}

}
