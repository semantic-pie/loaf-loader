package com.semanticpie.loafloader.services.sync;

import com.semanticpie.loafloader.services.jmantic.JManticService;
import com.semanticpie.loafloader.services.minio.MinioService;
import io.minio.errors.MinioException;
import org.ostis.scmemory.model.exception.ScMemoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class SyncResourcesServiceImpl implements SyncResourcesService{
    private final MinioService minioService;
    private final JManticService jManticService;


    @Autowired
    public SyncResourcesServiceImpl(MinioService minioService, JManticService jManticService) {
        this.minioService = minioService;
        this.jManticService = jManticService;
    }

    @Override
    public String sync(MultipartFile multipartFile)  {
        try {
            String hash = DigestUtils.md5DigestAsHex(multipartFile.getInputStream());

            syncWithSCMemory(hash, multipartFile);
            syncWithMinIO(hash, multipartFile);
            return hash;
        } catch (IOException e) {
            throw new SyncException(e);
        } catch (MinioException e) {
            throw new SyncException(e);
        } catch (ScMemoryException e) {
            throw new SyncException(e);
        }
    }

    private void syncWithMinIO(String hash, MultipartFile multipartFile) throws IOException, MinioException {
        minioService.putFile(hash, multipartFile.getInputStream());
    }

    private void syncWithSCMemory(String hash, MultipartFile multipartFile) throws ScMemoryException {
        jManticService.putFile(hash, multipartFile);
    }

    @Override
    public InputStream resourceInputStream(String hash) {
        try {
            return minioService.getFile(hash);
        } catch (MinioException e) {
            throw new SyncException("Can't load this resource", e);
        }
    }
}
