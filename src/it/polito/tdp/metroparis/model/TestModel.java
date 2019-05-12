package it.polito.tdp.metroparis.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {

		long start = System.nanoTime();
		Model m = new Model();

		m.creaGrafo();

		long finish = System.nanoTime();

//		System.out.println(m.getGrafo());
//		System.out.println(m.getFermate());
		System.out.format("Tempo impiegato: %.6f secondi\n", (finish - start) / 1e9);

		System.out.format("Creati %d vertici e %d archi\n", m.getGrafo().vertexSet().size(),
				m.getGrafo().edgeSet().size());

		Fermata source = m.getFermate().get(0);
		System.out.println("Parto da: " + source);
		List<Fermata> raggiungibi = m.fermateRaggiungibili(source);
		System.out.println("Fermate raggiunte: " + raggiungibi);
		System.out.println("Sono in totale: " + raggiungibi.size());

		Fermata target = m.getFermate().get(150);
		System.out.format("Il percorso da %s a %s è:", source.getNome(), target.getNome());
		List<Fermata> percorso = m.percorsoFinoA(target);
		System.out.println(percorso);
		
	}
}
