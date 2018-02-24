package custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.cms.model.v20170301.PutCustomMetricRequest;
import com.aliyuncs.cms.model.v20170301.QueryMetricListRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class CustomMetricExample {

	private static IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "<AK>",
			"<SK>");
	
	private static final String PROJECT = "acs_customMetric_<userId>";
	
	private static DefaultAcsClient client = new DefaultAcsClient(profile);

	// Please modify AK,SK,userId before running
	public static void main(String[] args) throws ServerException, ClientException {
		
		// The mock data is reported once every 10 seconds, and 10 time series data are reported each time. The report must continue for 2-3 minutes to query the data.
		int times = 18;
		for (int i = 0; i < times; i++) {
			putCustomMetric();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// query example 
		QueryMetricListRequest request = new QueryMetricListRequest();
		request.setPeriod("60");
		request.setProject(PROJECT);
		request.setMetric("metric_example");
		request.setAcceptFormat(FormatType.JSON);

		// When dimension has multiple fields, the built dimension must keep the alphabetical order according to key. Concrete implementation of reference DimensionsBuilder.
		Map<String, String> dimension = new HashMap<>();
		dimension.put("index", "2");
		dimension.put("instanceId", "i-test2");
		String queryString = DimensionsBuilder.build("96", dimension);
		System.out.println("queryString:" + queryString);
		request.setDimensions(queryString);
		
		HttpResponse response = client.doAction(request);
		System.out.println(new String(response.getHttpContent()));
	}

	private static void putCustomMetric() throws ClientException, ServerException {
		List<Map<String, String>> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Map<String, String> metric = new HashMap<>();
			metric.put("metricName", "metric_example");
			metric.put("instanceId", "i-test" + i);
			metric.put("groupId", "96");

			Map<String, String> dimensions = new HashMap<>();
			dimensions.put("instanceId", "i-test" + i);
			dimensions.put("index", String.valueOf(i));

			metric.put("dimensions", JSONObject.toJSONString(dimensions));
			metric.put("time", String.valueOf(System.currentTimeMillis()));
			metric.put("type", "0");
			metric.put("values", "{\"value\":10.5,\"Sum\":100}");

			list.add(metric);
		}

		PutCustomMetricRequest request = new PutCustomMetricRequest();
		request.setMethod(MethodType.GET);
		request.setAcceptFormat(FormatType.JSON);
		request.setMetricList(JSONObject.toJSONString(list));
		HttpResponse response = client.doAction(request);
		System.out.println(new String(response.getHttpContent()));
	}

}
