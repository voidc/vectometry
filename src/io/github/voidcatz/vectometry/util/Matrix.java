package io.github.voidcatz.vectometry.util;

import io.github.voidcatz.vectometry.Vector;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class Matrix {
	private float[][] matrix;
	
	public Matrix(float[][] matrix) {
		if(!isArrayRegular(matrix)) throw new InvalidParameterException("Given array is not regular");
		this.matrix = matrix;
	}

	public static Matrix identity(int n) {
		Matrix mat = new Matrix(n, n);
		for(int col = 0; col < n; col++) {
			for(int row = 0; row < n; row++) {
				mat.set(col, row, col == row ? 1 : 0);
			}
		}
		return mat;
	}

	public Matrix(int columns, int rows) {
		this(new float[columns][rows]);
	}
	
	public Matrix(int columns, int rows, float[] values) {
		this(columns, rows);
		if(this.size() != values.length) throw new InvalidParameterException("Given array does not fit into the matrix");
		for(int col = 0; col < this.columns(); col++) {
			for(int row = 0; row < this.rows(); row++) {
				this.set(col, row, values[col*(this.columns()+1)+row]);
			}
		}
	}

	public float get(int col, int row) {
		return matrix[col][row];
	}
	
	public void set(int col, int row, float value) {
		matrix[col][row] = value;
	}
	
	public int columns() {
		return matrix.length;
	}
	
	public int rows() {
		return matrix[0].length;
	}
	
	public int size() {
		return this.columns() * this.rows();
	}
	
	public float[] values() {
		float[] values = new float[this.size()];
		for(int col = 0; col < this.columns(); col++) {
			for(int row = 0; row < this.rows(); row++) {
				values[col*(this.columns()+1)+row] = this.get(col, row);
			}
		}
		return values;
	}
	
	public Vector dimensions() {
		return new Vector(this.columns(), this.rows());
	}
	
	public boolean typeEquals(Matrix mat) {
		return mat.dimensions().equals(this.dimensions());
	}
	
	public Matrix add(Matrix mat) {
		return this.operate(mat, (a, b) -> a + b);
	}
	
	public Matrix substract(Matrix mat) {
		return this.operate(mat, (a, b) -> a - b);
	}
	
	public Matrix scale(float scalar) {
		return this.operate(a -> a * scalar);
	}
	
	public Matrix multiply(Matrix mat) {
		if(this.columns() != mat.rows()) throw new InvalidParameterException("The given matrices can't be multiplied");
		Matrix result = new Matrix(mat.columns(  ), this.rows());
		for(int col = 0; col < result.columns(); col++) {
			for(int row = 0; row < result.rows(); row++) {
				float sum = 0;
				for(int i = 0; i < this.columns(); i++) {
					sum += mat.get(col, i) * this.get(i, row);
				}
				result.set(col, row, sum);
			}
		}
		return result;
	}
	
	public Matrix transpose() {
		Matrix result = new Matrix(this.rows(), this.columns());
		for(int col = 0; col < this.columns(); col++) {
			for(int row = 0; row < this.rows(); row++) {
				result.set(row, col, this.get(col, row));
			}
		}
		return result;
	}
	
	public Matrix joinRight(Matrix mat) {
		if(this.rows() != mat.rows()) throw new InvalidParameterException("The given matrices can't be joined right");
		Matrix result = new Matrix(this.columns() + mat.columns(), this.rows());
		for(int col = 0; col < result.columns(); col++) {
			for(int row = 0; row < result.rows(); row++) {
				result.set(col, row, col < this.columns() ? this.get(col, row) : mat.get(col - this.columns(), row));
			}
		}
		return result;
	}
	
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
