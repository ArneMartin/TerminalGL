package terminalGL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	private static Scanner scanner = new Scanner(System.in);
	private static char[][] screen;
	public static boolean isLinux;
	private static int width;
	private static int height;
	public static int xMax;
	public static int yMax;

	public static void main(String[] args) {
		propertyInitialisation();
		prepareScreen();

		while (true) {
			flushScreen();
			System.out.print(makeScreen());

			String input = scanner.nextLine();
			char action = (input.isEmpty()) ? ' ' : input.charAt(0);

			if (action == 'q')
				break;
		}

		scanner.close();
	}

	private static void propertyInitialisation() {

		if (new File("setup.txt").exists()) {
			System.out.println("\nDo you want to load the previous screen settings? If so, press enter. Otherwise, write something and then press enter.");

			if (scanner.nextLine().isEmpty()) {
				setupFromFile();
				return;
			}
		}

		setup();
		scanner.nextLine();
	}

	private static void setup() {
		File setup = new File("setup.txt");

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(setup))) {
			System.out.println("\nPlease note that this program will create a text file called 'setup.txt' in the active working directory. Do with it as you please after the program has terminated.");

			System.out.println("Please maximise your terminal window and press enter.");
			scanner.nextLine();

			System.out.println("Please input '1' if you're running Windows or '2' if you're running Linux.");
			isLinux = scanner.nextInt() == 2;
			writer.write("isLinux: " + isLinux);
			writer.newLine();

			System.out.println("\nPlease SPAM YOUR KEYBOARD as calmly as possible until your rambling covers the entire length of your terminal, or simply write the length if you already know it."
					+ "\nWhen you're finished with either; press enter.");
			String length = scanner.next();

			if (length.length() < 5 && Integer.parseInt(length) > 42)
				width = Integer.parseInt(length);
			else
				width = length.length();

			writer.write("width: " + width);
			writer.newLine();
			xMax = width - 3;

			System.out.println("\nThe width of your terminal window is: " + width + ". Press enter.\n");
			scanner.nextLine();

			for (int i = 0; i <= 123; i++)
				System.out.println(i);

			System.out.println("\nNow, please write the number that's shown on the top line of your terminal window and press enter.");
			height = 126 - scanner.nextInt();
			writer.write("height: " + height);
			writer.newLine();
			yMax = height - 3;

			System.out.println("\nSetup is now finished and the main screen will be shown."
					+ "\nFrom now on, press enter to show the next frame (keep it pressed to enter Superfast Mode™) or enter 'q' to quit."
					+ "\nPlease press enter one last time.");
			scanner.nextLine();
		} catch (IOException e) {
			System.out.println("Unable to write to file: " + setup.toString());
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static void setupFromFile() {
		File setup = new File("setup.txt");

		try (BufferedReader reader = new BufferedReader(new FileReader(setup))) {
			isLinux = (boolean) getPropertyValue(reader.readLine());
			width = (int) getPropertyValue(reader.readLine());
			xMax = width - 3;
			height = (int) getPropertyValue(reader.readLine());
			yMax = height - 3;
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + setup.toString());
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Unable to read file: " + setup.toString());
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static Object getPropertyValue(String line) {
		int valueStartIndex = line.indexOf(':');
		String value = line.substring(valueStartIndex + 1).trim();

		switch (line.substring(0, valueStartIndex)) {
			case "isLinux":
				return Boolean.valueOf(value);
			case "width":
				return Integer.valueOf(value);
			case "height":
				return Integer.valueOf(value);
			default:
				return null;
		}
	}

	private static void inputActionQueueHandler() {
		// TODO Yeah, just write, like, ANYTHING here, then that'd be great
	}

	private static void prepareScreen() {
		screen = new char[height][width];

		// Border corners
		screen[height - 1][0] = '╔';
		screen[height - 1][width - 1] = '╗';
		screen[0][0] = '╚';
		screen[0][width - 1] = '╝';

		// Upper and lower borders
		for (int col = 1; col < width - 1; col++) {
			screen[height - 1][col] = '═';
			screen[0][col] = '═';
		}

		// Left and right borders
		for (int row = 1; row < height - 1; row++) {
			screen[row][0] = '║';
			screen[row][width - 1] = '║';
		}

		flushScreen();
	}

	private static void flushScreen() {
		// Fills the screen with spaces
		for (int row = 1; row < height - 1; row++) {

			for (int col = 1; col < width - 1; col++) {
				screen[row][col] = ' ';
			}
		}
	}

	private static float angle = 0;

	private static StringBuffer makeScreen() {
		drawSquare(10, 5, 10);
		drawRotatingSquare(70, 11, 20, 5);
		drawQuadrilateral(15, 20, 22, 23, 40, 19, 20, 30);
		drawCircle(170, 40, 22);
		drawLine(80, 60, 5, angle);
		angle += 5;

		StringBuffer screenBuffer = new StringBuffer();

		for (int row = height - 1; row >= 0; row--) {

			for (int col = 0; col < width; col++) {
				screenBuffer.append(screen[row][col]);
			}

			if (isLinux)
				screenBuffer.append("\n");
		}

		return screenBuffer;
	}

	public static void putPixel(int x, int y) {
		putPixel(x, y, '*');
	}

	public static void putPixel(int x, int y, char c) {

		try {

			if (x < 0 || x > xMax || y < 0 || y > yMax)
				throw new ArrayIndexOutOfBoundsException();

			screen[++y][++x] = c;
		} catch (ArrayIndexOutOfBoundsException e) {
			displayMessage("Tried to write outside of the designated screen area: (x: " + x + ", y: " + y + ")");
		}
	}

	public static void drawLine(int startX, int startY, int endX, int endY) {

		if (endX - startX < 0) {
			// Swap startX and endX
			startX = startX ^ endX;
			endX = startX ^ endX;
			startX = startX ^ endX;
			// Swap startY and endY
			startY = startY ^ endY;
			endY = startY ^ endY;
			startY = startY ^ endY;
		}

		float deltaX = endX - startX;
		float deltaY = endY - startY;
		float signumY = Math.signum(deltaY);
		float error = 0;
		float deltaError = (deltaX == 0) ? Math.abs(deltaY) + 1 : Math.abs(deltaY / deltaX);
		int y = startY;

		// Finds and fills all pixels between the two points
		for (int x = startX; x <= endX; x++) {
			putPixel(x, y);
			error += deltaError;

			while (error >= 0.5 && signumY * y <= signumY * endY) {
				putPixel(x, y);
				y += signumY;
				error--;
			}
		}
	}

	public static void drawLine(int x, int y, int length, float angle) {
		boolean even = length % 2 == 0;

		if (even)
			length++;

		angle = (float) Math.toRadians(angle);
		int extendedX = (int) (Math.cos(angle) * (length / 2f));
		int extendedY = (int) (Math.sin(angle) * (length / 2f));
		int x0 = x - (extendedX - ((even) ? (int) Math.signum(extendedX) : 0));
		int y0 = y - (extendedY - ((even) ? (int) Math.signum(extendedY) : 0));
		int x1 = x + extendedX;
		int y1 = y + extendedY;
		drawLine(x0, y0, x1, y1);
	}

	public static void drawSquare(int bottomLeftX, int bottomLeftY, int size) {
		size--;
		// Bottom left point (starting point)
		int x0 = bottomLeftX;
		int y0 = bottomLeftY;
		// Bottom right point
		int x1 = x0 + size * 2 + 1;
		int y1 = y0;
		// Top right point
		int x2 = x1;
		int y2 = y1 + size;
		// Top left point
		int x3 = x2 - size * 2 - 1;
		int y3 = y2;

		drawLine(x0, y0, x1, y1);
		drawLine(x1, y1, x2, y2);
		drawLine(x2, y2, x3, y3);
		drawLine(x3, y3, x0, y0);
	}

	public static void drawRotatingSquare(int bottomLeftX, int bottomLeftY, int size, float rotationSpeed) {
		// Does not actually rotate.. yet..

		size--;
		// Bottom left point (starting point)
		int x0 = bottomLeftX;
		int y0 = bottomLeftY;
		// Bottom right point
		int x1 = x0 + size * 2 + 1;
		int y1 = y0;
		// Top right point
		int x2 = x1;
		int y2 = y1 + size;
		// Top left point
		int x3 = x2 - size * 2 - 1;
		int y3 = y2;

		drawLine(x0, y0, x1, y1);
		drawLine(x1, y1, x2, y2);
		drawLine(x2, y2, x3, y3);
		drawLine(x3, y3, x0, y0);
	}

	public static void drawQuadrilateral(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3) {
		drawLine(x0, y0, x1, y1);
		drawLine(x1, y1, x2, y2);
		drawLine(x2, y2, x3, y3);
		drawLine(x3, y3, x0, y0);
	}

	public static void drawCircle(int x0, int y0, int radius) {
		int x = radius;
		int y = 0;
		int radiusError = 1 - x;

		while (x >= y) {
			putPixel(x + x0, y + y0);
			putPixel(y + x0, x + y0);
			putPixel(-x + x0, y + y0);
			putPixel(-y + x0, x + y0);
			putPixel(-x + x0, -y + y0);
			putPixel(-y + x0, -x + y0);
			putPixel(x + x0, -y + y0);
			putPixel(y + x0, -x + y0);
			y++;

			if (radiusError < 0) {
				radiusError += 2 * y + 1;
			} else {
				x--;
				radiusError += 2 * (y - x) + 1;
			}
		}
	}

	public static void displayMessage(String message) {
		// TODO Support for multiple and multi-line messages, and messages expiring after a certain amount of time

		for (int i = 0; i < message.length(); i++)
			putPixel(i, 0, message.charAt(i));
	}
}
