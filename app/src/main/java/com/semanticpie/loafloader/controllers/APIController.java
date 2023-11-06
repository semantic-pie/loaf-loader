package com.semanticpie.loafloader.controllers;

import com.semanticpie.loafloader.dto.ErrorResponse;
import com.semanticpie.loafloader.services.sync.SyncResourcesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/loafloader")
public class APIController {

    private final SyncResourcesService syncResourcesService;

    @Autowired
    public APIController(SyncResourcesService syncResourcesService) {
        this.syncResourcesService = syncResourcesService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResource( @PathVariable String id) {
        try {
            var resource = syncResourcesService.resourceInputStream(id);
            return ResponseEntity.ok()
                    .body(new InputStreamResource(resource));
        } catch (RuntimeException e) {
            log.warn("OMG!!! Why so bad, bro. Error: {}", e.getMessage());
//            log.error("errr", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder().message(e.getMessage()).build());
        }
    }

    @CrossOrigin("*")
    @PostMapping()
    public ResponseEntity<?> postMultipleResource(@RequestParam("resources") List<MultipartFile> files) {
        try {
            StringBuilder result = new StringBuilder();
            for (var file : files) {
                var id = syncResourcesService.sync(file);
                result.append(id).append('\n');
            }


            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.warn("OMG!!! Why so bad, bro. Error: {}", e.getMessage());
            log.error("errr", e);
            return  ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.builder().message(e.getMessage()).build());
        }
    }
}
