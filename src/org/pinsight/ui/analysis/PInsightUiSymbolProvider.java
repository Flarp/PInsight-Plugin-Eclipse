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

import org.eclipse.jdt.annotation.NonNull;
import org.pinsight.ui.analysis.PInsightUiSymbolProviderPreferencePage;
import org.pinsight.core.analysis.PInsightSymbolProvider;
import org.pinsight.core.trace.PInsightTrace;
import org.eclipse.tracecompass.lttng2.ust.core.analysis.debuginfo.UstDebugInfoAnalysisModule;
import org.eclipse.tracecompass.tmf.ui.symbols.ISymbolProvider;
import org.eclipse.tracecompass.tmf.ui.symbols.ISymbolProviderPreferencePage;

/**
 * Symbol provider for UST traces with debug information.
 *
 * @author Alexandre Montplaisir
 * @see UstDebugInfoAnalysisModule
 */
public class PInsightUiSymbolProvider extends PInsightSymbolProvider implements ISymbolProvider {

    /**
     * Constructor
     *
     * @param trace
     *            the corresponding trace
     */
    public PInsightUiSymbolProvider(PInsightTrace trace) {
        super(trace);
    }

    @Override
    public @NonNull ISymbolProviderPreferencePage createPreferencePage() {
        return new PInsightUiSymbolProviderPreferencePage(this);
    }

}
