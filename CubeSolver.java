import java.util.*;

public class CubeSolver {
	private Cube cube;
	private boolean firstTurn;
	private ArrayList<Color> movesCol;
	private ArrayList<Integer> movesAmt;
	
	public CubeSolver(Cube c) {
		cube = c;
		firstTurn = true;
		movesCol = new ArrayList<Color>();
		movesAmt = new ArrayList<Integer>();
	}
	
	public void solve() {
		cross();
		f2l();
		eoll();
		ocll();
		cpll();
		epll();
		combTurns();
		printTurns();
	}
	
	private void combTurns() {
		boolean toComb = true;
		while (toComb) {
			toComb = false;
			for (int i = 0; i < movesCol.size()-1; i++) {
				if (movesCol.get(i) == movesCol.get(i + 1)) {
					toComb = true;
					Color col = movesCol.get(i);
					int newAmt = movesAmt.get(i) + movesAmt.get(i + 1);
					if (newAmt < -1) {
						newAmt += 4;
					}
					if (newAmt > 2) {
						newAmt -= 4;
					}
					
					for (int j = 0; j < 2; j++) {
						movesCol.remove(i);
						movesAmt.remove(i);
					}
					if (newAmt != 0) {
						movesCol.add(i, col);
						movesAmt.add(i, newAmt);
					}
				}
			}
		}
	}
	
	private void printTurns() {
		if (movesCol.size() == 1) {
			System.out.print("Solution (1 move): ");
		} else {
			System.out.print("Solution (" + movesCol.size() + " moves): ");
		}
		
		Map<Color, String> faceDir = Map.of(Color.WHITE, "U", Color.YELLOW, "D", Color.RED, "R", Color.ORANGE, "L", Color.GREEN, "F", Color.BLUE, "B");
		for (int i = 0; i < movesCol.size(); i++) {
			Color face = movesCol.get(i);
			int amount = movesAmt.get(i);
			
			if (firstTurn) {
				firstTurn = false;
			} else {
				System.out.print(" ");
			}
			
			System.out.print(faceDir.get(face));
			if (amount == -1) {
				System.out.print("'");
			} else if (amount == 2) {
				System.out.print("2");
			}
		}
		System.out.println();
	}
	
	private void turn(Color face, int amount) {
		if (amount == 0) {
			return;
		}
		if (amount == -2) {
			amount = 2;
		}
		
		movesCol.add(face);
		movesAmt.add(amount);
		
		cube.turn(face, amount);
	}
	
	private void cross() {
		Map<Color, Boolean> crossPlaced = new HashMap<Color, Boolean>();
		crossPlaced.put(Color.GREEN, false);
		crossPlaced.put(Color.RED, false);
		crossPlaced.put(Color.BLUE, false);
		crossPlaced.put(Color.ORANGE, false);
		
		while (crossCount(crossPlaced) != 4) {
			for (int k = 2; k >= 0; k--) {
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						Piece piece = cube.getPieces()[i][j][k];
						Map<Color, Color> pieceCols = piece.getColors();
						if (piece instanceof EdgePiece && pieceCols.keySet().contains(Color.YELLOW) && !piece.inPlace()) {
							Color otherCol = Color.WHITE;
							for (Color col : Color.YELLOW.getAdj()) {
								if (pieceCols.keySet().contains(col)) {
									otherCol = col;
								}
							}
							Color yelFace = pieceCols.get(Color.YELLOW);
							Color otherFace = pieceCols.get(otherCol);
							
							// piece is on bottom with yellow side facing down
							if (yelFace == Color.YELLOW) {
								int diff = Color.WHITE.findPivot(otherFace, otherCol);
								
								// solve with a single turn
								if (crossCount(crossPlaced) == 0) {
									diff *= -1;
									turn(Color.YELLOW, diff);
								// solve by bringing to the top and back down
								} else {
									turn(otherFace, 2);
									turn(Color.WHITE, diff);
									turn(otherCol, 2);
								}
							// piece is on bottom with yellow side not facing down
							} else if (k == 2) {
								// piece is in the right position, just swapped
								if (otherCol == yelFace) {
									turn(yelFace, 1);
									turn(Color.YELLOW, -1);
									turn(Color.WHITE.pivotColor(yelFace, 1), 1);
									turn(Color.YELLOW, 1);
								// piece needs to be in the opposite position of the bottom face
								} else if (otherCol == yelFace.complement()) {
									turn(yelFace, 1);
									turn(Color.YELLOW, 1);
									turn(Color.WHITE.pivotColor(yelFace, 1), 1);
									turn(Color.YELLOW, -1);
								// piece needs to be turned 90 degrees counterclockwise on the bottom face
								} else if (otherCol == Color.WHITE.pivotColor(yelFace, 1)) {
									turn(yelFace, 1);
									turn(Color.WHITE.pivotColor(yelFace, 1), 1);
								// piece needs to be turned 90 degrees clockwise on the bottom face
								} else {
									turn(yelFace, -1);
									turn(Color.WHITE.pivotColor(yelFace, -1), -1);
								}
							// piece is on the second layer
							} else if (k == 1) {
								int diff = Color.WHITE.findPivot(otherFace, otherCol);
								
								boolean isLeft = Color.WHITE.inOrder(yelFace, otherFace);
								
								turn(Color.YELLOW, diff);
								// the non-yellow side is to the left of its face
								if (isLeft) {
									turn(otherFace, -1);
								// the non-yellow side is to the right of its face
								} else {
									turn(otherFace, 1);
								}
								turn(Color.YELLOW, -diff);
							// piece is on top layer with yellow side facing up
							} else if (yelFace == Color.WHITE) {
								int diff = Color.WHITE.findPivot(otherFace, otherCol);
								
								turn(Color.WHITE, diff);
								turn(otherCol, 2);
							// piece is on top layer with yellow side not facing up
							} else {
								Color curYelFace = yelFace;
								if (yelFace == otherCol || yelFace == otherCol.complement()) {
									turn(Color.WHITE, 1);
									curYelFace = Color.WHITE.pivotColor(yelFace, 1);
								}
								
								boolean isLeft = Color.WHITE.inOrder(curYelFace, otherCol);
								
								// yellow side's face is to the left of the non-yellow side's face
								if (isLeft) {
									turn(curYelFace, 1);
									turn(otherCol, -1);
									if (crossPlaced.get(curYelFace)) {
										turn(curYelFace, -1);
									}
								// yellow side's face is to the right of the non-yellow side's face
								} else {
									turn(curYelFace, -1);
									turn(otherCol, 1);
									if (crossPlaced.get(curYelFace)) {
										turn(curYelFace, 1);
									}
								}
							}
							
							crossCount(crossPlaced);
						}
					}
				}
			}
		}
	}
	
	private int crossCount(Map<Color, Boolean> crossPlaced) {
		int[][] crossCoords = { {0,1}, {1,0}, {1,2}, {2,1} };
		Color[] coordCols = { Color.BLUE, Color.ORANGE, Color.RED, Color.GREEN };
		int count = 0;
		for (int i = 0; i < 4; i++) {
			int[] crossCoord = crossCoords[i];
			if (cube.getPieces()[crossCoord[0]][crossCoord[1]][2].inPlace()) {
				count++;
				crossPlaced.put(coordCols[i], true);
			}
		}
		return count;
	}
	
	private void f2l() {
		while (f2lCount() != 4) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < 3; k++) {
						Piece corner = cube.getPieces()[i][j][k];
						Map<Color, Color> cornerCols = corner.getColors();
						if (corner instanceof CornerPiece && !cornerCols.keySet().contains(Color.WHITE) && (!corner.inPlace() || !cube.getPieces()[i][j][1].inPlace())) {
							Color[] otherCols = new Color[2];
							int colInd = 0;
							for (Color col : Color.YELLOW.getAdj()) {
								if (cornerCols.keySet().contains(col)) {
									otherCols[colInd] = col;
									colInd++;
								}
							}
							
							Piece edge = new EdgePiece(Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE);
							int edgeX = 0;
							int edgeY = 0;
							int edgeZ = 0;
							for (int l = 0; l < 3; l++) {
								for (int m = 0; m < 3; m++) {
									for (int n = 0; n < 3; n++) {
										Piece tempPiece = cube.getPieces()[l][m][n];
										if (tempPiece instanceof EdgePiece && tempPiece.getColors().keySet().contains(otherCols[0]) && tempPiece.getColors().keySet().contains(otherCols[1])) {
											edge = tempPiece;
											edgeX = l;
											edgeY = m;
											edgeZ = n;
										}
									}
								}
							}
							Map<Color, Color> edgeCols = edge.getColors();
							
							// corner and edge both on top, yellow side not facing up
							if (k == 0 && cornerCols.get(Color.YELLOW) != Color.WHITE && edgeZ == 0) {
								Color otherEdgeFace = Color.WHITE;
								for (Color edgeFace : edgeCols.values()) {
									if (edgeFace != Color.WHITE) {
										otherEdgeFace = edgeFace;
									}
								}
								
								if (cornerCols.get(otherCols[0]) != Color.WHITE) {
									Color tempCol = otherCols[0];
									otherCols[0] = otherCols[1];
									otherCols[1] = tempCol;
								}
								
								Color[] otherColsOrder = new Color[2];
								if (Color.WHITE.inOrder(otherCols[0], otherCols[1])) {
									otherColsOrder[0] = otherCols[0];
									otherColsOrder[1] = otherCols[1];
								} else {
									otherColsOrder[0] = otherCols[1];
									otherColsOrder[1] = otherCols[0];
								}
								
								boolean yelRight = Color.WHITE.inOrder(cornerCols.get(otherCols[1]), cornerCols.get(Color.YELLOW));
								int rightAmt1 = 1;
								Color rightColOrder0 = otherColsOrder[0];
								Color rightColOrder1 = otherColsOrder[1];
								if (!yelRight) {
									rightAmt1 = -1;
									rightColOrder0 = otherColsOrder[1];
									rightColOrder1 = otherColsOrder[0];
								}
								
								boolean adjPieces = (i == edgeX || j == edgeY);
								boolean sameColsUp = (otherCols[0] == edge.pieceColor(Color.WHITE));
								
								alignTopCorner(corner, otherCols[0], otherCols[1]);
								
								// pieces aren't adjacent, colors facing up are different, and edge piece isn't opposite the yellow side of corner piece
								if (!adjPieces && !sameColsUp && cornerCols.get(Color.YELLOW).complement() != otherEdgeFace) {
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, -rightAmt1);
								// pieces are adjacent, colors facing up are the same, and edge piece isn't on the same face as yellow side of corner piece
								} else if (adjPieces && sameColsUp && cornerCols.get(Color.YELLOW) != otherEdgeFace) {
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder0, -rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder0, rightAmt1);
								// pieces are adjacent, colors facing up are different, and edge piece is on the same face as yellow side of corner piece
								} else if (adjPieces && !sameColsUp && cornerCols.get(Color.YELLOW) == otherEdgeFace) {
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder1, -rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, -rightAmt1);
								// pieces aren't adjacent, colors facing up are different, and edge piece is opposite the yellow side of corner piece
								} else if (!adjPieces && !sameColsUp) {
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, -rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, -rightAmt1);
								// pieces are adjacent, colors facing up are different, and edge piece isn't on the same face as yellow side of corner piece
								} else if (!sameColsUp) {
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder0, -rightAmt1);
									turn(Color.WHITE, 2);
									turn(rightColOrder0, rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, -rightAmt1);
								// pieces are adjacent, colors facing up are the same, and edge piece is on the same face as yellow side of corner piece
								} else if (adjPieces) {
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder1, -rightAmt1);
									turn(Color.WHITE, 2);
									turn(rightColOrder0, -rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder0, rightAmt1);
								// pieces aren't adjacent, colors facing up are the same, and edge piece isn't opposite the yellow side of corner piece
								} else if (cornerCols.get(Color.YELLOW).complement() != otherEdgeFace) {
									for (int l = 0; l < 2; l++) {
										turn(Color.WHITE, rightAmt1);
										turn(rightColOrder0, -rightAmt1);
										turn(Color.WHITE, 2);
										turn(rightColOrder0, rightAmt1);
									}
								// pieces aren't adjacent, colors facing up are the same, and edge piece is opposite the yellow side of corner piece
								} else {
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder0, -rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder0, rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder0, -rightAmt1);
									turn(Color.WHITE, 2);
									turn(rightColOrder0, rightAmt1);
								}
							// corner and edge both on top, yellow side facing up
							} else if (k == 0 && edgeZ == 0) {
								Color otherEdgeFace = Color.WHITE;
								for (Color edgeFace : edgeCols.values()) {
									if (edgeFace != Color.WHITE) {
										otherEdgeFace = edgeFace;
									}
								}
								
								Color[] otherColsOrder = new Color[2];
								if (Color.WHITE.inOrder(otherCols[0], otherCols[1])) {
									otherColsOrder[0] = otherCols[0];
									otherColsOrder[1] = otherCols[1];
								} else {
									otherColsOrder[0] = otherCols[1];
									otherColsOrder[1] = otherCols[0];
								}
								
								Color edgeColSide = edge.pieceColor(otherEdgeFace);
								
								boolean edgeRight = (otherEdgeFace == cornerCols.get(otherColsOrder[0]) || Color.WHITE.inOrder(cornerCols.get(otherColsOrder[0]), otherEdgeFace));
								
								Color corrCol = otherColsOrder[1];
								if (edgeRight) {
									corrCol = otherColsOrder[0];
								}
								
								int rightAmt1 = 1;
								Color rightColOrder0 = otherColsOrder[0];
								Color rightColOrder1 = otherColsOrder[1];
								if (!edgeRight) {
									rightAmt1 = -1;
									rightColOrder0 = otherColsOrder[1];
									rightColOrder1 = otherColsOrder[0];
								}
								
								boolean adjPieces = (i == edgeX || j == edgeY);
								
								alignTopCorner(corner, otherCols[0], otherCols[1]);
								
								// pieces are adjacent and side colors are different
								if (adjPieces && corrCol != edgeColSide) {
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, 2);
									turn(rightColOrder1, -rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, -rightAmt1);
								// pieces aren't adjacent and side colors are different
								} else if (corrCol != edgeColSide) {
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, 2);
									turn(rightColOrder1, -rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder1, -rightAmt1);
								// pieces aren't adjacent and side colors are the same
								} else if (!adjPieces) {
									turn(Color.WHITE, 2);
									turn(rightColOrder0, -rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder0, rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder0, -rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder0, rightAmt1);
								// pieces are adjacent and side colors are the same
								} else {
									turn(rightColOrder0, -rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder0, rightAmt1);
									turn(Color.WHITE, 2);
									turn(rightColOrder0, -rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder0, rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder0, -rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder0, rightAmt1);
								}
							// corner on top, edge in middle
							} else if (k == 0) {
								boolean yelUp = (cornerCols.get(Color.YELLOW) == Color.WHITE);
								
								Color[] otherColsOrder = new Color[2];
								if (Color.WHITE.inOrder(otherCols[0], otherCols[1])) {
									otherColsOrder[0] = otherCols[0];
									otherColsOrder[1] = otherCols[1];
								} else {
									otherColsOrder[0] = otherCols[1];
									otherColsOrder[1] = otherCols[0];
								}
								
								boolean edgeOrdered = Color.WHITE.inOrder(edgeCols.get(otherColsOrder[0]), edgeCols.get(otherColsOrder[1]));
								
								Color[] begColsOrder = new Color[2];
								if (edgeOrdered) {
									begColsOrder[0] = edgeCols.get(otherColsOrder[0]);
									begColsOrder[1] = edgeCols.get(otherColsOrder[1]);
								} else {
									begColsOrder[0] = edgeCols.get(otherColsOrder[1]);
									begColsOrder[1] = edgeCols.get(otherColsOrder[0]);
								}
								
								alignTopCorner(corner, begColsOrder[0], begColsOrder[1]);
								
								if (!yelUp) {
									if (cornerCols.get(otherCols[0]) != Color.WHITE) {
										Color tempCol = otherCols[0];
										otherCols[0] = otherCols[1];
										otherCols[1] = tempCol;
									}
									
									boolean yelRight = Color.WHITE.inOrder(cornerCols.get(otherCols[1]), cornerCols.get(Color.YELLOW));
									
									int rightAmt1 = 1;
									Color rightBegOrder0 = begColsOrder[0];
									Color rightBegOrder1 = begColsOrder[1];
									Color rightColOrder0 = otherColsOrder[0];
									Color rightColOrder1 = otherColsOrder[1];
									if (!yelRight) {
										rightAmt1 = -1;
										rightBegOrder0 = begColsOrder[1];
										rightBegOrder1 = begColsOrder[0];
										rightColOrder0 = otherColsOrder[1];
										rightColOrder1 = otherColsOrder[0];
									}
									
									// yellow side of corner piece isn't facing up and edge piece is orientated correctly
									if (edgeOrdered) {
										turn(Color.WHITE, rightAmt1);
										turn(rightBegOrder0, -rightAmt1);
										turn(Color.WHITE, rightAmt1);
										turn(rightBegOrder0, rightAmt1);
										alignTopCorner(corner, otherCols[0], otherCols[1]);
										turn(Color.WHITE, 2);
										turn(rightColOrder0, -rightAmt1);
										turn(Color.WHITE, 2);
										turn(rightColOrder0, rightAmt1);
									// yellow side of corner piece isn't facing up and edge piece isn't orientated correctly
									} else {
										turn(Color.WHITE, rightAmt1);
										turn(rightBegOrder0, -rightAmt1);
										turn(Color.WHITE, -rightAmt1);
										turn(rightBegOrder0, rightAmt1);
										alignTopCorner(corner, otherCols[0], otherCols[1]);
										turn(rightColOrder1, rightAmt1);
										turn(Color.WHITE, rightAmt1);
										turn(rightColOrder1, -rightAmt1);
									}
								} else {
									// yellow side of corner piece is facing up and edge piece isn't orientated correctly
									if (!edgeOrdered) {
										turn(begColsOrder[1], 1);
										turn(Color.WHITE, -1);
										turn(begColsOrder[1], -1);
										alignTopCorner(corner, otherCols[0], otherCols[1]);
										turn(Color.WHITE, -1);
										turn(otherColsOrder[0], -1);
										turn(Color.WHITE, 1);
										turn(otherColsOrder[0], 1);
									// yellow side of corner piece is facing up and edge piece is orientated correctly
									} else {
										turn(begColsOrder[1], 1);
										turn(Color.WHITE, 1);
										turn(begColsOrder[1], -1);
										turn(Color.WHITE, -1);
										turn(begColsOrder[1], 1);
										turn(Color.WHITE, 1);
										turn(begColsOrder[1], -1);
										alignTopCorner(corner, otherCols[0], otherCols[1]);
										turn(otherColsOrder[1], 1);
										turn(Color.WHITE, 1);
										turn(otherColsOrder[1], -1);
									}
								}
							// corner on bottom, edge on top
							} else if (edgeZ == 0) {
								boolean yelDown = (cornerCols.get(Color.YELLOW) == Color.YELLOW);
								
								Color[] otherColsOrder = new Color[2];
								if (Color.WHITE.inOrder(otherCols[0], otherCols[1])) {
									otherColsOrder[0] = otherCols[0];
									otherColsOrder[1] = otherCols[1];
								} else {
									otherColsOrder[0] = otherCols[1];
									otherColsOrder[1] = otherCols[0];
								}
								
								Color[] begColsOrder = new Color[2];
								int begColInd = 0;
								for (Color begCol : corner.getColors().values()) {
									if (begCol != Color.YELLOW) {
										begColsOrder[begColInd] = begCol;
										begColInd++;
									}
								}
								if (Color.WHITE.inOrder(begColsOrder[1], begColsOrder[0])) {
									Color tempCol = begColsOrder[0];
									begColsOrder[0] = begColsOrder[1];
									begColsOrder[1] = tempCol;
								}
								
								Color[] edgeColsOrder = new Color[2];
								if (edgeCols.get(otherCols[0]) == Color.WHITE) {
									edgeColsOrder[0] = otherCols[0];
									edgeColsOrder[1] = otherCols[1];
								} else {
									edgeColsOrder[0] = otherCols[1];
									edgeColsOrder[1] = otherCols[0];
								}
								
								boolean edgeRight = (edgeColsOrder[1] == otherColsOrder[1]);
								
								int rightAmt1 = 1;
								Color rightBegOrder0 = begColsOrder[0];
								Color rightBegOrder1 = begColsOrder[1];
								Color rightColOrder0 = otherColsOrder[0];
								Color rightColOrder1 = otherColsOrder[1];
								if (!edgeRight) {
									rightAmt1 = -1;
									rightBegOrder0 = begColsOrder[1];
									rightBegOrder1 = begColsOrder[0];
									rightColOrder0 = otherColsOrder[1];
									rightColOrder1 = otherColsOrder[0];
								}
								
								alignTopEdge(edge, begColsOrder[edgeRight ? 1 : 0]);
								
								// yellow side of corner piece is facing down
								if (yelDown) {
									turn(Color.WHITE, -rightAmt1);
									turn(rightBegOrder0, -rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightBegOrder0, rightAmt1);
									alignTopCorner(corner, otherCols[0], otherCols[1]);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder1, -rightAmt1);
								// yellow side of corner piece isn't facing down or on the same face as the edge
								} else if (cornerCols.get(Color.YELLOW) != edge.getColors().get(edgeColsOrder[1])) {
									turn(rightBegOrder1, rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightBegOrder1, -rightAmt1);
									alignTopCorner(corner, otherCols[0], otherCols[1]);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, -rightAmt1);
									turn(rightColOrder1, -rightAmt1);
								// yellow side of corner piece isn't facing down but is on the same face as the edge
								} else {
									turn(rightBegOrder1, rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightBegOrder1, -rightAmt1);
									alignTopCorner(corner, otherCols[0], otherCols[1]);
									turn(rightColOrder1, rightAmt1);
									turn(Color.WHITE, rightAmt1);
									turn(rightColOrder1, -rightAmt1);
								}
							// corner on bottom, edge in middle
							} else {
								boolean sameColumn = (i == edgeX && j == edgeY);
								
								Color[] otherColsOrder = new Color[2];
								if (Color.WHITE.inOrder(otherCols[0], otherCols[1])) {
									otherColsOrder[0] = otherCols[0];
									otherColsOrder[1] = otherCols[1];
								} else {
									otherColsOrder[0] = otherCols[1];
									otherColsOrder[1] = otherCols[0];
								}
								
								boolean edgeOrdered = Color.WHITE.inOrder(edgeCols.get(otherColsOrder[0]), edgeCols.get(otherColsOrder[1]));
								
								Color[] begColsOrder = new Color[2];
								if (edgeOrdered) {
									begColsOrder[0] = edgeCols.get(otherColsOrder[0]);
									begColsOrder[1] = edgeCols.get(otherColsOrder[1]);
								} else {
									begColsOrder[0] = edgeCols.get(otherColsOrder[1]);
									begColsOrder[1] = edgeCols.get(otherColsOrder[0]);
								}
								
								if (sameColumn) {
									boolean yelDown = (cornerCols.get(Color.YELLOW) == Color.YELLOW);
									
									if (!yelDown) {
										if (cornerCols.get(otherCols[0]) != Color.YELLOW) {
											Color tempCol = otherCols[0];
											otherCols[0] = otherCols[1];
											otherCols[1] = tempCol;
										}
										
										boolean yelRight = Color.WHITE.inOrder(cornerCols.get(otherCols[1]), cornerCols.get(Color.YELLOW));
										
										// yellow side of corner piece isn't facing down and edge is orientated correctly
										if (edgeOrdered) {
											if (yelRight) {
												turn(begColsOrder[1], 1);
												turn(Color.WHITE, -1);
												turn(begColsOrder[1], -1);
												alignTopCorner(corner, otherCols[0], otherCols[1]);
												turn(Color.WHITE, 1);
												turn(otherColsOrder[1], 1);
												turn(Color.WHITE, 2);
												turn(otherColsOrder[1], -1);
												turn(Color.WHITE, 1);
												turn(otherColsOrder[1], 1);
												turn(Color.WHITE, -1);
												turn(otherColsOrder[1], -1);
											} else {
												turn(begColsOrder[1], 1);
												turn(Color.WHITE, -1);
												turn(begColsOrder[1], -1);
												alignTopCorner(corner, otherCols[0], otherCols[1]);
												turn(Color.WHITE, -1);
												turn(otherColsOrder[1], 1);
												turn(Color.WHITE, 1);
												turn(otherColsOrder[1], -1);
												turn(Color.WHITE, -1);
												turn(otherColsOrder[1], 1);
												turn(Color.WHITE, 2);
												turn(otherColsOrder[1], -1);
											}
										// yellow side of corner piece isn't facing down and edge isn't orientated correctly
										} else {
											if (yelRight) {
												turn(begColsOrder[1], 1);
												turn(Color.WHITE, 1);
												turn(begColsOrder[1], -1);
												alignTopCorner(corner, otherCols[0], otherCols[1]);
												turn(otherColsOrder[1], 1);
												turn(Color.WHITE, -1);
												turn(otherColsOrder[1], -1);
												turn(Color.WHITE, 2);
												turn(otherColsOrder[0], -1);
												turn(Color.WHITE, -1);
												turn(otherColsOrder[0], 1);
											} else {
												turn(begColsOrder[1], 1);
												turn(Color.WHITE, -1);
												turn(begColsOrder[1], -1);
												alignTopCorner(corner, otherCols[0], otherCols[1]);
												turn(Color.WHITE, 1);
												turn(otherColsOrder[0], -1);
												turn(Color.WHITE, -1);
												turn(otherColsOrder[0], 1);
												turn(Color.WHITE, -1);
												turn(otherColsOrder[0], -1);
												turn(Color.WHITE, -1);
												turn(otherColsOrder[0], 1);
											}
										}
									// yellow side of corner piece is facing down
									} else {
										turn(begColsOrder[1], 1);
										turn(Color.WHITE, -1);
										turn(begColsOrder[1], -1);
										alignTopCorner(corner, otherCols[0], otherCols[1]);
										for (int l = 0; l < 2; l++) {
											turn(Color.WHITE, 1);
											turn(otherColsOrder[0], -1);
											turn(Color.WHITE, 2);
											turn(otherColsOrder[0], 1);
										}
									}
								// corner and edge pieces aren't in the same column
								} else {
									turn(begColsOrder[1], 1);
									turn(Color.WHITE, 1);
									turn(begColsOrder[1], -1);
									k--;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private int f2lCount() {
		int[][] f2lCoords = { {0,0}, {0,2}, {2,0}, {2,2} };
		int count = 0;
		for (int[] f2lCoord : f2lCoords) {
			if (cube.getPieces()[f2lCoord[0]][f2lCoord[1]][2].inPlace() && cube.getPieces()[f2lCoord[0]][f2lCoord[1]][1].inPlace()) {
				count++;
			}
		}
		return count;
	}
	
	private void alignTopCorner(Piece corner, Color face1, Color face2) {
		Color[] faceCols = new Color[2];
		int i = 0;
		for (Color faceCol : corner.getColors().values()) {
			if (faceCol != Color.WHITE) {
				faceCols[i] = faceCol;
				i++;
			}
		}
		if (Color.WHITE.inOrder(faceCols[1], faceCols[0])) {
			Color tempCol = faceCols[0];
			faceCols[0] = faceCols[1];
			faceCols[1] = tempCol;
		}
		
		if ((faceCols[0] == face1 && faceCols[1] == face2) || (faceCols[0] == face2 && faceCols[1] == face1)) {
			return;
		} else if (faceCols[0] == face1 || faceCols[0] == face2) {
			turn(Color.WHITE, 1);
		} else if (faceCols[1] == face1 || faceCols[1] == face2) {
			turn(Color.WHITE, -1);
		} else {
			turn(Color.WHITE, 2);
		}
	}
	
	private void alignTopEdge(Piece edge, Color face) {
		Color otherFace = Color.WHITE;
		for (Color faceCol : edge.getColors().values()) {
			if (faceCol != Color.WHITE) {
				otherFace = faceCol;
			}
		}
		
		if (otherFace == face) {
			return;
		} else if (otherFace.complement() == face) {
			turn(Color.WHITE, 2);
		} else if (Color.WHITE.inOrder(otherFace, face)) {
			turn(Color.WHITE, -1);
		} else {
			turn(Color.WHITE, 1);
		}
	}
	
	private void eoll() {
		int[][] crossCoords = { {0,1}, {1,2}, {2,1}, {1,0} };
		ArrayList<Integer> crossInds = new ArrayList<Integer>();
		for (int i = 0; i < 4; i++) {
			int[] crossCoord = crossCoords[i];
			if (cube.getPieces()[crossCoord[0]][crossCoord[1]][0].getColors().get(Color.WHITE) == Color.WHITE) {
				crossInds.add(i);
			}
		}
		
		// none of the yellow sides are facing up
		if (crossInds.size() == 0) {
			turn(Color.RED, 1);
			turn(Color.WHITE, 2);
			turn(Color.RED, 2);
			turn(Color.GREEN, 1);
			turn(Color.RED, 1);
			turn(Color.GREEN, -1);
			turn(Color.WHITE, 2);
			turn(Color.RED, -1);
			turn(Color.GREEN, 1);
			turn(Color.RED, 1);
			turn(Color.GREEN, -1);
		} else if (crossInds.size() == 2) {
			int rotAmt = 0;
			// two of the yellow sides are facing up in a line
			if (crossInds.get(1) - crossInds.get(0) == 2) {
				if (crossInds.get(0) == 0) {
					rotAmt = 1;
				}
				
				turnRot(Color.GREEN, 1, rotAmt);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, -1);
				turnRot(Color.GREEN, -1, rotAmt);
			// two of the yellow sides are facing up in an L shape
			} else {
				int leftInd = crossInds.get(0);
				if (crossInds.get(0) == 0 && crossInds.get(1) == 3) {
					leftInd = 3;
				}
				rotAmt = leftInd - 3;
				if (rotAmt < -1) {
					rotAmt += 4;
				}
				
				turnRot(Color.GREEN, 1, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, -1);
				turnRot(Color.RED, -1, rotAmt);
				turnRot(Color.GREEN, -1, rotAmt);
			}
		}
	}
	
	private void turnRot(Color face, int amount, int rotAmt) {
		if (rotAmt != 0 && face != Color.WHITE && face != Color.YELLOW) {
			face = Color.WHITE.pivotColor(face, rotAmt);
		}
		turn(face, amount);
	}
	
	private void ocll() {
		int[][] cornerCoords = { {0,0}, {0,2}, {2,2}, {2,0} };
		ArrayList<Integer> cornerInds = new ArrayList<Integer>();
		for (int i = 0; i < 4; i++) {
			int[] cornerCoord = cornerCoords[i];
			if (cube.getPieces()[cornerCoord[0]][cornerCoord[1]][0].getColors().get(Color.WHITE) == Color.WHITE) {
				cornerInds.add(i);
			}
		}
		int rotAmt = 0;
		
		if (cornerInds.size() == 1) {
			Map<Color, Color> pieceCols = cube.getPieces()[cornerCoords[cornerInds.get(0)][0]][cornerCoords[cornerInds.get(0)][1]][0].getColors();
			Color[] otherCols = new Color[2];
			int colInd = 0;
			for (Color col : Color.WHITE.getAdj()) {
				if (pieceCols.keySet().contains(col)) {
					otherCols[colInd] = col;
					colInd++;
				}
			}
			
			Color[] otherColsOrder = new Color[2];
			if (Color.WHITE.inOrder(otherCols[0], otherCols[1])) {
				otherColsOrder[0] = otherCols[0];
				otherColsOrder[1] = otherCols[1];
			} else {
				otherColsOrder[0] = otherCols[1];
				otherColsOrder[1] = otherCols[0];
			}
			
			int[] pieceCoords = cornerCoords[cornerInds.get(0)];
			int[] leftCoords = new int[2];
			if (pieceCoords[0] == pieceCoords[1]) {
				leftCoords[0] = ((pieceCoords[0] - 1) * -1) + 1;
				leftCoords[1] = pieceCoords[1];
			} else {
				leftCoords[0] = pieceCoords[0];
				leftCoords[1] = ((pieceCoords[1] - 1) * -1) + 1;
			}
			Map<Color, Color> leftCols = cube.getPieces()[leftCoords[0]][leftCoords[1]][0].getColors();
			boolean leftSide = (leftCols.get(Color.WHITE) == pieceCols.get(otherColsOrder[1]));
			
			// only one corner has its white side facing up and the corner to the left has its white side facing outwards
			if (leftSide) {
				rotAmt = cornerInds.get(0) - 2;
				if (rotAmt == -2) {
					rotAmt = 2;
				}
				
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, 2);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, 1, rotAmt);
			// only one corner has its white side facing up and the corner to the right has its white side facing outwards
			} else {
				rotAmt = cornerInds.get(0) - 1;
				
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, 2);
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, -1);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, -1);
				turnRot(Color.RED, -1, rotAmt);
			}
		} else if (cornerInds.size() == 2) {
			ArrayList<Piece> nonWhitePieces = new ArrayList<Piece>();
			ArrayList<Integer> nonWhiteInds = new ArrayList<Integer>();
			for (int i = 0; i < 4; i++) {
				int[] cornerCoord = cornerCoords[i];
				if (!cornerInds.contains(i)) {
					nonWhitePieces.add(cube.getPieces()[cornerCoord[0]][cornerCoord[1]][0]);
					nonWhiteInds.add(i);
				}
			}
			
			// two opposite corners have white sides facing up
			if (cornerInds.get(1) - cornerInds.get(0) == 2) {
				int rightInd = 0;
				for (int i = 0; i < 2; i++) {
					Color otherSideCol = Color.WHITE;
					for (Map.Entry<Color, Color> entry : nonWhitePieces.get(i).getColors().entrySet()) {
						if (entry.getKey() != Color.WHITE && entry.getValue() != Color.WHITE) {
							otherSideCol = entry.getKey();
						}
					}
					
					if (Color.WHITE.inOrder(nonWhitePieces.get(i).getColors().get(otherSideCol), nonWhitePieces.get(i).getColors().get(Color.WHITE))) {
						rightInd = nonWhiteInds.get(i);
						break;
					}
				}
				
				rotAmt = rightInd;
				if (rotAmt == 3) {
					rotAmt = -1;
				}
				
				turnRot(Color.GREEN, -1, rotAmt);
				turnRot(Color.ORANGE, 1, rotAmt);
				turnRot(Color.GREEN, 1, rotAmt);
				turnRot(Color.RED, -1, rotAmt);
				turnRot(Color.GREEN, -1, rotAmt);
				turnRot(Color.ORANGE, -1, rotAmt);
				turnRot(Color.GREEN, 1, rotAmt);
				turnRot(Color.RED, 1, rotAmt);
			// two adjacent corners have white sides facing up and the white sides of the other corners are not on the same face
			} else if (nonWhitePieces.get(0).getColors().get(Color.WHITE) != nonWhitePieces.get(1).getColors().get(Color.WHITE)) {
				int leftInd = 0;
				for (int i = 0; i < 2; i++) {
					Color otherSideCol = Color.WHITE;
					for (Map.Entry<Color, Color> entry : nonWhitePieces.get(i).getColors().entrySet()) {
						if (entry.getKey() != Color.WHITE && entry.getValue() != Color.WHITE) {
							otherSideCol = entry.getKey();
						}
					}
					
					if (Color.WHITE.inOrder(nonWhitePieces.get(i).getColors().get(Color.WHITE), nonWhitePieces.get(i).getColors().get(otherSideCol))) {
						leftInd = nonWhiteInds.get(i);
						break;
					}
				}
				
				rotAmt = leftInd;
				if (rotAmt == 3) {
					rotAmt = -1;
				}
				
				turnRot(Color.ORANGE, 1, rotAmt);
				turnRot(Color.GREEN, 1, rotAmt);
				turnRot(Color.RED, -1, rotAmt);
				turnRot(Color.GREEN, -1, rotAmt);
				turnRot(Color.ORANGE, -1, rotAmt);
				turnRot(Color.GREEN, 1, rotAmt);
				turnRot(Color.RED, 1, rotAmt);
				turnRot(Color.GREEN, -1, rotAmt);
			// two adjacent corners have white sides facing up and the white sides of the other corners are on the same face
			} else if (cornerInds.size() == 0) {
				int leftInd = 0;
				for (int i = 0; i < 2; i++) {
					Color otherSideCol = Color.WHITE;
					for (Map.Entry<Color, Color> entry : nonWhitePieces.get(i).getColors().entrySet()) {
						if (entry.getKey() != Color.WHITE && entry.getValue() != Color.WHITE) {
							otherSideCol = entry.getKey();
						}
					}
					
					if (Color.WHITE.inOrder(nonWhitePieces.get(i).getColors().get(Color.WHITE), nonWhitePieces.get(i).getColors().get(otherSideCol))) {
						leftInd = nonWhiteInds.get(i);
						break;
					}
				}
				
				rotAmt = leftInd - 2;
				if (rotAmt == -2) {
					rotAmt = 2;
				}
				
				turnRot(Color.RED, 2, rotAmt);
				turn(Color.YELLOW, 1);
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, 2);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.YELLOW, -1);
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, 2);
				turnRot(Color.RED, -1, rotAmt);
			}
		} else if (cornerInds.size() == 0) {
			boolean[] whiteRight = new boolean[4];
			for (int i = 0; i < 4; i++) {
				Map<Color, Color> pieceCols = cube.getPieces()[cornerCoords[i][0]][cornerCoords[i][1]][0].getColors();
				
				Color otherSideCol = Color.WHITE;
				for (Map.Entry<Color, Color> entry : pieceCols.entrySet()) {
					if (entry.getKey() != Color.WHITE && entry.getValue() != Color.WHITE) {
						otherSideCol = entry.getKey();
					}
				}
				
				whiteRight[i] = Color.WHITE.inOrder(pieceCols.get(otherSideCol), pieceCols.get(Color.WHITE));
			}
			
			boolean diffFaces = false;
			int pieceInd = 0;
			for (int i = 0; i < 4; i++) {
				int nextI = i + 1;
				if (nextI == 4) {
					nextI = 0;
				}
				
				if (whiteRight[i] && whiteRight[nextI]) {
					diffFaces = true;
					pieceInd = i;
					break;
				}
				
				if (whiteRight[i] && !whiteRight[nextI]) {
					pieceInd = i;
				}
			}
			
			// no corners have white sides facing up and the white sides of the corners don't all face faces in pairs
			if (diffFaces) {
				rotAmt = pieceInd;
				if (rotAmt == 3) {
					rotAmt = -1;
				}
				
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, 2);
				for (int i = 0; i < 2; i++) {
					turnRot(Color.RED, 2, rotAmt);
					turn(Color.WHITE, -1);
				}
				turnRot(Color.RED, 2, rotAmt);
				turn(Color.WHITE, 2);
				turnRot(Color.RED, 1, rotAmt);
			// no corners have white sides facing up and the white sides of the corners all face faces in pairs
			} else {
				if (pieceInd % 2 == 1) {
					rotAmt = 1;
				}
				
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, -1);
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, 2);
				turnRot(Color.RED, -1, rotAmt);
			}
		}
	}
	
	private void cpll() {
		int[][] cornerCoords = { {0,0}, {0,2}, {2,2}, {2,0} };
		int headlCount = 0;
		int headlInd = 0;
		for (int i = 0; i < 4; i++) {
			int nextI = i + 1;
			if (nextI == 4) {
				nextI = 0;
			}
			
			Map<Color, Color> pieceCols = cube.getPieces()[cornerCoords[i][0]][cornerCoords[i][1]][0].getColors();
			Map<Color, Color> nextCols = cube.getPieces()[cornerCoords[nextI][0]][cornerCoords[nextI][1]][0].getColors();
			
			Color pieceLeft = Color.WHITE;
			Color[] otherCols = new Color[2];
			int colInd = 0;
			for (Color col : Color.WHITE.getAdj()) {
				if (pieceCols.keySet().contains(col)) {
					otherCols[colInd] = col;
					colInd++;
				}
			}
			if (Color.WHITE.inOrder(otherCols[0], otherCols[1])) {
				pieceLeft = otherCols[0];
			} else {
				pieceLeft = otherCols[1];
			}
			
			Color nextRight = Color.WHITE;
			Color[] nextOtherCols = new Color[2];
			colInd = 0;
			for (Color col : Color.WHITE.getAdj()) {
				if (nextCols.keySet().contains(col)) {
					nextOtherCols[colInd] = col;
					colInd++;
				}
			}
			if (Color.WHITE.inOrder(nextOtherCols[0], nextOtherCols[1])) {
				nextRight = nextOtherCols[1];
			} else {
				nextRight = nextOtherCols[0];
			}
			
			if (pieceLeft == nextRight) {
				headlCount++;
				headlInd = i;
			}
		}
		
		// there is one "headlight"
		if (headlCount == 1) {
			int rotAmt = headlInd - 1;
			
			turnRot(Color.RED, 2, rotAmt);
			turnRot(Color.BLUE, 2, rotAmt);
			turnRot(Color.RED, 1, rotAmt);
			turnRot(Color.GREEN, 1, rotAmt);
			turnRot(Color.RED, -1, rotAmt);
			turnRot(Color.BLUE, 2, rotAmt);
			turnRot(Color.RED, 1, rotAmt);
			turnRot(Color.GREEN, -1, rotAmt);
			turnRot(Color.RED, 1, rotAmt);
		// there are no "headlights"
		} else if (headlCount == 0) {
			turn(Color.GREEN, 1);
			turn(Color.RED, 1);
			turn(Color.WHITE, -1);
			turn(Color.RED, -1);
			turn(Color.WHITE, -1);
			turn(Color.RED, 1);
			turn(Color.WHITE, 1);
			turn(Color.RED, -1);
			turn(Color.GREEN, -1);
			turn(Color.RED, 1);
			turn(Color.WHITE, 1);
			turn(Color.RED, -1);
			turn(Color.WHITE, -1);
			turn(Color.RED, -1);
			turn(Color.GREEN, 1);
			turn(Color.RED, 1);
			turn(Color.GREEN, -1);
		}
		
		Piece firstPiece = cube.getPieces()[0][0][0];
		Color[] firstCols = new Color[2];
		int colInd = 0;
		for (Color col : Color.WHITE.getAdj()) {
			if (firstPiece.getColors().keySet().contains(col)) {
				firstCols[colInd] = col;
				colInd++;
			}
		}
		
		alignTopCorner(firstPiece, firstCols[0], firstCols[1]);
	}
	
	private void epll() {
		int numPlaced = 0;
		int placedInd = 0;
		int[][] edgeCoords = { {0,1}, {1,2}, {2,1}, {1,0} };
		for (int i = 0; i < 4; i++) {
			if (cube.getPieces()[edgeCoords[i][0]][edgeCoords[i][1]][0].inPlace()) {
				numPlaced++;
				placedInd = i;
			}
		}
		int rotAmt = 0;
		
		if (numPlaced == 1) {
			rotAmt = placedInd - 2;
			if (rotAmt == -2) {
				rotAmt = 2;
			}
			
			int oppInd = placedInd - 2;
			if (oppInd < 0) {
				oppInd += 4;
			}
			
			int nextOppInd = oppInd + 1;
			if (nextOppInd == 4) {
				nextOppInd = 0;
			}
			
			Color oppCol = Color.WHITE;
			for (Color col : Color.WHITE.getAdj()) {
				if (cube.getPieces()[edgeCoords[oppInd][0]][edgeCoords[oppInd][1]][0].getColors().keySet().contains(col)) {
					oppCol = col;
				}
			}
			
			Color nextOppFace = Color.WHITE;
			for (Color col : Color.WHITE.getAdj()) {
				if (cube.getPieces()[edgeCoords[nextOppInd][0]][edgeCoords[nextOppInd][1]][0].getColors().values().contains(col)) {
					nextOppFace = col;
				}
			}
			
			// one edge is in place and the others need to be rotated clockwise
			if (oppCol == nextOppFace) {
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, 1);
				for (int i = 0; i < 2; i++) {
					turnRot(Color.RED, -1, rotAmt);
					turn(Color.WHITE, -1);
				}
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, 2, rotAmt);
			// one edge is in place and the others need to be rotated counterclockwise
			} else {
				turnRot(Color.RED, 2, rotAmt);
				turn(Color.WHITE, -1);
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, -1);
				for (int i = 0; i < 2; i++) {
					turnRot(Color.RED, 1, rotAmt);
					turn(Color.WHITE, 1);
				}
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, -1);
				turnRot(Color.RED, 1, rotAmt);
			}
		} else if (numPlaced == 0) {
			// no edges are in place and opposite edges need to be swapped
			if (cube.getPieces()[0][1][0].pieceColor(Color.BLUE) == Color.GREEN) {
				turn(Color.ORANGE, 2);
				turn(Color.RED, 2);
				turn(Color.YELLOW, 1);
				turn(Color.ORANGE, 2);
				turn(Color.RED, 2);
				turn(Color.WHITE, 2);
				turn(Color.ORANGE, 2);
				turn(Color.RED, 2);
				turn(Color.YELLOW, 1);
				turn(Color.ORANGE, 2);
				turn(Color.RED, 2);
			// no edges are in place and adjacent edges need to be swapped
			} else {
				if (cube.getPieces()[0][1][0].pieceColor(Color.BLUE) == Color.RED) {
					rotAmt = 1;
				}
				
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, -1);
				turnRot(Color.RED, 2, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, -1);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, 1);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, -1);
				turnRot(Color.RED, 1, rotAmt);
				turn(Color.WHITE, -1);
				turnRot(Color.RED, -1, rotAmt);
				turn(Color.WHITE, 2);
			}
		}
	}
}
