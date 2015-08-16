package mody.vkmusic.Player;

public class Repeat {
    public static final Integer REPEAT_NO_REPEAT = 0;
    public static final Integer REPEAT_ALL = 1;
    public static final Integer REPEAT_SINGLE = 2;
    private static Integer currentValue = 0;

    private Repeat() {}

    public static void setNextRepeat() {
        if (currentValue == 0 || currentValue == 1) currentValue++;
        else currentValue = 0;
    }
    public static Integer getValue(){
        return currentValue;
    }
}
