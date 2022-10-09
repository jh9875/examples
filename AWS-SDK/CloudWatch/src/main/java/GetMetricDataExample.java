import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import factory.CloudWatchClientFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataQuery;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;
import software.amazon.awssdk.services.cloudwatch.model.MetricStat;

public class GetMetricDataExample {

	public static void main(String[] args)
    {
        String accessKey = "accessKey";
        String secretKey = "secretKey";
        Region region = Region.AP_NORTHEAST_2;      // 아시아 서울

        CloudWatchClientFactory cloudWatchClientFactory = new CloudWatchClientFactory();
        CloudWatchClient cw = cloudWatchClientFactory.createCloudWatchClient(accessKey, secretKey, region);

        getMetricData(cw);
    }

    private static void getMetricData(CloudWatchClient cw)
    {
        List<String> instanceIds = new ArrayList<>();
        String identifier = "InstanceId";
        instanceIds.add("..");      // 추가할 인스턴스 아이디 입력

        String namespace = "AWS/EC2";
        String metricName = "CPUUtilization";
        int period = 60;
        String stat = "Average";
        
        ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
        int dayRange = 3;
        Instant startTimeInstant = LocalDate.now(ZONE_ID).minusDays(dayRange).atStartOfDay(ZONE_ID).toInstant();
        Instant endTimeInstant = LocalDate.now(ZONE_ID).atStartOfDay(ZONE_ID).toInstant();
        
        try
        {
            List<MetricDataQuery> metricDataQueryList = new ArrayList<>();
            int numbering = 0;

            for (String instanceId : instanceIds)
            {
                Dimension dimension = Dimension.builder().name(identifier).value(instanceId).build();
                Metric metric = Metric.builder().namespace(namespace).dimensions(dimension).metricName(metricName).build();
                MetricStat metricStat = MetricStat.builder().metric(metric).period(period).stat(stat).build();
                MetricDataQuery metricDataQuery = MetricDataQuery.builder().metricStat(metricStat).id("m" + (numbering++)).build();
                metricDataQueryList.add(metricDataQuery);
            }

            GetMetricDataRequest request = GetMetricDataRequest.builder().metricDataQueries(metricDataQueryList).startTime(startTimeInstant).endTime(endTimeInstant).build();
            GetMetricDataResponse response = cw.getMetricData(request);

            List<Instant> timestamps = null;
            List<Double> values = null;

            for (MetricDataResult result : response.metricDataResults())
            {
                timestamps = result.timestamps();
                values = result.values();
                System.out.println(String.format("id : %s", result.id()));
                for (int i = values.size() - 1; i >= 0; i--)
                {
                    System.out.println(String.format("timestamp : %s, value : %s", timestamps.get(i).atZone(ZONE_ID), values.get(i)));
                }
            }

        }
        catch (CloudWatchException e)
        {
            e.printStackTrace();
        }
    }
}
