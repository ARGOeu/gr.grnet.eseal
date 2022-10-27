package gr.grnet.eseal.enums;

public enum VisibleSignaturePosition {
  TOP_LEFT(0),
  BOTTOM_LEFT(1),
  TOP_RIGHT(2),
  BOTTOM_RIGHT(3),
  INVISIBLE(4);

  private final int id;

  VisibleSignaturePosition(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public static VisibleSignaturePosition getPosition(int pos) {
    switch (pos) {
      case 1:
        return BOTTOM_LEFT;
      case 2:
        return TOP_RIGHT;
      case 3:
        return BOTTOM_RIGHT;
      case 4:
        return INVISIBLE;
      default:
        return TOP_LEFT;
    }
  }

  public static boolean isLastPossiblePosition(int pos) {
    return pos >= 4;
  }
}
