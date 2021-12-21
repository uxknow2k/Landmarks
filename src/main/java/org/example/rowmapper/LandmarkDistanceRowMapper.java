package org.example.rowmapper;

import org.example.model.LandmarkDistanceModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LandmarkDistanceRowMapper implements RowMapper<LandmarkDistanceModel> {
    @Override
    public LandmarkDistanceModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new LandmarkDistanceModel(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("city"),
                rs.getString("landmarkAddress"),
                rs.getString("undergrounds"),
                rs.getString("landmarkDescription"),
                rs.getString("landmarkWebSite"),
                rs.getString("landmarkPhone"),
                rs.getString("open"),
                rs.getString("close"),
                rs.getBoolean("available")
        );
    }
}