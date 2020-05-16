package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.WildcardType;
import java.util.Scanner;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SaveAndReadFile {
	// current question id
	public static int currid = -1;

	public static void SaveFile(int arr[][]) throws IOException {

		File file = new File("array.txt"); // file to store the array
		PrintWriter out = new PrintWriter(file);
		out.println(Main.nowid);
		// write the array into file
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				out.write(arr[i][j] + " ");
			}
			out.println();
		}
		out.close();

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("提示");
		alert.setHeaderText(null);
		alert.setContentText("成功保存");
		alert.showAndWait();
	}

	public static int[][] ReadFile() throws IOException {
		File file = new File("array.txt"); // file to read from
		int[][] result = new int[9][9];
		BufferedReader in = new BufferedReader(new FileReader(file));
		currid = Integer.parseInt(in.readLine().trim());
		String line;
		int row = 0;
		while ((line = in.readLine()) != null) {
			String[] temp = line.split(" ");
			for (int j = 0; j < temp.length; j++) {
				result[row][j] = Integer.parseInt((temp[j]));
			}
			row++;
		}
		in.close();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("提示");
		alert.setHeaderText(null);
		alert.setContentText("成功读取");
		alert.showAndWait();
		return result;
	}

	// write the time to complete the game and check if it exceeds the record
	public static boolean writeTimeAndCompare(int times) {
		Scanner in = null;
		PrintWriter fw = null;
		try {
			in = new Scanner(new File("time.txt"));
			int existingTime = Integer.parseInt(in.next());
			// check if it exceeds
			if (times < existingTime) {
				fw = new PrintWriter(new File("time.txt"));
				fw.print(times);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
			if (fw != null) {
				fw.close();
			}
		}
		return false;
	}

	// read the best record from time.txt
	public static String readTime() {
		try {
			Scanner in = new Scanner(new File("time.txt"));
			return in.next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
