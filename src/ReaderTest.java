import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import com.google.common.io.ByteStreams;

public class ReaderTest {
	@SuppressWarnings("unchecked")
	public void getYamlFile() {
		/// 当前时间
		long time = System.currentTimeMillis();
		try {
			String[] strarray = new Yaml().load(new FileInputStream(new File("conf/testYaml.yaml"))).toString()
					.split("URL:");
			List<String> features = Arrays.asList(strarray);

			features.forEach((url) -> {

				// 开启线程
				new Thread(() -> {
					try {
						// 获取取网络流
						ByteStreams.copy((InputStream) new URL(url).openStream(),
								new FileOutputStream("conf/" + time + ".txt"));
						File file = new File("conf/" + time + ".txt");
						if (file.isFile() && file.exists()) { // 判断文件是否存在
							// 考虑到编码格式
							BufferedReader bufferedReader = new BufferedReader(
									new InputStreamReader(new FileInputStream(file), "utf-8"));
							String lineTxt = null;

							while ((lineTxt = bufferedReader.readLine()) != null) {

								JSONObject obj = new JSONObject(lineTxt);
								// System.out.println(obj.toString());
								j(obj);

							}
							new InputStreamReader(new FileInputStream(file), "utf-8").close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).start();
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		ReaderTest t = new ReaderTest();
		t.getYamlFile();
	}

	public void j(JSONObject jsonObject) {
		Iterator<String> jsonObjectKeys = jsonObject.keys();
		String key;
		Object o;
		while (jsonObjectKeys.hasNext()) {
			key = jsonObjectKeys.next();
			try {
				o = jsonObject.get(key);

				if (o instanceof JSONObject) {
					JSONObject o2 = new JSONObject(o);
					j(o2);

				} else if (o instanceof JSONArray) {
					JSONArray js = (JSONArray) o;
					for (int i = 0; i < js.length(); i++) {
						j((JSONObject) js.get(i));
					}
				} else {
					System.out.println("KEY:" + key + " " + "vlue:" + o);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
