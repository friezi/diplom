package de.zintel.diplom.graph;
/**
 * 
 */

import java.util.Comparator;
import java.util.PriorityQueue;

import de.zintel.diplom.util.ProgressBarAccessor;

/**
 * Comprises graph-algorithms.
 * 
 * @author Friedemann Zintel
 * 
 */
public abstract class Graph {

	protected static class Pair<Type> {

		public Type first;

		public Type second;

		public Pair(Type first, Type second) {
			this.first = first;
			this.second = second;
		}

	}

	protected static class CostComparator implements Comparator<Pair<Integer>> {

		double[][] costtable;

		public CostComparator(double[][] costtable) {
			this.costtable = costtable;
		}

		public int compare(Pair<Integer> pair1, Pair<Integer> pair2) {

			if ( costtable[pair1.first][pair1.second] < costtable[pair2.first][pair2.second] )
				return -1;
			else if ( costtable[pair1.first][pair1.second] > costtable[pair2.first][pair2.second] )
				return 1;
			else
				return 0;
		}

	}

	/**
	 * @param nodes
	 *            number of nodes
	 * @param adjacencytable
	 *            input - each entry should contain a "1" if nodes are adjacent
	 * @param costtable
	 *            input - each entry should contain the cost between nodes
	 * @param nextnodetable
	 *            output - each entry [x][y] will contain z if the next node
	 *            from node x on the way to node y is node z.
	 */
	public final static void shortestPaths(int nodes, int[][] adjacencytable, double[][] costtable, int[][] nextnodetable) {

		ProgressBarAccessor progressbar = new ProgressBarAccessor();

		progressbar.setMinimum(0);
		progressbar.setMaximum(nodes);
		progressbar.setValue(0);
		progressbar.setStringPainted(true);

		// initialize tables
		for ( int i = 0; i < nodes; i++ ) {
			for ( int j = 0; j < nodes; j++ ) {

				if ( adjacencytable[i][j] == 1 )
					nextnodetable[i][j] = j;
				else
					nextnodetable[i][j] = -1;

			}
		}

		// the algorithm
		// floyd-warshall

		for ( int z = 0; z < nodes; z++ ) {

			progressbar.setValue(z);

			for ( int x = 0; x < nodes; x++ ) {

				if ( x == z )
					continue;

				if ( nextnodetable[x][z] == -1 )
					continue;

				for ( int y = 0; y < nodes; y++ ) {

					if ( y == z || y == x )
						continue;

					if ( nextnodetable[z][y] == -1 )
						continue;

					if ( costtable[x][z] + costtable[z][y] < costtable[x][y] ) {

						costtable[x][y] = costtable[x][z] + costtable[z][y];
						nextnodetable[x][y] = nextnodetable[x][z];

						if ( costtable[x][y] < 0 )
							System.out.println("Graph: WARNING! Negativ costs");

					}

				}

			}

		}

		progressbar.setValue(0);
		progressbar.setStringPainted(false);
		//
		// System.out.println("Costtable:");
		// for ( int x = 0; x < Engine.getNumberOfNodes(); x++ ) {
		// for ( int y = 0; y < Engine.getNumberOfNodes(); y++ ) {
		// System.out.print(Engine.getCosttable()[x][y] + " ");
		// }
		// System.out.println();
		// }

	}

	/**
	 * Calculates a minimum spanning tree according to the costtable.
	 * Spanningtreetable will be filled horizontally first;
	 * 
	 * @param nodes
	 *            number of nodes
	 * @param adjacencytable
	 *            input - each entry should contain a "1" if nodes are adjacent
	 * @param costtable
	 *            input - each entry should contain the cost between nodes
	 * @param spanningtreetable
	 *            output - each entry will contain a "1" if nodes are adjacent
	 */
	public final static void spanningTree(int nodes, byte[][] adjacencytable, double[][] costtable, byte[][] spanningtreetable) {

		// calculate number of edges for the progressbar
		int edges = 0;
		int edgecount = 0;
		for ( int i = 0; i < nodes; i++ )
			for ( int j = 0; j < nodes; j++ )
				if ( adjacencytable[i][j] == 1 )
					edges++;

		ProgressBarAccessor progressbar = new ProgressBarAccessor();

		progressbar.setMaximum(edges);
		progressbar.setMinimum(0);
		progressbar.setValue(0);
		progressbar.setStringPainted(true);

		boolean[] visited = new boolean[nodes];
		Pair<Integer> edge;

		PriorityQueue<Pair<Integer>> pqueue = new PriorityQueue<Pair<Integer>>(1, new CostComparator(costtable));

		// initialize queue
		for ( int i = 0; i < nodes; i++ )
			if ( adjacencytable[0][i] == 1 )
				pqueue.offer(new Pair<Integer>(0, i));

		visited[0] = true;

		while ( pqueue.isEmpty() == false ) {

			edge = pqueue.poll();

			progressbar.setValue(edgecount++);

			if ( visited[edge.second] == true )
				continue;
			else {

				visited[edge.second] = true;

				spanningtreetable[edge.first][edge.second] = 1;

				for ( int i = 0; i < nodes; i++ )
					if ( adjacencytable[edge.second][i] == 1 )
						pqueue.offer(new Pair<Integer>(edge.second, i));

			}

		}

		progressbar.setValue(0);
		progressbar.setStringPainted(false);

	}
}
