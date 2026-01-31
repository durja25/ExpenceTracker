package org.development.ExpenceTracker.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.development.ExpenceTracker.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ExpenseRepo extends JpaRepository<ExpenseEntity, Long> {
    //select * from tbl_expenses where profile_id = ? order by date
    // desc
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    //select * from tbl_expenses where profile_id = ? order by date
    // desc limit 5
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("select coalesce(sum(e.amount), 0) from ExpenseEntity e " +
            "where e.profile.id = :profileId")
    BigDecimal findTotalExpensesByProfileId(@Param("profileId") Long profileId);

    // select * from tbl_expenses where profile_id = ? and date
    // between ? and ? and name like %xx%
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    // select * from tbl_expenses where profile_id = ? and date
    // between ? and ?
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    //select * from tbl_expenses where profile_id = ? and date = ?
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId,
                                               LocalDate date);
}