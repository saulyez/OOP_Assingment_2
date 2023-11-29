package uob.oop;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class ArticlesEmbedding extends NewsArticles {
    private int intSize = -1;
    private String processedText = "";

    private INDArray newsEmbedding = Nd4j.create(0);

    public ArticlesEmbedding(String _title, String _content, NewsArticles.DataType _type, String _label) {
        //TODO Task 5.1 - 1 Mark
        super(_title,_content, _type,_label);
    }

    public void setEmbeddingSize(int _size) {
        //TODO Task 5.2 - 0.5 Marks
        intSize = _size;
    }

    public int getEmbeddingSize(){
        return intSize;
    }

    @Override
    public String getNewsContent() {
        //TODO Task 5.3 - 10 Marks

        List<String> lemText = new ArrayList<>();
        String content = textCleaning(super.getNewsContent());


            try {
                Properties props = new Properties();

                props.setProperty("annotators", "tokenize,pos,lemma");
                StanfordCoreNLP pipeLine = new StanfordCoreNLP(props);
                CoreDocument document = new CoreDocument(content);

                pipeLine.annotate(document);

                List<CoreLabel> tokens = document.tokens();

                for (CoreLabel token : tokens) {
                    String lemma = token.lemma();
                    lemText.add(lemma);
                }


                StringBuilder processedTextBuilder = new StringBuilder();

                for (String lemma : lemText) {
                    boolean isStopWord = false;
                    for (String stopWord : Toolkit.STOPWORDS) {
                        if (lemma.equals(stopWord)) {
                            isStopWord = true;
                            break;
                        }
                    }
                    if (!isStopWord) {
                        processedTextBuilder.append(lemma).append(" ");
                    }
                }

                // Remove the trailing space and convert to a string

                processedText = processedTextBuilder.toString().trim();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        return processedText.trim();
    }

    public INDArray getEmbedding() throws Exception {
        //TODO Task 5.4 - 20 Marks


        return Nd4j.vstack(newsEmbedding.mean(1));
    }

    /***
     * Clean the given (_content) text by removing all the characters that are not 'a'-'z', '0'-'9' and white space.
     * @param _content Text that need to be cleaned.
     * @return The cleaned text.
     */
    private static String textCleaning(String _content) {
        StringBuilder sbContent = new StringBuilder();

        for (char c : _content.toLowerCase().toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || Character.isWhitespace(c)) {
                sbContent.append(c);
            }
        }

        return sbContent.toString().trim();
    }
}
