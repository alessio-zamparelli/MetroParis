package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
//import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private class EdgeTraverseGraphListener implements TraversalListener<Fermata, DefaultEdge> {
		// creo una innerclass per compattare il codice
		@Override
		public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
		}

		@Override
		public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
		}

		@Override
		public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> ev) {
			Fermata sourceVertex = grafo.getEdgeSource(ev.getEdge()); // parent
			Fermata targetVertex = grafo.getEdgeTarget(ev.getEdge()); // child
			if (!backVisit.containsKey(targetVertex) && backVisit.containsKey(sourceVertex)) {
				backVisit.put(targetVertex, sourceVertex);
			} else if (!backVisit.containsKey(sourceVertex) && backVisit.containsKey(targetVertex)) {
				// serve solo per grafi non orientati
				backVisit.put(sourceVertex, targetVertex);
			}
		}

		@Override
		public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
		}

		@Override
		public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
		}
	}

	private Graph<Fermata, DefaultEdge> grafo;
	private List<Fermata> fermate;
	private Map<Integer, Fermata> fermateIdMap;
	private Map<Fermata, Fermata> backVisit;

	/**
	 * crea e popola il grafo
	 */
	public void creaGrafo() {
		// creo il grafo
		this.grafo = new SimpleDirectedGraph<>(DefaultEdge.class);

		// aggiungo i vertici
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.getAllFermate();
		Graphs.addAllVertices(this.grafo, this.fermate);

		// creo idMap
		this.fermateIdMap = new HashMap<Integer, Fermata>();
		for (Fermata f : this.fermate)
			fermateIdMap.put(f.getIdFermata(), f);

//		// aggiungi gli archi v1 - lenta a bestia
//		for (Fermata partenza : this.grafo.vertexSet()) {
//			for (Fermata arrivo : this.grafo.vertexSet()) {
//
//				if (dao.esisteConnessione(partenza, arrivo)) 
//					this.grafo.addEdge(partenza, arrivo);
//				
//
//			}
//		}

		// aggiungi gli archi v2.1 - non funziona e mai funzionerà
//		for (Fermata partenza : this.grafo.vertexSet()) {
//			List<Fermata> arrivi = dao.stazioniArrivo(partenza);
//
//			for (Fermata arrivo : arrivi)
//				this.grafo.addEdge(partenza, arrivo);
//
//		}

		// aggiungi gli archi v2.2
		for (Fermata partenza : this.grafo.vertexSet()) {
			List<Fermata> arrivi = dao.stazioniArrivo(partenza, fermateIdMap);

			for (Fermata arrivo : arrivi)
				this.grafo.addEdge(partenza, arrivo);

		}

		// aggiungi gli archi v3

	}

	public List<Fermata> fermateRaggiungibili(Fermata source) {

		List<Fermata> result = new ArrayList<>();
		backVisit = new HashMap<>();

		GraphIterator<Fermata, DefaultEdge> it = new BreadthFirstIterator<>(this.grafo, source);
//		GraphIterator<Fermata, DefaultEdge> it = new DepthFirstIterator<>(this.grafo, source);

		it.addTraversalListener(new Model.EdgeTraverseGraphListener());
		// posso creare anche una classe anonima (si usa con pochi metodi, tipo comparator)
		backVisit.put(source, null);

		while (it.hasNext())
			result.add(it.next());

		return result;

	}

	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}

	public List<Fermata> getFermate() {
		return fermate;
	}

	public Map<Integer, Fermata> getFermateIdMap() {
		return fermateIdMap;
	}

	public List<Fermata> percorsoFinoA(Fermata target) {

		if (!backVisit.containsKey(target)) {
			// il target non è raggiungibile dalla source
			return null;
		}

		List<Fermata> percorso = new LinkedList<>();

		Fermata f = target;

		while (f != null) {
			percorso.add(0, f);
			System.out.println("considero la fermata: " + f.getNome());
			f = backVisit.get(f);
		}

		return percorso;

	}

}
