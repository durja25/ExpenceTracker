package org.development.ExpenceTracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.development.ExpenceTracker.dto.ExpenseDTO;
import org.development.ExpenceTracker.entity.CategoryEntity;
import org.development.ExpenceTracker.entity.ExpenseEntity;
import org.development.ExpenceTracker.entity.ProfileEntity;
import org.development.ExpenceTracker.repository.CategoryRepo;
import org.development.ExpenceTracker.repository.ExpenseRepo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryService categoryService;
    private final ExpenseRepo expenseRepo;
    private final ProfileService profileService;
    private final CategoryRepo categoryRepo;

    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        ProfileEntity currentProfile =
                profileService.getCurrentProfile();
        CategoryEntity category =
                categoryRepo.findById(expenseDTO.getCategoryId()).orElseThrow(() -> new RuntimeException("Category " + "not found"));
        ExpenseEntity newExpense = toEntity(expenseDTO,
                                            currentProfile, category);
        newExpense = expenseRepo.save(newExpense);
        return toDTO(newExpense);
    }

    //retrives all expenses for current user based on start date
    // and end date
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list =
                expenseRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    //delete  expense by id for current user
    public void deleteExpenseById(Long expenseId) {
        ProfileEntity currentProfile =
                profileService.getCurrentProfile();
        ExpenseEntity expense =
                expenseRepo.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense " + "not found"));

        if (!expense.getProfile().getId().equals(currentProfile.getId())) {
            throw new RuntimeException("Unauthorised yo delete");
        }
        expenseRepo.delete(expense);
    }

    //Get latest 5 expenses for current user
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list =
                expenseRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //get total expenses for current user
    public BigDecimal getTotalExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total =
                expenseRepo.findTotalExpensesByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    //filter expenses for current user
    public List<ExpenseDTO> filterExpenses(LocalDate startDate,
                                           LocalDate endDate,
                                           String keyword,
                                           Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list =
                expenseRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }

    //Notifications
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId
            , LocalDate date) {
        List<ExpenseEntity> list =
                expenseRepo.findByProfileIdAndDate(profileId,
                                                   date);
        return list.stream().map(this::toDTO).toList();
    }


    //help methods
    private ExpenseEntity toEntity(ExpenseDTO expenseDTO,
                                   ProfileEntity profile,
                                   CategoryEntity category) {
        return ExpenseEntity.builder().name(expenseDTO.getName()).icon(expenseDTO.getIcon()).date(expenseDTO.getDate()).amount(expenseDTO.getAmount()).category(category).profile(profile).build();
    }

    public ExpenseDTO toDTO(ExpenseEntity expense) {
        return ExpenseDTO.builder().id(expense.getId()).name(expense.getName()).icon(expense.getIcon()).categoryName(expense.getCategory() != null ? expense.getCategory().getName() : "N/A").categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null).amount(expense.getAmount()).date(expense.getDate()).createdAt(expense.getCreatedAt()).updatedAt(expense.getUpdatedAt()).build();
    }
}