package org.development.ExpenceTracker.service;

import lombok.RequiredArgsConstructor;
import org.development.ExpenceTracker.dto.ExpenseDTO;
import org.development.ExpenceTracker.dto.IncomeDTO;
import org.development.ExpenceTracker.dto.RecentTransactionDTO;
import org.development.ExpenceTracker.entity.ProfileEntity;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        ProfileEntity currentProfile =
                profileService.getCurrentProfile();
        Map<String, Object> dashboardData = new LinkedHashMap<>();
        List<IncomeDTO> latest5Incomes =
                incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> latest5Expenses =
                expenseService.getLatest5ExpensesForCurrentUser();
        //resent trans
        List<RecentTransactionDTO> recentTransactionDTOS = concat(latest5Incomes
                                                     .stream()
                                                     .map(income ->
                                                              RecentTransactionDTO.builder().id(
                                                                                      income.getId())
                                                                                  .profileId(
                                                                                      currentProfile.getId())
                                                                                  .name(
                                                                                      income.getName())
                                                                                  .icon(
                                                                                      income.getIcon())
                                                                                  .amount(
                                                                                      income.getAmount())
                                                                                  .date(
                                                                                      income.getDate())
                                                                                  .createdAt(
                                                                                      income.getCreatedAt())
                                                                                  .updatedAt(
                                                                                      income.getUpdatedAt())
                                                                                  .type("income")
                                                                                  .build()
                                                     ),
                                                 latest5Expenses
                                                     .stream()
                                                     .map(expenseDTO ->
                                                              RecentTransactionDTO
                                                                  .builder()
                                                                  .id(expenseDTO.getId())
                                                                  .profileId(currentProfile.getId())
                                                                  .name(expenseDTO.getName())
                                                                  .icon(expenseDTO.getIcon())
                                                                  .amount(expenseDTO.getAmount())
                                                                  .date(expenseDTO.getDate())
                                                                  .createdAt(
                                                                      expenseDTO.getCreatedAt())
                                                                  .updatedAt(
                                                                      expenseDTO.getUpdatedAt())
                                                                  .type("expense")
                                                                  .build()
                                                     )).sorted((a, b) -> {
            int compare = b.getDate().compareTo(a.getDate());
            if (compare == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
            return compare;
        }).toList();

        dashboardData.put("totalBalance",
                incomeService.getTotalIncomesForCurrentUser().subtract(expenseService.getTotalExpensesForCurrentUser()));
        dashboardData.put("totalIncome",
                incomeService.getTotalIncomesForCurrentUser());
        dashboardData.put("totalExpense",
                expenseService.getTotalExpensesForCurrentUser());
        dashboardData.put("latest5Expenses",
                expenseService.getLatest5ExpensesForCurrentUser());
        dashboardData.put("latest5Incomes",
                incomeService.getLatest5IncomesForCurrentUser());
        dashboardData.put("recentTransactions", recentTransactionDTOS);
        return dashboardData;

    }
}