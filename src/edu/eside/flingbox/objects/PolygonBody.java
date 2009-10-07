package edu.eside.flingbox.objects;

import edu.eside.flingbox.graphics.Renderizable;
import edu.eside.flingbox.math.PolygonUtils;

public abstract class PolygonBody extends AtomicBody implements Renderizable {
	protected final float[] mPoints;
	protected final short mPointsCount;
	
	protected final short[] mTriangulationIndexes;
	protected final short mTrianglesCount;
	
	public PolygonBody(float[] points) {
		mPoints = PolygonUtils.douglasPeuckerReducer(points, 5.0f);
		mTriangulationIndexes = PolygonUtils.triangulatePolygon(mPoints);

		mPointsCount = (short) (mPoints.length / 2);
		mTrianglesCount = (short) (mPointsCount - 2);
	}
	
	public int getPointsCount() {
		return mPointsCount;
	}

}
