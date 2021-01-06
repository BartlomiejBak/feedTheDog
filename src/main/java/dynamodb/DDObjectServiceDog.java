package dynamodb;

import entities.Dog;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public class DDObjectServiceDog implements DDBObjectService<Dog> {

    private final String id = "id";
    private final String name = "name";
    private final String breed = "breed";
    private final String weight = "weight";


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
            return dogConverter(returnedDog);
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
        String dogId = returnedDog.get(id).toString().substring(17);
        String dogName = returnedDog.get(name).toString().substring(17);
        String dogBreed = returnedDog.get(breed).toString().substring(17);
        String dogWeight = returnedDog.get(weight).toString().substring(17);

        return Dog.builder()
                .id(Integer.parseInt(dogId.substring(0, dogId.length() - 1)))
                .name(dogName.substring(0, dogName.length() - 1))
                .breed(dogBreed.substring(0, dogBreed.length() - 1))
                .weight(Double.parseDouble(dogWeight.substring(0, dogWeight.length() - 1)))
                .build();
    }
}
