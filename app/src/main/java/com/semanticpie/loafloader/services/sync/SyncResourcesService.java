package com.semanticpie.loafloader.services.sync;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface SyncResourcesService {
    String sync(MultipartFile multipartFile);
    InputStream resourceInputStream(String hash);
}
