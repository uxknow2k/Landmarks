package org.example.manager;

import lombok.RequiredArgsConstructor;
import org.example.dto.LandmarkGetAllResponseDTO;
import org.example.dto.LandmarkGetByIdResponseDTO;
import org.example.dto.LandmarkSaveRequestDTO;
import org.example.dto.LandmarkSaveResponseDTO;
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

    public LandmarkGetByIdResponseDTO getById(long id) {
        try {
            final LandmarkFullModel item = template.queryForObject(
                    // language=PostgreSQL
                    """
                            SELECT id, name, city, landmark_address, undergrounds,landmark_description, 
                            landmark_web_site, landmark_phone, open, close, CURRENT_TIME BETWEEN open AND close AS available, lat, lng,  image FROM landmarks
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
                    item.getLng(),
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
                            landmark_web_site, landmark_phone, image, open, close)  
                            VALUES (:name, :city, :landmarkAddress, :undergrounds, 
                            :landmarkDescription, :landmarkWebSite, :workingTime, :landmarkPhone, :image, :open::time with time zone AT TIME ZONE  'UTC',
                            :close::time with time zone AT TIME ZONE  'UTC')
                        RETURNING id, name, city, landmark_address, undergrounds, landmark_description, 
                            landmark_web_site, landmark_phone, image, open, close
                        """,
                Map.of(
                        "name", requestDTO.getName(),
                        "city", requestDTO.getCity(),
                        "landmarkAddress", requestDTO.getLandmarkAddress(),
                        "undergrounds", requestDTO.getUndergrounds(),
                        "landmarkDescription", requestDTO.getLandmarkDescription(),
                        "landmarkWebSite", requestDTO.getLandmarkWebSite(),
                        "landmarkPhone", requestDTO.getLandmarkPhone(),
                        "image", requestDTO.getImage() == null ? defaultImage : requestDTO.getImage(),
                        "open", "08:00 +03:00",
                        "close", "20:00 +03:00"
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
                item.getLandmarkPhone(),
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
                                landmark_web_site = :landmarkWebSite, landmark_phone = :landmarkPhone
                                WHERE id = :id and removed = FALSE
                            RETURNING id, name, city, image, landmark_address, undergrounds, landmark_description, 
                                landmark_web_site, landmark_phone
                            """,
                    Map.of(
                            "id", requestDTO.getId(),
                            "name", requestDTO.getName(),
                            "city", requestDTO.getCity(),
                            "landmarkDescription", requestDTO.getLandmarkDescription(),
                            "landmarkPhone", requestDTO.getLandmarkPhone(),
                            "image", requestDTO.getImage() == null ? defaultImage : requestDTO.getImage(),
                            "open", "08:00 +03:00",
                            "close", "20:00 +03:00"

                    ),
                    landmarkFullMapper

            );

            final LandmarkSaveResponseDTO responseDTO = new LandmarkSaveResponseDTO(new LandmarkSaveResponseDTO.Landmark(
                    item.getId(),
                    item.getName(),
                    item.getCity(),
                    item.getImage(),
                    item.getLandmarkAddress(),
                    item.getUndergrounds(),
                    item.getLandmarkDescription(),
                    item.getLandmarkWebSite(),
                    item.getLandmarkPhone()
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
            throw new LandmarkNotFoundException("landmark with id " + id + " not found");
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
            throw new LandmarkNotFoundException("landmark with id " + id + " not found");
        }
    }
        private double distance (double lat, double lon, double lat2, double lon2) {
return (1111.2 * Math.sqrt((lon - lon2) * (lon - lon2) + (lat - lat2) * Math.cos(Math.PI * lon / 180) * (lat - lat2) * Math.cos(Math.PI * lon / 180)));
        }
    }

