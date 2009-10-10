package edu.eside.flingbox.objects;

import edu.eside.flingbox.graphics.Renderizable;
import edu.eside.flingbox.math.PolygonUtils;

public abstract class PolygonBody extends AtomicBody implements Renderizable {
	protected final float[] mPoints;
	protected final short mPointsCount;
	
	protected final short[] mTriangulationIndexes;
	protected final short mTrianglesCount;
	
	/**
	 * Default Constructor for a Polygon
	 * @param points	Array of float with 2D polygon points {x0, y0, x1, y1, ...}
	 * @throws IllegalArgumentException		If not enough points
	 */
	public PolygonBody(float[] points) throws IllegalArgumentException {
		final short pointsCount = (short) (points.length / 2);
		
		if (pointsCount < 3)
			throw new IllegalArgumentException("Not points enough to build a polygon.");
		
		mPoints = PolygonUtils.douglasPeuckerReducer(points, 4.0f);
		mTriangulationIndexes = PolygonUtils.triangulatePolygon(mPoints);
		
		mPointsCount = (short) (mPoints.length / 2);;
		mTrianglesCount = (short) (mPointsCount - 2);
	}
	
	public int getPointsCount() {
		return mPointsCount;
	}

}
