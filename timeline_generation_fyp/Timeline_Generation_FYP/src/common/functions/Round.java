package common.functions;

public class Round {
    public static int roundDown(double number) {
        double result = number / 10;
        result = Math.floor(result);
        result *= 10;
        return (int) result;
    }

    public static int roundUp(double number) {
        double result = number / 10;
        result = Math.ceil(result);
        result *= 10;
        return (int) result;
    }
}
