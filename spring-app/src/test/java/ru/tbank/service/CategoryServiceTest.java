package ru.tbank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.category.CategoryDTO;
import ru.tbank.dto.category.CategoryKudagoResponseDTO;
import ru.tbank.model.Category;
import ru.tbank.repository.CustomRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CustomRepository<Category> repository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void testGetAllCategories() {
        Category airportCategory = createCategory("Аэропорты", "airports");
        Category amusementCategory = createCategory("Развлечения", "amusement");
        when(repository.findAll()).thenReturn(Arrays.asList(airportCategory, amusementCategory));

        Collection<CategoryDTO> result = categoryService.getAllCategories();

        assertEquals(2, result.size());

        Iterator<CategoryDTO> iterator = result.iterator();
        String firstElementName = iterator.next().getName();
        assertEquals("Аэропорты", firstElementName);

        String secondElementSlug = iterator.next().getSlug();
        assertEquals("amusement", secondElementSlug);
    }

    @Test
    void testGetCategoryById() {
        Category airportCategory = createCategory("Аэропорты", "airports");
        when(repository.findById(1L)).thenReturn(airportCategory);

        CategoryDTO result = categoryService.getCategoryById(1L);

        assertEquals("Аэропорты", result.getName());
    }

    @Test
    void testCreateCategory() {
        CategoryDTO dto = createCategoryDTO("Аэропорты", "airports");
        when(repository.save(any(Category.class))).thenReturn(1L);

        Long result = categoryService.createCategory(dto);

        assertEquals(1L, result);
    }

    @Test
    void testCreateLocationDuringInitialize() {
        CategoryKudagoResponseDTO dto = new CategoryKudagoResponseDTO();
        dto.setSlug("airports");
        dto.setName("Аэропорты");

        categoryService.createCategory(dto);

        verify(repository).save(any(Category.class));
    }

    @Test
    void testUpdateCategoryById() {
        CategoryDTO dto = createCategoryDTO("Аэропорты", "airports");

        categoryService.updateCategory(1L, dto);

        verify(repository).update(eq(1L), any(Category.class));
    }

    @Test
    void testDeleteCategoryById() {
        categoryService.deleteCategory(1L);

        verify(repository).delete(1L);
    }

    @Test
    void testGetCategoryByNonExistentId() {
        when(repository.findById(1L)).thenReturn(null);

        CategoryDTO result = categoryService.getCategoryById(1L);

        assertNull(result);
    }

    @Test
    void testUpdateCategoryByNonExistentId() {
        CategoryDTO dto = createCategoryDTO("Аэропорты", "airports");
        doThrow(new IllegalArgumentException()).when(repository).update(eq(1L), any(Category.class));

        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(1L, dto));
    }

    @Test
    void testDeleteCategoryByNonExistentId() {
        doThrow(new IllegalArgumentException()).when(repository).delete(1L);

        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(1L));
    }

    private Category createCategory(String name, String slug) {
        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);

        return category;
    }

    private CategoryDTO createCategoryDTO(String name, String slug) {
        CategoryDTO dto = new CategoryDTO();
        dto.setName(name);
        dto.setSlug(slug);

        return dto;
    }
}
