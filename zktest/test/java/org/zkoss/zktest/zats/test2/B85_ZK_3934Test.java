/* B85_ZK_3934Test.java

	Purpose:
		
	Description:
		
	History:
		4:52 PM 2022/10/3, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B85_ZK_3934Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		click(jq("@button:eq(1)"));
		click(jq("@button:eq(2)"));

		waitResponse();
		String zkLog = getZKLog();
		assertTrue(zkLog.contains(".z-listcell : 150 / 150"));
		assertTrue(zkLog.contains(".z-cell : 150 / 150"));
		assertTrue(zkLog.contains(".z-treecell : 150 / 150"));
	}
}
