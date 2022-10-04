package gecko10000.incrementalpurchases;

public class Utils {

    public static String formatMoney(double amount) {
        StringBuilder b = new StringBuilder();
        String s = String.valueOf((long) amount);
        for (int i = 0; i < s.length(); i++) {
            if (i % 3 == 0 && i != 0) b.append(',');
            b.append(s.charAt(s.length() - i - 1));
        }
        b.reverse();
        amount *= 100;
        amount %= 100;
        b.append(String.format(".%02d", (int) amount));
        return b.toString();
    }

}
