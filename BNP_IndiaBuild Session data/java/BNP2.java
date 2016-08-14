package bnp2;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author prakash
 */
public class BNP2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Properties props = new Properties();

        //What all we want to do
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        
        //put that in a pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        //your input sentence
        String text = "Narendra Modi went to USA.";

        //a datastructure for the annotation
        Annotation document = new Annotation(text);

        // run the pipeline on that data structure
        pipeline.annotate(document);

        // access the annotations which has worked on a sentence 
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        //get every sentence 
        for (CoreMap sentence : sentences) {
            
            //get every token in the sentence
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                
                String word = token.get(TextAnnotation.class);
                System.out.println("TOKEN: "+word);
                String pos = token.get(PartOfSpeechAnnotation.class);
                System.out.println("POS: "+pos);
                String ne = token.get(NamedEntityTagAnnotation.class);
                System.out.println("NER: "+ne);
                
                System.out.println("===");
            }
        }

    }
}

