import java.util.*;

enum Color {
	WHITE, YELLOW, RED, ORANGE, GREEN, BLUE;
	
	public Color complement() {
		switch (this) {
			case WHITE:
				return YELLOW;
			case YELLOW:
				return WHITE;
			case RED:
				return ORANGE;
			case ORANGE:
				return RED;
			case GREEN:
				return BLUE;
		}
		return GREEN;
	}
	
	public int index() {
		switch (this) {
			case WHITE:
				return 0;
			case YELLOW:
				return 5;
			case RED:
				return 2;
			case ORANGE:
				return 4;
			case GREEN:
				return 1;
		}
		return 3;
	}
	
	public Color[] getAdj() {
		switch (this) {
			case WHITE:
				Color[] whiteAdj = { Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE };
				return whiteAdj;
			case YELLOW:
				Color[] yellowAdj = { Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE };
				return yellowAdj;
			case RED:
				Color[] redAdj = { Color.WHITE, Color.BLUE, Color.YELLOW, Color.GREEN };
				return redAdj;
			case ORANGE:
				Color[] orangeAdj = { Color.WHITE, Color.GREEN, Color.YELLOW, Color.BLUE };
				return orangeAdj;
			case GREEN:
				Color[] greenAdj = { Color.WHITE, Color.RED, Color.YELLOW, Color.ORANGE };
				return greenAdj;
		}
		Color[] blueAdj = { Color.WHITE, Color.ORANGE, Color.YELLOW, Color.RED };
		return blueAdj;
	}
	
	public Color pivotColor(Color start, int amount) {
		Color[] pivotCols = new Color[4];
		switch (this) {
			case WHITE:
				Color[] whitePivotCols = { Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE };
				pivotCols = whitePivotCols;
				break;
			case YELLOW:
				Color[] yellowPivotCols = { Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE };
				pivotCols = yellowPivotCols;
				break;
			case RED:
				Color[] redPivotCols = { Color.WHITE, Color.BLUE, Color.YELLOW, Color.GREEN };
				pivotCols = redPivotCols;
				break;
			case ORANGE:
				Color[] orangePivotCols = { Color.WHITE, Color.GREEN, Color.YELLOW, Color.BLUE };
				pivotCols = orangePivotCols;
				break;
			case GREEN:
				Color[] greenPivotCols = { Color.WHITE, Color.RED, Color.YELLOW, Color.ORANGE };
				pivotCols = greenPivotCols;
				break;
			default:
				Color[] bluePivotCols = { Color.WHITE, Color.ORANGE, Color.YELLOW, Color.RED };
				pivotCols = bluePivotCols;
		}
		
		int i = Arrays.asList(pivotCols).indexOf(start);
		if (amount == -1) {
			amount = 3;
		}
		i += amount;
		i %= 4;
		return pivotCols[i];
	}
	
	public int findPivot(Color start, Color end) {
		Map<Color, Integer> pivotCols = new HashMap<Color, Integer>();
		switch (this) {
			case WHITE:
				Map<Color, Integer> whitePivotCols = Map.of(Color.BLUE, 0, Color.RED, 1, Color.GREEN, 2, Color.ORANGE, 3);
				pivotCols = whitePivotCols;
				break;
			case YELLOW:
				Map<Color, Integer> yellowPivotCols = Map.of(Color.GREEN, 0, Color.RED, 1, Color.BLUE, 2, Color.ORANGE, 3);
				pivotCols = yellowPivotCols;
				break;
			case RED:
				Map<Color, Integer> redPivotCols = Map.of(Color.WHITE, 0, Color.BLUE, 1, Color.YELLOW, 2, Color.GREEN, 3);
				pivotCols = redPivotCols;
				break;
			case ORANGE:
				Map<Color, Integer> orangePivotCols = Map.of(Color.WHITE, 0, Color.GREEN, 1, Color.YELLOW, 2, Color.BLUE, 3);
				pivotCols = orangePivotCols;
				break;
			case GREEN:
				Map<Color, Integer> greenPivotCols = Map.of(Color.WHITE, 0, Color.RED, 1, Color.YELLOW, 2, Color.ORANGE, 3);
				pivotCols = greenPivotCols;
				break;
			default:
				Map<Color, Integer> bluePivotCols = Map.of(Color.WHITE, 0, Color.ORANGE, 1, Color.YELLOW, 2, Color.RED, 3);
				pivotCols = bluePivotCols;
		}
		
		int startIndex = pivotCols.get(start);
		int endIndex = pivotCols.get(end);
		int diff = endIndex - startIndex;
		if (diff < -1) {
			diff += 4;
		}
		if (diff > 2) {
			diff -= 4;
		}
		return diff;
	}
	
	public boolean inOrder(Color leftCol, Color rightCol) {
		return (findPivot(leftCol, rightCol) == -1);
	}
}
