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


public class Populate {
    public static void start() {
        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            
            /*chargement de l'ontologie ie le vocabulaire
            * à utiliser pour décrire les données
            */
            
            //fichier contenant l'ontologie
            File file = new File("/Users/gautier/Documents/web_sem/onto1.owl"); //modifier et mettre le bon chemin
            //parsage du fichier pour obtenir un objet onto de type OWLOntology
            OWLOntology onto = manager.loadOntologyFromOntologyDocument(file);
            System.out.println("Loaded ontology: " + onto);
            
            //récupération de l'IRI de l'ontologie onto
            IRI IRIonto = onto.getOntologyID().getOntologyIRI();
            
            /*création d'une nouvelle ontologie ontoPeup
            * qui contiendra les instances et relations entre instances extraites des sources
            * et décrites à partir du vocabulaire défini dans l'ontologie onto
            */
            
            //création d'une nouvelle IRI à partir de l'IRI de l'ontologie onto
            IRI IRIontoPeup = IRI.create(IRIonto.toString()+"peup");
            // création de la nouvelle ontologie ontoPeupidentifiée à partir de cette IRI
            OWLOntology ontoPeup = manager.createOntology(new OWLOntologyID(IRIontoPeup));
            // création du fichier qui permettra de sauvegarder ontoPeup une fois peuplée
            File fileOut = new File("/Users/gautier/Documents/web_sem/onto1Peup.owl");
            
            //récupération de l'ensemble des données contenues dans les ontologies chargées
            OWLDataFactory factory = manager.getOWLDataFactory();
            
            //import de l'ontologie onto dans l'ontologie ontoPeup de façon à faire évoluer les deux indépedamment
            OWLImportsDeclaration importDeclaraton = factory.getOWLImportsDeclaration(IRIonto);
            manager.applyChange(new AddImport(ontoPeup, importDeclaraton));
            
            // définitions de prefixes à partir des IRI des deux ontologies pour accéder plus facilement à leur élément
            PrefixManager pmonto = new DefaultPrefixManager(IRIonto.toString()+"#");
            PrefixManager pmontoP = new DefaultPrefixManager(IRIontoPeup.toString()+"#");
            
            /*
            * exemple de récupération d'une classe dans l'ontologie onto
            */
            OWLClass Film = factory.getOWLClass(":film", pmonto);
            
            /*
            * exemple de création d'une instance dans l'ontologie ontoPeup
            */
            OWLNamedIndividual nvfilm = factory.getOWLNamedIndividual(":NV",pmontoP);
            
            /*
            * exemple d'assertion indiquant que l'instance nvfilm est de type Film
            */
            OWLClassAssertionAxiom classAssertion =
                    factory.getOWLClassAssertionAxiom(Film, nvfilm);
            manager.addAxiom(ontoPeup, classAssertion); //on enregistre l'assertion dans l'ontologie ontoPeup
            
            /*
            * exemple d'ajout de label à l'instance nvfilm
            */
            OWLAnnotation lab = factory.getOWLAnnotation(factory.getRDFSLabel(),factory.getOWLLiteral("mon nouveau film","fr"));
            OWLAnnotationAssertionAxiom labassertion= factory.getOWLAnnotationAssertionAxiom(nvfilm.getIRI(), lab);
            manager.addAxiom(ontoPeup, labassertion);
            
            
            /*
            * exemple d'ajout d'une dataproperty à l'instance nvfilm
            */
            OWLDatatype integerDatatype = factory.getOWLDatatype(OWL2Datatype.XSD_INTEGER.getIRI());//recuration du type INTEGER
            OWLLiteral literal3 = factory.getOWLLiteral("33", integerDatatype); //creation du literal 33
            
            OWLDataProperty proNbEntree=factory.getOWLDataProperty(":aPourDuree",pmonto);// récupération de la dataproperty définie dans onto
            OWLDataPropertyAssertionAxiom dataassersion = factory.getOWLDataPropertyAssertionAxiom(proNbEntree, nvfilm, literal3);// assertion du lien entre l instance, la propriété et le litéral
            manager.addAxiom(ontoPeup, dataassersion);	//stockage de l'assertion dans ontoPeup
            
            
            /*
            * exemple d'ajout d'une objectproperty à l'instance nvfilm
            */
            
            OWLNamedIndividual nvreal = factory.getOWLNamedIndividual(":NVREAL",pmontoP); //création d'une instance nvreal
            
            OWLAnnotation labreal = factory.getOWLAnnotation(factory.getRDFSLabel(),factory.getOWLLiteral("mon nouveau real","fr"));
            OWLAnnotationAssertionAxiom labassertionreal= factory.getOWLAnnotationAssertionAxiom(nvreal.getIRI(), labreal);
            manager.addAxiom(ontoPeup, labassertionreal);
            
            OWLObjectProperty propReal =factory.getOWLObjectProperty(":aReal",pmonto);//recupération de l objectproperty dans onto
            OWLObjectPropertyAssertionAxiom objectassersion = factory.getOWLObjectPropertyAssertionAxiom(propReal, nvfilm, nvreal); //assertion du lien
            manager.addAxiom(ontoPeup, objectassersion); //ajout de l'assertion dans ontoPeup
            
            
            //ajout de faits supplémentaires pour s'amuser...
            OWLNamedIndividual france = factory.getOWLNamedIndividual(":France",pmonto);
            OWLNamedIndividual toulouse = factory.getOWLNamedIndividual(":Toulouse",pmontoP);
            OWLObjectPropertyAssertionAxiom tenfassersion = factory.getOWLObjectPropertyAssertionAxiom(factory.getOWLObjectProperty(":seSitueEn",pmonto), toulouse, france);
            manager.addAxiom(ontoPeup, tenfassersion);
            OWLObjectPropertyAssertionAxiom nvfatassersion = factory.getOWLObjectPropertyAssertionAxiom(factory.getOWLObjectProperty(":tourneA",pmonto), nvfilm, toulouse);
            manager.addAxiom(ontoPeup, nvfatassersion);
            
            
            //sauvegarde de l'ontologie peuplée dans le fichier fileOut
            manager.saveOntology(ontoPeup, IRI.create(fileOut.toURI()));
            System.out.println("Sauvegarde de l'ontologie peuplée :" + fileOut.toURI());
            
        }
        catch (Exception ie) {
            System.out.print("b"+ie);
            
        }
    }
    
}
