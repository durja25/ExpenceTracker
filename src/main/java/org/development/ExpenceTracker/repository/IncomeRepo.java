package org.development.ExpenceTracker.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.development.ExpenceTracker.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IncomeRepo extends JpaRepository<IncomeEntity, Long> {
    //select * from tbl_incomes where profile_id = ? order by date
    // desc
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    //select * from tbl_incomes where profile_id = ? order by date
    // desc limit 5
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("select coalesce(sum(i.amount), 0) from IncomeEntity i " +
            "where i.profile.id = :profileId")
    BigDecimal findTotalIncomesByProfileId(@Param("profileId") Long profileId);

    // select * from tbl_incomes where profile_id = ? and date
    // between ? and ? and name like %xx%
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    // select * from tbl_incomes where profile_id = ? and date
    // between ? and ?
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

}