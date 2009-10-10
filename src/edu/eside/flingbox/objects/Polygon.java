package edu.eside.flingbox.objects;

import java.util.Random;

import edu.eside.flingbox.graphics.PolygonRender;
import edu.eside.flingbox.graphics.Renderizable;

public class Polygon extends PolygonRender implements Renderizable {

	public Polygon(float[] points) throws IllegalArgumentException {
		super(points);
	}
	
	public void setRandomColor() {
		Random rnd = new Random();
		setColor(rnd.nextFloat() ,rnd.nextFloat() ,rnd.nextFloat() , 1.0f);
	}

}
