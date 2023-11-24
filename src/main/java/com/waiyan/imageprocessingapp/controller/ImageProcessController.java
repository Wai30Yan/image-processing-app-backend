package com.waiyan.imageprocessingapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.spring.vision.CloudVisionException;
import com.google.cloud.spring.vision.CloudVisionTemplate;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature.Type;


@RestController
@CrossOrigin(origins = "https://image-processing-app-frontend.vercel.app")
public class ImageProcessController {
    @Autowired ResourceLoader resourceLoader;
    @Autowired CloudVisionTemplate cloudVisionTemplate;

    @GetMapping("/logo")
    public ResponseEntity<List<String>> analyzeLogo(@RequestParam String url) {
        try {
            AnnotateImageResponse text = this.cloudVisionTemplate.analyzeImage(resourceLoader.getResource(url), Type.LOGO_DETECTION);
    
            List<EntityAnnotation> logos = text.getLogoAnnotationsList();
            System.out.println(logos);
            if (logos.isEmpty()) {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
            }

            List<String> res = new ArrayList<>();
            for (EntityAnnotation i: logos) {
                System.out.println(i.getDescription());
                res.add(i.getDescription());
            }
    
            return new ResponseEntity<>(res, HttpStatus.OK);
            
        } catch (CloudVisionException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/text")
    public ResponseEntity<String> extractText(@RequestParam String url) {
        try {
            String text = this.cloudVisionTemplate.extractTextFromImage(resourceLoader.getResource(url));
            System.out.println(text);
            return new ResponseEntity<>(text, HttpStatus.OK);
            
        } catch (CloudVisionException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/face")
    public ResponseEntity<List<Map<String, String>>> analyzeFace(@RequestParam String url) {

        try {
            AnnotateImageResponse text = this.cloudVisionTemplate.analyzeImage(resourceLoader.getResource(url), Type.FACE_DETECTION);
    
            List<FaceAnnotation> faces = text.getFaceAnnotationsList();
            System.out.println(faces.size());

            if (faces.isEmpty()) {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
            }
    
            List<Map<String, String>> res = new ArrayList<>();
            for (FaceAnnotation i: faces) {
                Map<String, String> map = new HashMap<>();
                map.put("Joy", i.getJoyLikelihood().toString());
                map.put("Anger", i.getAngerLikelihood().toString());
                map.put("HeadWear", i.getHeadwearLikelihood().toString());
                map.put("Sorrow", i.getSorrowLikelihood().toString());
                map.put("Blurred", i.getBlurredLikelihood().toString());
                map.put("Surprised", i.getSurpriseLikelihood().toString());
                map.put("UnderExposed", i.getUnderExposedLikelihood().toString());
    
                res.add(map);
            }
    
            System.out.println(res);
    
            return new ResponseEntity<>(res, HttpStatus.OK);
            
        } catch (CloudVisionException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
        }
    }
}
