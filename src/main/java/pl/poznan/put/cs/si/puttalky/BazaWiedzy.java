package pl.poznan.put.cs.si.puttalky;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.api.runtime.KieSession;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/** Author: agalawrynowicz<br>
 * Date: 19-Dec-2016 */

public class BazaWiedzy {

    private OWLOntologyManager manager = null;
    private OWLOntology ontologia;
    private Set<OWLClass> listaKlas;
    private Set<OWLClass> listaDodatkow;
    private Set<OWLClass> listaPizz;
    private Set<String> proponowanePizze;
    
    OWLReasoner silnik;
    
    public void inicjalizuj() {
		InputStream plik = this.getClass().getResourceAsStream("/pizza.owl");
		manager = OWLManager.createOWLOntologyManager();
		
		try {
			ontologia = manager.loadOntologyFromOntologyDocument(plik);
			silnik = new Reasoner.ReasonerFactory().createReasoner(ontologia);
			listaKlas = ontologia.getClassesInSignature();
			listaDodatkow = new HashSet<OWLClass>();
			listaPizz = new HashSet<OWLClass>();
			proponowanePizze= new HashSet<String>();
			OWLClass dodatek  = manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#Dodatek"));
			for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: silnik.getSubClasses(dodatek, false)) {
				listaDodatkow.add(klasa.getRepresentativeElement());
			}
			OWLClass pizza  = manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#Pizza"));
			for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: silnik.getSubClasses(pizza, false)) {
				listaPizz.add(klasa.getRepresentativeElement());
			}
			
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    public Set<String> dopasujDodatek(String s){
    	Set<String> result = new HashSet<String>();
    	for (OWLClass klasa : listaDodatkow){
    		if (klasa.toString().toLowerCase().contains(s.toLowerCase()) && s.length()>2){
    			result.add(klasa.getIRI().toString());
    		}
    	}
    	return result;
    }
    
    public Set<String> wyszukajPizzePoDodatkach(Set<String> dodatki){
    	Set<String> pizze = new HashSet<String>();
    	OWLObjectProperty maDodatek = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#maDodatek"));
    	Set<OWLClassExpression> ograniczeniaEgzystencjalne = new HashSet<OWLClassExpression>();
    	
    	for(String iri : dodatki){
	    	OWLClass dodatek = manager.getOWLDataFactory().getOWLClass(IRI.create(iri));
	    	OWLClassExpression wyrazenie = manager.getOWLDataFactory().getOWLObjectSomeValuesFrom(maDodatek, dodatek);
	    	ograniczeniaEgzystencjalne.add(wyrazenie);
    	}
    	
    	OWLClassExpression pozadanaPizza = manager.getOWLDataFactory().getOWLObjectIntersectionOf(ograniczeniaEgzystencjalne);
    	
		for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: silnik.getSubClasses(pozadanaPizza, false)) {
			pizze.add(klasa.getEntities().iterator().next().asOWLClass().getIRI().getFragment());
		}
	
		return pizze;
    }
    
    public List<String> dopasujPizze(String s){
    	List<String> result = new ArrayList<String>();
    	if(s.length()<3) return result;
    	boolean czyBez = s.toLowerCase().contains("bez ");
    	if(czyBez) s = s.substring(4);

    	for (OWLClass klasaPizza : listaPizz){
    		if (klasaPizza.toString().toLowerCase().contains(s.toLowerCase())){
    			result.add(klasaPizza.getIRI().toString());
    			NodeSet<OWLClass> selected = silnik.getSubClasses(klasaPizza, false);
    			if(!selected.isEmpty()){
    				for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: selected) {
    					Set<OWLClass> set = klasa.getEntitiesMinusBottom();
    					if(!set.isEmpty()){
        					if(!czyBez) result.remove(klasaPizza.getIRI().toString());
    						result.add(set.iterator().next().asOWLClass().getIRI().toString());
    					}
	    			}
    			}
    		}
    	}
    	if(czyBez){
    		List<String> tmp = new ArrayList<String>();
    		for (OWLClass klasaPizza : listaPizz) tmp.add(klasaPizza.getIRI().toString());
    		for (String sss:result) tmp.remove(sss);
    		result=tmp;
    	}

    	
    	List<String> check = new ArrayList<String>();
		for (String nazwa : result){
			if (proponowanePizze.contains(nazwa)){
				check.add(nazwa);
			};
		}
		result.clear();
		for (String pizza : check){
			String[] temp = pizza.split("#");
			result.add(temp[1]);
		}
    	return result;
    }
    
    public Set<String> wyszukajPizze(String s){
    	Set<String> pizze = new HashSet<String>();

    	OWLClass pizza = manager.getOWLDataFactory().getOWLClass(IRI.create(s));
    	
    	return pizze;
    }
	
	public static void main(String[] args) {
		BazaWiedzy baza = new BazaWiedzy();
		baza.inicjalizuj();
		
		OWLClass mieso = baza.manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#DodatekMięsny"));
		for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: baza.silnik.getSubClasses(mieso, true)) {
			System.out.println("klasa:"+klasa.toString());
		}
		for (OWLClass d:  baza.listaDodatkow){
			System.out.println("dodatek: "+d.toString());
		}
	
	}

	public OWLOntologyManager getManager(){
		return this.manager;
	}

	public void utworzListePizz(Set<String> pizze){
		
		for (String pizza : pizze){
			for (OWLClass klasaPizza : this.listaPizz){
				if (klasaPizza.toString().toLowerCase().contains(pizza.toLowerCase())){
					proponowanePizze.add(klasaPizza.getIRI().toString());	
	    			break;
				}
			}
			
		}
	}

	
}
