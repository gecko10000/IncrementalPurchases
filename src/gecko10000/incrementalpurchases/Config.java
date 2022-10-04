package gecko10000.incrementalpurchases;

import java.util.HashMap;
import java.util.Map;

public class Config {

    public static String unsetCommand = "lp user <player> permission unset <permission>";
    public static String setCommand = "lp user <player> permission set <permission>";
    public static Map<String, IncrementalPurchase> purchases = new HashMap<>(Map.of("example",
            new IncrementalPurchase("example", 3, 10, Operator.MULTIPLY, "example.amount.", 20)
    ));

}
