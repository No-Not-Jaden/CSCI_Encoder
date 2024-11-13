package student;

import provided.BinarySequence;

public class HuffmanCodeTree {
    /**
     * The root node of the tree.
     */
    private final HuffmanNode root;

    /**
     * Initialize a new tree.
     *
     * @param root The root of the tree.
     */
    public HuffmanCodeTree(HuffmanNode root) {
        this.root = root;
    }

    /**
     * Initialize a new tree.
     *
     * @param codebook A codebook containing binary sequences for the tree.
     */
    public HuffmanCodeTree(HuffmanCodeBook codebook) {
        root = new HuffmanNode(null);
        for (char letter : codebook) {
            put(codebook.getSequence(letter), letter);
        }
    }

    /**
     * Check if the binary tree is valid.
     *
     * @return True if the tree is a valid Huffman Binary Tree.
     */
    public boolean isValid() {
        return root.isValidTree();
    }

    /**
     * Add a letter to the tree.
     *
     * @param seq    Binary sequence that represents the letter.
     * @param letter Letter to add to the tree.
     */
    public void put(BinarySequence seq, char letter) {
        HuffmanNode lastNode = root;
        for (boolean val : seq) {
            HuffmanNode nextNode = val ? lastNode.getOne() : lastNode.getZero();
            if (nextNode == null) {
                nextNode = new HuffmanNode(null);
                if (val) {
                    lastNode.setOne(nextNode);
                } else {
                    lastNode.setZero(nextNode);
                }
            }
            lastNode = nextNode;
        }
        lastNode.setData(letter);
    }

    /**
     * Decode a binary sequence into readable text with the tree.
     *
     * @param s Binary sequence to decode.
     * @return The resulting characters of the decoded string.
     */
    public String decode(BinarySequence s) {
        if (!isValid()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        HuffmanNode node = root;
        for (boolean val : s) {
            node = val ? node.getOne() : node.getZero();
            if (node.isLeaf()) {
                builder.append(node.getData());
                node = root;
            }
        }
        return builder.toString();
    }
}
