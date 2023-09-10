package org.pinsight.omp.trace;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.lttng2.ust.core.trace.layout.LttngUst29EventLayout;

/**
 * 
 * @author Ethan Dorta
 * 
 * Gets the event layout of an OMP trace event done by pinsight.
 * The default instruction pointer is not updated properly when
 * pinsight is used for profiling; "parallel_codeptr" needs to be
 * used instead, so the default layout is overridden to redirect
 * to this context field.
 *
 */
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