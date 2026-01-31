package org.development.ExpenceTracker.service;

import lombok.RequiredArgsConstructor;
import org.development.ExpenceTracker.dto.IncomeDTO;
import org.development.ExpenceTracker.entity.CategoryEntity;
import org.development.ExpenceTracker.entity.IncomeEntity;
import org.development.ExpenceTracker.entity.ProfileEntity;
import org.development.ExpenceTracker.repository.CategoryRepo;
import org.development.ExpenceTracker.repository.IncomeRepo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepo categoryRepository;
    private final IncomeRepo incomeRepository;
    private final ProfileService profileService;

    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity currentProfile =
                profileService.getCurrentProfile();
        CategoryEntity category =
                categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category " +
                        "not found"));
        IncomeEntity newIncome = toEntity(incomeDTO,
                currentProfile, category);
        newIncome = incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }
    //retrives all incomes for current user based on start date
    // and end date
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list =
                incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    //delete  income by id for current user
    public void deleteIncomeById(Long incomeId) {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        IncomeEntity income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));

        if(!income.getProfile().getId().equals(currentProfile.getId())){
            throw new RuntimeException("Unauthorised yo delete");
        }
        incomeRepository.delete(income);
    }

    //Get latest 5 incomes for current user
    public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //get total incomes for current user
    public BigDecimal getTotalIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total =  incomeRepository.findTotalIncomesByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }
    //filter Incomes for current user
    public List<IncomeDTO> filterIncomes(LocalDate startDate,
                                           LocalDate endDate,
                                           String keyword,
                                           Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list =
                incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDTO).toList();
    }



    //help methods
    private IncomeEntity toEntity(IncomeDTO income,
                                  ProfileEntity profile,
                                  CategoryEntity category) {
        return IncomeEntity.builder()
                .name(income.getName())
                .icon(income.getIcon())
                .date(income.getDate())
                .amount(income.getAmount())
                .category(category)
                .profile(profile)
                .build();
    }

    public IncomeDTO toDTO(IncomeEntity income) {
        return IncomeDTO.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .categoryName(income.getCategory() != null ?
                        income.getCategory().getName() : "N/A")
                .categoryId(income.getCategory() != null ?
                        income.getCategory().getId() : null)
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }
}