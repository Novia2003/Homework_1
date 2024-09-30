package ru.tbank.initializer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.category.CategoryKudagoResponseDTO;
import ru.tbank.dto.location.LocationKudagoResponseDTO;
import ru.tbank.service.CategoryService;
import ru.tbank.service.KudagoService;
import ru.tbank.service.LocationService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private LocationService locationService;

    @Mock
    private KudagoService kudagoService;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void testInit_PositiveScenario() {
        CategoryKudagoResponseDTO[] categories = {new CategoryKudagoResponseDTO(), new CategoryKudagoResponseDTO()};
        LocationKudagoResponseDTO[] locations = {new LocationKudagoResponseDTO(), new LocationKudagoResponseDTO()};

        when(kudagoService.getCategories()).thenReturn(categories);
        when(kudagoService.getLocations()).thenReturn(locations);

        dataInitializer.init();

        verify(categoryService, times(categories.length)).createCategory(any(CategoryKudagoResponseDTO.class));
        verify(locationService, times(locations.length)).createLocation(any(LocationKudagoResponseDTO.class));
    }

    @Test
    void testInit_NoCategoriesFetched() {
        when(kudagoService.getCategories()).thenReturn(null);
        LocationKudagoResponseDTO[] locations = {new LocationKudagoResponseDTO(), new LocationKudagoResponseDTO()};
        when(kudagoService.getLocations()).thenReturn(locations);

        dataInitializer.init();

        verify(categoryService, never()).createCategory(any(CategoryKudagoResponseDTO.class));
        verify(locationService, times(locations.length)).createLocation(any(LocationKudagoResponseDTO.class));
    }

    @Test
    void testInit_NoLocationsFetched() {
        CategoryKudagoResponseDTO[] categories = {new CategoryKudagoResponseDTO(), new CategoryKudagoResponseDTO()};
        when(kudagoService.getCategories()).thenReturn(categories);
        when(kudagoService.getLocations()).thenReturn(null);

        dataInitializer.init();

        verify(categoryService, times(categories.length)).createCategory(any(CategoryKudagoResponseDTO.class));
        verify(locationService, never()).createLocation(any(LocationKudagoResponseDTO.class));
    }

    @Test
    void testInit_NoCategoriesAndLocationsFetched() {
        when(kudagoService.getCategories()).thenReturn(null);
        when(kudagoService.getLocations()).thenReturn(null);

        dataInitializer.init();

        verify(categoryService, never()).createCategory(any(CategoryKudagoResponseDTO.class));
        verify(locationService, never()).createLocation(any(LocationKudagoResponseDTO.class));
    }
}
