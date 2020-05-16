package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class LoadOriginalTest {
	// current question id
	public static int id = -1;

	// get a quetion by difficulty
	public static int[][][] hashgetMap(int level) {
		Random r = new Random();
		int random = r.nextInt(10);
		return getMap(level * 10 + random);
	}

	// get a quetion by id
	public static int[][][] getMap(int id) {
		LoadOriginalTest.id = id;
		int[][] questionMap = new int[9][9];
		int[][] ansMap = new int[9][9];
		try (InputStream is = new FileInputStream(new File("numdata.json")); JsonReader rdr = Json.createReader(is)) {

			JsonObject obj = rdr.readObject();
			JsonArray jsqs = obj.getJsonArray("data");
			JsonArray jsas = obj.getJsonArray("ans");

			int[] tmp = new int[81];
			for (int i = 0; i < jsqs.size(); i++) {
				if (i == id) {
					tmp = toArray(jsqs.getValuesAs(JsonArray.class).get(i));
				}
			}
			int pointer = 0;
			for (int i = 0; i < 9; i++) {
				for (int k = 0; k < 9; k++) {
					questionMap[i][k] = tmp[pointer++];
				}
			}

			for (int i = 0; i < jsas.size(); i++) {
				if (i == id) {
					tmp = toArray(jsas.getValuesAs(JsonArray.class).get(i));
				}
			}

			pointer = 0;
			for (int i = 0; i < 9; i++) {
				for (int k = 0; k < 9; k++) {
					ansMap[i][k] = tmp[pointer++];
				}
			}
			return new int[][][] { questionMap, ansMap };

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static int[] toArray(JsonArray jsa) {
		int[] a = new int[jsa.size()];
		for (int i = 0; i < jsa.size(); i++)
			a[i] = jsa.getJsonNumber(i).intValue();
		return a;
	}
}
