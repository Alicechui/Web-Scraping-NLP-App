// GROUP 1


import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorpusBuilder {

    private String text;
    private String[] sentences;
    private List<List<String>> tokens;
    private List<List<String>> posTags;
    private List<List<String>> lemmas;


    /**
     * Create a CorpusBuilder which generates POS tags and Lemmas for text.
     * @param text The text which should be annotated.
     */
    CorpusBuilder(String text) {
        this.text = text;
        createSentences();
        createTokens();
        createPosTags();
        createLemmas();
    }


    /**
     * Create a CorpusBuilder which generates POS tags and Lemmas for text from a file.
     * @param filename The file which should be annotated.
     */
    CorpusBuilder(File filename) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuffer fileText = new StringBuffer();
        reader.lines().forEach(fileText::append);
        text = fileText.toString();

        createSentences();
        createTokens();
        createPosTags();
        createLemmas();
    }

    /**
     * Returns the text of this CorpusBuilder
     *
     * @return The text of this CorpusBuilder
     */
    public String getText() {
        return text;
    }


    /**
     * Helper method for getSentences()
     */
    public void createSentences() {
        try (InputStream modelIn = new FileInputStream("en-sentence.bin")) {
            SentenceModel model = new SentenceModel(modelIn);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
            this.sentences = sentenceDetector.sentDetect(text);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Return an array with the sentences of the CorpusBuilder
     * @return An array with the sentences of the CorpusBuildr
     */
    public String[] getSentences() { return sentences; }


    /**
     * Helper method for getTokens()
     */
    private void createTokens() {
        tokens = new ArrayList<>();

        try (InputStream modelIn = new FileInputStream("en-tokens.bin")) {

            TokenizerModel model = new TokenizerModel(modelIn);
            Tokenizer tokenizer = new TokenizerME(model);
            for (String s : sentences) {
                String[] sTokens = tokenizer.tokenize(s);
                List<String> curToken = new ArrayList<>(Arrays.asList(sTokens));
                tokens.add(curToken);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Return a List of List with the tokens/words of the text of CorpusBuilder. The first list holds the words of the
     * first sentence, the second list holds the words of the second sentence and so on.
     * @return A List of List the tokens/words of the text of the CorpusBuilder.
     */
    public List<List<String>> getTokens() { return tokens; }


    /**
     * Helper method for getPosTags()
     */
    private void createPosTags() {
        posTags = new ArrayList<>();

        try (InputStream modelIn = new FileInputStream("en-pos.bin")) {
            POSModel model = new POSModel(modelIn);
            POSTaggerME tagger = new POSTaggerME(model);
            for (List<String> st : tokens) {
                String[] tags = tagger.tag(st.toArray(new String[0]));
                List<String> curTag = new ArrayList<>(Arrays.asList(tags));
                posTags.add(curTag);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getPosTag() { return posTags; }


    /**
     * Return a List of List with the POS tags of the text of CorpusBuilder. The first list holds the POS tags of the
     * first sentence, the second list holds the POS tags of the second sentence and so on.
     * @return A List of List with the POS tags of the text of CorpusBuilder.
     */
    public List<List<String>> getPosTags() { return posTags; }


    private void createLemmas() {
        lemmas = new ArrayList<>();

        try (InputStream modelIn = new FileInputStream("en-lemmatizer.bin")) {
            LemmatizerModel model = new LemmatizerModel(modelIn);
            LemmatizerME lemmatizer = new LemmatizerME(model);

            for (int i = 0; i < tokens.size(); i++) {
                List<String> st = tokens.get(i);
                List<String> tmpPos = posTags.get(i);
                String[] tmpLemmas = lemmatizer.lemmatize(
                        st.toArray(new String[0]),
                        tmpPos.toArray(new String[0]));
                lemmas.add(Arrays.asList(tmpLemmas));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Return a List of List with the Lemmas of the text of CorpusBuilder. The first list holds the lemmas of the
     * first sentence, the second list holds the Lemmas of the second sentence and so on.
     *
     * @return A List of List with the Lemmas of the text of CorpusBuilder.
     */
    public List<List<String>> getLemmas() { return lemmas; }

    public static void main(String[] args) {

        CorpusBuilder cbStr = new CorpusBuilder("I love mom. Mom is beautiful!");
        for (String sent : cbStr.getSentences())
            System.out.println(sent);
        System.out.println();
        for (List<String> tokens : cbStr.tokens)
            System.out.println(tokens);
        System.out.println();
        for (List<String> tags : cbStr.posTags)
            System.out.println(tags);
        System.out.println();
        for (List<String> lemmas : cbStr.lemmas)
            System.out.println(lemmas);

        try {
            CorpusBuilder cbFile = new CorpusBuilder(new File("trial.txt"));

            for (String sent : cbFile.getSentences())
                System.out.println(sent);
            System.out.println();
            for (List<String> tokens : cbFile.tokens)
                System.out.println(tokens);
            System.out.println();
            for (List<String> tags : cbFile.posTags)
                System.out.println(tags);
            System.out.println();
            for (List<String> lemmas : cbFile.lemmas)
                System.out.println(lemmas);
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist");
        }
    }
}
