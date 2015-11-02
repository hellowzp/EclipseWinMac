package binarytree;

public class HuffmanTree {
	private HuffmanTreeNode root;

	public HuffmanTree(HuffmanTreeNode root) {
		this.root = root;
	}

	public HuffmanTreeNode getRoot() {
		return root;
	}

	public void setRoot(HuffmanTreeNode root) {
		this.root = root;
	}
	
	static class HuffmanTreeNode implements Comparable<HuffmanTreeNode>{
		int weight;
		HuffmanTreeNode left, right;
		
		public HuffmanTreeNode(int weight, HuffmanTreeNode left, HuffmanTreeNode right) {
			this.weight = weight;
			this.left = left;
			this.right = right;
		}

		@Override
		public int compareTo(HuffmanTreeNode other) {
			return this.weight - other.weight;
		}
	}
	
}
