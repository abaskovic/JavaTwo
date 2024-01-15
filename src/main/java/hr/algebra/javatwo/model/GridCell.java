package hr.algebra.javatwo.model;

import javafx.scene.Node;

import java.io.Serializable;

public class GridCell implements Serializable {

    private static final long serialVersionUID = 1L;

    private String node;
    private int row;
    private  int col;

    public GridCell(String node, int row, int col) {
        this.node = node;
        this.row = row;
        this.col = col;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
