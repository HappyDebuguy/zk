/* Z60_Touch_025Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 21 18:05:58 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import java.util.Collections;

import org.hamcrest.MatcherAssert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.chrome.ChromeOptions;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ForkJVMTestOnly;
import org.zkoss.zktest.zats.WebDriverTestCase;

@Category(ForkJVMTestOnly.class)
public class Z60_Touch_025Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPad"));
	}
	
	@Test
	public void test() {
		connect();
		waitResponse();
		
		int originScrollHeight = jq(".z-panelchildren").scrollHeight();
		MatcherAssert.assertThat(originScrollHeight, greaterThan(jq(".z-panelchildren").height()));
		
		click(jq("@button"));
		waitResponse();
		MatcherAssert.assertThat(originScrollHeight, lessThan(jq(".z-panelchildren").scrollHeight()));
	}
}
