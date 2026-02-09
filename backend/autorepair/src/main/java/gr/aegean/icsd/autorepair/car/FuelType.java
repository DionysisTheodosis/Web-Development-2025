package gr.aegean.icsd.autorepair.car;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FuelType {
    ELECTRIC, DIESEL, LPG, HYBRID;
    @JsonCreator
    public static FuelType from(String value) {
        return value == null ? null : FuelType.valueOf(value.toUpperCase());
    }
}
