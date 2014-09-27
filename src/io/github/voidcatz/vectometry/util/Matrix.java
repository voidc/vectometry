package io.github.voidcatz.vectometry.util;

import io.github.voidcatz.vectometry.Vector;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * uses column-major order
 */
public class Matrix {
	private float[][] matrix;
	
	/**
	 * creates a new matrix with the given values
	 * @param values regular 2D array of float values
	 */
	public Matrix(float[][] values) {
		if(!isArrayRegular(values)) throw new InvalidParameterException("Given array is not regular");
		this.matrix = values;
	}

	/**
	 * creates new matrix
	 * @param n
	 * @return identity n x n matrix
	 */
	public static Matrix identity(int n) {
		Matrix mat = new Matrix(n, n);
		for(int col = 0; col < n; col++) {
			for(int row = 0; row < n; row++) {
				mat.set(col, row, col == row ? 1 : 0);
			}
		}
		return mat;
	}

	/**
	 * creates new matrix with the given numbers of columns and rows
	 * @param columns
	 * @param rows
	 */
	public Matrix(int columns, int rows) {
		this(new float[columns][rows]);
	}
	
	/**
	 * creates a new matrix with the given values
	 * @param columns
	 * @param rows
	 * @param values array of float values in column-major order
	 */
	public Matrix(int columns, int rows, float[] values) {
		this(columns, rows);
		if(this.size() != values.length) throw new InvalidParameterException("Given array does not fit into the matrix");
		for(int col = 0; col < this.columns(); col++) {
			for(int row = 0; row < this.rows(); row++) {
				this.set(col, row, values[col*(this.columns()+1)+row]);
			}
		}
	}

	/**
	 * @param col
	 * @param row
	 * @return the value at the given column and row
	 */
	public float get(int col, int row) {
		return matrix[col][row];
	}
	
	/**
	 * only for the construction of new matrices
	 */
	protected void set(int col, int row, float value) {
		matrix[col][row] = value;
	}
	
	/**
	 * @return number of columns
	 */
	public int columns() {
		return matrix.length;
	}
	
	/**
	 * @return number of rows
	 */
	public int rows() {
		return matrix[0].length;
	}
	
	/**
	 * @return total number of values
	 */
	public int size() {
		return this.columns() * this.rows();
	}
	
	/**
	 * @return array of all values in column-major order
	 */
	public float[] values() {
		float[] values = new float[this.size()];
		for(int col = 0; col < this.columns(); col++) {
			for(int row = 0; row < this.rows(); row++) {
				values[col*(this.columns()+1)+row] = this.get(col, row);
			}
		}
		return values;
	}
	
	/**
	 * @return vector whose x and y coordinates correspond to these columns and rows
	 */
	public Vector dimensions() {
		return new Vector(this.columns(), this.rows());
	}
	
	/**
	 * @param other matrix
	 * @return true if these dimensions are equal to the other matrix's dimensions
	 */
	public boolean typeEquals(Matrix other) {
		return other.dimensions().equals(this.dimensions());
	}
	
	/**
	 * @param other matrix
	 * @return new matrix whose values are the sums of these values and the other matrix's values
	 */
	public Matrix add(Matrix other) {
		return this.operate(other, (a, b) -> a + b);
	}
	
	/**
	 * @param other matrix
	 * @return new matrix whose values are the differences of these values and the other matrix's values
	 */
	public Matrix substract(Matrix mat) {
		return this.operate(mat, (a, b) -> a - b);
	}
	
	/**
	 * @param other matrix
	 * @return new matrix whose values are the scaled values of this matrix
	 */
	public Matrix scale(float scalar) {
		return this.operate(a -> a * scalar);
	}
	
	/**
	 * @param other matrix whose number of rows equal this matrix's number of columns
	 * @return new matrix with the same number of columns as the other matrix and the same number of rows as this matrix
	 */
	public Matrix multiply(Matrix other) {
		if(this.columns() != other.rows()) throw new InvalidParameterException("The given matrices can't be multiplied");
		Matrix result = new Matrix(other.columns(  ), this.rows());
		for(int col = 0; col < result.columns(); col++) {
			for(int row = 0; row < result.rows(); row++) {
				float sum = 0;
				for(int i = 0; i < this.columns(); i++) {
					sum += other.get(col, i) * this.get(i, row);
				}
				result.set(col, row, sum);
			}
		}
		return result;
	}
	
	/**
	 * @return the transposed matrix
	 * -> this[x, y] = transposed[y, x]
	 */
	public Matrix transpose() {
		Matrix result = new Matrix(this.rows(), this.columns());
		for(int col = 0; col < this.columns(); col++) {
			for(int row = 0; row < this.rows(); row++) {
				result.set(row, col, this.get(col, row));
			}
		}
		return result;
	}
	
	/**
	 * appends the other matrix to the right side of this matrix
	 * @param other matrix
	 * @return united matrix
	 */
	public Matrix joinRight(Matrix other) {
		if(this.rows() != other.rows()) throw new InvalidParameterException("The given matrices can't be joined right");
		Matrix result = new Matrix(this.columns() + other.columns(), this.rows());
		for(int col = 0; col < result.columns(); col++) {
			for(int row = 0; row < result.rows(); row++) {
				result.set(col, row, col < this.columns() ? this.get(col, row) : other.get(col - this.columns(), row));
			}
		}
		return result;
	}
	
	/**
	 * appends the other matrix below this matrix
	 * @param other matrix
	 * @return united matrix
	 */
	public Matrix joinBottom(Matrix mat) {
		if(this.columns() != mat.columns()) throw new InvalidParameterException("The given matrices can't be joined right");
		Matrix result = new Matrix(this.columns(), this.rows() + mat.rows());
		for(int col = 0; col < result.columns(); col++) {
			for(int row = 0; row < result.rows(); row++) {
				result.set(col, row, row < this.rows() ? this.get(col, row) : mat.get(col, row - this.rows()));
			}
		}
		return result;
	}
	
	public Matrix operate(UnaryOperator<Float> operator) {
		Matrix nMat = new Matrix(this.columns(), this.rows());
		for(int x = 0; x < this.columns(); x++) {
			for(int y = 0; y < this.rows(); y++) {
				nMat.set(x, y, operator.apply(this.get(x, y)));
			}
		}
		return nMat;
	}
	
	public Matrix operate(Matrix mat, BinaryOperator<Float> operator) {
		if(!this.typeEquals(mat)) throw new InvalidParameterException("The given matrix is not the same size as this matrix");
		Matrix nMat = new Matrix(this.columns(), this.rows());
		for(int x = 0; x < this.columns(); x++) {
			for(int y = 0; y < this.rows(); y++) {
				nMat.set(x, y, operator.apply(this.get(x, y), mat.get(x, y)));
			}
		}
		return nMat;
	}
	
	private boolean isArrayRegular(float[][] array) {
		int rows = array[0].length;
		for(float[] column : array) {
			if(column.length != rows) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		String s = "";
		Matrix printMatrix = this.transpose();
		for(int i = 0; i < printMatrix.columns(); i++) {
			for(int j = 0; j < printMatrix.rows(); j++) {
				s += printMatrix.get(i, j) + (printMatrix.rows()-j > 1 ? "\t" : "");
			}
			s += "\n";
		}
		return s;
	}
	
	@Override
	protected Matrix clone() throws CloneNotSupportedException {
		float[][] array = new float[this.columns()][];
		for(int col = 0; col < this.columns(); col++) {
			array[col] = Arrays.copyOf(matrix[col], matrix[col].length);
		}
		return new Matrix(array);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Matrix)) return false;
		Matrix mat = (Matrix) obj;
		return Arrays.deepEquals(this.matrix, mat.matrix);
	}

}
