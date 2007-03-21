/* Box.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 20 21:51:32     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;
import java.io.IOException;

import org.zkoss.lang.JVMs;
import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;

import org.zkoss.zul.impl.XulElement;

/**
 * A box.
 *
 * @author tomyeh
 */
public class Box extends XulElement {
	private String _spacing;
	private String _valign = "top";
	/** Array of width/height for each cell. */
	private String[] _sizes;

	/** Default: vertical ({@link Vbox}).
	 */
	public Box() {
		setMold("vertical");
	}
	/**
	 * @param orient either "horizontal" or "vertical".
	 */
	public Box(String orient) {
		setOrient(orient);
	}

	/** Returns the orient (the same as {@link #getMold}).
	 * <p>Default: "vertical".
	 */
	public String getOrient() {
		return getMold();
	}
	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be "+orient);

		setMold(orient);
	}
	/** Returns the spacing.
	 * <p>Default: null (depending on CSS).
	 */
	public String getSpacing() {
		return _spacing;
	}
	/** Sets the spacing.
	 * @param spacing the spacing (such as "0", "5px", "3pt" or "1em")
	 */
	public void setSpacing(String spacing) {
		if (spacing != null && spacing.length() == 0) spacing = null;
		if (!Objects.equals(_spacing, spacing)) {
			_spacing = spacing;
			invalidate();
		}
	}

	/** Returns the vertical alignment of the adjacent cells of a box.
	 * It is meaningful only if {@link #getOrient} is horizontal (i.e.,
	 * {@link Hbox}).
	 * <p>Default: top.
	 */
	public String getValign() {
		return _valign;
	}
	/** Sets the vertical alignment of the adjacent cells of a box.
	 * @param valign the vertical alignment: top, middle and bottom.
	 * If empty or null, the system default is used (usually middle).
	 */
	public void setValign(String valign) {
		if (!Objects.equals(_valign, valign)) {
			_valign = valign;
			smartUpdate("valign", _valign);
		}
	}

	/** Returns the widths/heights, which is a list of numbers separated by comma
	 * to denote the width/height of each cell in a box.
	 * If {@link Hbox} (i.e., {@link #getOrient} is horizontal),
	 * it is a list of widths.
	 * If {@link Vbox} (i.e., {@link #getOrient} is vertical),
	 * it is a list of heights.
	 *
	 * <p>It is the same as {@link #getHeights}.
	 *
	 * <p>Default: empty.
	 */
	public String getWidths() {
		return Utils.arrayToString(_sizes);
	}
	/** Returns the heights/widths, which is a list of numbers separated by comma
	 * to denote the height/width of each cell in a box.
	 * If {@link Hbox} (i.e., {@link #getOrient} is horizontal),
	 * it is a list of widths.
	 * If {@link Vbox} (i.e., {@link #getOrient} is vertical),
	 * it is a list of heights.
	 *
	 * <p>It is the same as {@link #getWidths}.
	 *
	 * <p>Default: empty.
	 */
	public String getHeights() {
		return getWidths();
	}
	/** Sets the widths/heights, which is a list of numbers separated
	 * by comma to denote the width/height of each cell in a box.
	 *
	 * <p>It is the same as {@link #setHeights}.
	 *
	 * <p>For example, "10%,20%,30%" means the second cell shall
	 * occupy 10% width, the second cell 20%, the third cell 30%,
	 * and the following cells don't specify any width.
	 *
	 * <p>Note: the splitters are ignored, i.e., they are not cells.
	 *
	 * <p>Another example, ",,30%" means the third cell shall occupy
	 * 30% width, and the rest of cells don't specify any width.
	 * Of course, the real widths depend on the interpretation of
	 * the browser.
	 */
	public void setWidths(String widths) throws WrongValueException {
		final String[] sizes = Utils.stringToArray(widths, null);
		if (!Objects.equals(sizes, _sizes)) {
			_sizes = sizes;
			invalidate();
		}
	}
	/** Sets the widths/heights, which is a list of numbers separated
	 * by comma to denote the width/height of each cell in a box.
	 *
	 * <p>It is the same as {@link #setWidths}.
	 */
	public void setHeights(String heights) throws WrongValueException {
		setWidths(heights);
	}

	/** Returns the attributes used to wrap the children (never null).
	 * Used only by component development to generate HTML tags.
	 */
	public String getChildAttrs(Component child) {
		final boolean vert = "vertical".equals(getOrient());
		if (child instanceof Splitter)
			return (vert ? " height": " width") + "=\"8px\"";

		final StringBuffer sb = new StringBuffer(64);
		HTMLs.appendAttribute(sb, "class", vert ? "vbox": "hbox");

		String size = null;
		if (_sizes != null) {
			int j = 0;
			for (Iterator it = getChildren().iterator(); it.hasNext();) {
				final Object o = it.next();
				if (child == o) {
					size = _sizes[j];
					break;
				} else if (!(o instanceof Splitter)) {
					if (++j >= _sizes.length)
						break; //not found
				}
			}
		}

		if (_spacing != null || size != null) {
			sb.append("style=\"");
			if (_spacing != null)
				sb.append("padding-")
					.append(vert ? "bottom": "right")
					.append(':').append(_spacing).append(';');
			if (size != null)
				sb.append(vert ? "height": "width")
					.append(':').append(size);
			sb.append('"');
		}
		return sb.toString();
	}

	//-- Component --//
	public void onDrawNewChild(Component child, StringBuffer out)
	throws IOException {
		final String chdattrs = getChildAttrs(child);

		if ("vertical".equals(getOrient())) {
			final StringBuffer sb = new StringBuffer(16)
				.append("<tr id=\"").append(child.getUuid())
				.append("!chdextr\"><td ").append(chdattrs).append('>');
			if (JVMs.isJava5()) out.insert(0, sb); //Bug 1682844
			else out.insert(0, sb.toString());
			out.append("</td></tr>");
		} else {
			final StringBuffer sb = new StringBuffer(32)
				.append("<td id=\"").append(child.getUuid())
				.append("!chdextr\"");
			if (child instanceof HtmlBasedComponent) {
				final String width = ((HtmlBasedComponent)child).getWidth();
				if (width != null)
					sb.append(" width=\"").append(width).append('"');
			}
			sb.append(chdattrs).append('>');
			if (JVMs.isJava5()) out.insert(0, sb); //Bug 1682844
			else out.insert(0, sb.toString());
			out.append("</td>");
		}
	}
}
