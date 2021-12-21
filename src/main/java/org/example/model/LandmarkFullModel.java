package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LandmarkFullModel {
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
