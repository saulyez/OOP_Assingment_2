package uob.oop;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Toolkit {
    public static List<String> listVocabulary = null;
    public static List<double[]> listVectors = null;
    private static final String FILENAME_GLOVE = "glove.6B.50d_Reduced.csv";

    public static final String[] STOPWORDS = {"a", "able", "about", "across", "after", "all", "almost", "also", "am", "among", "an", "and", "any", "are", "as", "at", "be", "because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does", "either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however", "i", "if", "in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most", "must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "own", "rather", "said", "say", "says", "she", "should", "since", "so", "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us", "wants", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you", "your"};

    public void loadGlove() throws IOException {
        BufferedReader myReader = null;
        listVocabulary = new ArrayList<>();
        listVectors = new ArrayList<>();

        try {
            File myFile = getFileFromResource(FILENAME_GLOVE);
            myReader = new BufferedReader(new FileReader(myFile));
            String line;

            while ((line = myReader.readLine()) != null) {
                String[] separated = line.split(",");
                String word = separated[0];
                listVocabulary.add(word);
                double[] wordValues = new double[separated.length - 1];

                for (int i = 0; i < separated.length - 1; i++) {
                    wordValues[i] = Double.parseDouble(separated[i + 1]);
                }
                listVectors.add(wordValues);
            }

        } catch (FileNotFoundException | URISyntaxException e) {
            throw new IOException(e.getMessage());
        } finally {
            assert myReader != null;
            myReader.close();
        }
        //TODO Task 4.1 - 5 marks
    }
    private static File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = Toolkit.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException(fileName);
        } else {
            return new File(resource.toURI());
        }
    }

    public List<NewsArticles> loadNews() {

        List<NewsArticles> listNews = new ArrayList<>();
        File folder;
        List<String> htmls = new ArrayList<>();
        try {
            folder = new File(String.valueOf((getFileFromResource("News"))));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            if (folder.exists() && folder.isDirectory()) {
                File[] htms = folder.listFiles();
                for (File htm : htms) {
                    if (htm.getName().endsWith(".htm")) {
                        try {
                            BufferedReader newReader = new BufferedReader(new FileReader(htm.toPath().toFile()));
                            String strLine;
                            StringBuilder body = new StringBuilder();
                            while ((strLine = newReader.readLine()) != null) {
                                body.append(strLine);
                            }
                            htmls.add(String.valueOf((body)));
                            newReader.close();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }


        } finally {
            for (String file : htmls) {
                String title = HtmlParser.getNewsTitle(file);
                String content = HtmlParser.getNewsContent(file);
                NewsArticles.DataType dataType = HtmlParser.getDataType(file);
                String label = HtmlParser.getLabel(file);
                NewsArticles articleObject = new NewsArticles(title, content, dataType, label);

                listNews.add(articleObject);
            }
        }
        return listNews;
    }



    public static List<String> getListVocabulary() {
        return listVocabulary;
    }

    public static List<double[]> getlistVectors() {
        return listVectors;
    }
}
