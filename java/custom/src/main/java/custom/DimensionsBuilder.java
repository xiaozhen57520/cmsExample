package custom;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONObject;

public class DimensionsBuilder {

	private final static int max = 64;

	private final static String exclude = "=&,";

	private final static byte[] excludeByte = new byte[128];

	static {
		for (char c : exclude.toCharArray()) {
			excludeByte[c] = (byte) c;
		}
	}

	public static String build(String groupId, Map<String, String> dimension) {
		TreeMap<String, String> map = new TreeMap<>();
		dimension.forEach((key, value) -> {
			map.put(fixDimensionInfo(key), fixDimensionInfo(value));
		});
		String queryString = dimensionStr(map);
		Map<String, String> dimensions = new HashMap<>();
		dimensions.put("groupId", groupId);
		dimensions.put("dimension", queryString);
		return JSONObject.toJSONString(dimensions);
	}

	private static String dimensionStr(TreeMap<String, String> dimension) {
		if (dimension == null || dimension.isEmpty()) {
			return "";
		}
		StringBuilder ret = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> entry : dimension.entrySet()) {
			if (first) {
				first = false;
			} else {
				ret.append('&');
			}
			ret.append(entry.getKey()).append('=').append(entry.getValue());
		}
		return ret.toString();
	}

	private static String fixDimensionInfo(String key) {
		if (key == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Math.min(key.length(), max); i++) {
			char c = key.charAt(i);
			if (c < 0 || c >= 128) {
				c = '_';
			}
			if (excludeByte[c] != 0) {// 如果为非法字符，替换为下划线
				c = '_';
			}
			sb.append(c);
		}
		return sb.toString();
	}

}
