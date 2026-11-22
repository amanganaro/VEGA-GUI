# Changelog

Beta builds (`-b1`, `-b2`, ...) are handed to testers and listed under the version they lead to; the version gets
its date when the official build is distributed.

## 1.2.9 - not released yet

### 1.2.9-b5 - 22/11/2026
- About box: the website link points to www.vegahub.eu instead of the old www.vega-qsar.eu
- This changelog now describes the three changes of 1.2.9-b1 that can alter a prediction, and which models they
  affect

### 1.2.9-b4 - 22/11/2026
- About box: ProtoQSAR logo next to the Mario Negri one, linking to the ProtoQSAR Italia website; new line with a
  link to the source code on GitHub; the list of open source libraries is removed and the window is shorter

### 1.2.9-b3 - 22/11/2026
- Two explicit build variants: `generic` (default, no Python environment inside) and `win-x64`
  (`mvn -P win-x64 package`, ships the conda-pack environments for Windows x86_64). The profile `with-condapack` is
  replaced by `win-x64`. The jar name carries the variant (`Vega-GUI-<version>-generic.jar`,
  `Vega-GUI-<version>-win-x64.jar`), and so do the log at start-up and the About box.
- VegaModels-All 1.3.3: the Python models no longer bring the Python environment, so the generic build is about
  1.2 GB instead of about 3 GB (most of what is left is model data: the CDDD encoder and the DiliBayer model files). Python models in the generic build still need the environment resolver planned
  for the Core: until then, ship the win-x64 build.

### 1.2.9-b2 - 22/11/2026
- All local files (logs, conda, Python environments, model scripts, CDDD files, PaDEL service) live under a single
  folder, `~/vega-gui`, which can be moved with `-Dvega.home=<path>` or the `VEGA_HOME` environment variable. It is
  separate from the folders of the previous releases, so this build installs everything again the first time and can
  coexist with an older VEGA on the same machine. Uninstallation (`-uninstall`) removes this folder.
- VegaModels-All 1.3.2, with Core 1.3.25 and the CDDD models on VegaResources-Cddd 1.1.1: the trained CDDD model
  stays in the VEGA folder instead of the location of the cddd library, and the Mitochondrial Dysfunction model
  files, which could not be extracted on a fresh installation, are fixed

### 1.2.9-b1 - 21/11/2026
- Updated to VegaModels-All 1.3.1, which brings Core 1.3.24:
    - PaDEL descriptors are computed by a separate local service, started on demand by the models that need it
    - the CDDD model files come from VegaResources-Cddd, with the corrected `app-cddd.py`
- **Predictions can change** in this build, for three independent reasons, each described below: the BCF Meylan
  model no longer alters the molecule it is given, the Cramer and Verhaar schemes are computed by VEGA itself, and
  the CDDD descriptors are now reproducible

#### Predictions that can change: BCF Meylan altered the molecule

When a model looks for a structural fragment, the chemistry library it uses re-reads the molecule first and rewrites
how its rings and aromatic bonds are described. BCF Meylan let it do so on the molecule itself, and all the models
of a run share one molecule: every model executed after BCF Meylan therefore saw a structure described differently
from the one VEGA had prepared, and could return a different result. Run one at a time, those models gave the
correct value; run together, as the application does, they did not.

BCF Meylan (and its copies in BCF Arnot-Gobas) now searches for its fragments without letting the molecule be
rewritten, which is what every other model in VEGA already did.

Affected molecules are those with heteroaromatic or sulphur-containing rings; everything else is unchanged. On a
test of 50 molecules, 99.6% of the values are identical to the previous version, and the models that changed now
return the values they return when run in isolation. The models that can be affected are the LogP models and
everything that is computed from them:

- LogP (Meylan-Kowwin), LogP (ALogP), LogP (MLogP)
- BCF (Arnot-Gobas), kM/Half-Life (Arnot-EpiSuite)
- Algae EC50 and NOEC (IRFMN), Daphnia Magna EC50 and NOEC (IRFMN), LC50 (DEMETRA) and LC50 (EPA)
- Fish LC50 (IRFMN), LC50 (NIC), NOEC (IRFMN), Fathead Minnow LC50 (EPA), MOA fish toxicity (EPA T.E.S.T.)
- Water solubility (IRFMN), Vapour Pressure and Melting Point (CONCERT/Kode), Melting Point (CONCERT/KNN)
- Persistence in soil and in water, quantitative models (IRFMN)
- Skin permeation (Potts and Guy) and (Ten Berge), Plasma Protein Binding LogK (IRFMN)
- Aromatase activity (IRFMN) and (TOX21), P-Glycoprotein activity (NIC), Hepatic Steatosis MIE assays for NRF2,
  PPARg up and PXR up (TOXCAST)

A molecule found in a model's training set can now be recognised again, so a few predictions are replaced by the
experimental value of that molecule.

#### Predictions that can change: Cramer and Verhaar

The Cramer and Verhaar classifications were computed through the external ToxTree library, which was built for a
chemistry toolkit much older than the one VEGA uses. The decision rules of both schemes have been rewritten inside
VEGA, so that they now read the molecule exactly as the rest of the application does. The rules themselves are
unchanged, including the known quirks of the original, which were kept on purpose so that results stay comparable.

Both schemes were checked question by question against the original on thousands of molecules. The classification
is the same for 99.9% of them. What changes are molecules the old library could not read at all, which were
reported as not classified and now get their class, plus a few whose class moves by one step. In the test of 50
molecules, one carotenoid moved from Cramer class II (Intermediate) to class I (Low).

#### Predictions that can change: the models based on CDDD descriptors

The CDDD descriptors, used by the DILI (Bayer/ONTOX), Apical Cardiotoxicity, Mitochondrial Dysfunction,
Cardiotoxicity Multitask and ONTOX assay models, were not exactly reproducible: the same molecule could get
slightly different descriptors depending on how many molecules were computed with it, and the difference could
reach the predicted probabilities. The descriptor calculation is now stable, and always returns the same values for
the same molecule.

The predicted class does not change: in the test of 50 molecules the differences are in the second or third decimal
of the probability, and every prediction keeps the same Active / Not Active assessment.
- The Core version is fixed by the GUI, and the build fails if a dependency is silently downgraded
- Removed the unused JavaFX dependency
- Compiled for Java 17, the version the Core requires
- The version and build date shown in the window title and in the About box come from the build

## 1.2.8 - date not recorded

## 1.2.7 - date not recorded
- Fixed problem in CDDD script when there is only one invalid molecule (also valid for previous version with a clean installation)
- Use of python environment in Windows OS instead of conda environment, built with conda-pack
- For Windows OS put the packed environment inside the resource of the jar
- Changed the miniconda server where download in linux and macos to miniforge repo instead of anaconda repo.
- Fixed tag of ISS-CARC-GX model

## 1.2.6 - 29/01/2026
- Fixed the bug for paths containing spaces for conda
- Added more verbosity to the possible problems in model setup
- The conda environments now use only public free without license channels, no more conda default channel in conda env
- Enhanced the logs
- Added the possibility to run VEGA without installing conda
- New models released:
    - DIO1 (ED SCREEN)
    - TTR (ED SCREEN)
- Updated models released:
    - STEROIDOGENESIS (ED SCREEN)

## 1.2.5 - 21/10/2025
- Included a custom conda installation to run python models
- Introduced logs to monitor the problems
- CDDD from Bayer descriptors calculator added
- New models released:
    - DILI (BAYER)
    - APICAL CARDIO TOX (ALTERNATIVE)
    - MITOCHONDRIAL DYSFUNCTION (ALTERNATIVE)
    - CARDIO TOX MULTITASK (ALTERNATIVE)
    - ACE (ONTOX)
    - NMDA (ONTOX)
    - PXR (ONTOX)
    - ACHE (ONTOX)
    - AHR (ONTOX)
    - AT1R (ONTOX)
    - BMP (ONTOX)
    - BSEP (ONTOX)
    - COX1 (ONTOX)
    - CYP26 (ONTOX)
    - FGFR1 (ONTOX)
    - FGFR2 (ONTOX)
    - FGFR3 (ONTOX)
    - FGFR4 (ONTOX)
    - GR (ONTOX)
    - HDEAC (ONTOX)
    - OAT1 (ONTOX)
    - PPARA (ONTOX)
    - PPARD (ONTOX)
    - PPARG (ONTOX)
    - THRA (ONTOX)
    - THRB (ONTOX)
    - TTR (ONTOX)
    - VGSC (ONTOX)
    - WNT (ONTOX)
    - STEROIDOGENESIS (OBERON)
