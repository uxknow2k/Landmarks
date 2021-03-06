package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LandmarkSaveRequestDTO {
    private long id;
        private String name;
        private String city;
        private String landmarkAddress;
        private String undergrounds;
        private String landmarkDescription;
        private String landmarkWebSite;
        private String landmarkPhone;
        private String image;
        private String open;
        private String close;
        private Float lat;
        private Float lon;
    }

