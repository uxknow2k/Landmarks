package org.example.rowmapper;

import org.example.model.LandmarkBasicModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
// Сокрашенный
@Component
public class LandmarkBasicRowMapper implements RowMapper<LandmarkBasicModel> {
    @Override
    public LandmarkBasicModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new LandmarkBasicModel(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("city"),
                rs.getString("landmark_description"),
                rs.getString("landmark_phone"),
                rs.getString("image")
        );
    }
}
