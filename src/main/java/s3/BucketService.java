package s3;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

public class BucketService {

    public void setupBucket(S3Client s3Client, String bucketName, Region region) {
        try {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .createBucketConfiguration(CreateBucketConfiguration.builder()
                            .locationConstraint(region.id())
                            .build())
                    .build());

            System.out.println("Creating bucket: " + bucketName);

            s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            System.out.println(bucketName + " is ready");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    public void uploadFile(String bucket, String key, S3Client s3) {
        System.out.println("uploading file");
        try {
            s3.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build(),
                    RequestBody.fromString("testing file"));
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    public void clearBucket(S3Client s3Client, String bucketName, String keyName) {
        System.out.println("cleaning up...");

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.deleteBucket(deleteBucketRequest);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }
}
