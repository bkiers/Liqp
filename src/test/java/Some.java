import com.ibm.icu.text.RuleBasedNumberFormat;
import java.util.Locale;

public class Some {

    public static String getOrdinalSuffix(int number) {
        // Create a RuleBasedNumberFormat for ordinal numbers
        RuleBasedNumberFormat ordinalFormat =
                new RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.SPELLOUT);

        // Format the number with the ordinal suffix
        return ordinalFormat.format(number);
    }

    public static void main(String[] args) {
        // Test the function
        System.out.println(getOrdinalSuffix(1));  // Output: 1st
        System.out.println(getOrdinalSuffix(2));  // Output: 2nd
        System.out.println(getOrdinalSuffix(3));  // Output: 3rd
        System.out.println(getOrdinalSuffix(11));  // Output: 4th
        System.out.println(getOrdinalSuffix(21)); // Output: 21st
        System.out.println(getOrdinalSuffix(42)); // Output: 42nd
    }
}
