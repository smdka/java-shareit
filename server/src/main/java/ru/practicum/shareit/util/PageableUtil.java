package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageableUtil {
    private static final Sort SORT_BY_START = Sort.by(Sort.Direction.DESC, "start");

    public PageRequest getPageRequestSortByStart(Integer from, Integer size) {
        return PageRequest.of(from / size, size, SORT_BY_START);
    }

    public PageRequest getPageRequest(Integer from, Integer size) {
        return PageRequest.of(from / size, size);
    }
}
