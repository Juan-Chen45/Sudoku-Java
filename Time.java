package application;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.util.GregorianCalendar;
import java.util.Calendar;

//used to count the time
public class Time extends Pane implements Runnable {

	private int hour = 0;
	private int minute = 0;
	private int second = 0;

	public Time() {
		Thread t = new Thread(this); // new a thread
		t.setDaemon(true); // make it as a daemon thread so the programe can exit
		t.start();
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}

	public void setTime(int hour1, int minute1, int second1) {
		this.hour = hour1;
		this.minute = minute1;
		this.second = second1 - 1;

	}

	// print the time on the pane
	public void PaintTime() {
		getChildren().clear();
		String timeString = "用时:00:" + "0" + getMinute() + ":" + getSecond();
		Text textTime = new Text(5, 10, timeString);
		getChildren().add(textTime);
	}

	// modify the time running
	public void Runtime() {
		second++;
		if (second == 60) {
			minute++;
			second = 0;
		}
		if (minute == 60) {
			hour++;
			minute = 0;
			int a = getHour();
			if (a > 12)
				a = a % 12;
			;
		}
		if (hour == 24) {
			hour = 0;
		}
	}

	@Override
	public void run() {

		while (true) {
			try {

				Runtime();
				// make it run on java application thread
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						PaintTime();
					}
				});
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				System.out.println("运行出错");
			}
		}
	}

}
