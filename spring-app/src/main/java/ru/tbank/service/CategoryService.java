package ru.tbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.dto.category.CategoryDTO;
import ru.tbank.dto.category.CategoryKudagoResponseDTO;
import ru.tbank.model.Category;
import ru.tbank.repository.CustomRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CustomRepository<Category> repository;

    public Collection<CategoryDTO> getAllCategories() {
        return repository
                .findAll()
                .stream()
                .map(this::parseModelToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        return parseModelToDTO(repository.findById(id));
    }

    public Long createCategory(CategoryDTO dto) {
        return repository.save(parseDTOToModel(dto));
    }

    public void createCategory(CategoryKudagoResponseDTO dto) {
        repository.save(parseKudagoResponseDTOToModel(dto));
    }

    public void updateCategory(Long id, CategoryDTO dto) {
        repository.update(id, parseDTOToModel(dto));
    }

    public void deleteCategory(Long id) {
        repository.delete(id);
    }

    private CategoryDTO parseModelToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());

        return dto;
    }

    private Category parseDTOToModel(CategoryDTO dto) {
        Category category = new Category();
        category.setSlug(dto.getSlug());
        category.setName(dto.getName());

        return category;
    }

    private Category parseKudagoResponseDTOToModel(CategoryKudagoResponseDTO dto) {
        Category category = new Category();
        category.setSlug(dto.getSlug());
        category.setName(dto.getName());

        return category;
    }
}
