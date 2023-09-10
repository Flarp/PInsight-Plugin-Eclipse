# pinsight Eclipse Plugin
A modified version of the `org.eclipse.tracecompass.lttng.ust` plugin for analyzing LTTng-UST traces that accepts `pinsight` traces by correctly extracting instruction pointer from `parallel_codeptr` field in event.

# To build
After loading the project from the GitHub repository into Eclipse as a Plug-in, create a new project from the *Plug-in development* menu called a *Feature project*. In this project, select the feature.xml file and in the *Included plug-ins* area, add the org.pinsight plug-in. Then, go into *Dependencies* and click Compute to bring in all the dependencies from the Pinsight plugin (mostly TraceCompass/LTTng helpers). Right-click on the feature project that was created and click Export, and then select *Deployable plug-in and fragments* and continue. Select the feature that you created and create an Archive and export. This will create a ZIP file you can distribute to others.

# To install
Go to *Help > Install new software* and in the *Available software* section, click Add. Click Archive and then find where you exported (or saved, if you are downloading it from another location) the archive, and click Okay. It had a few errors for me, but just clicking Continue made everything seem to work as planned with no issues.

# Additional information
I have attempted to add comments to the code to make it more understandable; if there are any further questions, feel free to let me know. There are probably better ways to implement a lot of the features in this library; I will chalk it up to a lack of experience in higher-level Java.
