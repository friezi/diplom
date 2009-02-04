package de.zintel.diplom.simulation;
import java.awt.Graphics;


/**
 * 
 */

/**
 * @author Friedemann Zintel
 * 
 */
public abstract class VirtualNode extends Node {

	public VirtualNode() {
		super(0, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see DisplayableObject#draw(java.awt.Graphics, int, int)
	 */
	@Override
	protected void draw(Graphics g, int x, int y) {
	}

	/* (non-Javadoc)
	 * @see Node#init()
	 */
	@Override
	public void start() {
	}


}
