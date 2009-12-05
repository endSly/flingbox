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
 * vectors.
 */
public class Vector2D {
	/** Vector components */
	public float i, j;
	
	/**
	 * Default constructor for an empty vector
	 */
	public Vector2D() { }
	
	/**
	 * Creates a new vector.
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
	 * Copies values from other vector
	 * 
	 * @param v vector to be copied
	 */
	public Vector2D set(Vector2D v) {
		this.i = v.i;
		this.j = v.j;
		return this;
	}
	
	/**
	 * Sets vector's values
	 * 
	 * @param i x component of the vector
	 * @param j y component of the vector
	 */
	public Vector2D set(float i, float j) {
		this.i = i;
		this.j = j;
		return this;
	}

	/**
	 * Computes the length of the vector
	 * 
	 * @return 	Length of vector
	 */
	public float length() {
		return (float) Math.sqrt((this.i * this.i) + (this.j * this.j));
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
	 * Negates current vector
	 */
	public static Vector2D negate(Vector2D v) {
		return new Vector2D(-v.i, -v.j);
	}
	
	/**
	 * Adds a vector
	 * 
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
	 * 
	 * @param v	Vector
	 * @return	result vector
	 */
	public Vector2D sub(Vector2D v) {
		this.i -= v.i;
		this.j -= v.j;
		return this;
	}
	
	/**
	 * Multiplies by a scalar
	 * 
	 * @param s	Scalar
	 * @return	result vector
	 */
	public Vector2D mul(float s) {
		this.i *= s;
		this.j *= s;
		return this;
	}
	
	/**
	 * Computes the dot product
	 * 
	 * @param v vector
	 * @return dot product
	 */
	public float dotProduct(Vector2D v) {
		return this.i * v.i + this.j * v.j;
	}
	
	/**
	 * computes the length of projected vector
	 * 
	 * @param v base vector
	 * @return length of projection
	 */
	public float projectOver(Vector2D v) {
		return this.i * v.i + this.j * v.j;
	}
	
	/**
	 * computes the side of vector
	 * 
	 * @param v vector to be compared
	 * @return true if two vectors angle is less than 90�
	 */
	public boolean isAtSameSide(Vector2D v) {
		return (this.i * v.i + this.j * v.j) > 0f;
	}
	
	/**
	 * Computes the Z axis of the cross product
	 * 
	 * @param v vector
	 * @return Z axis of cross product
	 */
	public float crossProduct(Vector2D v) {
		return this.i * v.j - this.j * v.i;
	}
	
	/**
	 * Computes angle formed by current vector and a given vector.
	 * 
	 * @param v Vector
	 * @return angle formed [0..PI]
	 */
	public float angleWithVector(Vector2D v) {
		float d = dotProduct(v) / (length() * v.length());
		return (float) Math.acos(d);
	}
	
	/**
	 * Multiplies a vector by a matrix
	 * 
	 * @param v	Vector
	 * @param m	Matrix
	 * @return	New vector with the result
	 */
	public static Vector2D mul(Vector2D v, Matrix22 m) {
		return new Vector2D(
				v.i * m.values[0] + v.j * m.values[1],
				v.i * m.values[2] + v.j * m.values[3]);
	}
	
	/**
	 * Normalizes the vector
	 * 
	 * @return	Normalized vector
	 */
	public Vector2D normalize() {
		final float len = length();
		this.i /= len;
		this.j /= len;
		return this;
	}
	
	/**
	 * Computes the vector's normal
	 * 
	 * @return Vector with the normal.
	 */
	public Vector2D normalVector() {
		this.i = -this.j;
		this.j = this.i;
		return this;
	}
	
	/**
	 * Computes the vector's normal
	 * 
	 * @return New vector with the normal.
	 */
	public static Vector2D normalVector(Vector2D v) {
		return new Vector2D(-v.j, v.i);
	}

	/**
	 * Calculates the distance to a given point
	 * 
	 * @param p point
	 * @return distance
	 */
	public float distanceToPoint(Vector2D p) {
		Vector2D dir = (new Vector2D(this)).normalize();
		dir.mul(dir.dotProduct(p));
		return (float) Math.abs(Math.sqrt(
				(p.i - dir.i) * (p.i - dir.i) + (p.j - dir.j) * (p.j - dir.j)));
	}
	
	/**
	 * Returns a string representing the vector
	 */
	public String toString() {
		return "[Vector (" + this.i + "i + " + this.j + "j)]";
	}
	
}
