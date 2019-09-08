import java.util.*;

public class NullPiece implements Piece {
	
	public NullPiece() {
	}
	
	public Map<Color, Color> getColors() {
		return new HashMap<Color, Color>();
	}
	
	public boolean containsFace(Color faceColor) {
		return false;
	}
	
	public Color pieceColor(Color faceColor) {
		return Color.WHITE;
	}
	
	public void assignColors(ArrayList<Color> fromColors, ArrayList<Color> toColors) {
	}
	
	public boolean inPlace() {
		return true;
	}
}
