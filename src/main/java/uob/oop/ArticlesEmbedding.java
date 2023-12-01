package uob.oop;

import com.sun.source.tree.TryTree;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;

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
        if (!processedText.isEmpty()) {
            return processedText.trim();
        }

        String content = textCleaning(super.getNewsContent());

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,pos,lemma");
        StanfordCoreNLP pipeLine = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument(content);

        pipeLine.annotate(document);

        StringBuilder builder = new StringBuilder();

        for (CoreLabel token : document.tokens()) {
            String lem = token.lemma();
            boolean isStop = false;
            for (String stopWord : Toolkit.STOPWORDS) {
                if (lem.equals(stopWord)) {
                    isStop = true;
                    break;
                }
            }
            if (!isStop) {
                builder.append(lem).append(" ");
            }
        }

        processedText = builder.toString().toLowerCase();

        return processedText.trim();
    }

    public INDArray getEmbedding() throws Exception {
        // Ensure intSize is initialized

        int intSize = getEmbeddingSize();
        if (intSize == -1) {
            throw new InvalidSizeException("Invalid size");
        }

        if (processedText.isEmpty()) {
            throw new InvalidTextException("Invalid text");
        }

        int vectorSize = AdvancedNewsClassifier.listGlove.get(0).getVector().getVectorSize();
        String[] splitText = processedText.split("\\s+");
        INDArray newsEmbedding = Nd4j.create(splitText.length, vectorSize);

        for (int i = 0; i < splitText.length; i++) {
            String word = splitText[i];
            for (Glove myGlove : AdvancedNewsClassifier.listGlove) {
                if (myGlove.getVocabulary().equals(word)) {
                    double[] gloveDouble = myGlove.getVector().getAllElements();
                    INDArray ndjArray = Nd4j.create(gloveDouble);
                    newsEmbedding.putRow(i, ndjArray);
                    break;
                }
            }
        }

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
