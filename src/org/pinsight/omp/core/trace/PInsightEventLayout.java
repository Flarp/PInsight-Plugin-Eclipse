package org.pinsight.omp.core.trace;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.LttngUst29EventLayout;

public class PInsightEventLayout extends LttngUst29EventLayout {
	private static @Nullable PInsightEventLayout INSTANCE = null;

    /**
     * Get a singleton instance.
     *
     * @return The instance
     */
    public static synchronized PInsightEventLayout getInstance() {
    	PInsightEventLayout instance = INSTANCE;
        if (instance == null) {
            instance = new PInsightEventLayout();
            INSTANCE = instance;
        }
        return instance;
    }
    
    @Override
    public String contextIp() {
    	return "parallel_codeptr";
    }
}