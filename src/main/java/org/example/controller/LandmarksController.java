package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.manager.LandmarkManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/landmarks")
@RequiredArgsConstructor
public class LandmarksController {
    private final LandmarkManager manager;

    @GetMapping("/getAll")
    public LandmarkGetAllResponseDTO getAll() {
        return manager.getAll();
    }

    @GetMapping("/getById/{id}")
    public LandmarkGetByIdResponseDTO getById(@PathVariable long id) {
        return manager.getById(id);
    }

    @PostMapping("/save")
    public LandmarkSaveResponseDTO save(@RequestBody LandmarkSaveRequestDTO responseDTO) {
        return manager.save(responseDTO);
    }

    @PostMapping("/removeById/{id}")
    public void removeById(@PathVariable long id) {
        manager.removeById(id);
    }

    @PostMapping("/restoreById/{id}")
    public void restoreById(@PathVariable long id) {
        manager.restoreById(id);
    }

    @GetMapping("/distance/{id}")
    @ResponseBody
    public double getDistance(@PathVariable long id, @RequestParam float lat, @RequestParam float lon) {
        return manager.getDistanceBetweenPlaces(id, lat, lon);
    }

    @GetMapping("/distance/radius/")
    @ResponseBody
    public LandmarkGetDistanceBetweenPlacesDTO getDistancePlaces(@PathVariable long id, @RequestParam float lat, @RequestParam float lon, @RequestParam int radius) {
        return manager.getAllInThisRadius(id, lat, lon, radius);
    }
}



