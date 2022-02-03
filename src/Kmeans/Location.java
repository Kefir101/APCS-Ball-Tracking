package Kmeans;
import java.io.Serializable;

/**
 * Represent a location in a rectangular grid.
 *
 * @author David J. Barnes and Michael Kolling.  Modified by David Dobervich 2007-2013
 * @version 2006.03.30
 */
public class Location implements Serializable
{
    // Row and column positions.
    private int row;
    private int col;

    /**
     * Represent a row and column.
     * @param row The row.
     * @param col The column.
     */
    public Location(int col, int row)
    {
        this.row = row;
        this.col = col;
    }

    /**
     * Implement content equality.
     */
    public boolean equals(Object obj)
    {
        if(obj instanceof Location) {
            Location other = (Location) obj;
            return row == other.getRow() && col == other.getCol();
        }
        else {
            return false;
        }
    }

    /**
     * Return a string of the form row,column
     * @return A string representation of the location.
     */
    public String toString()
    {
        return col + "," + row;
    }

    /**
     * Use the top 16 bits for the row value and the bottom for
     * the column. Except for very big grids, this should give a
     * unique hash code for each (row, col) pair.
     * @return A hashcode for the location.
     */
    public int hashCode()
    {
        return (row << 16) + col;
    }

    /**
     * @return The row.
     */
    public int getRow()
    {
        return row;
    }

    /**
     * @return The column.
     */
    public int getCol()
    {
        return col;
    }

    /**
     * @return The row.
     */
    public int gety()
    {
        return row;
    }

    /**
     * @return The column.
     */
    public int getx()
    {
        return col;
    }

    public void setx(int x){
        col = x;
    }
    public void sety(int y){
        row = y;
    }
}
