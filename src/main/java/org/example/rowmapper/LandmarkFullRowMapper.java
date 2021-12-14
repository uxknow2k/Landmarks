package org.example.rowmapper;

import org.example.model.LandmarkBasicModel;
import org.example.model.LandmarkFullModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LandmarkFullRowMapper implements  RowMapper<LandmarkFullModel> {
    @Override
public LandmarkFullModel mapRow(ResultSet rs, int rowNum) throws  SQLException {
        return new LandmarkFullModel(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("city"),
                rs.getString("landmark_address"),
                rs.getString("undergrounds"),
                rs.getString("landmark_description"),
                rs.getString("landmark_web_site"),
                rs.getString("landmark_phone"),
                rs.getString("open"),
                rs.getString("close"),
                rs.getBoolean("available"),
                rs.getDouble("lat"),
                rs.getDouble("lng"),
                rs.getString("image")
        );
    }
}

