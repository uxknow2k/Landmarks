package org.example.manager;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.exception.LandmarkNotFoundException;
import org.example.model.LandmarkBasicModel;
import org.example.model.LandmarkFullModel;
import org.example.rowmapper.LandmarkBasicRowMapper;
import org.example.rowmapper.LandmarkFullRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.dto.LandmarkGetDistanceBetweenPlacesDTO.*;

@Component
@RequiredArgsConstructor
public class LandmarkManager {
    private final NamedParameterJdbcTemplate template;
    private final LandmarkBasicRowMapper landmarkBasicMapper;
    private final LandmarkFullRowMapper landmarkFullMapper;
    private final String defaultImage = "noimage.png";

    public LandmarkGetAllResponseDTO getAll() {
        List<LandmarkBasicModel> items = template.query(
                // language=PostgreSQL
                """
                        SELECT id, name, city,landmark_description, open, close, landmark_phone, image FROM landmarks
                        WHERE removed = FALSE
                        ORDER BY id
                        LIMIT 50
                        """, landmarkBasicMapper);
        LandmarkGetAllResponseDTO responseDTO = new LandmarkGetAllResponseDTO(new ArrayList<>(items.size()));
        for (LandmarkBasicModel item : items) {
            responseDTO.getLandmarks().add(new LandmarkGetAllResponseDTO.Landmark(
                    item.getId(),
                    item.getName(),
                    item.getCity(),
                    item.getLandmarkDescription(),
                    item.getLandmarkPhone(),
                    item.getImage()
            ));
        }
        return responseDTO;
    }

    public LandmarkGetDistanceBetweenPlacesDTO getFullAllResponseDTO() {
        try {
            final List<LandmarkFullModel> items = template.query(
                    // language=PostgreSQL
                    """
                            SELECT id, name, city, landmark_address, undergrounds,landmark_description, 
                            landmark_web_site, landmark_phone, open, close, CURRENT_TIME BETWEEN open AND close AS available, lat, lon,  image FROM landmarks
                            WHERE removed = FALSE   
                            """,
                    landmarkFullMapper
            );
            LandmarkGetDistanceBetweenPlacesDTO responseDTO = new LandmarkGetDistanceBetweenPlacesDTO();
            responseDTO.setLandmarks(items
                    .stream()
                    .map(item ->
                            new Landmark(
                                    item.getId(),
                                    item.getName(),
                                    item.getCity(),
                                    item.getLandmarkAddress(),
                                    item.getUndergrounds(),
                                    item.getLandmarkDescription(),
                                    item.getLandmarkWebSite(),
                                    item.getLandmarkPhone(),
                                    item.getOpen(),
                                    item.getClose(),
                                    item.getAvailable(),
                                    item.getLat(),
                                    item.getLon(),
                                    item.getImage()
                            ))
                    .collect(Collectors.toList()));

            return responseDTO;
        } catch (EmptyResultDataAccessException e) {
            throw new LandmarkNotFoundException(e);
        }
    }

    public double getDistanceBetweenPlaces(long sourceId, double lat, double lon) {
        LandmarkGetByIdResponseDTO landmarkGetByIdResponseDTO = getById(sourceId);
        return distanceFormula(landmarkGetByIdResponseDTO.getLandmark().getLat(), landmarkGetByIdResponseDTO.getLandmark().getLog(),
                lat, lon);
    }

    public List<Landmark> getAllInThisRadius(long id, double lat, double lon, int radius) {
        LandmarkGetByIdResponseDTO landmarkGetByIdResponseDTO = getById(id);
        LandmarkGetDistanceBetweenPlacesDTO responseDTO = getFullAllResponseDTO();
        List<LandmarkGetDistanceBetweenPlacesDTO.Landmark> result = new ArrayList<>();
        for (LandmarkGetDistanceBetweenPlacesDTO.Landmark landmark : responseDTO.getLandmarks()) {
            if (
                    radius > distanceFormula(landmarkGetByIdResponseDTO.getLandmark().getLat(), landmarkGetByIdResponseDTO.getLandmark().getLog(),
                            landmark.getLat(), landmark.getLog())) ;
            {
                result.add(landmark);
            }
        }


        return result;
    }

    public LandmarkGetByIdResponseDTO getById(long id) {
        try {
            final LandmarkFullModel item = template.queryForObject(
                    // language=PostgreSQL
                    """
                            SELECT id, name, city, landmark_address, undergrounds,landmark_description, 
                            landmark_web_site, landmark_phone, open, close, CURRENT_TIME BETWEEN open AND close AS available, lat, lon,  image FROM landmarks
                            WHERE id = :id AND removed = FALSE   
                            """,
                    Map.of(
                            "id", id
                    ),
                    landmarkFullMapper
            );
            final LandmarkGetByIdResponseDTO responseDTO = new LandmarkGetByIdResponseDTO(new LandmarkGetByIdResponseDTO.Landmark(
                    item.getId(),
                    item.getName(),
                    item.getCity(),
                    item.getLandmarkAddress(),
                    item.getUndergrounds(),
                    item.getLandmarkDescription(),
                    item.getLandmarkWebSite(),
                    item.getLandmarkPhone(),
                    item.getOpen(),
                    item.getClose(),
                    item.getAvailable(),
                    item.getLon(),
                    item.getLat(),
                    item.getImage()
            ));

            return responseDTO;
        } catch (EmptyResultDataAccessException e) {
            throw new LandmarkNotFoundException(e);
        }
    }

    public LandmarkSaveResponseDTO save(LandmarkSaveRequestDTO requestDTO) {
        return requestDTO.getId() == 0 ? create(requestDTO) : update(requestDTO);
    }


    private LandmarkSaveResponseDTO create(LandmarkSaveRequestDTO requestDTO) {
        final LandmarkFullModel item = template.queryForObject(
                // language=PostgreSQL
                """
                        INSERT INTO landmarks (name, city, landmark_address, undergrounds, landmark_description, 
                            landmark_web_site, landmark_phone, image, open, close, lat, lon)  
                            VALUES (:name, :city, :landmarkAddress, :undergrounds, 
                            :landmarkDescription, :landmarkWebSite, :landmarkPhone, :image, :open::time with time zone AT TIME ZONE  'UTC',
                            :close::time with time zone AT TIME ZONE  'UTC', :lat, :lon)
                        RETURNING id, name, city, landmark_address, undergrounds, landmark_description, 
                            landmark_web_site, landmark_phone, image, open, close, CURRENT_TIME BETWEEN open AND close AS available, lat, lon
                        """,
                Map.ofEntries(
                        Map.entry("name", requestDTO.getName()),
                        Map.entry("city", requestDTO.getCity()),
                        Map.entry("landmarkAddress", requestDTO.getLandmarkAddress()),
                        Map.entry("undergrounds", requestDTO.getUndergrounds()),
                        Map.entry("landmarkDescription", requestDTO.getLandmarkDescription()),
                        Map.entry("landmarkWebSite", requestDTO.getLandmarkWebSite()),
                        Map.entry("landmarkPhone", requestDTO.getLandmarkPhone()),
                        Map.entry("image", requestDTO.getImage() == null ? defaultImage : requestDTO.getImage()),
                        Map.entry("open", requestDTO.getOpen()),
                        Map.entry("close", requestDTO.getClose()),
                        Map.entry("lat", requestDTO.getLon()),
                        Map.entry("lon", requestDTO.getLat())
                ),
                landmarkFullMapper

        );

        final LandmarkSaveResponseDTO responseDTO = new LandmarkSaveResponseDTO(new LandmarkSaveResponseDTO.Landmark(
                item.getId(),
                item.getName(),
                item.getCity(),
                item.getLandmarkAddress(),
                item.getUndergrounds(),
                item.getLandmarkDescription(),
                item.getLandmarkWebSite(),
                item.getOpen(),
                item.getClose(),
                item.getAvailable(),
                item.getLandmarkPhone(),
                item.getLat(),
                item.getLon(),
                item.getImage()
        ));
        return responseDTO;
    }

    private LandmarkSaveResponseDTO update(LandmarkSaveRequestDTO requestDTO) {
        try {
            final LandmarkFullModel item = template.queryForObject(
                    // language=PostgreSQL
                    """
                            UPDATE landmarks SET  name = :name, city = :city, image = :image, landmark_address = :landmarkAddress, 
                            undergrounds = :undergrounds, landmark_description = :landmarkDescription, 
                                landmark_web_site = :landmarkWebSite, landmark_phone = :landmarkPhone, lat = :lat, lon = :lon
                                WHERE id = :id and removed = FALSE
                            RETURNING id, name, city, image, landmark_address, undergrounds, landmark_description, 
                                landmark_web_site, landmark_phone, lat, lon
                            """,
                    Map.of(
                            "id", requestDTO.getId(),
                            "name", requestDTO.getName(),
                            "city", requestDTO.getCity(),
                            "landmarkDescription", requestDTO.getLandmarkDescription(),
                            "landmarkPhone", requestDTO.getLandmarkPhone(),
                            "image", requestDTO.getImage() == null ? defaultImage : requestDTO.getImage(),
                            "open", "08:00 +03:00",
                            "close", "20:00 +03:00",
                            "lat", requestDTO.getLat(),
                            "lon", requestDTO.getLon()
                    ),
                    landmarkFullMapper

            );

            final LandmarkSaveResponseDTO responseDTO = new LandmarkSaveResponseDTO(new LandmarkSaveResponseDTO.Landmark(
                    item.getId(),
                    item.getName(),
                    item.getCity(),
                    item.getLandmarkAddress(),
                    item.getUndergrounds(),
                    item.getLandmarkDescription(),
                    item.getLandmarkWebSite(),
                    item.getOpen(),
                    item.getClose(),
                    item.getAvailable(),
                    item.getLandmarkPhone(),
                    item.getLat(),
                    item.getLon(),
                    item.getImage()
            ));
            return responseDTO;
        } catch (EmptyResultDataAccessException e) {
            throw new LandmarkNotFoundException();
        }

    }

    private String getImage(String image) {
        return image == null ? defaultImage : image;
    }


    public void removeById(long id) {
        final int affected = template.update(
                // language=PostgreSQL
                """
                        UPDATE landmarks SET removed = TRUE WHERE id = :id
                        """,
                Map.of("id", id)
        );
        if (affected == 0) {
            throw new LandmarkNotFoundException("landmark c таким id(" + id + ") не найден.");
        }
    }

    public void restoreById(long id) {
        final int affected = template.update(
                // language=PostgreSQL
                """
                        UPDATE landmarks SET removed = FALSE WHERE id = :id
                        """,
                Map.of("id", id)
        );
        if (affected == 0) {
            throw new LandmarkNotFoundException("landmark c таким id(" + id + ") не найден.");
        }
    }

    private int distanceFormula(double lat, double lon, double lat2, double lon2) {
        return (int) (1111.2 * Math.sqrt((lon - lon2) * (lon - lon2) + (lat - lat2) * Math.cos(Math.PI * lon / 180) * (lat - lat2) * Math.cos(Math.PI * lon / 180)));
    }
}

