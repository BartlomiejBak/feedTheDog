package sns;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

public class SNSService {

    public String createTopic(SnsClient snsClient, String topicName) {
        CreateTopicResponse result;
        try {
            CreateTopicRequest request = CreateTopicRequest.builder()
                    .name(topicName)
                    .build();
            result = snsClient.createTopic(request);
            System.out.println("topic created");
            System.out.println(result.topicArn());
            return result.topicArn();
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }

    public String subscribeSQSTopic(SnsClient snsClient, String topicArn, String arn) {
        try {
            SubscribeRequest request = SubscribeRequest.builder()
                    .protocol("sqs")
                    .endpoint(arn)
                    .returnSubscriptionArn(true)
                    .topicArn(topicArn)
                    .build();
            SubscribeResponse result = snsClient.subscribe(request);
            System.out.println("created subscription " + result.subscriptionArn());
            return result.subscriptionArn();
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return "";
    }

    public void publishTopic(SnsClient snsClient, String message, String topicArn) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .subject("dog feeding")
                    .message(message)
                    .topicArn(topicArn)
                    .build();
            PublishResponse result = snsClient.publish(request);
            System.out.println(" Message sent " + message);
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    public void unsubscribe(SnsClient snsClient, String subArn) {
        try {
            UnsubscribeRequest request = UnsubscribeRequest.builder()
                    .subscriptionArn(subArn)
                    .build();
            UnsubscribeResponse response = snsClient.unsubscribe(request);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    public void deleteTopic(SnsClient snsClient, String topicArn) {
        try {
            DeleteTopicRequest request = DeleteTopicRequest.builder()
                    .topicArn(topicArn)
                    .build();
            DeleteTopicResponse result = snsClient.deleteTopic(request);
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }
}
