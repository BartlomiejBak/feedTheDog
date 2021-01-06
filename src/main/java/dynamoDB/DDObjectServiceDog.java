package dynamoDB;

import entities.Dog;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public class DDObjectServiceDog implements DDBObjectService<Dog> {

    private String id = "id";
    private String name = "name";
    private String breed = "breed";
    private String weight = "weight";


    @Override
    public void add(Dog dog, DynamoDbClient dynamoDbClient, String tableName) {
        HashMap<String, AttributeValue> dogValues = new HashMap<>();

        System.out.println("created dogValues");

        dogValues.put(id, AttributeValue.builder().s(String.valueOf(dog.getId())).build());
        dogValues.put(name, AttributeValue.builder().s(dog.getName()).build());
        dogValues.put(breed, AttributeValue.builder().s(dog.getBreed()).build());
        dogValues.put(weight, AttributeValue.builder().s(String.valueOf(dog.getWeight())).build());

        System.out.println("puted elements");

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(dogValues)
                .build();

        System.out.println("created request");

        try {
            dynamoDbClient.putItem(request);
            System.out.println("Table successfully updated");
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public Dog get(int idNumber, DynamoDbClient dynamoDbClient, String tableName) {

        HashMap<String, AttributeValue> dogToGet = new HashMap<>();

        dogToGet.put(id, AttributeValue.builder()
                .s(String.valueOf(idNumber))
                .build());

        GetItemRequest request = GetItemRequest.builder()
                .key(dogToGet)
                .tableName(tableName)
                .build();

        try {
            Map<String, AttributeValue> returnedDog = dynamoDbClient.getItem(request).item();
            Dog convertedDog = dogConverter(returnedDog);
            return convertedDog;
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    //will update weight
    @Override
    public void update(Dog dog, DynamoDbClient dynamoDbClient, String tableName) {

        HashMap<String, AttributeValue> dogKey = new HashMap<>();

        dogKey.put(id, AttributeValue.builder()
                .s(String.valueOf(dog.getId()))
                .build());
        HashMap<String, AttributeValueUpdate> updatedDogKey = new HashMap<>();

        updatedDogKey.put(weight, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(String.valueOf(dog.getWeight())).build())
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(dogKey)
                .attributeUpdates(updatedDogKey)
                .build();
        try {
            dynamoDbClient.updateItem(request);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void delete(Dog dog, DynamoDbClient dynamoDbClient, String tableName) {
        HashMap<String, AttributeValue> dogKey = new HashMap<>();

        dogKey.put(id, AttributeValue.builder()
                .s(String.valueOf(dog.getId()))
                .build());

        DeleteItemRequest request = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(dogKey)
                .build();
        try {
            dynamoDbClient.deleteItem(request);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        }
    }

    private Dog dogConverter(Map<String, AttributeValue> returnedDog) {
        String id = returnedDog.get("id").toString().substring(17);
        String name = returnedDog.get("name").toString().substring(17);
        String breed = returnedDog.get("breed").toString().substring(17);
        String weight = returnedDog.get("weight").toString().substring(17);

        if (returnedDog != null) {
            Dog returnedEntity = Dog.builder()
                    .id(Integer.valueOf(id.substring(0, id.length() - 1)))
                    .name(name.substring(0, name.length() - 1))
                    .breed(breed.substring(0, breed.length() - 1))
                    .weight(Double.valueOf(weight.substring(0, weight.length() - 1)))
                    .build();
            return returnedEntity;
        }
        return null;
    }
}
