package binarytree.huffman;

public class HuffmanTreeNode implements Comparable<HuffmanTreeNode>{
	int weight;
	HuffmanTreeNode left, right, parent;
	
	public HuffmanTreeNode(int weight, HuffmanTreeNode left,
				HuffmanTreeNode right, HuffmanTreeNode parent) {
		this.weight = weight;
		this.left = left;
		this.right = right;
		this.parent = parent;
	}

	@Override
	public int compareTo(HuffmanTreeNode other) {
		return this.weight - other.weight;
	}

}
