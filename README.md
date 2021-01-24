# feedTheDog
AWS demo project

## Table of Contents
1. [General info](#General-info)
2. [Technologies](#Technologies)
3. [Setup](#Setup)
4. [Status](#Status)

## General info

App creates simple dog entity. After creation of dog, app creates DynamoDB table and insert dog into. 
They are also created: 
- SNS topic, 
- SQS queue (which is subscribed to topic) 
- empty bucket

User can feed the dog and by doing that increase its weight for 1%. Every time dog eats, database weight value is updated and SNS notification is send. 
After feeding user have possibility to display database entry and some of SQS queue messages. Last message body is saved in created bucket.
In the end app deletes every previously created resource (database, queue, topic, subscription, bucket).

## Technologies
- Java 11 
####
- Amazon SNS
- Amazon SQS
- DynamoDb
- Amazon S3
####
- Lombok
####
## Setup
The application is developed in IntelliJ IDEA Ultimate Edition. To run this app, you need AWS account IAM user with CRUD permissions in S3, SNS, SQS and DynamoDb
and properly configure credentials. Instructions can be found there: https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html

Before start, you should change region value in App class to your choice.

## Status
to do:
- implement DynamoDBMapper
