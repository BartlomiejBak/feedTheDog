package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import dynamoDB.DDObjectServiceDog;
import dynamoDB.DynamoDBService;
import entities.Dog;
import s3.BucketService;
import sns.SNSService;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import sqs.SQSService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws IOException {

        int dogId = 100;
        Dog dog = Dog.builder()
                .id(dogId)
                .name("Burek")
                .breed("Kundel")
                .weight(12.0)
                .build();

        Scanner scanner = new Scanner(System.in);
        ObjectMapper objectMapper = new ObjectMapper();

        //initial setup for AWS
        Region region = Region.EU_WEST_1;

        //initial setup for S3
        String bucket = "bucket" + System.currentTimeMillis();
        String key = "key";
        S3Client s3Client = S3Client.builder().region(region).build();
        BucketService bucketService = new BucketService();

        //initial setup for DynamoDB
        String tableName = "dogs";
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder().region(region).build();
        DynamoDBService dynamoDBService = new DynamoDBService();
        DDObjectServiceDog serviceDog = new DDObjectServiceDog();

        //initial setup for SQS
        SqsClient sqsClient = SqsClient.builder().region(region).build();
        String queueName = "dogQueue";
        SQSService sqsService = new SQSService();

        //initial setup for SNS
        SnsClient snsClient = SnsClient.builder().region(region).build();
        String topicName = "dogTopic";
        SNSService snsService = new SNSService();

        //create bucket
        bucketService.setupBucket(s3Client, bucket, region);

        //create and feed table
        dynamoDBService.createTable(dynamoDbClient, tableName, "id");
        serviceDog.add(dog, dynamoDbClient, tableName);

        //create queue
        String queueUrl = sqsService.createQueue(sqsClient, queueName);
        String queueArn = sqsService.getQueueArn(sqsClient, queueUrl);

        //create topic
        String topicArn = snsService.createTopic(snsClient, topicName);

        //subscribe queue to topic
        sqsService.grantQueuePermissionToSubscribe(queueArn, topicArn, queueUrl, sqsClient);
        String subscriptionArn = snsService.subscribeSQSTopic(snsClient, topicArn, queueArn);



        boolean exit = true;
        while(exit) {
            System.out.println("feed the dog? (y/n)");
            String command = scanner.next();
            if (command.equalsIgnoreCase("y")) {
                dog.feed();
                System.out.println("dog is happy");
                String message = objectMapper.writeValueAsString(dog);

                //update dog
                serviceDog.update(dog, dynamoDbClient, tableName);
                key = message;

                //send dog to subscribers
                snsService.publishTopic(snsClient, message, topicArn);

            } else {
                System.out.println(dog);
                exit = false;
            }
        }

        //insert dog into bucket
        bucketService.uploadFile(bucket, key, s3Client);

        exit = true;
        while(exit) {
            System.out.println("Do you want to display dog entry from database and queue messages?");
            String command = scanner.next();
            if (command.equalsIgnoreCase("y")) {
                System.out.println("database entry: ");
                System.out.println(serviceDog.get(dogId, dynamoDbClient, tableName));
                System.out.println("queue entry: ");
                List<Message> messages = sqsService.showMessages(sqsClient, queueUrl);
                for (Message message : messages) {
                    System.out.println(message.body());
                }
            } else {
                exit = false;
                System.out.println("time for cleaning");
            }
        }

        //cleanup
        bucketService.clearBucket(s3Client, bucket, key);
        dynamoDBService.deleteTable(dynamoDbClient, tableName);
        snsService.unsubscribe(snsClient, subscriptionArn);
        snsService.deleteTopic(snsClient, topicArn);
        sqsService.deleteQueue(sqsClient, queueName);

        s3Client.close();
        dynamoDbClient.close();
        sqsClient.close();
        snsClient.close();

    }
}
