package ru.tbank.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.dto.category.CategoryDTO;
import ru.tbank.service.CategoryService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CategoryController.class})
public class CategoryControllerTest {

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAllCategories() throws Exception {
        List<CategoryDTO> categories = Arrays.asList(
                createCategoryDTO("Аэропорты", "airports"),
                createCategoryDTO("Развлечения", "amusement")
        );

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/v1/places/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        """
                                [
                                {
                                    "slug": "airports",
                                    "name": "Аэропорты"
                                },
                                {
                                    "slug": "amusement",
                                    "name": "Развлечения"
                                }
                                ]
                                """
                ));
    }

    @Test
    public void getCategoryById() throws Exception {
        CategoryDTO dto = createCategoryDTO("Аэропорты", "airports");

        when(categoryService.getCategoryById(anyLong())).thenReturn(dto);

        mockMvc.perform(get("/api/v1/places/categories/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        """
                                {
                                    "slug": "airports",
                                    "name": "Аэропорты"
                                }
                                """
                ));
    }

    @Test
    public void getCategoryByNonExistentId() throws Exception {
        when(categoryService.getCategoryById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/api/v1/places/categories/{id}", 999))
                .andExpect(status().isNotFound());
    }


    @Test
    public void createCategory() throws Exception {
        String categoryDTO = """
                {
                    "slug": "airports",
                    "name": "Аэропорты"
                }
                """;

        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(1L);

        mockMvc.perform(post("/api/v1/places/categories")
                        .content(categoryDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void updateCategoryById() throws Exception {
        String categoryDTO = """
                {
                    "slug": "airports",
                    "name": "Аэропорты"
                }
                """;

        doNothing().when(categoryService).updateCategory(anyLong(), any(CategoryDTO.class));

        mockMvc.perform(put("/api/v1/places/categories/{id}", 1)
                        .content(categoryDTO)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCategoryById() throws Exception {
        doNothing().when(categoryService).deleteCategory(anyLong());

        mockMvc.perform(delete("/api/v1/places/categories/{id}", 1))
                .andExpect(status().isOk());
    }

    private CategoryDTO createCategoryDTO(String name, String slug) {
        CategoryDTO dto = new CategoryDTO();
        dto.setName(name);
        dto.setSlug(slug);

        return dto;
    }
}
