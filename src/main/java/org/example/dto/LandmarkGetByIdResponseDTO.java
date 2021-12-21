package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LandmarkGetByIdResponseDTO {
    private Landmark landmark;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Landmark {
        private long id;
        private String name;
        private String city;
        private String landmarkAddress;
        private String undergrounds;
        private String landmarkDescription;
        private String landmarkWebSite;
        private String landmarkPhone;
        private String open;
        private String close;
        private Boolean available;
        private Float lat;
        private Float lon;
        private String image;

    }
}