package parser_owl;

/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//import org.semanticweb.HermiT.Reasoner;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.HashMap;

/**
 * An example that shows how to do a Protege like DLQuery. The example contains
 * several helper classes:<br>
 * DLQueryEngine - This takes a string representing a class expression built
 * from the terms in the signature of some ontology. DLQueryPrinter - This takes
 * a string class expression and prints out the sub/super/equivalent classes and
 * the instances of the specified class expression. DLQueryParser - this parses
 * the specified class expression string
 * 
 * @author Matthew Horridge, The University of Manchester, Bio-Health
 *         Informatics Group, Date: 13-May-2010
 */
public class Query {

   
    public static void start(String[] args) {
        try {
            // Load an example ontology. In this case, we'll just load the pizza
            // ontology.
        	if (args.length !=2) {
        		System.out.println("Give as arguments the path to the file where the ontology is and the path to the file where the populated ontology is");
        		System.exit(1);
        	}
        	File ontoFile=new File(args[0]);
        	File ontoPeupFile=new File(args[1]);
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            
           
            OWLOntology ontology = manager
                    .loadOntologyFromOntologyDocument(ontoFile);
            OWLOntology ontologyPeup = manager
                    .loadOntologyFromOntologyDocument(ontoPeupFile);
            OWLDataFactory df=manager.getOWLDataFactory();
            
            System.out.println("Loaded ontologies: " + ontology+" and "+ontologyPeup);
            // We need a reasoner to do our query answering
            CorrespLabEnt cor= new CorrespLabEnt (ontology, ontologyPeup,manager);
            HashMap<String, OWLEntity> labEnt = new HashMap<>();
            labEnt=cor.get();
            
            OWLReasoner reasoner = createReasoner(ontologyPeup);
            // Entities are named using IRIs. These are usually too long for use
            // in user interfaces. To solve this
            // problem, and so a query can be written using short class,
            // property, individual names we use a short form
            // provider. In this case, we'll just use a simple short form
            // provider that generates short froms from IRI
            // fragments.
            ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
            // Create the DLQueryPrinter helper class. This will manage the
            // parsing of input and printing of results
            DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(
                    new DLQueryEngine(reasoner, shortFormProvider),
                    shortFormProvider,ontology,ontologyPeup,df);
            // Enter the query loop. A user is expected to enter class
            // expression on the command line.
            doQueryLoop(dlQueryPrinter,labEnt);
        } catch (OWLOntologyCreationException e) {
            System.out.println("Could not load ontology: " + e.getMessage());
        } catch (IOException ioEx) {
            System.out.println(ioEx.getMessage());
        }
    }

    private static void doQueryLoop(DLQueryPrinter dlQueryPrinter,HashMap<String, OWLEntity> labEnt)
            throws IOException {
        while (true) {
            // Prompt the user to enter a class expression
            System.out
                    .println("Please type a class expression in Manchester Syntax and press Enter (or press x to exit): labels have to be quoted \n ex 'film en France'");
            System.out.println("");
            String classExpression = readInput();
            // Check for exit condition
            if (classExpression.equalsIgnoreCase("x")) {
                break;
            }
            
           // String classExpression="";
          //  while (classExpression.isQuoted()){
            	
            	
         //   }
            while (classExpression.contains("'")){
            	int deb = classExpression.indexOf("'");
            	int fin = classExpression.indexOf("'",deb+1);
            	String lab = classExpression.substring(deb+1, fin);
            	if (labEnt.containsKey(lab)) System.out.println(lab+" corresponds to fragment"+labEnt.get(lab).getIRI().getFragment());
            	else System.out.println(lab+ " is not a label");
            	
            	classExpression = classExpression.replace(classExpression.substring(deb, fin+1), labEnt.get(lab).getIRI().getFragment());
            	
            }
            System.out.println("en fragment : "+classExpression);
            dlQueryPrinter.askQuery(classExpression.trim());
            System.out.println();
            System.out.println();
        }
    }

    private static String readInput() throws IOException {
        InputStream is = System.in;
        InputStreamReader reader;
        reader = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(reader);
        return br.readLine();
    }

    private static OWLReasoner createReasoner(final OWLOntology rootOntology) {
        // We need to create an instance of OWLReasoner. An OWLReasoner provides
        // the basic query functionality that we need, for example the ability
        // obtain the subclasses of a class etc. To do this we use a reasoner
        // factory.
        // Create a reasoner factory.
    	 OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
      //  OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        return reasonerFactory.createReasoner(rootOntology);
    }
}

/**
 * This example shows how to perform a "dlquery". The DLQuery view/tab in
 * Protege 4 works like this.
 */
class DLQueryEngine {

    private final OWLReasoner reasoner;
    private final DLQueryParser parser;

    /**
     * Constructs a DLQueryEngine. This will answer "DL queries" using the
     * specified reasoner. A short form provider specifies how entities are
     * rendered.
     * 
     * @param reasoner
     *        The reasoner to be used for answering the queries.
     * @param shortFormProvider
     *        A short form provider.
     */
    public DLQueryEngine(OWLReasoner reasoner,
            ShortFormProvider shortFormProvider) {
        this.reasoner = reasoner;
        OWLOntology rootOntology = reasoner.getRootOntology();
        parser = new DLQueryParser(rootOntology, shortFormProvider);
        
    }
    
    
    /**
     * Gets the .
     * 
     * @param classExpressionString
     *        The string from which the class expression will be parsed.
     * @param direct
     *        Specifies whether direct superclasses should be returned or not.
     * @return .
     */
   // public Set<OWLAnnotation> getLabels(String classExpressionString) {
   //     if (classExpressionString.trim().length() == 0) {
   //        return Collections.emptySet();
   //     }
   //     OWLClassExpression classExpression = parser
   //             .parseClassExpression(classExpressionString);
     //   NodeSet<OWLClass> superClasses = reasoner.get
     //   		getSuperClasses(
      //          classExpression);
     //   return superClasses.getFlattened();
   // }
    

    /**
     * Gets the superclasses of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *        The string from which the class expression will be parsed.
     * @param direct
     *        Specifies whether direct superclasses should be returned or not.
     * @return The superclasses of the specified class expression If there was a
     *         problem parsing the class expression.
     */
    public Set<OWLClass> getSuperClasses(String classExpressionString,
            boolean direct) throws ParserException {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(
                classExpression, direct);
        return superClasses.getFlattened();
    }

    /**
     * Gets the equivalent classes of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *        The string from which the class expression will be parsed.
     * @return The equivalent classes of the specified class expression If there
     *         was a problem parsing the class expression.
     */
    public Set<OWLClass> getEquivalentClasses(String classExpressionString) throws ParserException{
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        Node<OWLClass> equivalentClasses = reasoner
                .getEquivalentClasses(classExpression);
        Set<OWLClass> result;
        if (classExpression.isAnonymous()) {
            result = equivalentClasses.getEntities();
        } else {
            result = equivalentClasses.getEntitiesMinus(classExpression
                    .asOWLClass());
        }
        return result;
    }

    /**
     * Gets the subclasses of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *        The string from which the class expression will be parsed.
     * @param direct
     *        Specifies whether direct subclasses should be returned or not.
     * @return The subclasses of the specified class expression If there was a
     *         problem parsing the class expression.
     */
    public Set<OWLClass> getSubClasses(String classExpressionString,
            boolean direct) throws ParserException{
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLClass> subClasses = reasoner.getSubClasses(classExpression,
                direct);
        return subClasses.getFlattened();
    }

    /**
     * Gets the instances of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *        The string from which the class expression will be parsed.
     * @param direct
     *        Specifies whether direct instances should be returned or not.
     * @return The instances of the specified class expression If there was a
     *         problem parsing the class expression.
     */
    public Set<OWLNamedIndividual> getInstances(String classExpressionString,
            boolean direct) throws ParserException {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(
                classExpression, direct);
        return individuals.getFlattened();
    }
}

class DLQueryParser {

    private final OWLOntology rootOntology;
    private final BidirectionalShortFormProvider bidiShortFormProvider;

    /**
     * Constructs a DLQueryParser using the specified ontology and short form
     * provider to map entity IRIs to short names.
     * 
     * @param rootOntology
     *        The root ontology. This essentially provides the domain vocabulary
     *        for the query.
     * @param shortFormProvider
     *        A short form provider to be used for mapping back and forth
     *        between entities and their short names (renderings).
     */
    public DLQueryParser(OWLOntology rootOntology,
            ShortFormProvider shortFormProvider) {
        this.rootOntology = rootOntology;
        OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
        Set<OWLOntology> importsClosure = rootOntology.getImportsClosure();
        // Create a bidirectional short form provider to do the actual mapping.
        // It will generate names using the input
        // short form provider.
        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(
                manager, importsClosure, shortFormProvider);
    }

    
    
    
    
    /**
     * Parses a class expression string to obtain a class expression.
     * 
     * @param classExpressionString
     *        The class expression string
     * @return The corresponding class expression if the class expression string
     *         is malformed or contains unknown entity names.
     *         
     *         
     */
    public OWLClassExpression
            parseClassExpression(String classExpressionString) throws ParserException{
        OWLDataFactory dataFactory = rootOntology.getOWLOntologyManager()
                .getOWLDataFactory();
        // Set up the real parser
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
                dataFactory, classExpressionString);
        parser.setDefaultOntology(rootOntology);
        // Specify an entity checker that wil be used to check a class
        // expression contains the correct names.
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(
                bidiShortFormProvider);
        
        	
        parser.setOWLEntityChecker(entityChecker);
        // Do the actual parsing
        return parser.parseClassExpression();
    }
}

class DLQueryPrinter {

    private final DLQueryEngine dlQueryEngine;
    private final ShortFormProvider shortFormProvider;
    private final OWLOntology onto;
    private final OWLOntology ontoPeup;
    private final OWLDataFactory df;

    /**
     * @param engine
     *        the engine
     * @param shortFormProvider
     *        the short form provider
     */
    public DLQueryPrinter(DLQueryEngine engine,
            ShortFormProvider shortFormProvider,OWLOntology onto,OWLOntology ontoPeup,OWLDataFactory df) {
        this.shortFormProvider = shortFormProvider;
        dlQueryEngine = engine;
        this.onto = onto;
        this.ontoPeup = ontoPeup;
        this.df=df;
        
        
    }

    /**
     * @param classExpression
     *        the class expression to use for interrogation
     */
    public void askQuery(String classExpression) {
        if (classExpression.length() == 0) {
            System.out.println("No class expression specified bla");
        } else {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("\n--------------------------------------------------------------------------------\n");
                sb.append("QUERY:   ");
                sb.append(classExpression);
                sb.append("\n");
                sb.append("--------------------------------------------------------------------------------\n\n");
                // Ask for the subclasses, superclasses etc. of the specified
                // class expression. Print out the results.
                Set<OWLClass> superClasses = dlQueryEngine.getSuperClasses(
                        classExpression, false);
                printEntities("SuperClasses", superClasses, sb);
                Set<OWLClass> equivalentClasses = dlQueryEngine
                        .getEquivalentClasses(classExpression);
                printEntities("EquivalentClasses", equivalentClasses, sb);
                Set<OWLClass> subClasses = dlQueryEngine.getSubClasses(
                        classExpression, false);
                printEntities("SubClasses", subClasses, sb);
                Set<OWLNamedIndividual> individuals = dlQueryEngine
                        .getInstances(classExpression, false);
                printEntities("Instances", individuals, sb);
                System.out.println(sb.toString());
            } catch (ParserException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    
    private String printLabelEntities(OWLEntity ent) {
    	
    	String slab="";
    	OWLAnnotationProperty label = df
                .getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
    	Set<OWLAnnotation> labels = ent.getAnnotations(onto, label);
    	if (!labels.isEmpty()){
    		slab+=slab="\t-label(s) : ";
    		for (OWLAnnotation anot : labels ){
    			slab+=anot.getValue().toString();
    		}
    	}
    	Set<OWLAnnotation> labelspeup = ent.getAnnotations(ontoPeup, label);
        	if (!labelspeup.isEmpty()){
        		if (slab.isEmpty()) slab+=slab="\t-label(s) : ";
        		for (OWLAnnotation anot : labelspeup ){
        			slab+=anot.getValue().toString();
        		}
    		
    	}

    	return slab;
    	
    }

    private void printEntities(String name, Set<? extends OWLEntity> entities,
            StringBuilder sb) {
        sb.append(name);
        int length = 50 - name.length();
        for (int i = 0; i < length; i++) {
            sb.append(".");
        }
        sb.append("\n\n");
        if (!entities.isEmpty()) {
            for (OWLEntity entity : entities) {
                sb.append("\t");
                sb.append(shortFormProvider.getShortForm(entity));
            
                sb.append(printLabelEntities(entity));
             
                sb.append("\n");
            }
        } else {
            sb.append("\t[NONE]\n");
        }
        sb.append("\n");
    }
}

class CorrespLabEnt {
	 private final OWLOntology onto;
	private final OWLOntology ontoPeup;
	private final OWLOntologyManager manager;
	
	public CorrespLabEnt(OWLOntology onto, OWLOntology ontoPeup,OWLOntologyManager manager) {
        
        this.onto = onto;
        this.ontoPeup = ontoPeup;
        this.manager=manager;
       
     }
	
	public HashMap<String, OWLEntity> get() {
		
		HashMap<String, OWLEntity> ens= new HashMap();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLAnnotationProperty label = factory
                .getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
		
		for (OWLClass c : onto.getClassesInSignature()) {
			
	    	Set<OWLAnnotation> labels = c.getAnnotations(onto, label);
	   
	    		for (OWLAnnotation anot : labels ){
	    			String lab = anot.getValue().toString().split("@")[0].replace("\"", "");
	    			if (!ens.containsKey(lab)) ens.put(lab, c);
	    			else System.out.println("attention pb plusieurs entity pour"+lab);
	    		}
		}	
		for (OWLClass c : ontoPeup.getClassesInSignature()) {
			
	    	Set<OWLAnnotation> labels = c.getAnnotations(ontoPeup, label);
	   
	    		for (OWLAnnotation anot : labels ){
	    			String lab = anot.getValue().toString().split("@")[0].replace("\"", "");
	    			if (!ens.containsKey(lab)) ens.put(lab, c);
	    			else System.out.println("attention pb plusieurs entity pour"+lab);
	    		}
		}
		
	for (OWLNamedIndividual c : onto.getIndividualsInSignature()) {
			
	    	Set<OWLAnnotation> labels = c.getAnnotations(onto, label);
	   
	    		for (OWLAnnotation anot : labels ){
	    			String lab = anot.getValue().toString().split("@")[0].replace("\"", "");
	    			if (!ens.containsKey(lab)) ens.put(lab, c);
	    			else System.out.println("attention pb plusieurs entity pour"+lab);
	    		}
		}
		for (OWLNamedIndividual c : ontoPeup.getIndividualsInSignature()) {
			
	    	Set<OWLAnnotation> labels = c.getAnnotations(ontoPeup, label);
	   
	    		for (OWLAnnotation anot : labels ){
	    			String lab = anot.getValue().toString().split("@")[0].replace("\"", "");
	    			if (!ens.containsKey(lab)) ens.put(lab, c);
	    			else System.out.println("attention pb plusieurs entity pour"+lab);
	    		}
		}
		for (OWLDataProperty c : onto.getDataPropertiesInSignature()) {
			
	    	Set<OWLAnnotation> labels = c.getAnnotations(onto, label);
	   
	    		for (OWLAnnotation anot : labels ){
	    			String lab = anot.getValue().toString().split("@")[0].replace("\"", "");
	    			if (!ens.containsKey(lab)) ens.put(lab, c);
	    			else System.out.println("attention pb plusieurs entity pour"+lab);
	    		}
		}
		for (OWLDataProperty c : ontoPeup.getDataPropertiesInSignature()) {
			
	    	Set<OWLAnnotation> labels = c.getAnnotations(ontoPeup, label);
	   
	    		for (OWLAnnotation anot : labels ){
	    			String lab = anot.getValue().toString().split("@")[0].replace("\"", "");
	    			if (!ens.containsKey(lab)) ens.put(lab, c);
	    			else System.out.println("attention pb plusieurs entity pour"+lab);
	    		}
		}
		for (OWLObjectProperty c : onto.getObjectPropertiesInSignature()) {
			
	    	Set<OWLAnnotation> labels = c.getAnnotations(onto, label);
	   
	    		for (OWLAnnotation anot : labels ){
	    			String lab = anot.getValue().toString().split("@")[0].replace("\"", "");
	    			if (!ens.containsKey(lab)) ens.put(lab, c);
	    			else System.out.println("attention pb plusieurs entity pour"+lab);
	    		}
		}
		for (OWLObjectProperty c : ontoPeup.getObjectPropertiesInSignature()) {
			
	    	Set<OWLAnnotation> labels = c.getAnnotations(ontoPeup, label);
	   
	    		for (OWLAnnotation anot : labels ){
	    			String lab = anot.getValue().toString().split("@")[0].replace("\"", "");
	    			if (!ens.containsKey(lab)) ens.put(lab, c);
	    			else System.out.println("attention pb plusieurs entity pour"+lab);
	    		}
		}
		
		
		return ens;
	}
	
	
	
	
	
}

