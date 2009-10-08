package edu.eside.flingbox.graphics;

import javax.microedition.khronos.opengles.GL10;

public interface Renderizable {
	/**
	 * 
	 * @param gl
	 * @return
	 */
	public boolean onRender(GL10 gl);
}
