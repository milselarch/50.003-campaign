import java.security.SecureRandom;
import java.util.*;

/*
random string generator taken and adapted from
https://stackoverflow.com/questions/41107/
*/

public class RandomString {
    /**
     * Generate a random string.
     */
    public String next_string() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }

    public static String generate() {
        return RandomString.generate(5);
    }

    public static String generate(int length) {
        RandomString generator = new RandomString();
        return generator.next_string();
    }

    public static ArrayList<String> generate_multi() {
        return RandomString.generate_multi(5, 5);
    }

    public static ArrayList<String> generate_multi(int num_samples) {
        return RandomString.generate_multi(num_samples, 5);
    }

    public static ArrayList<String> generate_multi(
        int num_samples, int length
    ) {
        ArrayList<String> rand_strings = new ArrayList<>();
        RandomString generator = new RandomString(length);
        for (int k=0; k<num_samples; k++) {
            rand_strings.add(generator.next_string());
        }

        return rand_strings;
    }

    public static ArrayList<String> generate_multi_exc(
        int num_samples, int length
    ) {
        return generate_multi_exc(
            num_samples, length, new ArrayList<>()
        );
    }

    public static ArrayList<String> generate_multi_exc(
        int num_samples, int length, List<String> exclusions
    ) {
        /*
        generate a random arraylist of strings that are
        unique and don't appear in the exclusions arraylist either
        */
        ArrayList<String> rand_strings = new ArrayList<>();
        RandomString generator = new RandomString(length);

        while (rand_strings.size() < num_samples) {
            String sample = generator.next_string();
            if (exclusions.contains(sample)) { continue; }
            if (rand_strings.contains(sample)) { continue; }
            rand_strings.add(sample);
        }

        return rand_strings;
    }

    public static String unique_random(
        int length, List<String> exclusions
    ) {
        /*
        generate a string whose length is length that
        isn't in the exclusions arraylist
        */
        return generate_multi_exc(
            1, length, exclusions
        ).get(0);
    }

    public static ArrayList<String> sample(
        ArrayList<String> samples, int num_samples
    ) {
        RandomString generator = new RandomString();
        ArrayList<String> new_samples = new ArrayList<>();
        for (int k=0; k<num_samples; k++) {
            int index = generator.random.nextInt(samples.size());
            String sample = samples.get(index);
            new_samples.add(sample);
        }
        return new_samples;
    }

    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String lower = upper.toLowerCase(Locale.ROOT);

    public static final String digits = "0123456789";

    public static final String alphanum = upper + lower + digits;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public RandomString(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Create an alphanumeric string generator.
     */
    public RandomString(int length, Random random) {
        this(length, random, alphanum);
    }

    /**
     * Create an alphanumeric strings from a secure generator.
     */
    public RandomString(int length) {
        this(length, new SecureRandom());
    }

    /**
     * Create session identifiers.
     */
    public RandomString() {
        this(5);
    }
}
