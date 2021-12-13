package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class LandmarkGetAllResponseDTO {
    private List<Landmark> landmarks;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Landmark {
        private long id;
        private String name;
        private String city;
        private String landmarkDescription;
        private String landmarkPhone;
        private String image;
    }
}


