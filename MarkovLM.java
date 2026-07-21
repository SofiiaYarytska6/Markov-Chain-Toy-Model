import java.util.Random;
import java.util.TreeMap;

public class MarkovLM {
    // ASCII number of characters
    private static final int ASCII = 128;
    // random number generator for predictNext()
    private static final Random RANDOM = new Random();
    // Symbol table for frequency of KGram
    private TreeMap<String, Integer> frequencyKGram;
    // Symbol table for frequency of characters
    // following the KGram
    private TreeMap<String, Integer> frequencyC;
    // order of model
    private int k;

    // Creates a Markov language  model of order k from the given text.
    public MarkovLM(String text, int k) {
        // building frequencyKGram
        // 1. make string circular
        this.k = k;
        frequencyKGram = new TreeMap<String, Integer>();
        frequencyC = new TreeMap<String, Integer>();
        String cyclicText = text + text.substring(0, k);
        int length = text.length();

        // 2. loop for collecting k-gram frequency (get and put)
        for (int i = 0; i < length; i++) {
            String kgram = cyclicText.substring(i, i + k);
            // if seen skip
            if (!frequencyKGram.containsKey(kgram)) {
                frequencyKGram.put(kgram, 0);
            }
            frequencyKGram.put(kgram, frequencyKGram.get(kgram) + 1);

            char x = cyclicText.charAt(i + k);
            String kGramAndX = kgram + x;

            if (!frequencyC.containsKey(kGramAndX)) {
                frequencyC.put(kGramAndX, 0);
            }
            frequencyC.put(kGramAndX, frequencyC.get(kGramAndX) + 1);

        }
    }


    // Returns the order k of the model.
    public int order() {
        return this.k;
    }

    // Returns a string representation of the model (see below).
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String word : frequencyKGram.keySet()) {
            sb.append(word + ": ");
            for (String key : frequencyC.keySet()) {
                if (key.startsWith(word)) {
                    char c = key.charAt(key.length() - 1);
                    sb.append(c);
                    sb.append(" ");
                    sb.append(frequencyC.get(key));
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // Returns the number of times the k-gram occurs in the input text.
    public int freq(String kgram) {
        if (kgram.length() != this.order()) {
            throw new IllegalArgumentException("kgram"
                    + " has wrong length");
        }
        if (!frequencyKGram.containsKey(kgram)) return 0;
        return frequencyKGram.get(kgram);
    }

    // Returns the number of times character c follows the k-gram
    // in the input text.
    public int freq(String kgram, char c) {
        if (kgram.length() != this.order()) {
            throw new IllegalArgumentException("kgram has wrong length");
        }
        String key = kgram + c;
        if (!frequencyC.containsKey(key)) return 0;
        return frequencyC.get(key);
    }

    // Returns an index chosen at random, with probability proportional
    // to probabilities[i]. Entries must be nonnegative and sum to 1.
    // (Replacement for StdRandom.discrete().)
    private static int discrete(double[] probabilities) {
        double r = RANDOM.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (r < cumulative) return i;
        }
        // guard against floating-point round-off
        return probabilities.length - 1;
    }

    // Returns a character chosen at random, with probability proportional
    // to the number of times each character follows the k-gram in
    // the input text.
    public char predictNext(String kgram) {
        if (kgram.length() != this.order()) {
            throw new IllegalArgumentException("kgram "
                    + "has wrong length");
        }
        if (!frequencyKGram.containsKey(kgram)) {
            throw new IllegalArgumentException("kgram "
                    + "does not appear "
                    + "in the input text");
        }
        int countDistinct = 0;
        for (String word : frequencyC.keySet()) {
            if (word.startsWith(kgram)) countDistinct++;
        }
        double[] frequencies = new double[countDistinct];
        char[] characters = new char[countDistinct];
        int i = 0;
        for (String word : frequencyC.keySet()) {
            if (word.startsWith(kgram)) {
                characters[i] = word.charAt(word.length() - 1);
                frequencies[i] = ((double) frequencyC.get(word)
                        / (double) freq(kgram));
                i++;
            }
        }
        char x = characters[discrete(frequencies)];
        return x;
    }

    // Tests this class by directing calling all instance methods.
    public static void main(String[] args) {
        String text1 = "banana";
        MarkovLM model1 = new MarkovLM(text1, 2);
        System.out.println("freq(\"an\", 'a')    = "
                + model1.freq("an", 'a'));
        System.out.println("freq(\"na\", 'b')    = "
                + model1.freq("na", 'b'));
        System.out.println("freq(\"na\", 'a')    = "
                + model1.freq("na", 'a'));
        System.out.println("freq(\"na\")         = "
                + model1.freq("na"));
        System.out.println();

        String text3 = "one fish two fish red fish blue fish";
        MarkovLM model3 = new MarkovLM(text3, 4);
        System.out.println("freq(\"ish \", 'r') = "
                + model3.freq("ish ", 'r'));
        System.out.println("freq(\"ish \", 'x') = "
                + model3.freq("ish ", 'x'));
        System.out.println("freq(\"ish \")      = "
                + model3.freq("ish "));
        System.out.println("freq(\"tuna\")      = "
                + model3.freq("tuna"));
        System.out.println("order()            = " + model3.order());
        System.out.println("predictNext(\"ish \") = "
                + model3.predictNext("ish "));
    }

}
