package sqs;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQSService {

    public String createQueue(SqsClient sqsClient, String queueName) {

        try {
            CreateQueueRequest request = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();

            sqsClient.createQueue(request);

            GetQueueUrlResponse urlResponse = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build());

            String queueUrl = urlResponse.queueUrl();
            return queueUrl;
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }

    public String getQueueArn(SqsClient sqsClient, String queueUrl) {
        try {
            GetQueueAttributesRequest request = GetQueueAttributesRequest.builder()
                    .queueUrl(queueUrl)
                    .attributeNamesWithStrings("All")
                    .build();

            GetQueueAttributesResponse response = sqsClient.getQueueAttributes(request);
            Map<String, String> attributeMap = response.attributesAsStrings();
            String arn = attributeMap.get("QueueArn");
            return arn;
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }

    public void deleteQueue(SqsClient sqsClient, String queueName) {
        try {
            GetQueueUrlRequest getRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();
            String queueUrl = sqsClient.getQueueUrl(getRequest).queueUrl();

            DeleteQueueRequest deleteRequest = DeleteQueueRequest.builder()
                    .queueUrl(queueUrl)
                    .build();
            sqsClient.deleteQueue(deleteRequest);
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    public List<Message> showMessages(SqsClient sqsClient, String queueUrl) {
        try {
            ReceiveMessageRequest messageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(20)
                    .build();
            List<Message> messages = sqsClient.receiveMessage(messageRequest).messages();
            return messages;
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return null;
    }

    public void grantQueuePermissionToSubscribe(String queueArn, String topicArn, String queueUrl, SqsClient sqsClient) {
        String policyDocument =
                "{" +
                        "  \"Version\": \"2012-10-17\"," +
                        "  \"Statement\": [{" +
                        "    \"Effect\":\"Allow\"," +
                        "    \"Principal\": {" +
                        "      \"Service\": \"sns.amazonaws.com\"" +
                        "    }," +
                        "    \"Action\":\"sqs:SendMessage\"," +
                        "    \"Resource\":\"" + queueArn + "\"," +
                        "    \"Condition\":{" +
                        "      \"ArnEquals\":{" +
                        "        \"aws:SourceArn\":\"" + topicArn + "\"" +
                        "      }" +
                        "    }" +
                        "  }]" +
                        "}";
        Map<QueueAttributeName, String> attributes = new HashMap<>();
        attributes.put(QueueAttributeName.POLICY, policyDocument);
        SetQueueAttributesRequest request = SetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributes(attributes)
                .build();
        SetQueueAttributesResponse response = sqsClient.setQueueAttributes(request);
    }
}
