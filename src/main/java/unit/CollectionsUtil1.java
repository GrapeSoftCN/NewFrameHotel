package unit;

import java.util.Collections;
import java.util.Comparator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.star.uno.RuntimeException;

public class CollectionsUtil1 {
	/**
	 * TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @param js
	 * @param feild
	 *            排序字段
	 * @param sort
	 *            1顺序 -1逆序
	 */
	@SuppressWarnings("unchecked")
	public static void sort_double(JSONArray js, String feild, int sort) {
		Collections.sort(js, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject o1, JSONObject o2) {
				double ob1 = (double) o1.get(feild);
				double ob2 = (double) o2.get(feild);
				if (sort == 1) {
					if (ob1 > ob2) {
						return -1;
					} else if (ob1 < ob2) {
						return 1;
					} else {
						return 0;
					}
				}
				if (sort == -1) {
					if (ob1 > ob2) {
						return 1;
					} else if (ob1 < ob2) {
						return -1;
					} else {
						return 0;
					}
				}
				throw new RuntimeException("sort非法");
			}
		});
	}
}
