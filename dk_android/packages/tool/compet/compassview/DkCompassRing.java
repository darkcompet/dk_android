/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.compassview;

import java.util.List;

import static java.lang.Character.UPPERCASE_LETTER;

public class DkCompassRing {
	/**
	 * Value true means this ring is visible, otherwise means this ring should be gone
	 */
	public boolean isVisible = true;

	/**
	 * Clockwise rotated angle in degrees.
	 */
	public float rotatedDegrees;

	/**
	 * Name of ring, anything you want to tag.
	 */
	public String ringName;

	/**
	 * Words of this ring.
	 */
	public List<String> words;

	/**
	 * Indicate each word is horizontal or vertical.
	 */
	public boolean isHorizontalWord;

	/**
	 * Indicate each word is curved or straight when drawing.
	 */
	public boolean isCurvedWord;

	/**
	 * Indicate each word is normal, uppercase or lowercase. Value is one of
	 * {Character.UNASSIGNED, Character.UPPERCASE_LETTER or Character.LOWERCASE_LETTER}.
	 */
	public int wordCase;

	/**
	 * Indicate each word is normal, bold or italic.
	 */
	public int wordStyle;

	/**
	 * Font size of each word.
	 */
	public int wordFontSize;

	/**
	 * Number of characters will be shown from left of each word when drawing.
	 */
	public int shownCharCount;

	public boolean isWordUpperCase() {
		return wordCase == UPPERCASE_LETTER;
	}

	public boolean isWordLowerCase() {
		return wordCase == UPPERCASE_LETTER;
	}

	public List<String> getWords() {
		return words;
	}

	public float getRotatedDegrees() {
		return rotatedDegrees;
	}

	public boolean isHorizontalWord() {
		return isHorizontalWord;
	}

	public int getShownCharCount() {
		return shownCharCount;
	}

	public boolean isVisible() {
		return isVisible;
	}
}
