package com.semanticpie.loafloader.services.minio;

import io.minio.errors.MinioException;

import java.io.InputStream;

public interface MinioService {
    InputStream getFile(String hash) throws MinioException;
    void putFile(String hash, InputStream inputStream) throws MinioException;
}
