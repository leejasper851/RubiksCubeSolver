import java.util.*;

public class CenterPiece implements Piece {
	private Map<Color, Color> color;
	
	public CenterPiece(Color c) {
		color = new HashMap<Color, Color>();
		color.put(c, c);
	}
	
	public Map<Color, Color> getColors() {
		Map<Color, Color> colorCopy = new HashMap<Color, Color>();
		colorCopy.putAll(color);
		return colorCopy;
	}
	
	public boolean containsFace(Color faceColor) {
		return color.containsValue(faceColor);
	}
	
	public Color pieceColor(Color faceColor) {
		return color.keySet().iterator().next();
	}
	
	public void assignColors(ArrayList<Color> fromColors, ArrayList<Color> toColors) {
		if (!toColors.isEmpty()) {
			color.put(color.keySet().iterator().next(), toColors.get(0));
		}
	}
	
	public boolean inPlace() {
		return true;
	}
}
