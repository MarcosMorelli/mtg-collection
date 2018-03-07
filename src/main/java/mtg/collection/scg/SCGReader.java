package mtg.collection.scg;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mtg.collection.editions.Editions;

public class SCGReader {

	private final ConcurrentLinkedQueue<SCGThread> editionsThreadsList;
	private final ExecutorService executor;

	public SCGReader(final int numberOfChromes, final ConcurrentLinkedQueue<Editions> editionsList) {
		executor = Executors.newFixedThreadPool(numberOfChromes);
		editionsThreadsList = new ConcurrentLinkedQueue<SCGThread>();
		while (!editionsList.isEmpty()) {
			editionsThreadsList.add(new SCGThread(editionsList.poll()));
		}
	}

	public void start() {
		editionsThreadsList.forEach(thread -> {
			executor.execute(thread);
		});
		executor.shutdown();
	}
	
	public boolean isDone() {
		return executor.isTerminated();
	}
	
	public ConcurrentLinkedQueue<SCGThread> getEditionsThreads() {
		return editionsThreadsList;
	}

}
