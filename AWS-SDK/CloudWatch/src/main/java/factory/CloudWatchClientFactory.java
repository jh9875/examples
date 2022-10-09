package factory;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;

public class CloudWatchClientFactory
{
    public CloudWatchClient createCloudWatchClient(String accessKey, String secretKey, Region region)
    {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        CloudWatchClient client = CloudWatchClient.builder().credentialsProvider(StaticCredentialsProvider.create(awsCredentials)).region(region).build();

        System.out.println("client 생성 완료");

        return client;
    }
}