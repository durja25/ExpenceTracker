package org.development.ExpenceTracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionExcelDTO {
    private String type;
    private String name;
    private String categoryName;
    private LocalDate date;
    private BigDecimal amount;
}