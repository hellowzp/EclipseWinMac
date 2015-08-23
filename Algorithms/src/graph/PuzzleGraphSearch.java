package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

enum Direction {
	UP(-3), RIGHT(1), DOWN(3), LEFT(-1);

	public int dir;
	Direction(int dir) {
		this.dir = dir;
	}
}

class PuzzleGraphException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

	public PuzzleGraphException( String name )
    {
        super( name );
    }
}

class PuzzleVertex {
	public String state;
	public PuzzleVertex pre;
	public double dis;
	public boolean isVisited;

	public PuzzleVertex(String state) throws SimpleGraphException {
		if(state.length() != 9 ) 
			throw new SimpleGraphException("invalid state: " + state);
		else {
			HashSet<Character> set = new HashSet<>();
			for(int i=0; i<state.length(); i++) {
				char c = state.charAt(i);
				if(c<48 || c>56 || !set.add(Character.valueOf(c)))	
					throw new SimpleGraphException("invalid state: " + state);
			}
		}
		
		this.state = state;
		pre = null;
		dis = Double.MAX_VALUE;
		isVisited = false;
	}
	

	public PuzzleVertex(String state, PuzzleVertex pre, double dis) {
		this.state = state;
		this.pre = pre;
		this.dis = dis;
		isVisited = false;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof PuzzleVertex))
			return false;
		return this.state.equals( ((PuzzleVertex) o).state);
	}

	public PuzzleEdge[] getEdges() {
		PuzzleEdge[] edges = new PuzzleEdge[4];
		edges[0] = adjacentEdge(Direction.UP.dir);
		edges[1] = adjacentEdge(Direction.RIGHT.dir);
		edges[2] = adjacentEdge(Direction.DOWN.dir);
		edges[3] = adjacentEdge(Direction.LEFT.dir);
		return edges;
	}


	private PuzzleEdge adjacentEdge(int adj) {
		StringBuilder sb = new StringBuilder(this.state);
		int pos = sb.indexOf("0");
		if(pos+adj<0 || pos+adj>8) return null;		
		if(adj==Direction.RIGHT.dir && (pos==2 || pos==5)) 			
			return null;			
		if(adj==Direction.LEFT.dir  && (pos==3 || pos==6)) 
			return null;
		
		sb.setCharAt(pos, sb.charAt(pos+adj));
		sb.setCharAt(pos+adj, '0');	
		return new PuzzleEdge(new PuzzleVertex(sb.toString(),
				              this, this.dis + 1));
	}
	
	public boolean solvable() {
		int inversion = 0;
		for(int i = 1; i<state.length(); i++) {
			for(int j=0; j<i; j++) {
				if(state.charAt(j)>state.charAt(i))
					inversion++;
			}
		}
		//target state 123456780 has an inversion of 8
		System.out.println(inversion);
		return inversion%2==0;
	}

	public boolean isVisited(List<PuzzleVertex> vertexSpace) {
		return vertexSpace.contains(this);
//		for(Iterator<PuzzleVertex> it=vertexSpace.iterator(); it.hasNext();) {
//			String s1 = it.next().state;
//			String s2 = this.state;
//			boolean b = s1.equals(s2);
//			if(b) return true;
//			System.out.println(s1 + " " + s2 + " " + s1.equals(s2));
//		}
//		return false;
	}

}

class PuzzleEdge {
	public double cost;
	public PuzzleVertex to;

	public PuzzleEdge(PuzzleVertex v) {
		cost = 1.0;
		to = v;
	}
}

public class PuzzleGraphSearch {
	private List<PuzzleVertex> vertexSpace = new ArrayList<>();
	private static final PuzzleVertex targetVertex = new PuzzleVertex("123456780");
	private boolean isFound = false;
	private int steps = 0;
	
	public void recursiveDFS(String initState) {
		PuzzleVertex iV = new PuzzleVertex(initState);
		if(iV.equals(targetVertex)) return;
		
		iV.dis = 0;
		recursiveAddVertex(iV,0);
		
		System.out.println("404 error");
	}
	
	private void recursiveAddVertex(PuzzleVertex v, int depth) {
//		if(depth>=5) return;
		System.out.println(v.state);
		
		while(!isFound && vertexSpace.add(v)) {
			PuzzleEdge[] edges = v.getEdges();
			for(int i=0; i<4; i++) {
				if(edges[i]!=null && edges[i].to.dis==Double.MAX_VALUE) {
					steps ++;
					edges[i].to.pre = v;
					edges[i].to.dis = v.dis + edges[i].cost;
					if(edges[i].to.equals(targetVertex)) {
						isFound = true;
						vertexSpace.add(edges[i].to);
						System.out.println("Found! steps used: " + steps);
						printStack(edges[i].to);
						return;
					} else {
						recursiveAddVertex(edges[i].to,++depth);
					}
				}
			}
		}	
	}
	
	private void printStack(PuzzleVertex to) {
		if(to.pre!=null) printStack(to.pre);		
		System.out.println(to.state);		
	}

	public void nonrecursiveDFS(String initState) {
		PuzzleVertex iV = new PuzzleVertex(initState);
		if(iV.equals(targetVertex)) return;	
		iV.dis = 0;
		
		Stack<PuzzleVertex> vStack = new Stack<>();
		vStack.push(iV);
		vertexSpace.add(iV);
		
		while(!vStack.isEmpty() && !isFound) {
			PuzzleVertex v = vStack.pop();
//			System.out.println("pop  " + v.state + " " + v.dis);
			PuzzleEdge[] edges = v.getEdges();
			for(int i=0; i<4; i++) {
				if(edges[i]!=null && !edges[i].to.isVisited(vertexSpace)) {
					steps ++;
					vStack.push(edges[i].to);
//					System.out.println("push " + edges[i].to.state + " " + edges[i].to.dis);
					vertexSpace.add(edges[i].to);
					if(edges[i].to.equals(targetVertex)) {
						isFound = true;					
						break;
					}
				}
			}		
		}
		
		if(isFound) {
			printStack(vStack.pop());
			System.out.println("Steps used: " + vStack.size());
		} else {
			System.out.println("Iteration times " + steps + " 404 error!");
		}
		
	}

	public static void main(String[] args) {
	
//		new PuzzleGraphSearch().recursiveDFS("083456172");

//		Set<String> s = new HashSet<>();
//		s.add(new String("STRING"));
//		s.add(new String("STRING"));
//		System.out.println(s.size());
//		
//		Set<PuzzleVertex> vertexSpace = new HashSet<>();
//		PuzzleVertex v1 = new PuzzleVertex("012345678");
//		PuzzleVertex v2 = new PuzzleVertex("012345678");
//		vertexSpace.add(v1);
//		vertexSpace.add(v2);
//		System.out.println(vertexSpace.size());
//		System.out.println(v1.equals(v2));
		
		new PuzzleGraphSearch().nonrecursiveDFS("083456172");
	}
}
