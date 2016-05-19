/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package parser_owl;

import java.io.File;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 *
 * @author gautier
 */
public class ParserOwlManager {
    private OWLOntologyManager manager;
    private File inputFile;
    private File outputFile;
    private OWLOntology inputOnto;
    private OWLOntology outputOnto;
    private PrefixManager inputPrefix;
    private PrefixManager outputPrefix;
    private OWLDataFactory factory;
    
    private static final String INPUT_PATH = "/Users/gautier/Documents/web_sem/onto1.owl";
    private static final String OUTPUT_PATH = "/Users/gautier/Documents/web_sem/onto1Peup.owl";
    
    private static final String HAS_DIRECTOR_URI = "OWLObjectProperty_f4947b42_4507_4868_b989_eb02d4a6c209";
    private static final String HAS_NATIONALITY_URI = "OWLObjectProperty_2da62a26_b107_4b25_a584_f8b116d46cf3";
    private static final String ACT_IN_URI = "OWLObjectProperty_2f267344_aa9a_4965_9430_f23db0b52ff9";
    private static final String DIRECTED_IN_URI = "OWLObjectProperty_431cc85e_11c7_4965_8498_5076c22638a1";
    private static final String RELEASE_DATE_URI = "OWLObjectProperty_b7e0da74_0b3c_4ef0_8139_c0637f74e877";

    private static final String RATING_URI = "OWLDataProperty_b7d539b5_320a_42dd_ab93_e2f1f59635f3";
    private static final String DURATION_URI = "OWLDataProperty_c2793f2f_f0e7_4d28_9677_244ec1a824ac";
    
    public ParserOwlManager() {
        try {
            this.manager = OWLManager.createOWLOntologyManager();
            this.inputFile = new File(ParserOwlManager.INPUT_PATH);
            this.inputOnto = manager.loadOntologyFromOntologyDocument(this.inputFile);
            System.out.println("Loaded ontology: " + inputOnto);
            
            IRI IRIonto = this.inputOnto.getOntologyID().getOntologyIRI();
            IRI IRIoutputOnto = IRI.create(IRIonto.toString()+"peup");
            this.outputOnto = manager.createOntology(new OWLOntologyID(IRIoutputOnto));
            this.outputFile = new File(ParserOwlManager.OUTPUT_PATH);
            
            this.factory = manager.getOWLDataFactory();
            OWLImportsDeclaration importDeclaraton = this.factory.getOWLImportsDeclaration(IRIonto);
            manager.applyChange(new AddImport(this.outputOnto, importDeclaraton));
            this.inputPrefix = new DefaultPrefixManager(IRIonto.toString()+"#");
            this.outputPrefix = new DefaultPrefixManager(IRIoutputOnto.toString()+"#");
        }
        catch (Exception ie) {
            System.out.print("exception: "+ie);
        }
    }
    
    public void store(Film film) {
        try {
            OWLNamedIndividual newFilm = createIndividual(film.getTitle());
            
            OWLNamedIndividual newDirector = createIndividual(film.getDirector());
            createObjectProperty(ParserOwlManager.HAS_DIRECTOR_URI, newFilm, newDirector);
            
            OWLNamedIndividual newPlace = createIndividual(film.getAddress());
            createObjectProperty(ParserOwlManager.DIRECTED_IN_URI, newFilm, newPlace);
            
            film.getActors().stream().forEach((actor) -> {
                createObjectProperty(ParserOwlManager.ACT_IN_URI, createIndividual(actor), newFilm);
            });
            
            if (film.hasYear()) {
                OWLNamedIndividual newYear = createIndividual(film.getYear());
                createObjectProperty(ParserOwlManager.RELEASE_DATE_URI, newFilm, newYear);
            }
            if (film.hasCountry()) {
                OWLNamedIndividual nationality = createIndividual(film.getCountry());
                createObjectProperty(ParserOwlManager.HAS_NATIONALITY_URI, newFilm, nationality);
            }
            if (film.hasRating()) {
                createFloatDataProperty(ParserOwlManager.RATING_URI, newFilm, film.getRating());
            }
            if (film.hasDuration()) {
                createPositiveIntDataProperty(ParserOwlManager.DURATION_URI, newFilm, film.getDuration());
            }
        }
        catch (Exception ie) {
            System.out.print("exception: "+ie);
            
        }
    }
    
    private OWLNamedIndividual createIndividual(String name) {
        OWLNamedIndividual newIndividual = this.factory.getOWLNamedIndividual(":"+name,this.outputPrefix);
        OWLAnnotation anno = this.factory.getOWLAnnotation(this.factory.getRDFSLabel(),this.factory.getOWLLiteral(name,"fr"));
        OWLAnnotationAssertionAxiom annoAssertion = this.factory.getOWLAnnotationAssertionAxiom(newIndividual.getIRI(), anno);
        manager.addAxiom(this.outputOnto, annoAssertion);
        return newIndividual;
    }
    
    private OWLObjectProperty createObjectProperty(String uri, OWLNamedIndividual ind1, OWLNamedIndividual ind2) {
        OWLObjectProperty objectProperty =factory.getOWLObjectProperty(":"+uri,this.inputPrefix);
        OWLObjectPropertyAssertionAxiom objectAssersion = factory.getOWLObjectPropertyAssertionAxiom(objectProperty, ind1, ind2);
        manager.addAxiom(this.outputOnto, objectAssersion);
        return objectProperty;
    }
    
    private OWLDataProperty createDataProperty(String uri, OWLNamedIndividual ind1, String data, OWL2Datatype type) {
        OWLDatatype integerDatatype = factory.getOWLDatatype(type.getIRI());
        OWLLiteral literal = factory.getOWLLiteral(data, integerDatatype);
        OWLDataProperty dataProperty = factory.getOWLDataProperty(":"+uri,this.inputPrefix);
        OWLDataPropertyAssertionAxiom dataAssersion = factory.getOWLDataPropertyAssertionAxiom(dataProperty, ind1, literal);
        manager.addAxiom(this.outputOnto, dataAssersion);
        return dataProperty;
    }
    
    private OWLDataProperty createFloatDataProperty(String uri, OWLNamedIndividual ind1, String data) {
        return createDataProperty(uri, ind1, data, OWL2Datatype.XSD_FLOAT);
    }
    
    private OWLDataProperty createPositiveIntDataProperty(String uri, OWLNamedIndividual ind1, String data) {
        return createDataProperty(uri, ind1, data, OWL2Datatype.XSD_INTEGER);
    }
    
    public void save() {
        try {
            manager.saveOntology(this.outputOnto, IRI.create(this.outputFile.toURI()));
        }
        catch (Exception ie) {
            System.out.print("exception: "+ie);
            
        }
    }
}
