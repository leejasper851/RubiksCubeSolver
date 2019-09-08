import java.util.*;

public class Cube {
	private Piece[][][] pieces;
	
	public Cube(Color[][][] faceCols) {
		int[] colCounts = new int[6];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					colCounts[faceCols[i][j][k].index()]++;
				}
			}
		}
		if (colCounts[0] != 9 || colCounts[1] != 9 || colCounts[2] != 9 || colCounts[3] != 9 || colCounts[4] != 9 || colCounts[5] != 9) {
			throw new IllegalArgumentException("There must be nine tiles of each color.");
		}
		
		pieces = new Piece[3][3][3];
		pieces[0][0][0] = new CornerPiece(faceCols[0][0][0], Color.WHITE, faceCols[3][0][2], Color.BLUE, faceCols[4][0][0], Color.ORANGE);
		pieces[0][1][0] = new EdgePiece(faceCols[0][0][1], Color.WHITE, faceCols[3][0][1], Color.BLUE);
		pieces[0][2][0] = new CornerPiece(faceCols[0][0][2], Color.WHITE, faceCols[2][0][2], Color.RED, faceCols[3][0][0], Color.BLUE);
		pieces[1][0][0] = new EdgePiece(faceCols[0][1][0], Color.WHITE, faceCols[4][0][1], Color.ORANGE);
		pieces[1][1][0] = new CenterPiece(Color.WHITE);
		pieces[1][2][0] = new EdgePiece(faceCols[0][1][2], Color.WHITE, faceCols[2][0][1], Color.RED);
		pieces[2][0][0] = new CornerPiece(faceCols[0][2][0], Color.WHITE, faceCols[1][0][0], Color.GREEN, faceCols[4][0][2], Color.ORANGE);
		pieces[2][1][0] = new EdgePiece(faceCols[0][2][1], Color.WHITE, faceCols[1][0][1], Color.GREEN);
		pieces[2][2][0] = new CornerPiece(faceCols[0][2][2], Color.WHITE, faceCols[1][0][2], Color.GREEN, faceCols[2][0][0], Color.RED);
		
		pieces[0][0][1] = new EdgePiece(faceCols[3][1][2], Color.BLUE, faceCols[4][1][0], Color.ORANGE);
		pieces[0][1][1] = new CenterPiece(Color.BLUE);
		pieces[0][2][1] = new EdgePiece(faceCols[2][1][2], Color.RED, faceCols[3][1][0], Color.BLUE);
		pieces[1][0][1] = new CenterPiece(Color.ORANGE);
		pieces[1][1][1] = new NullPiece();
		pieces[1][2][1] = new CenterPiece(Color.RED);
		pieces[2][0][1] = new EdgePiece(faceCols[1][1][0], Color.GREEN, faceCols[4][1][2], Color.ORANGE);
		pieces[2][1][1] = new CenterPiece(Color.GREEN);
		pieces[2][2][1] = new EdgePiece(faceCols[1][1][2], Color.GREEN, faceCols[2][1][0], Color.RED);
		
		pieces[0][0][2] = new CornerPiece(faceCols[3][2][2], Color.BLUE, faceCols[4][2][0], Color.ORANGE, faceCols[5][2][0], Color.YELLOW);
		pieces[0][1][2] = new EdgePiece(faceCols[3][2][1], Color.BLUE, faceCols[5][2][1], Color.YELLOW);
		pieces[0][2][2] = new CornerPiece(faceCols[2][2][2], Color.RED, faceCols[3][2][0], Color.BLUE, faceCols[5][2][2], Color.YELLOW);
		pieces[1][0][2] = new EdgePiece(faceCols[4][2][1], Color.ORANGE, faceCols[5][1][0], Color.YELLOW);
		pieces[1][1][2] = new CenterPiece(Color.YELLOW);
		pieces[1][2][2] = new EdgePiece(faceCols[2][2][1], Color.RED, faceCols[5][1][2], Color.YELLOW);
		pieces[2][0][2] = new CornerPiece(faceCols[1][2][0], Color.GREEN, faceCols[4][2][2], Color.ORANGE, faceCols[5][0][0], Color.YELLOW);
		pieces[2][1][2] = new EdgePiece(faceCols[1][2][1], Color.GREEN, faceCols[5][0][1], Color.YELLOW);
		pieces[2][2][2] = new CornerPiece(faceCols[1][2][2], Color.GREEN, faceCols[2][2][0], Color.RED, faceCols[5][0][2], Color.YELLOW);
	}
	
	public Piece[][][] getPieces() {
		return pieces;
	}
	
	public void turn(Color face, int amount) {
		if (amount == 0) {
			return;
		}
		if (amount == -2) {
			amount = 2;
		}
		Piece[][][] changedPieces = new Piece[3][3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					if (pieces[i][j][k].containsFace(face)) {
						ArrayList<Color> fromFaces = new ArrayList<Color>();
						ArrayList<Color> toFaces = new ArrayList<Color>();
						for (Color nextFace : pieces[i][j][k].getColors().values()) {
							if (nextFace != face) {
								fromFaces.add(nextFace);
							}
						}
						for (Color fromFace : fromFaces) {
							toFaces.add(face.pivotColor(fromFace, amount));
						}
						pieces[i][j][k].assignColors(fromFaces, toFaces);
						
						int[] newCoords = turnCoords(i, j, k, face, amount);
						changedPieces[newCoords[0]][newCoords[1]][newCoords[2]] = pieces[i][j][k];
					}
				}
			}
		}
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					if (changedPieces[i][j][k] != null) {
						pieces[i][j][k] = changedPieces[i][j][k];
					}
				}
			}
		}
	}
	
	private int[] turnCoords(int x, int y, int z, Color face, int amount) {
		int[] coords = { x, y, z };
		switch (face) {
			case WHITE:
				coords = turnCoordsAmt(coords, 0, 1, amount, false);
				break;
			case YELLOW:
				coords = turnCoordsAmt(coords, 0, 1, amount, true);
				break;
			case RED:
				coords = turnCoordsAmt(coords, 2, 0, amount, true);
				break;
			case ORANGE:
				coords = turnCoordsAmt(coords, 2, 0, amount, false);
				break;
			case GREEN:
				coords = turnCoordsAmt(coords, 2, 1, amount, false);
				break;
			default:
				coords = turnCoordsAmt(coords, 2, 1, amount, true);
		}
		return coords;
	}
	
	private int[] turnCoordsAmt(int[] coords, int x, int y, int amount, boolean backwards) {
		if (backwards) {
			amount *= -1;
		}
		
		int xVal = coords[x] - 1;
		int yVal = coords[y] - 1;
		if (amount == 1) {
			coords[x] = yVal + 1;
			coords[y] = -xVal + 1;
		} else if (amount == -1) {
			coords[x] = -yVal + 1;
			coords[y] = xVal + 1;
		} else {
			coords[x] = -xVal + 1;
			coords[y] = -yVal + 1;
		}
		return coords;
	}
	
	public boolean isSolved() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 3; k++) {
					if (!pieces[i][j][k].inPlace()) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
