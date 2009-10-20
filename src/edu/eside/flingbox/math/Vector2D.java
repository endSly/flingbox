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
	 * 
	 * @param v vector to copy
	 */
	public Vector2D(Vector2D v) {
		this.i = v.i;
		this.j = v.j;
	}
	
	/**
	 * Constructs vector to point
	 * 
	 * @param p point
	 */
	public Vector2D(Point p) {
		this.i = p.x;
		this.j = p.y;
	}
	
	/**
	 * Copys values form other vector
	 * 
	 * @param v vector to be copied
	 */
	public void set(Vector2D v) {
		this.i = v.i;
		this.j = v.j;
	}
	
	/**
	 * Sets vector's values
	 * 
	 * @param i x component of the vector
	 * @param j y component of the vector
	 */
	public void set(float i, float j) {
		this.i = i;
		this.j = j;
	}

	/**
	 * Computes length of the vector
	 * 
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
	 * 
	 * @param v
	 * @return
	 */
	public float dotProduct(Vector2D v) {
		return this.i * v.i + this.j * v.j;
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
	
	/**
	 * Computes distance from current vector, to point
	 * pointed by p.
	 * 
	 * @param p vector to point
	 * @return distance
	 */
	public float distanceToPoint(Vector2D p) {
		Vector2D dir = (new Vector2D(this)).normalize();
		float f = dir.dotProduct(p);
		dir.mul(f);
		// Return distance
		return (float) Math.abs(Math.sqrt(
				(p.i - dir.i) * (p.i - dir.i) + (p.j - dir.j) * (p.j - dir.j)));
	}
	
	/**
	 * Resturn string with the vector
	 */
	public String toString() {
		return "Vector(" + this.i + "i + " + this.j + "j)";
	}
	
}
