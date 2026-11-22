# Changelog

Beta builds (`-b1`, `-b2`, ...) are handed to testers and listed under the version they lead to; the version gets
its date when the official build is distributed.

## 1.2.9 - not released yet

### 1.2.9-b2 - 22/11/2026
- All local files (logs, conda, Python environments, model scripts, CDDD files, PaDEL service) live under a single
  folder, `~/vega-gui`, which can be moved with `-Dvega.home=<path>` or the `VEGA_HOME` environment variable. It is
  separate from the folders of the previous releases, so this build installs everything again the first time and can
  coexist with an older VEGA on the same machine. Uninstallation (`-uninstall`) removes this folder.
- Core 1.3.25 and VegaResources-Cddd 1.1.1: the trained CDDD model stays in the VEGA folder instead of the location
  of the cddd library

### 1.2.9-b1 - 21/11/2026
- Updated to VegaModels-All 1.3.1, which brings Core 1.3.24:
    - PaDEL descriptors are computed by a separate local service, started on demand by the models that need it
    - the CDDD model files come from VegaResources-Cddd, with the corrected `app-cddd.py`
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
