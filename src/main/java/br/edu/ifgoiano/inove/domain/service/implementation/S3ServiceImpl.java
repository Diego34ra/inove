package br.edu.ifgoiano.inove.domain.service.implementation;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class S3ServiceImpl {

    private final S3Client s3Client;

    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(String bucketName, String keyName, InputStream inputStream) throws IOException {
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build(), RequestBody.fromInputStream(inputStream, inputStream.available()));

        return "https://" + bucketName + ".s3.amazonaws.com/" + keyName;
    }

    public InputStream getFile(String bucketName, String keyName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();
        return s3Client.getObject(getObjectRequest);
    }

    public void deleteFile(String bucketName, String keyName) {
        s3Client.deleteObject(builder -> builder.bucket(bucketName).key(keyName).build());
    }

    public long getObjectSize(String bucketName, String keyName) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
        return headObjectResponse.contentLength();
    }

}
