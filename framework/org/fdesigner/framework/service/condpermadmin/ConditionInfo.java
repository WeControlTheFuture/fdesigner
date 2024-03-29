/*
 * Copyright (c) OSGi Alliance (2004, 2016). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fdesigner.framework.service.condpermadmin;

import java.util.ArrayList;
import java.util.List;

/**
 * Condition representation used by the Conditional Permission Admin service.
 * 
 * <p>
 * This class encapsulates two pieces of information: a Condition <i>type</i>
 * (class name), which must implement {@code Condition}, and the arguments
 * passed to its constructor.
 * 
 * <p>
 * In order for a Condition represented by a {@code ConditionInfo} to be
 * instantiated and considered during a permission check, its Condition class
 * must be available from the system classpath.
 * 
 * <p>
 * The Condition class must either:
 * <ul>
 * <li>Declare a public static {@code getCondition} method that takes a
 * {@code Bundle} object and a {@code ConditionInfo} object as arguments. That
 * method must return an object that implements the {@code Condition} interface.
 * </li>
 * <li>Implement the {@code Condition} interface and define a public constructor
 * that takes a {@code Bundle} object and a {@code ConditionInfo} object as
 * arguments.</li>
 * </ul>
 * 
 * @Immutable
 * @author $Id$
 */
public class ConditionInfo {
	private final String	type;
	private final String[]	args;

	/**
	 * Constructs a {@code ConditionInfo} from the specified type and args.
	 * 
	 * @param type The fully qualified class name of the Condition represented
	 *        by this {@code ConditionInfo}.
	 * @param args The arguments for the Condition. These arguments are
	 *        available to the newly created Condition by calling the
	 *        {@link #getArgs()} method.
	 * @throws NullPointerException If {@code type} is {@code null}.
	 */
	public ConditionInfo(String type, String[] args) {
		this.type = type;
		this.args = (args != null) ? args.clone() : new String[0];
		if (type == null) {
			throw new NullPointerException("type is null");
		}
	}

	/**
	 * Constructs a {@code ConditionInfo} object from the specified encoded
	 * {@code ConditionInfo} string. White space in the encoded
	 * {@code ConditionInfo} string is ignored.
	 * 
	 * @param encodedCondition The encoded {@code ConditionInfo}.
	 * @see #getEncoded()
	 * @throws IllegalArgumentException If the specified
	 *         {@code encodedCondition} is not properly formatted.
	 */
	public ConditionInfo(String encodedCondition) {
		if (encodedCondition == null) {
			throw new NullPointerException("missing encoded condition");
		}
		if (encodedCondition.length() == 0) {
			throw new IllegalArgumentException("empty encoded condition");
		}
		try {
			char[] encoded = encodedCondition.toCharArray();
			int length = encoded.length;
			int pos = 0;

			/* skip whitespace */
			while (Character.isWhitespace(encoded[pos])) {
				pos++;
			}

			/* the first character must be '[' */
			if (encoded[pos] != '[') {
				throw new IllegalArgumentException("expecting open bracket");
			}
			pos++;

			/* skip whitespace */
			while (Character.isWhitespace(encoded[pos])) {
				pos++;
			}

			/* type is not quoted or encoded */
			int begin = pos;
			while (!Character.isWhitespace(encoded[pos]) && (encoded[pos] != ']')) {
				pos++;
			}
			if (pos == begin || encoded[begin] == '"') {
				throw new IllegalArgumentException("expecting type");
			}
			this.type = new String(encoded, begin, pos - begin);

			/* skip whitespace */
			while (Character.isWhitespace(encoded[pos])) {
				pos++;
			}

			/* type may be followed by args which are quoted and encoded */
			List<String> argsList = new ArrayList<String>();
			while (encoded[pos] == '"') {
				pos++;
				begin = pos;
				while (encoded[pos] != '"') {
					if (encoded[pos] == '\\') {
						pos++;
					}
					pos++;
				}
				argsList.add(unescapeString(encoded, begin, pos));
				pos++;

				if (Character.isWhitespace(encoded[pos])) {
					/* skip whitespace */
					while (Character.isWhitespace(encoded[pos])) {
						pos++;
					}
				}
			}
			this.args = argsList.toArray(new String[0]);

			/* the final character must be ']' */
			char c = encoded[pos];
			pos++;
			while ((pos < length) && Character.isWhitespace(encoded[pos])) {
				pos++;
			}
			if ((c != ']') || (pos != length)) {
				throw new IllegalArgumentException("expecting close bracket");
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("parsing terminated abruptly");
		}
	}

	/**
	 * Returns the string encoding of this {@code ConditionInfo} in a form
	 * suitable for restoring this {@code ConditionInfo}.
	 * 
	 * <p>
	 * The encoded format is:
	 * 
	 * <pre>
	 *   [type &quot;arg0&quot; &quot;arg1&quot; ...]
	 * </pre>
	 * 
	 * where <i>argN</i> are strings that must be encoded for proper parsing.
	 * Specifically, the {@code "}, {@code \}, carriage return, and line feed
	 * characters must be escaped using {@code \"}, {@code \\}, {@code \r}, and
	 * {@code \n}, respectively.
	 * 
	 * <p>
	 * The encoded string contains no leading or trailing whitespace characters.
	 * A single space character is used between type and &quot;<i>arg0</i>&quot;
	 * and between the arguments.
	 * 
	 * @return The string encoding of this {@code ConditionInfo}.
	 */
	public final String getEncoded() {
		StringBuilder output = new StringBuilder();
		output.append('[');
		output.append(type);

		for (int i = 0; i < args.length; i++) {
			output.append(" \"");
			escapeString(args[i], output);
			output.append('\"');
		}

		output.append(']');

		return output.toString();
	}

	/**
	 * Returns the string representation of this {@code ConditionInfo}. The
	 * string is created by calling the {@code getEncoded} method on this
	 * {@code ConditionInfo}.
	 * 
	 * @return The string representation of this {@code ConditionInfo}.
	 */
	@Override
	public String toString() {
		return getEncoded();
	}

	/**
	 * Returns the fully qualified class name of the condition represented by
	 * this {@code ConditionInfo}.
	 * 
	 * @return The fully qualified class name of the condition represented by
	 *         this {@code ConditionInfo}.
	 */
	public final String getType() {
		return type;
	}

	/**
	 * Returns arguments of this {@code ConditionInfo}.
	 * 
	 * @return The arguments of this {@code ConditionInfo}. An empty array is
	 *         returned if the {@code ConditionInfo} has no arguments.
	 */
	public final String[] getArgs() {
		return args.clone();
	}

	/**
	 * Determines the equality of two {@code ConditionInfo} objects.
	 * 
	 * This method checks that specified object has the same type and args as
	 * this {@code ConditionInfo} object.
	 * 
	 * @param obj The object to test for equality with this
	 *        {@code ConditionInfo} object.
	 * @return {@code true} if {@code obj} is a {@code ConditionInfo}, and has
	 *         the same type and args as this {@code ConditionInfo} object;
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof ConditionInfo)) {
			return false;
		}

		ConditionInfo other = (ConditionInfo) obj;

		if (!type.equals(other.type) || args.length != other.args.length)
			return false;

		for (int i = 0; i < args.length; i++) {
			if (!args[i].equals(other.args[i]))
				return false;
		}
		return true;
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return A hash code value for this object.
	 */
	@Override
	public int hashCode() {
		int h = 31 * 17 + type.hashCode();
		for (int i = 0; i < args.length; i++) {
			h = 31 * h + args[i].hashCode();
		}
		return h;
	}

	/**
	 * This escapes the quotes, backslashes, \n, and \r in the string using a
	 * backslash and appends the newly escaped string to a StringBuilder.
	 */
	private static void escapeString(String str, StringBuilder output) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			switch (c) {
				case '"' :
				case '\\' :
					output.append('\\');
					output.append(c);
					break;
				case '\r' :
					output.append("\\r");
					break;
				case '\n' :
					output.append("\\n");
					break;
				default :
					output.append(c);
					break;
			}
		}
	}

	/**
	 * Takes an encoded character array and decodes it into a new String.
	 */
	private static String unescapeString(char[] str, int begin, int end) {
		StringBuilder output = new StringBuilder(end - begin);
		for (int i = begin; i < end; i++) {
			char c = str[i];
			if (c == '\\') {
				i++;
				if (i < end) {
					c = str[i];
					switch (c) {
						case '"' :
						case '\\' :
							break;
						case 'r' :
							c = '\r';
							break;
						case 'n' :
							c = '\n';
							break;
						default :
							c = '\\';
							i--;
							break;
					}
				}
			}
			output.append(c);
		}

		return output.toString();
	}
}
