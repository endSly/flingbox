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
 * 2D vector without application point.
 * Also includes some basic operations with 
 * vector.
 */
public final class Vector2D {
	// Vector components
	public float i = 0f, j = 0f;
	
	/**
	 * Default constructor for an empty vector
	 */
	public Vector2D() { }
	
	/**
	 * Creates new vector.
	 * 
	 * @param i		X component
	 * @param j		Y component
	 */
	public Vector2D(float i, float j) {
		this.i = i;
		this.j = j;
	}
	
	/**
	 * Copy constructor
	 * @param v vector to copy
	 */
	public Vector2D(Vector2D v) {
		this.i = v.i;
		this.j = v.j;
	}

	/**
	 * @return 	Length of vector
	 */
	public float length() {
		return (float) Math.sqrt((i * i) + (j * j));
	}
	
	/**
	 * Negates current vector
	 */
	public Vector2D negate() {
		this.i = -this.i;
		this.j = -this.j;
		return this;
	}
	
	/**
	 * Adds a vector
	 * @param v	Vector
	 * @return	result vector
	 */
	public Vector2D add(Vector2D v) {
		this.i += v.i;
		this.j += v.j;
		return this;
	}
	
	/**
	 * Subs a vector
	 * @param v	Vector
	 * @return	result vector
	 */
	public Vector2D sub(Vector2D v) {
		this.i -= v.i;
		this.j -= v.j;
		return this;
	}
	
	/**
	 * Multiplicates vector by scalar
	 * @param s	Scalar
	 * @return	result vector
	 */
	public Vector2D mul(float s) {
		this.i *= s;
		this.j *= s;
		return this;
	}
	
	/**
	 * Multiplicates vector by matrix
	 * @param m	Matrix
	 * @return	New vector with result
	 */
	public Vector2D mul(Matrix22 m) {
		return new Vector2D(
				this.i * m.values[0] + this.j * m.values[1],
				this.i * m.values[2] + this.j * m.values[3]);
	}
	
	/**
	 * Normalizes vector
	 * @return	Normalized vector
	 */
	public Vector2D normalize() {
		final float len = length();
		this.i /= len;
		this.j /= len;
		return this;
	}
	
	/**
	 * Computes vector's normal
	 * 
	 * @return New vector with the normal.
	 */
	public Vector2D normalVector() {
		return new Vector2D(this.j, -this.i);
	}
	
}
