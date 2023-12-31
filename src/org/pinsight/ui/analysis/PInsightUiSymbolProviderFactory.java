/*******************************************************************************
 * Copyright (c) 2016 EfficiOS Inc. and others
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License 2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.pinsight.ui.analysis;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.lttng2.ust.ui.analysis.debuginfo.UstDebugInfoUiSymbolProvider;
import org.eclipse.tracecompass.lttng2.ust.core.analysis.debuginfo.UstDebugInfoAnalysisModule;
import org.eclipse.tracecompass.lttng2.ust.core.trace.LttngUstTrace;
import org.eclipse.tracecompass.tmf.core.symbols.ISymbolProvider;
import org.eclipse.tracecompass.tmf.core.symbols.ISymbolProviderFactory;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceUtils;
import org.pinsight.core.trace.PInsightTrace;

/**
 * Factory to create {@link UstDebugInfoUiSymbolProvider}. Provided to TMF via
 * the extension point. Only works with LTTng-UST traces.
 *
 * @author Alexandre Montplaisir
 */
public class PInsightUiSymbolProviderFactory implements ISymbolProviderFactory {

    @Override
    public @Nullable ISymbolProvider createProvider(ITmfTrace trace) {
        /*
         * This applies only to UST traces that fulfill the DebugInfo analysis
         * requirements.
         */
        UstDebugInfoAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace,
                UstDebugInfoAnalysisModule.class, UstDebugInfoAnalysisModule.ID);

        if (module != null && trace instanceof PInsightTrace) {
            return new PInsightUiSymbolProvider((PInsightTrace) trace);
        }
        return null;
    }

}
