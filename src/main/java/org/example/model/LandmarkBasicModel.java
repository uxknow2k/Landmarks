package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LandmarkBasicModel {
    private long id;
    private String name;
    private String city;;
    private String landmarkDescription;
    private String landmarkPhone;
    private String image;
}
