package student;

public class HuffmanNode {
    /**
     * The bit zero node.
     */
    private HuffmanNode zero;
    /**
     * The bit one child node.
     */
    private HuffmanNode one;
    /**
     * The character that this node stores.
     */
    private Character data;

    /**
     * Create a new Huffman internal node.
     *
     * @param zero The bit zero child.
     * @param one  The bit one child.
     */
    public HuffmanNode(HuffmanNode zero, HuffmanNode one) {
        this.zero = zero;
        this.one = one;
        data = null;
    }

    /**
     * Create a new Huffman leaf node.
     *
     * @param data The data in the leaf.
     */
    public HuffmanNode(Character data) {
        this.data = data;
        zero = null;
        one = null;
    }

    /**
     * Get the data in this node.
     *
     * @return The data or null if none exists.
     */
    public Character getData() {
        return data;
    }

    /**
     * Get the bit one child of this node.
     *
     * @return The bit one child or null if none exists.
     */
    public HuffmanNode getOne() {
        return one;
    }

    /**
     * Get the bit zero child of this node.
     *
     * @return The bit zero child or null if none exists.
     */
    public HuffmanNode getZero() {
        return zero;
    }

    /**
     * Set the data that this node stores.
     *
     * @param data The new data.
     */
    public void setData(Character data) {
        this.data = data;
    }

    /**
     * Set the bit one child of this node.
     *
     * @param one The new child.
     */
    public void setOne(HuffmanNode one) {
        this.one = one;
    }

    /**
     * Set the bit zero child of this node.
     *
     * @param zero The new child.
     */
    public void setZero(HuffmanNode zero) {
        this.zero = zero;
    }

    /**
     * Check if this node is a leaf.
     * A node is a leaf if there are no children.
     *
     * @return true if the node is a leaf node.
     */
    public boolean isLeaf() {
        return one == null && zero == null;
    }

    /**
     * Checks if this node is valid.
     *
     * @return True if the node is valid.
     */
    public boolean isValidNode() {
        return (one == null && zero == null && data != null)
                || (one != null && zero != null && data == null);
    }

    /**
     * Checks if this node and all nodes at a greater height are valid.
     *
     * @return True if the tree is valid.
     */
    public boolean isValidTree() {
        if (isLeaf()) {
            return data != null;
        }

        return this.isValidNode() && one.isValidTree() && zero.isValidTree();
    }
}
