package org.pinsight.omp.trace;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.LttngUst29EventLayout;

public class PInsightOmpEventLayout extends LttngUst29EventLayout {
	private static @Nullable PInsightOmpEventLayout INSTANCE = null;

    /**
     * Get a singleton instance.
     *
     * @return The instance
     */
    public static synchronized PInsightOmpEventLayout getInstance() {
    	PInsightOmpEventLayout instance = INSTANCE;
        if (instance == null) {
            instance = new PInsightOmpEventLayout();
            INSTANCE = instance;
        }
        return instance;
    }
    

    @Override
    public String contextIp() {
    	return "parallel_codeptr";
    }
    
}