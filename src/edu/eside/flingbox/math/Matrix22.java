/*
 *  Flingbox - An OpenSource physics sandbox for Google's Android
 *  Copyright (C) 2009  Jon Ander Peñalba & Endika Gutiérrez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.eside.flingbox.math;

/**
 * Basic 2x2 matrix implementation
 * This will be used to storage rotational matrix
 */
public final class Matrix22 {
	// Matrix values
	public float[] values;
	
	/**
	 * Creates zero matrix
	 */
	public Matrix22() {
		this.values = new float[4]; 
	}
	
	/**
	 * Creates matrix with values
	 * @param m	Values
	 */
	public Matrix22(float[] m) {
		this.values = new float[4];
		// Copy vector
		for (int i = m.length > 4 ? 4 : m.length; i-- > 0; )
			this.values[i] = m[i];
	}
	
	/**
	 * Constructor for a Rotation matrix
	 * @param angle	Angle for rotation
	 * @return		New rotation matrix 
	 */
	public static Matrix22 rotationMatrix(float angle) {
		final float cos = (float) Math.cos(angle);
		final float sin = (float) Math.sin(angle);
		
		return new Matrix22(new float[] {
				cos, -sin, 
				sin, cos});
	}
	
	/**
	 * Transposes matrix
	 * @return	New matrix with transpose
	 */
	public Matrix22 transpose() {
		return new Matrix22(new float[] {
				values[0], values[2], 
				values[1], values[3]});
	}
	
	/**
	 * Computes determinat
	 * @return	determinant
	 */
	public float determinant() {
		return values[0] * values[3] - values[1]* values[2];
	}
	
	/**
	 * Computes matrix invert
	 * @return	New matrix with inverted current matrix or null if determinat is Zero
	 */
	public Matrix22 invert() {
		final float det = determinant();
		if (det == 0)
			return null;
		
		return new Matrix22(new float[] {
				values[3] / det, values[1] / det, 
				values[2] / det, values[0] / det});
	}
}
