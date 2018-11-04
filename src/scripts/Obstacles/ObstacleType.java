package scripts.Obstacles;

public enum ObstacleType {
    OBJECT,NPC,ITEM_SELECTION, WIDGET;


    public static ObstacleType parse(String s) {
        for(ObstacleType o:ObstacleType.values()){
            if(o.name().equalsIgnoreCase(s)) {
                return o;
            }
        }
        throw new IllegalArgumentException("Obstacle type invalid: " + s);
    }
}
