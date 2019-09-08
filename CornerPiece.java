import java.util.*;

public class CornerPiece implements Piece {
	private Map<Color, Color> colors;
	
	public CornerPiece(Color col1, Color cen1, Color col2, Color cen2, Color col3, Color cen3) {
		colors = new HashMap<Color, Color>();
		colors.put(col1, cen1);
		colors.put(col2, cen2);
		colors.put(col3, cen3);
	}
	
	public Map<Color, Color> getColors() {
		Map<Color, Color> colorsCopy = new HashMap<Color, Color>();
		colorsCopy.putAll(colors);
		return colorsCopy;
	}
	
	public boolean containsFace(Color faceColor) {
		return colors.containsValue(faceColor);
	}
	
	public Color pieceColor(Color faceColor) {
		for (Map.Entry<Color, Color> entry : colors.entrySet()) {
			if (entry.getValue() == faceColor) {
				return entry.getKey();
			}
		}
		return Color.WHITE;
	}
	
	public void assignColors(ArrayList<Color> fromColors, ArrayList<Color> toColors) {
		ArrayList<Color> keys = new ArrayList<Color>();
		for (Color fromColor : fromColors) {
			for (Map.Entry<Color, Color> entry : colors.entrySet()) {
				if (entry.getValue() == fromColor) {
					keys.add(entry.getKey());
				}
			}
		}
		for (int i = 0; i < keys.size(); i++) {
			colors.put(keys.get(i), toColors.get(i));
		}
	}
	
	public boolean inPlace() {
		for (Color colorVal : Color.values()) {
			if (colors.containsKey(colorVal) && colorVal != colors.get(colorVal)) {
				return false;
			}
		}
		return true;
	}
}
