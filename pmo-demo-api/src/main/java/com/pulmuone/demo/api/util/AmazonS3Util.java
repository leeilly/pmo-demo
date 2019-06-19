package com.pulmuone.demo.api.util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class AmazonS3Util{

    @Autowired
    AmazonS3 amazonS3Client;

    private static final String bucketName = "es-dev-dic";

    public void uploadFile(File file) {
        try {
            amazonS3Client.putObject(bucketName, file.getName(), file);

        } catch (AmazonServiceException ase) {
            log.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
            log.info("Error Message: {}", ase.getMessage());
            log.info("HTTP Status Code: {}", ase.getStatusCode());
            log.info("AWS Error Code: {}", ase.getErrorCode());
            log.info("Error Type: {}", ase.getErrorType());
            log.info("Request ID: {}", ase.getRequestId());
        } catch (AmazonClientException ace) {
            log.info("Caught an AmazonClientException: ");
            log.info("Error Message: {}", ace.getMessage());
        }
    }

}
