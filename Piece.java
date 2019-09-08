import java.util.*;

public interface Piece {
	Map<Color, Color> getColors();
	boolean containsFace(Color faceColor);
	Color pieceColor(Color faceColor);
	void assignColors(ArrayList<Color> fromColors, ArrayList<Color> toColors);
	boolean inPlace();
}
