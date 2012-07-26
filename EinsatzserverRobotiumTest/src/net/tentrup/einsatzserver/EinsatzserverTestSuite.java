package net.tentrup.einsatzserver;

import android.test.suitebuilder.TestSuiteBuilder;
import junit.framework.Test;
import junit.framework.TestSuite;

public class EinsatzserverTestSuite extends TestSuite {
	public static Test suite() {
		return new TestSuiteBuilder(EinsatzserverTestSuite.class).includeAllPackagesUnderHere().build();
	}
}
