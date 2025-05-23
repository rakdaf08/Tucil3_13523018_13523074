package components;

public class Move {
  private Piece piece;
  private int startX;
  private int startY;
  private String direction;
  private int steps;

  public Move(Piece piece, int startX, int startY, String direction, int steps) {
    if (piece == null) {
      throw new IllegalArgumentException("Piece cannot be null");
    }
    if (direction == null) {
      throw new IllegalArgumentException("Direction cannot be null");
    }
    this.piece = piece;
    this.startX = startX;
    this.startY = startY;
    this.direction = direction;
    this.steps = steps;
  }

  public int[] getEndPosition() {
    int endX = startX;
    int endY = startY;

    switch (direction) {
      case "UP":
        endY -= steps;
        break;
      case "DOWN":
        endY += steps;
        break;
      case "LEFT":
        endX -= steps;
        break;
      case "RIGHT":
        endX += steps;
        break;
    }

    return new int[] { endX, endY };
  }

  public Piece getPiece() {
    return piece;
  }

  public int getStartX() {
    return startX;
  }

  public int getStartY() {
    return startY;
  }

  public String getDirection() {
    return direction;
  }

  public int getSteps() {
    return steps;
  }
}
