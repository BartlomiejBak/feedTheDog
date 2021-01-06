package dynamodb;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public interface DDBObjectService<T>{
    void add(T object, DynamoDbClient dynamoDbClient, String tableName);
    T get(int i, DynamoDbClient dynamoDbClient, String tableName);
    void update(T object, DynamoDbClient dynamoDbClient, String tableName);
    void delete(T object, DynamoDbClient dynamoDbClient, String tableName);
}
