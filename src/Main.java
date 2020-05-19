package application;

import java.io.IOException;
import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	public static int queMap[][];// question map,with 0 for blank space
	public static int[][] maps = new int[9][9];// map with all numbers---answer map
	public static int nowid = -1;// the id of current question in json
	public static int ideaid = 3;// left chance to get a help
	public static TextField[][] text = new TextField[9][9];// text
	public static BorderPane pane = new BorderPane();
	public static ScrollPane outter = new ScrollPane();
	public static HBox hBox = new HBox(10);
	public static ComboBox<String> diff = new ComboBox<>();// get difficulty
	public static Time t = new Time();//used to show the time we use
	public static TextField lastChangedField = null;// for regret
	public static Button finish = new Button("提示 剩余次数:" + ideaid);
	public void start(Stage primaryStage) {
		// BorderPane 布局
		pane.setTop(getMenuBar());
		pane.setCenter(getHBox());
		pane.setBottom(getGridPane());
		outter.setContent(pane);
		Scene scene = new Scene(outter, 600, 400);
		primaryStage.setResizable(true);

		primaryStage.setTitle("Project--数独");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
		primaryStage.show();
	}

	// get the menu
	private MenuBar getMenuBar() {
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("文件");
		MenuItem saveMenuItem = new MenuItem("保存游戏");
		MenuItem readMenuItem = new MenuItem("读取游戏");
		MenuItem exitMenuItem = new MenuItem("退出");

		// exit
		exitMenuItem.setOnAction(actionEvent -> Platform.exit());

		// save to array.txt
		saveMenuItem.setOnAction(actionEvent -> {
			try {
				SaveAndReadFile.SaveFile(getCurrMap());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		// read from array.txt
		readMenuItem.setOnAction(actionEvent -> {
			try {
				ideaid = 3;
				pane.setBottom(ReadGridPane(SaveAndReadFile.ReadFile(), SaveAndReadFile.currid));
				finish.setText("提示 剩余次数:" + ideaid);
				nowid = SaveAndReadFile.currid;
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		fileMenu.getItems().addAll(saveMenuItem, readMenuItem, new SeparatorMenuItem(), exitMenuItem);
		menuBar.getMenus().addAll(fileMenu);
		return menuBar;
	}

	// get the Hbox
	private HBox getHBox() {

		hBox.setPadding(new Insets(10, 10, 10, 10));
		
		// get help from system
				finish = new Button("提示 剩余次数:" + ideaid);
				finish.setStyle("-fx-color:white");
				finish.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						if (ideaid == 0) {
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle("提示");
							alert.setHeaderText(null);
							alert.setContentText("已无提示次数");
							alert.showAndWait();
							return;
						}
						for (int i = 0; i < 9; i++) {
							for (int j = 0; j < 9; j++) {
								if (text[i][j].getText().isEmpty()) {
									text[i][j].setText("" + maps[i][j]);
									ideaid--;
									finish.setText("提示 剩余次数:" + ideaid);
									return;
								}
							}
						}
					}
				});


		
		// start a new game
		Button start = new Button("新建游戏");
		start.setStyle("-fx-color:white");
		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				t.setTime(0, 0, 0);
				ideaid = 3;
				pane.setBottom(getGridPane());
				finish.setText("提示 剩余次数:" + ideaid);
			}
		});

		hBox.getChildren().add(start);

		// difficulty
		diff.getItems().addAll("简单", "一般", "困难");
		diff.setStyle("-fx-color:white");
		diff.setValue("简单");
		hBox.getChildren().add(diff);

		// submit button
		Button submit = new Button("提交");
		submit.setStyle("-fx-color:white");
		SubmitButton submitButton = new SubmitButton();
		submit.setOnAction(submitButton);
		hBox.getChildren().add(submit);
		
		hBox.getChildren().add(finish);
		
		// regret
		Button regret = new Button("撤销");
		regret.setStyle("-fx-color:white");
		regret.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (lastChangedField == null) {
					return;
				} else {
					lastChangedField.setText("");
				}
			}
		});
		hBox.getChildren().add(regret);
		hBox.getChildren().add(t);
		// best record
		Text timeRecord = new Text(5, 10, "最佳纪录:" + SaveAndReadFile.readTime() + " 秒");
		hBox.getChildren().add(timeRecord);

		return hBox;
	}

	// GirdPane --used for new a game
	private GridPane getGridPane() {

		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(1);
		pane.setVgap(1);
		int[][][] result = LoadOriginalTest.hashgetMap(getDiff(diff));
		nowid = LoadOriginalTest.id;
		maps = result[1];
		queMap = result[0];
		for (int k = 0; k < 9; k++) {
			for (int n = 0; n < 9; n++) {
				text[k][n] = new TextField();
				text[k][n].setPrefSize(60, 60);
				text[k][n].setAlignment(Pos.CENTER);

				pane.add(text[k][n], n, k);
				if (queMap[k][n] != 0) {
					text[k][n].setText("" + maps[k][n]);
					text[k][n].setEditable(false);
				}
				text[k][n].setBackground(new Background(new BackgroundFill(getColor(maps[k][n]), null, null)));
			}
		}

		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				int row = i;
				int col = k;
				text[i][k].textProperty().addListener((observable, oldvalue, newvalue) -> {
					lastChangedField = text[row][col];
					int[][] m = getCurrMap();
					if (!check(row, col, m)) {
						text[row][col].setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
					} else {
						text[row][col].setBackground(
								new Background(new BackgroundFill(getColor(maps[row][col]), null, null)));
					}
				});
			}
		}
		setGap(pane);
		return pane;

	}

	// GridPane --used for new a game from data.txt
	private GridPane ReadGridPane(int[][] fileArr, int id) {
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(1);
		pane.setVgap(1);
		int[][][] result = LoadOriginalTest.getMap(id);
		maps = result[1];

		for (int k = 0; k < 9; k++) {
			for (int n = 0; n < 9; n++) {
				text[k][n] = new TextField();
				text[k][n].setPrefSize(60, 60);
				text[k][n].setAlignment(Pos.CENTER);

				pane.add(text[k][n], n, k);

				if (fileArr[k][n] != 0) {
					text[k][n].setText("" + fileArr[k][n]);
					text[k][n].setEditable(false);
				}
				text[k][n].setBackground(new Background(new BackgroundFill(getColor(maps[k][n]), null, null)));
			}
		}

		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				int row = i;
				int col = k;
				text[i][k].textProperty().addListener((observable, oldvalue, newvalue) -> {
					lastChangedField = text[row][col];
					int[][] m = getCurrMap();
					if (!check(row, col, m)) {
						text[row][col].setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
					} else {
						text[row][col].setBackground(
								new Background(new BackgroundFill(getColor(maps[row][col]), null, null)));
					}
				});
			}
		}
		setGap(pane);
		return pane;
	}

	// get difficulty
	private int getDiff(ComboBox<String> diff) {
		String n = diff.getValue();
		int i = 0;
		if (n.equals("简单")) {
			i = 0;
		}
		if (n.equals("一般")) {
			i = 1;
		}
		if (n.equals("困难")) {
			i = 2;
		}
		return i;
	}

	// get color to decorate the textfield
	public static Color getColor(int i) {
		Color color = Color.PINK;
		switch (i) {
		case 1:
			color = new Color(1, 1, 0.87, 1.0);
			break;
		case 2:
			color = new Color(0.8, 1, 1, 1.0);
			break;
		case 3:
			color = new Color(1, 0.8, 0.8, 1.0);
			break;
		case 4:
			color = new Color(1, 0.8, 0.6, 1.0);
			break;
		case 5:
			color = new Color(0.8, 1, 0.6, 1.0);
			break;
		case 6:
			color = new Color(0.8, 0.8, 0.8, 1.0);
			break;
		case 7:
			color = new Color(1, 0.8, 0.8, 1.0);
			break;
		case 8:
			color = new Color(1, 1, 1, 1.0);
			break;
		case 9:
			color = new Color(0.6, 1, 0.6, 1.0);
			break;
		default:
			break;
		}
		return color;
	}

	// use to set the gap of the gridPane
	@SuppressWarnings("all")
	public void setGap(GridPane pane) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (j != 2)
					pane.setMargin(text[j][2], new Insets(0, 12, 0, 0));
				if (j != 5)
					pane.setMargin(text[j][5], new Insets(0, 12, 0, 0));
				if (i != 2)
					pane.setMargin(text[2][i], new Insets(0, 0, 12, 0));
				if (i != 5)
					pane.setMargin(text[5][i], new Insets(0, 0, 12, 0));
				pane.setMargin(text[2][2], new Insets(0, 12, 12, 0));
				pane.setMargin(text[5][2], new Insets(0, 12, 12, 0));
				pane.setMargin(text[2][5], new Insets(0, 12, 12, 0));
				pane.setMargin(text[5][5], new Insets(0, 12, 12, 0));
			}
		}
	}

	// used to check if its right after clicking the submit button
	public boolean finalCheck() {

		int[][] map = getCurrMap();
		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				if (map[i][k] < 1 || map[i][k] > 9) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("提示");
					alert.setHeaderText(null);
					alert.setContentText("填入数据有误");
					alert.showAndWait();
					return false;
				}
			}
		}
		HashMap<Integer, Integer>[] rows = new HashMap[9];
		HashMap<Integer, Integer>[] columns = new HashMap[9];
		HashMap<Integer, Integer>[] boxes = new HashMap[9];

		for (int i = 0; i < 9; i++) {
			rows[i] = new HashMap<Integer, Integer>();
			columns[i] = new HashMap<Integer, Integer>();
			boxes[i] = new HashMap<Integer, Integer>();
		}

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				int n = map[i][j];
				int box_index = (i / 3) * 3 + j / 3;

				rows[i].put(n, rows[i].getOrDefault(n, 0) + 1);
				columns[j].put(n, columns[j].getOrDefault(n, 0) + 1);
				boxes[box_index].put(n, boxes[box_index].getOrDefault(n, 0) + 1);

				if (rows[i].get(n) > 1) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("提示");
					alert.setHeaderText(null);
					alert.setContentText("横排重复");
					alert.showAndWait();
					return false;
				} else if (columns[j].get(n) > 1) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("提示");
					alert.setHeaderText(null);
					alert.setContentText("竖排重复");
					alert.showAndWait();
					return false;
				} else if (boxes[box_index].get(n) > 1) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("提示");
					alert.setHeaderText(null);
					alert.setContentText("填入有误");
					alert.showAndWait();
					return false;
				}
			}
		}
		return true;
	}

	// get current map from textfield
	private int[][] getCurrMap() {
		int result[][] = new int[9][9];
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++) {
				if (!text[i][j].getText().isEmpty())
					result[i][j] = Integer.parseInt(text[i][j].getText().trim());
			}
		return result;
	}

	// a concrete class for submit
	class SubmitButton implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			boolean ifFinished = true;
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if (text[i][j].getText().isEmpty() && ifFinished) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("提示");
						alert.setHeaderText(null);
						alert.setContentText("游戏未填完");
						alert.showAndWait();
						ifFinished = false;
					}
				}
			}
			if (ifFinished) {
				if (!finalCheck()) {
					return;
				}
				boolean ifExceed = SaveAndReadFile.writeTimeAndCompare(t.getMinute() * 60 + t.getSecond());
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("提示");
				alert.setHeaderText(null);
				alert.setContentText("恭喜你正确完成游戏");
				alert.showAndWait();
				if (ifExceed) {
					Text record = (Text) hBox.getChildren().get(hBox.getChildren().size() - 1);
					record.setText("最佳纪录:" + SaveAndReadFile.readTime() + " 秒");
					Alert timeAlertExceed = new Alert(AlertType.INFORMATION);
					timeAlertExceed.setTitle("提示");
					timeAlertExceed.setHeaderText(null);
					timeAlertExceed.setContentText("恭喜打破先前时间记录！");
					timeAlertExceed.showAndWait();
				} else {
					Alert timeAlert = new Alert(AlertType.INFORMATION);
					timeAlert.setTitle("提示");
					timeAlert.setHeaderText(null);
					timeAlert.setContentText("很遗憾，并未打破纪录！");
					timeAlert.showAndWait();
				}

			}
		}
	}

	// used to dynamically check the current map
	private boolean check(int i, int j, int[][] map) {
		for (int k = 0; k < 9; k++) {
			if (map[i][j] > 9 || map[i][j] < 1) {
				return false;
			}
			if (map[i][j] == map[i][k] && j != k) {
				return false;
			}
			if (map[i][j] == map[k][j] && i != k) {
				return false;
			}

			int x = (i / 3) * 3 + k / 3;
			int y = (j / 3) * 3 + k % 3;
			if (map[i][j] == map[x][y] && !(i == x && j == y)) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}