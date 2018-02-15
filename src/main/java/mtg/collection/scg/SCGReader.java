package mtg.collection.scg;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mtg.collection.CollectionManager;
import mtg.collection.editions.Editions;

public class SCGReader {

	private final int numberOfChromes;
	private ConcurrentLinkedQueue<Editions> editionsList;

	public SCGReader(final int numberOfChromes, final ConcurrentLinkedQueue<Editions> editionsList) {
		this.numberOfChromes = numberOfChromes;
		this.editionsList = editionsList;
	}

	public void start() {
		final ExecutorService executor = Executors.newFixedThreadPool(numberOfChromes);
		while (!editionsList.isEmpty()) {
			Runnable worker = new SCGThread(editionsList.poll());
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		CollectionManager.writeCollection();
	}

}
