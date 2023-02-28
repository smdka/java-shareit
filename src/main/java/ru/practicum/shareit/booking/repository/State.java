package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.function.Supplier;

public enum State implements Supplier<Collection<Booking>> {
    ALL {
        @Override
        public Collection<Booking> get() {
            return null;
        }
    }, CURRENT {
        @Override
        public Collection<Booking> get() {
            return null;
        }
    }, PAST {
        @Override
        public Collection<Booking> get() {
            return null;
        }
    }, FUTURE {
        @Override
        public Collection<Booking> get() {
            return null;
        }
    }, WAITING {
        @Override
        public Collection<Booking> get() {
            return null;
        }
    }, REJECTED {
        @Override
        public Collection<Booking> get() {
            return null;
        }
    }
}
