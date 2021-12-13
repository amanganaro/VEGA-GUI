package insilico.vega.gui.models;

import insilico.algae_combaseEC50.ismAlgaeCombaseEC50;
import insilico.algae_combaseclass.ismAlgaeCombaseClass;
import insilico.algae_ec50.ismAlgaeEC50;
import insilico.algae_noec.ismAlgaeNOEC;
import insilico.aromatase_activity.ismAromataseTox21;
import insilico.aromatase_irfmn.ismAromataseIRFMN;
import insilico.bcf_arnotgobas.ismBCFArnotGobas;
import insilico.bcf_caesar.ismBCFCaesar;
import insilico.bcf_knn.ismBCFKnn;
import insilico.bcf_meylan.ismBCFMeylan;
import insilico.bee_knn.ismBeeKnn;
import insilico.carcinogenicity_antares.ismCarcinogenicityAntares;
import insilico.carcinogenicity_bb.ismCarcinogenicityBB;
import insilico.carcinogenicity_caesar.ismCarcinogenicityCaesar;
import insilico.carcinogenicity_isscancgx.ismCarcinogenicityIsscanCgx;
import insilico.carcinogenicity_rat_female.ismCarcinogenicityRatFemale;
import insilico.carcinogenicity_rat_male.ismCarcinogenicityRatMale;
import insilico.carcinogenicity_sfi_classification.ismCarcinogenicitySFIClassification;
import insilico.carcinogenicity_sfiregression.ismCarcinogenicitySFIRegression;
import insilico.carcinogenicity_sfoclassification.ismCarcinogenicitySFOClassification;
import insilico.carcinogenicity_sforegression.ismCarcinogenicitySFORegression;
import insilico.chromosomal_coral.ismChromosomalAberrationCoral;
import insilico.core.exception.InitFailureException;
import insilico.core.model.InsilicoModel;
import insilico.core.model.iInsilicoModel;
import insilico.core.model.iInsilicoModelConsensus;
import insilico.cramer_toxtree.ismCramerToxtree;
import insilico.daphnia_combase.ismDaphniaCombase;
import insilico.daphnia_demetra.ismDaphniaDemetra;
import insilico.daphnia_ec50.ismDaphniaEC50;
import insilico.daphnia_epa.ismDaphniaEPA;
import insilico.daphnia_noec.ismDaphniaNOEC;
import insilico.devtox_caesar.ismDevtoxCaesar;
import insilico.devtox_pg.ismDevToxPG;
import insilico.endocrine_disruptors_irfmn.ismEndocrineDisruptorsIRFMN;
import insilico.fathead_epa.ismFatheadEPA;
import insilico.fathead_knn.ismFatheadKnn;
import insilico.fish_combase.ismFishCombase;
import insilico.fish_irfmn.ismFishIRFMN;
import insilico.fish_knn.ismFishKnn;
import insilico.fish_lc50.ismFishLC50;
import insilico.fish_nic.ismFishNic;
import insilico.fish_noec.ismFishNOEC;
import insilico.guppy_knn.ismGuppyKnn;
import insilico.henryslaw.ismHenrysLawOpera;
import insilico.hepatoxicty_irfmn.ismHepatotoxicityIrfmn;
import insilico.hydrolysis_coral.ismHydrolysisCoral;
import insilico.km_arnot.ismKmArnot;
import insilico.koa_opera.ismKoaOpera;
import insilico.koc_opera.ismKocOpera;
import insilico.ld50.ismLD50;
import insilico.loael_coral_liver.ismLoaelCoralLiver;
import insilico.logk.ismLogK;
import insilico.logp_alogp.ismLogPALogP;
import insilico.logp_mlogp.ismLogPMLogP;
import insilico.meylanlogp.ismLogPMeylan;
import insilico.micronculeus_vitro.ismMicronucleusInVitro;
import insilico.micronuclueus_vivo.ismMicronucleusInVivo;
import insilico.moa_epa.ismMoaEpa;
import insilico.moa_irfmn.ismMoaIrfmn;
import insilico.mutagenicity_bb.ismMutagenicityBB;
import insilico.mutagenicity_caesar.ismMutagenicityCaesar;
import insilico.mutagenicity_consensus.ismcMutagenicity;
import insilico.mutagenicity_knn.ismMutagenicityKnn;
import insilico.mutagenicity_sarpy.ismMutagenicitySarpy;
import insilico.noael_coral_liver.ismNoaelCoralLiver;
import insilico.noel_coral.ismNoaelCoral;
import insilico.nrf2_up.ismNRF2Up;
import insilico.persistence_air_coral.ismPersistenceAirCoral;
import insilico.persistence_quantative_water_irfmn.ismPersistenceWaterQuantitativeIrfmn;
import insilico.persistence_sediment_irfmn.ismPersistenceSedimentIrfmn;
import insilico.persistence_sediment_quantitative_irfmn.ismPersistenceSedimentQuantitativeIrfmn;
import insilico.persistence_soil_irfmn.ismPersistenceSoilIrfmn;
import insilico.persistence_soil_quantitative_irfmn.ismPersistenceSoilQuantitativeIrfmn;
import insilico.persistence_water_irfmn.ismPersistenceWaterIrfmn;
import insilico.pgp_nic.ismPgpNic;
import insilico.ppara_up.ismPPARAUp;
import insilico.pparg_up.ismPPARGup;
import insilico.ppb_coral.ismPPBCoral;
import insilico.pxr_up.ismPxrUp;
import insilico.rba_cerapp.ismEstrogenBindingCerapp;
import insilico.rba_compara_irfmn.ismAndrogenBindingComparaIRFMN;
import insilico.rba_irfmn.ismRbaIRFMN;
import insilico.readybio_irfmn.ismReadyBioIRFMN;
import insilico.skin_caesar.ismSkinCaesar;
import insilico.skin_cosmetics.ismSkinCosmetics;
import insilico.skin_irfmn.ismSkinIRFMN;
import insilico.skin_permeation_potts.ismSkinPermeationPotts;
import insilico.skin_permeation_tenberge.ismSkinPermeationTenBerge;
import insilico.skin_sensitization_toxtree.ismSkinSensitizationToxTree;
import insilico.sludge_combaseEC50.ismSludgeCombaseEC50;
import insilico.sludge_combaseclass.ismSludgeCombaseClass;
import insilico.thyroid_tralpha_nrmea.ismTRAlphaNRMEA;
import insilico.thyroid_trbeta_nrmea.ismTRBetaNRMEA;
import insilico.tissueblood_ineris.ismTissueBloodIneris;
import insilico.totalhl_qsarins.ismTotalHLQsarins;
import insilico.verhaar_toxtree.ismVerhaarToxtree;
import insilico.watersolubility.ismWaterSolubilityIRFMN;
import insilico.zebrafish_coral.ismZebrafishCoral;

import java.util.ArrayList;


/**
 * Class for gathering all available models together
 * 
 * @author amanganaro
 */
public class VegaModelsWrapper {

    // Available sections for models
    public final static int SECTION_UNDEFINED = 0;
    public final static int SECTION_HUMAN = 1;
    public final static int SECTION_ECOTOX = 2;
    public final static int SECTION_FATE = 3;
    public final static int SECTION_PHYS = 4;
    public final static int SECTION_HUMAN_PBPK = 5;
    public final static int SECTION_ECO_PBPK = 6;

    public final static String[] SECTION_NAMES ={
        "N.A.",
        "Human Toxicity",
        "EcoToxicity",
        "Fate & Distribution",
        "Physical-Chemical properties",
        "Human PBPK",
        "Ecological PBPK",
    };
    
    public ArrayList<VegaEndpoint> Endpoints;
    
   
    public class VegaModel {
        public iInsilicoModel Model;
        public boolean Selected;
        
        public VegaModel(iInsilicoModel model) {
            Model = model;
            Selected = false;
        }
    }
    
    public class VegaModelConsensus {
        public iInsilicoModelConsensus Model;
        public boolean Selected;
        
        public VegaModelConsensus(iInsilicoModelConsensus model) {
            Model = model;
            Selected = false;
        }
    }

    public class VegaEndpoint {
        public String Name;
        public int Section;
        public ArrayList<VegaModel> Models;
        public ArrayList<VegaModelConsensus> ModelsConsensus;
        
        public VegaEndpoint(String name, int section) {
            Name = name;
            Section = section;
            Models = new ArrayList<>();
            ModelsConsensus = new ArrayList<>();
        }
        
        public void AddModel(iInsilicoModel model) {
            this.Models.add(new VegaModel(model));
        }

        public void AddModelConsensus(iInsilicoModelConsensus model) {
            this.ModelsConsensus.add(new VegaModelConsensus(model));
        }
    }

    
    public VegaModelsWrapper() throws InitFailureException {
        
        VegaEndpoint ep;
        Endpoints = new ArrayList<>();

        // Human

        ep = new VegaEndpoint("Mutagenicity (Ames test)", SECTION_HUMAN);
        ep.AddModel(new ismMutagenicityCaesar());
        ep.AddModel(new ismMutagenicityBB());
        ep.AddModel(new ismMutagenicitySarpy());
        ep.AddModel(new ismMutagenicityKnn());
        ep.AddModelConsensus(new ismcMutagenicity());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Developmental toxicity", SECTION_HUMAN);
        ep.AddModel(new ismDevtoxCaesar());
        ep.AddModel(new ismDevToxPG());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Carcinogenicity", SECTION_HUMAN);
        ep.AddModel(new ismCarcinogenicityCaesar());
        ep.AddModel(new ismCarcinogenicityBB());
        ep.AddModel(new ismCarcinogenicityIsscanCgx());
        ep.AddModel(new ismCarcinogenicityAntares());
        ep.AddModel(new ismCarcinogenicitySFOClassification());
        ep.AddModel(new ismCarcinogenicitySFORegression());
        ep.AddModel(new ismCarcinogenicitySFIClassification());
        ep.AddModel(new ismCarcinogenicitySFIRegression());
        ep.AddModel(new ismCarcinogenicityRatMale());
        ep.AddModel(new ismCarcinogenicityRatFemale());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Acute Toxicity (LD50)", SECTION_HUMAN);
        ep.AddModel(new ismLD50());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Skin Sensitization", SECTION_HUMAN);
        ep.AddModel(new ismSkinCaesar());
        ep.AddModel(new ismSkinIRFMN());
        ep.AddModel(new ismSkinCosmetics());
        ep.AddModel(new ismSkinSensitizationToxTree());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Chromosomal aberration", SECTION_HUMAN);
        ep.AddModel(new ismChromosomalAberrationCoral());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Micronucleus assay", SECTION_HUMAN);
        ep.AddModel(new ismMicronucleusInVitro());
        ep.AddModel(new ismMicronucleusInVivo());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Estrogen receptor effect", SECTION_HUMAN);
        ep.AddModel(new ismEstrogenBindingCerapp());
        ep.AddModel(new ismRbaIRFMN());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Androgen receptor effect", SECTION_HUMAN);
        ep.AddModel(new ismAndrogenBindingComparaIRFMN());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Thyroid receptor effect", SECTION_HUMAN);
        ep.AddModel(new ismTRAlphaNRMEA());
        ep.AddModel(new ismTRBetaNRMEA());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Endocrine Disruptor activity", SECTION_HUMAN);
        ep.AddModel(new ismEndocrineDisruptorsIRFMN());
        Endpoints.add(ep);

        ep = new VegaEndpoint("NOAEL", SECTION_HUMAN);
        ep.AddModel(new ismNoaelCoral());
        ep.AddModel(new ismNoaelCoralLiver());
        Endpoints.add(ep);

        ep = new VegaEndpoint("LOAEL", SECTION_HUMAN);
        ep.AddModel(new ismLoaelCoralLiver());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Cramer classification", SECTION_HUMAN);
        ep.AddModel(new ismCramerToxtree());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Hepatotoxicity", SECTION_HUMAN);
        ep.AddModel(new ismHepatotoxicityIrfmn());
        Endpoints.add(ep);


        // Ecotox

        ep = new VegaEndpoint("BCF", SECTION_ECOTOX);
        ep.AddModel(new ismBCFCaesar());
        ep.AddModel(new ismBCFMeylan());
        ep.AddModel(new ismBCFArnotGobas());
        ep.AddModel(new ismBCFKnn());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Aquatic Acute Toxicity", SECTION_ECOTOX);
        ep.AddModel(new ismFishLC50());
        ep.AddModel(new ismFishNic());
        ep.AddModel(new ismFishKnn());
        ep.AddModel(new ismFishIRFMN());
        ep.AddModel(new ismFishCombase());
        ep.AddModel(new ismFatheadEPA());
        ep.AddModel(new ismFatheadKnn());
        ep.AddModel(new ismDaphniaEC50());
        ep.AddModel(new ismDaphniaEPA());
        ep.AddModel(new ismDaphniaDemetra());
        ep.AddModel(new ismDaphniaCombase());
        ep.AddModel(new ismGuppyKnn());
        ep.AddModel(new ismAlgaeEC50());
        ep.AddModel(new ismAlgaeCombaseClass());
        ep.AddModel(new ismAlgaeCombaseEC50());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Aquatic Chronic Toxicity", SECTION_ECOTOX);
        ep.AddModel(new ismFishNOEC());
        ep.AddModel(new ismDaphniaNOEC());
        ep.AddModel(new ismAlgaeNOEC());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Mode of Action", SECTION_ECOTOX);
        ep.AddModel(new ismVerhaarToxtree());
        ep.AddModel(new ismMoaEpa());
        ep.AddModel(new ismMoaIrfmn());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Terrestrial Acute Toxicity", SECTION_ECOTOX);
        ep.AddModel(new ismBeeKnn());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Sludge Toxicity", SECTION_ECOTOX);
        ep.AddModel(new ismSludgeCombaseClass());
        ep.AddModel(new ismSludgeCombaseEC50());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Zebrafish embryo activity", SECTION_ECOTOX);
        ep.AddModel(new ismZebrafishCoral());
        Endpoints.add(ep);


        // Fate and Distribution

        ep = new VegaEndpoint("Ready biodegradability", SECTION_FATE);
        ep.AddModel(new ismReadyBioIRFMN());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Persistence (sediment)", SECTION_FATE);
        ep.AddModel(new ismPersistenceSedimentIrfmn());
        ep.AddModel(new ismPersistenceSedimentQuantitativeIrfmn());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Persistence (soil)", SECTION_FATE);
        ep.AddModel(new ismPersistenceSoilIrfmn());
        ep.AddModel(new ismPersistenceSoilQuantitativeIrfmn());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Persistence (water)", SECTION_FATE);
        ep.AddModel(new ismPersistenceWaterIrfmn());
        ep.AddModel(new ismPersistenceWaterQuantitativeIrfmn());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Persistence (air)", SECTION_FATE);
        ep.AddModel(new ismPersistenceAirCoral());
        Endpoints.add(ep);


        // Physical Chemical properties

        ep = new VegaEndpoint("Octanol/Water partition coefficient (logP)", SECTION_PHYS);
        ep.AddModel(new ismLogPMeylan());
        ep.AddModel(new ismLogPMLogP());
        ep.AddModel(new ismLogPALogP());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Water solubility", SECTION_PHYS);
        ep.AddModel(new ismWaterSolubilityIRFMN());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Hydrolysis", SECTION_PHYS);
        ep.AddModel(new ismHydrolysisCoral());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Henry's law constant", SECTION_PHYS);
        ep.AddModel(new ismHenrysLawOpera());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Octanol/air partition coefficient (KOA)", SECTION_PHYS);
        ep.AddModel(new ismKoaOpera());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Soil adsorption coefficient of organic compounds (KOC)", SECTION_PHYS);
        ep.AddModel(new ismKocOpera());
        Endpoints.add(ep);


        // Human PBPK

        ep = new VegaEndpoint("Plasma Protein Binding", SECTION_HUMAN_PBPK);
        ep.AddModel(new ismLogK());
        ep.AddModel(new ismPPBCoral());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Aromatase activity", SECTION_HUMAN_PBPK);
        ep.AddModel(new ismAromataseIRFMN());
        ep.AddModel(new ismAromataseTox21());
        Endpoints.add(ep);

        ep = new VegaEndpoint("P-Glycoprotein activity", SECTION_HUMAN_PBPK);
        ep.AddModel(new ismPgpNic());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Hepatic Steatosis MIE", SECTION_HUMAN_PBPK);
        ep.AddModel(new ismPxrUp());
        ep.AddModel(new ismPPARGup());
        ep.AddModel(new ismPPARAUp());
        ep.AddModel(new ismNRF2Up());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Skin permeation (LogKp)", SECTION_HUMAN_PBPK);
        ep.AddModel(new ismSkinPermeationPotts());
        ep.AddModel(new ismSkinPermeationTenBerge());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Adipose tissue-blood partition", SECTION_HUMAN_PBPK);
        ep.AddModel(new ismTissueBloodIneris());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Body elimination half-life", SECTION_HUMAN_PBPK);
        ep.AddModel(new ismTotalHLQsarins());
        Endpoints.add(ep);


        // Eco PBPK

        ep = new VegaEndpoint("kM/Half Life", SECTION_ECO_PBPK);
        ep.AddModel(new ismKmArnot());
        Endpoints.add(ep);

    }
    
    
    public ArrayList<InsilicoModel> GetSelectedModels() {
        ArrayList<InsilicoModel> Res = new ArrayList<>();
        for (VegaEndpoint ep : Endpoints) 
            for (VegaModel model : ep.Models)
                if (model.Selected)
                    Res.add((InsilicoModel)model.Model);
        return Res;
    }

    public ArrayList<iInsilicoModelConsensus> GetSelectedModelsCons() {
        ArrayList<iInsilicoModelConsensus> Res = new ArrayList<>();
        for (VegaEndpoint ep : Endpoints) 
            for (VegaModelConsensus model : ep.ModelsConsensus)
                if (model.Selected)
                    Res.add(model.Model);
        return Res;
    }
}
