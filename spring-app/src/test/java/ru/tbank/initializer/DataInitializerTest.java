package ru.tbank.initializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.dto.category.CategoryKudagoResponseDTO;
import ru.tbank.dto.location.LocationKudagoResponseDTO;
import ru.tbank.service.CategoryService;
import ru.tbank.service.KudagoService;
import ru.tbank.service.LocationService;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @BeforeEach
    void setUp() {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
        dataInitializer = new DataInitializer(categoryService, locationService, kudagoService, fixedThreadPool, scheduledThreadPool);
    }

    @Test
    void testInit_PositiveScenario() throws InterruptedException {
        CategoryKudagoResponseDTO[] categories = {new CategoryKudagoResponseDTO(), new CategoryKudagoResponseDTO()};
        LocationKudagoResponseDTO[] locations = {new LocationKudagoResponseDTO(), new LocationKudagoResponseDTO()};

        when(kudagoService.getCategories()).thenReturn(categories);
        when(kudagoService.getLocations()).thenReturn(locations);

        CountDownLatch latch = new CountDownLatch(1);
        dataInitializer.init();
        latch.await(1, TimeUnit.SECONDS); // Wait for the initialization to complete

        ArgumentCaptor<CategoryKudagoResponseDTO> categoryCaptor = ArgumentCaptor.forClass(CategoryKudagoResponseDTO.class);
        ArgumentCaptor<LocationKudagoResponseDTO> locationCaptor = ArgumentCaptor.forClass(LocationKudagoResponseDTO.class);

        verify(categoryService, times(categories.length)).createCategory(categoryCaptor.capture());
        verify(locationService, times(locations.length)).createLocation(locationCaptor.capture());

        List<CategoryKudagoResponseDTO> capturedCategories = categoryCaptor.getAllValues();
        List<LocationKudagoResponseDTO> capturedLocations = locationCaptor.getAllValues();

        for (CategoryKudagoResponseDTO category : categories) {
            assertTrue(capturedCategories.contains(category));
        }

        for (LocationKudagoResponseDTO location : locations) {
            assertTrue(capturedLocations.contains(location));
        }
    }

    @Test
    void testInit_NoCategoriesFetched() throws InterruptedException {
        when(kudagoService.getCategories()).thenReturn(null);
        LocationKudagoResponseDTO[] locations = {new LocationKudagoResponseDTO(), new LocationKudagoResponseDTO()};
        when(kudagoService.getLocations()).thenReturn(locations);

        CountDownLatch latch = new CountDownLatch(1);
        dataInitializer.init();
        latch.await(1, TimeUnit.SECONDS); // Wait for the initialization to complete

        ArgumentCaptor<LocationKudagoResponseDTO> locationCaptor = ArgumentCaptor.forClass(LocationKudagoResponseDTO.class);

        verify(categoryService, never()).createCategory(any(CategoryKudagoResponseDTO.class));
        verify(locationService, times(locations.length)).createLocation(locationCaptor.capture());

        List<LocationKudagoResponseDTO> capturedLocations = locationCaptor.getAllValues();

        for (LocationKudagoResponseDTO location : locations) {
            assertTrue(capturedLocations.contains(location));
        }
    }

    @Test
    void testInit_NoLocationsFetched() throws InterruptedException {
        CategoryKudagoResponseDTO[] categories = {new CategoryKudagoResponseDTO(), new CategoryKudagoResponseDTO()};
        when(kudagoService.getCategories()).thenReturn(categories);
        when(kudagoService.getLocations()).thenReturn(null);

        CountDownLatch latch = new CountDownLatch(1);
        dataInitializer.init();
        latch.await(1, TimeUnit.SECONDS); // Wait for the initialization to complete

        ArgumentCaptor<CategoryKudagoResponseDTO> categoryCaptor = ArgumentCaptor.forClass(CategoryKudagoResponseDTO.class);

        verify(categoryService, times(categories.length)).createCategory(categoryCaptor.capture());
        verify(locationService, never()).createLocation(any(LocationKudagoResponseDTO.class));

        List<CategoryKudagoResponseDTO> capturedCategories = categoryCaptor.getAllValues();

        for (CategoryKudagoResponseDTO category : categories) {
            assertTrue(capturedCategories.contains(category));
        }
    }

    @Test
    void testInit_NoCategoriesAndLocationsFetched() throws InterruptedException {
        when(kudagoService.getCategories()).thenReturn(null);
        when(kudagoService.getLocations()).thenReturn(null);

        CountDownLatch latch = new CountDownLatch(1);
        dataInitializer.init();
        latch.await(1, TimeUnit.SECONDS); // Wait for the initialization to complete

        verify(categoryService, never()).createCategory(any(CategoryKudagoResponseDTO.class));
        verify(locationService, never()).createLocation(any(LocationKudagoResponseDTO.class));
    }
}
