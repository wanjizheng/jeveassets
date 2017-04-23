/*
 * Copyright 2009-2017 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.eve.jeveasset.io.local;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.w3c.dom.Node;


public final class AttributeGetters {

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

	private AttributeGetters() { }

	public static boolean haveAttribute(final Node node, final String attributeName) {
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		if (attributeNode == null) {
			return false;
		}
		return true;
	}

	public static String getString(final Node node, final String attributeName) {
		return getNodeValue(node, attributeName);
	}

	public static Date getDate(final Node node, final String attributeName) {
		String value = getNodeValue(node, attributeName);
		try {
			return FORMAT.parse(value);
		} catch (ParseException ex) {
			return new Date(Long.parseLong(value));
		}
	}

	public static int getInt(final Node node, final String attributeName) {
		String value = getNodeValue(node, attributeName);
		return Integer.parseInt(value);
	}

	public static long getLong(final Node node, final String attributeName) {
		String value = getNodeValue(node, attributeName);
		return safeStringToLong(value); //Long.parseLong(sTemp);
	}

	public static double getDouble(final Node node, final String attributeName) {
		String value = getNodeValue(node, attributeName);
		return Double.valueOf(value); //Long.parseLong(sTemp);
	}

	public static float getFloat(final Node node, final String attributeName) {
		String value = getNodeValue(node, attributeName);
		return Float.valueOf(value); //Long.parseLong(sTemp);
	}

	public static boolean getBoolean(final Node node, final String attributeName) {
		String value = getNodeValue(node, attributeName);
		return (value.equals("true") || value.equals("1"));
	}

	private static Long safeStringToLong(final String s) {
		int nE = s.indexOf("E");
		if (nE == -1) {
			nE = s.indexOf("e");
		}
		if (nE == -1) {
			return Long.parseLong(s);
		}
		String sFirstNumber = s.substring(0, nE);
		String sLastNumber = s.substring(nE + 2);
		double nFirstNumber = Double.parseDouble(sFirstNumber);
		double nLastNumber = Double.parseDouble(sLastNumber);

		long nOutput = 10;
		for (int a = 1; a < nLastNumber; a++) {
			nOutput = nOutput * 10;
		}
		nOutput = (long) Math.ceil(nFirstNumber * nOutput);
		return nOutput;
	}

	private static String getNodeValue(final Node node, final String attributeName) {
		Node attributeNode = node.getAttributes().getNamedItem(attributeName);
		if (attributeNode == null) {
			throw new RuntimeException("Failed to parse attribute from node: " + node.getNodeName() + " > " + attributeName);
		}
		return attributeNode.getNodeValue();
	}
}
