package org.development.ExpenceTracker.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.development.ExpenceTracker.dto.CategoryDTO;
import org.development.ExpenceTracker.entity.CategoryEntity;
import org.development.ExpenceTracker.entity.ProfileEntity;
import org.development.ExpenceTracker.repository.CategoryRepo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {


    private final ProfileService profileService;
    private final CategoryRepo categoryRepo;


    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        if (categoryRepo.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
            throw new RuntimeException("Category already exists");
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }
        CategoryEntity newCategory = toEntity(categoryDTO, profile);
        newCategory = categoryRepo.save(newCategory);
        return toDTO(newCategory);
    }

    //get categories for current user
    public List<CategoryDTO> getCategoriesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepo.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    //get categories by type for current user
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> entities = categoryRepo.findByTypeAndProfileId(type, profile.getId());
        return entities.stream().map(this::toDTO).toList();
    }

    //update  category
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepo
            .findByIdAndProfileId(categoryId, profile.getId()).orElseThrow(
                () -> new RuntimeException("Category not found or" + " not " + "accessible"));
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setIcon(categoryDTO.getIcon());
        existingCategory.setType(categoryDTO.getType());
        existingCategory = categoryRepo.save(existingCategory);
        return toDTO(existingCategory);
    }

    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profileDTO) {
        return CategoryEntity.builder().id(categoryDTO.getId()).name(categoryDTO.getName())
                             .createdAt(categoryDTO.getCreatedAt()).updatedAt(
                categoryDTO.getUpdatedAt()).type(categoryDTO.getType()).icon(categoryDTO.getIcon())
                             .profile(profileDTO).build();
    }

    private CategoryDTO toDTO(CategoryEntity categoryEntity) {
        return CategoryDTO.builder()
                          .id(categoryEntity.getId())
                          .name(categoryEntity.getName())
                          .createdAt(categoryEntity.getCreatedAt())
                          .updatedAt(categoryEntity.getUpdatedAt())
                          .type(categoryEntity.getType())
                          .icon(categoryEntity.getIcon())
                          .profileId(null != categoryEntity.getProfile()
                                         ? categoryEntity.getProfile().getId()
                                         : null)
                          .build();
    }


}
