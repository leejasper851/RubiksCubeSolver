import java.util.*;

public class CubeSolverRunner {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_WHITE = "\u001B[47m";
	public static final String ANSI_YELLOW = "\u001B[43m";
	public static final String ANSI_BLUE = "\u001B[44m";
	public static final String ANSI_GREEN = "\u001B[42m";
	public static final String ANSI_RED = "\u001B[41m";
	
	public static void main(String[] args) {
		System.out.println("For each face, you will orientate the cube a specific way and input nine letters in a row. Each character will be the first letter of the color of the corresponding square (white-w, yellow-y, red-r, orange-o, green-g, blue-b). This is the order:");
		System.out.println("|1|2|3|");
		System.out.println("|4|5|6|");
		System.out.println("|7|8|9|");
		System.out.println();
		
		System.out.println("For example, if the face looks like this:");
		System.out.println("|" + ANSI_BLUE + " " + ANSI_RESET + "|" + ANSI_YELLOW + " " + ANSI_RESET + "|" + ANSI_RED + " " + ANSI_RESET + "|");
		System.out.println("|" + ANSI_GREEN + " " + ANSI_RESET + "|" + ANSI_WHITE + " " + ANSI_RESET + "|" + ANSI_WHITE + " " + ANSI_RESET + "|");
		System.out.println("|" + ANSI_RED + " " + ANSI_RESET + "|" + ANSI_GREEN + " " + ANSI_RESET + "|" + ANSI_YELLOW + " " + ANSI_RESET + "|");
		System.out.println("You should type \"byrgwwrgy\".");
		System.out.println();
		
		String faceColsStr = "";
		boolean correct = true;
		Map<Character, Integer> strNums = Map.of('w', 0, 'g', 1, 'r', 2, 'b', 3, 'o', 4, 'y', 5);
		do {
			faceColsStr = "";
			String whiteFace = getFace("Turn to the face with the white center piece. Make sure the face with the blue center piece is above. List the colors: ", 'w');
			String greenFace = getFace("Turn to the face with the green center piece and the white center piece face on top. List the colors: ", 'g');
			String redFace = getFace("Turn to the face with the red center piece and the white center piece face on top. List the colors: ", 'r');
			String blueFace = getFace("Turn to the face with the blue center piece and the white center piece face on top. List the colors: ", 'b');
			String orangeFace = getFace("Turn to the face with the orange center piece and the white center piece face on top. List the colors: ", 'o');
			String yellowFace = getFace("Turn to the face with the yellow center piece and the green center piece face on top. List the colors: ", 'y');
			faceColsStr = whiteFace + greenFace + redFace + blueFace + orangeFace + yellowFace;
			
			correct = true;
			int[] colCounts = new int[6];
			for (int i = 0; i < 54; i++) {
				colCounts[strNums.get(faceColsStr.charAt(i))]++;
			}
			if (colCounts[0] != 9 || colCounts[1] != 9 || colCounts[2] != 9 || colCounts[3] != 9 || colCounts[4] != 9 || colCounts[5] != 9) {
				System.out.println("Make sure you enter nine total tiles of each color.\n");
				correct = false;
			}
		} while (!correct);
		
		Color[][][] faceCols = new Color[6][3][3];
		Map<Character, Color> strCols = Map.of('w', Color.WHITE, 'g', Color.GREEN, 'r', Color.RED, 'b', Color.BLUE, 'o', Color.ORANGE, 'y', Color.YELLOW);
		int charInd = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					faceCols[i][j][k] = strCols.get(faceColsStr.charAt(charInd));
					charInd++;
				}
			}
		}
		
		Cube cube = new Cube(faceCols);
		Piece[][][] pieces = cube.getPieces();
		
		System.out.println("Turn the cube so that the green-centered face is on the front and the white-centered face is on top. 'U' is upper face, 'F' is front face, 'R' is right face, 'D' is bottom face (down), 'B' is back face, and 'L' is left face. ' means one turn counterclockwise, '2' means two turns, and neither means one turn clockwise.\n");
		CubeSolver solver = new CubeSolver(cube);
		solver.solve();
	}
	
	private static String getFace(String message, char centerCol) {
		String faceCols = "";
		boolean correct = true;
		
		do {
			Scanner in = new Scanner(System.in);
			System.out.print(message);
			faceCols = in.nextLine();
			correct = true;
			System.out.println();
			
			for (int i = 0; i < faceCols.length(); i++) {
				if (faceCols.charAt(i) != 'w' && faceCols.charAt(i) != 'y' && faceCols.charAt(i) != 'g' && faceCols.charAt(i) != 'b' && faceCols.charAt(i) != 'r' && faceCols.charAt(i) != 'o') {
					System.out.println("You can only enter the letters 'w', 'y', 'g', 'b', 'r', and 'o'.\n");
					correct = false;
					break;
				}
			}
			if (!correct) {
				continue;
			}
			if (faceCols.length() != 9) {
				System.out.println("You must enter nine letters.\n");
				correct = false;
				continue;
			}
			if (faceCols.charAt(4) != centerCol) {
				System.out.println("Make sure you're entering in the correct center color.\n");
				correct = false;
				continue;
			}
		} while (!correct);
		
		return faceCols;
	}
}
