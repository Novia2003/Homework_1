package ru.tbank.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tbank.dto.category.CategoryKudagoResponseDTO;
import ru.tbank.pattern.observer.command.Command;
import ru.tbank.service.CategoryService;
import ru.tbank.service.KudagoService;

@Slf4j
@RequiredArgsConstructor
public class FetchAndSaveCategoriesCommand implements Command {

    private final KudagoService kudagoService;
    private final CategoryService categoryService;

    @Override
    public void execute() {
        var categories = kudagoService.getCategories();

        if (categories == null) {
            log.info("No categories fetched. Received null response.");
        } else {
            log.info("Fetched {} categories.", categories.length);

            for (CategoryKudagoResponseDTO category : categories) {
                categoryService.createCategory(category);
            }
        }
    }
}
