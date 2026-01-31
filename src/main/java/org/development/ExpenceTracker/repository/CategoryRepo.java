package org.development.ExpenceTracker.repository;

import java.util.List;
import java.util.Optional;
import org.development.ExpenceTracker.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<CategoryEntity, Long> {

    // Seletct * from categories where prfile_id = ?1
    List<CategoryEntity> findByProfileId(Long profileId);

    //select * from categories where profile_id = 1l and type = ?2
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    //select * from categories where type = ?1 and profile_id = ?2
    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

    Boolean existsByNameAndProfileId(String name, Long ProfileId);

}