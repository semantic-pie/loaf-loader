package com.semanticpie.loafloader.services.jmantic;

import org.ostis.scmemory.model.exception.ScMemoryException;
import org.springframework.web.multipart.MultipartFile;

public interface JManticService {
    void putFile(String hash, MultipartFile multipartFile) throws ScMemoryException;
}
