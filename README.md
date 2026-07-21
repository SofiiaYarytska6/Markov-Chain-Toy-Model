# Chat — a tiny Markov language model

**Learning project.** I built this to understand, at the simplest possible
level, how statistical language models generate text: count what follows each
*k*-gram in a training text, then sample the next character from those counts.
It's the same "predict the next token" idea behind modern LLMs, shrunk down to
two short Java files with no dependencies.

## How it works

- **`MarkovLM.java`** — an order-*k* Markov model built from an input text.
  Two `TreeMap` symbol tables store (1) how often each *k*-gram occurs and
  (2) how often each character follows each *k*-gram. The text is treated as
  circular (the first character follows the last) so generation never hits a
  dead end. `predictNext(kgram)` samples the next character with probability
  proportional to its observed frequency.
- **`Chat.java`** — the text generator. Takes an order *k* and output
  length *T*, reads a training text from standard input, seeds the state with
  the text's first *k* characters, and repeatedly samples the next character,
  sliding the *k*-gram window forward one character at a time.

Higher *k* means more context and more coherent output — order 0 is
letter-frequency soup, order 7 on Shakespeare produces eerily plausible
almost-English.

## Dependencies

None — only the Java standard library (`java.util.TreeMap`,
`java.util.Random`). Requires a JDK (tested with Java 21).

## Compiling and running

```bash
javac MarkovLM.java Chat.java

# run the built-in tests
java MarkovLM

# generate T characters using an order-k model trained on input.txt
java Chat k T < input.txt

# example: order-2 model, 11 characters
echo -n "gagggagaggcgagaaa" | java Chat 2 11
```

The training text can be anything ASCII — a novel, your own essays, song-free
public-domain poetry from Project Gutenberg. Output differs on every run.

## Sample output

Order-2 model trained on a 17-character toy input:

```
$ java Chat 2 11 < input17.txt
gaggcgagaag
```

## What I learned

- Using ordered symbol tables (`TreeMap`) to get O(log n) lookups and
  lexicographically sorted iteration for free
- Weighted random sampling via cumulative probabilities
- Why circular text handling matters for Markov chains (no dead-end states)
- That a model this simple already captures a surprising amount of an
  author's style — and why real LLMs need vastly more context than a
  fixed-length k-gram window
