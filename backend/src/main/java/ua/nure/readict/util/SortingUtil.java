package ua.nure.readict.util;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class SortingUtil {

    private SortingUtil() {
    }

    /**
     * Generates a sorting object based on parameters.
     *
     * @param sort             Sorting parameters in the format "field,direction".
     * @param defaultField     Default field to sort by.
     * @param defaultDirection Default sorting direction.
     * @param entityClass      Entity class for field validation.
     * @return {@link Sort} object.
     */
    public static Sort getSort(String sort, String defaultField, Sort.Direction defaultDirection, Class<?> entityClass) {
        String[] sortParams = sort != null ? sort.split(",") : new String[0];
        String sortField = sortParams.length > 0 ? sortParams[0] : defaultField;
        Sort.Direction sortDirection;

        try {
            sortDirection = sortParams.length > 1 ? Sort.Direction.fromString(sortParams[1]) : defaultDirection;
        } catch (IllegalArgumentException e) {
            sortDirection = defaultDirection;
        }

        if (!isValidSortField(sortField, entityClass)) {
            sortField = defaultField;
        }

        return Sort.by(sortDirection, sortField);
    }

    /**
     * Validates if the field is valid for sorting.
     *
     * @param field       Field name.
     * @param entityClass Entity class.
     * @return true if the field exists; otherwise, false.
     */
    private static boolean isValidSortField(String field, Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .anyMatch(f -> f.getName().equals(field));
    }
}
