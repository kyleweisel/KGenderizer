package edu.drexel.kyleweisel;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Test {


    public static ArrayList<Person> extractPerson(String file) {

        //location of the classifier model
        String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";

        ArrayList<Person> result = new ArrayList<Person>();

        final KGenderizer genderizer = new KGenderizer();

        try {

            AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

            String fileContents = IOUtils.slurpFile(file);

            List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(fileContents);

            //used for preventing duplicated names.
            HashSet<String> existingNames = new HashSet<String>();

            for (Triple<String, Integer, Integer> item : list) {

                if (item.first().equals("PERSON")) {

                    String namestr = fileContents.substring(item.second(), item.third());
                    namestr = namestr.replace("\n", " ").replace("\r", " ").replaceAll("\\s+", " ").trim();

                    if (!existingNames.contains(namestr)) {

                        existingNames.add(namestr);
                        String[] names = namestr.split(" ");
                        Person p = new Person(names[0]);

                        if(names.length > 1) {
                            p.setLastName(names[1]);
                        }

                        String gender = genderizer.getGender(p.getFirstName());

                        if (gender != null) {
                            p.setGender(gender);
                        }

                        else {
                            p.setGender("female");
                        }

                        result.add(p);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static  void main(final String...args) throws MalformedURLException {


        ArrayList<Person> persons = Test.extractPerson(/*System.getProperty("user.dir") + */"harrypotter.txt");

        System.out.println("The persons array is " + (persons.isEmpty() ? "empty" : "notempty"));

        for (Person p : persons) {
            System.out.println("Person : " + p.toString());
        }

        //final KGenderizer genderizer = new KGenderizer();

        //System.out.println("Testing Matt... " + genderizer.getGender("Matt"));
    }

}
