import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Chat{
    // client for the MarkovLM that generates
    // random text based on kgram frequency
    // and frequency of the character following it
    public static void main(String[] args) throws IOException {
        int k = Integer.parseInt(args[0]);
        final int T = Integer.parseInt(args[1]);
        // read the entire input text from standard input
        String s = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);
        MarkovLM model = new MarkovLM(s, k);
        String firstKGram = s.substring(0, k);
        System.out.print(firstKGram);

        for (int i = 0; i < T - k; i++) {
            char x = model.predictNext(firstKGram);
            System.out.print(x);
            if (k > 0) {
                firstKGram = firstKGram.substring(1) + x;
            }
        }
        System.out.println();
    }
}
