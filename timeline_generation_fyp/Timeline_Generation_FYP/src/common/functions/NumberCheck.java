package common.functions;

public class NumberCheck {
    public static boolean check(String toCheck) {
        try {
            Integer.parseInt(toCheck);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
