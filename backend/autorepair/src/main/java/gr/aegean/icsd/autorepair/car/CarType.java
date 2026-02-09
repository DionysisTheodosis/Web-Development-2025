package gr.aegean.icsd.autorepair.car;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CarType {
    PASSENGER, TRUCK, BUS;
    @JsonCreator
    public static CarType from(String value) {
        return value == null ? null : CarType.valueOf(value.toUpperCase());
    }
}