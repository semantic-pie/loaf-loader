package com.semanticpie.loafloader.services.minio;

import com.semanticpie.loafloader.config.MinioConfiguration;
import io.minio.*;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
@Slf4j
@Service
public class MinioServiceImpl implements MinioService {

    @Value("${minio.bucket}")
    public String BUCKET_NAME;
    private final MinioClient minio;

    @Autowired
    public MinioServiceImpl(MinioClient minio) {
        this.minio = minio;
    }

    @Override
    public InputStream getFile(String hash) throws MinioException {
        try {
            return minio.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(hash)
                            .build()
            );
        } catch (Exception e) {
            throw new MinioException("Failed to download file from MinIO");
        }
    }

    @Override
    public void putFile(String hash, InputStream inputStream) throws MinioException {
        try {
            minio.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(hash)
                    .stream(inputStream, inputStream.available(), -1).build());
        } catch (Exception e) {
            log.info("Error", e);
            throw new MinioException("Failed to upload file to MinIO");
        }
    }
}
