package br.edu.ifgoiano.inove.domain.service.implementation;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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

    public InputStream getFileStream(GetObjectRequest getObjectRequest) {
        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
        return s3Object;
    }

    public void deleteFile(String bucketName, String keyName) {
        s3Client.deleteObject(builder -> builder.bucket(bucketName).key(keyName).build());
    }


}
