package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LandmarkGetFullAllResponseDTO {
    private List<Landmark> landmarks;

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
        private Double lat;
        private Double log;
        private String image;

    }
}
