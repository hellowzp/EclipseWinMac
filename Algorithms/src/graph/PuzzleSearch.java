package graph;

//http://www.sunshine2k.de/coding/java/15puzzle/15Puzzle.html
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

class SimpleGraphException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SimpleGraphException(String name) {
		super(name);
	}
}

class SimplePuzzleVertex {
	public String state;
	public SimplePuzzleVertex pre;
	public int dis;

	public static final int COST = 1;

	public SimplePuzzleVertex(String state) throws PuzzleGraphException {
		if (state.length() != 9)
			throw new PuzzleGraphException("invalid state: " + state);
		else {
			HashSet<Character> set = new HashSet<>();
			for (int i = 0; i < state.length(); i++) {
				char c = state.charAt(i);
				if (c < 48 || c > 56 || !set.add(Character.valueOf(c)))
					throw new PuzzleGraphException("invalid state: " + state);
			}
		}

		this.state = state;
		pre = null;
		dis = Integer.MAX_VALUE;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof SimplePuzzleVertex))
			return false;
		return this.state.equals(((SimplePuzzleVertex) o).state);
	}

	public List<SimplePuzzleVertex> getEdges() {
		int[] dir = {-3,1,3,-1};
		List<SimplePuzzleVertex> edges = new ArrayList<>(4);
		for(int i=0; i<4; i++) {
			SimplePuzzleVertex v = adjacentVertex(dir[i]);
			if(v!=null) edges.add(v);			
		}
//		SimplePuzzleVertex[] edges = new SimplePuzzleVertex[4];
//		edges[0] = adjacentVertex(-3);
//		edges[1] = adjacentVertex(1);
//		edges[2] = adjacentVertex(3);
//		edges[3] = adjacentVertex(-1);
		return edges;
	}

	private SimplePuzzleVertex adjacentVertex(int dir) {
		StringBuilder sb = new StringBuilder(this.state);
		int pos = sb.indexOf("0");
		if (pos + dir < 0 || pos + dir > 8)
			return null;
		if (dir == 1 && (pos == 2 || pos == 5))
			return null;
		if (dir == -1 && (pos == 3 || pos == 6))
			return null;

		sb.setCharAt(pos, sb.charAt(pos + dir));
		sb.setCharAt(pos + dir, '0');
		return new SimplePuzzleVertex(sb.toString());
	}

	//http://zhidao.baidu.com/question/52725360.html
	public boolean solvable() {
		int inversion = 0;
		for (int i = 1; i < state.length(); i++) {
			for (int j = 0; j < i; j++) {
				if (state.charAt(j) > state.charAt(i))
					inversion++;
			}
		}
		// target state 123456780 has an inversion of 8
		System.out.println(inversion);
		return inversion % 2 == 0;
	}

	public boolean isVisited(Set<SimplePuzzleVertex> vertexSpace) {
		return vertexSpace.contains(this);
	}
}

public class PuzzleSearch {
	/* Compared with ArrayList, LinkedList can avoid the large overhead 
	 * when add new elements while the capacity is already reached,
	 * in which situation ArrayList will have to reallocate bigger memory.  
	 * While searching in a list is cost when determining whether visited, 
	 * so it's best to use HashSet
	 */
	private List<SimplePuzzleVertex> vertexSpace = new LinkedList<>();
	private static final SimplePuzzleVertex TARGET_VERTEX = new SimplePuzzleVertex("123456780");
	
	public void DFS(String initState) {
		SimplePuzzleVertex iV = new SimplePuzzleVertex(initState);
		if (iV.equals(TARGET_VERTEX)) return;
		
		iV.dis = 0;
		vertexSpace.add(iV);
		boolean isFound = false;
		int steps = 0;
		
		Stack<SimplePuzzleVertex> vStack = new Stack<>();
		vStack.push(iV);
		
		while (!vStack.isEmpty() && !isFound ) {
			iV = vStack.pop();
			System.out.println("pop " + iV.state + " Stack size: " + vStack.size());
			List<SimplePuzzleVertex> edges = iV.getEdges();
			for (SimplePuzzleVertex eV : edges) {
				boolean isVisited = vertexSpace.contains(eV);
				if (isVisited) {
					if (eV.dis > iV.dis + SimplePuzzleVertex.COST) {
						eV.pre = iV;
						eV.dis = iV.dis + SimplePuzzleVertex.COST;
					}
				} else {
					steps ++;
					vStack.push(eV);
					vertexSpace.add(eV);
					eV.pre = iV;
					eV.dis = iV.dis + SimplePuzzleVertex.COST;

					if (eV.equals(TARGET_VERTEX)) {
						isFound = true;
						break;
					}
				}
			}
		}

		if (isFound) {
			int depth = 1;
			System.out.println("Steps used: " + steps);
			printStack(vStack.pop(),depth);
		} else {
			System.out.println("Iteration times " + steps + " 404 error!");
		}
	}
	
	private void printStack(SimplePuzzleVertex v, int depth) {
		if(depth>1000) {
			System.out.println("1000 stacks reached! Bigger number may cause stackOverFlow!!!");
			return;
		}
		if (v.pre != null) {
			printStack(v.pre,++depth);
		} else {
			System.out.println("Depth: " + depth);
		}
		System.out.println(v.state + " " + v.dis);
	}
	
	public void BFS(String initState) {
		SimplePuzzleVertex iV = new SimplePuzzleVertex(initState);
		if (iV.equals(TARGET_VERTEX)) return;
		
		iV.dis = 0;
		vertexSpace.add(iV);
		boolean isFound = false;
		int steps = 0;
		
		Queue<SimplePuzzleVertex> vQueue = new LinkedList<>();
		vQueue.add(iV);
		
		while (!vQueue.isEmpty() && !isFound ) {
			iV = ((LinkedList<SimplePuzzleVertex>) vQueue).pop(); //equivalent to remove();
			System.out.println("pop  " + iV.state + " " + iV.dis + " Queue size: " + vQueue.size());
			List<SimplePuzzleVertex> edges = iV.getEdges();
			for (SimplePuzzleVertex eV : edges) {
				if (vertexSpace.contains(eV)) {
					/* For unweighed BFS, the following check is unnecessary
					 * because if eV already exists, it must be located
					 * in the parent layer or at least the brother layer of iV
					 */
//					if (eV.dis > iV.dis + SimplePuzzleVertex.COST) {
//						eV.pre = iV;
//						eV.dis = iV.dis + SimplePuzzleVertex.COST;
//					}
				} else {
					steps ++;
					vQueue.add(eV);
					vertexSpace.add(eV);
					eV.pre = iV;
					eV.dis = iV.dis + SimplePuzzleVertex.COST;

					if (eV.equals(TARGET_VERTEX)) {
						isFound = true;
						break;
					}
				}
			}
		}

		if (isFound) {
			int depth = 1;
			System.out.println("Steps used: " + steps);
			printStack(((LinkedList<SimplePuzzleVertex>) vQueue).pop(),depth); //pop is equal to removeLast
		} else {
			System.out.println("Iteration times " + steps + " 404 error!");
		}
	}
	
	public static void main(String[] args) {
		new PuzzleSearch().BFS("083456172");
	}

}
