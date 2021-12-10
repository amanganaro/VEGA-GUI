package insilico.vega.gui.models;

import insilico.aromatase_activity.ismAromataseTox21;
import insilico.carcinogenicity_caesar.ismCarcinogenicityCaesar;
import insilico.carcinogenicity_isscancgx.ismCarcinogenicityIsscanCgx;
import insilico.carcinogenicity_rat_female.ismCarcinogenicityRatFemale;
import insilico.carcinogenicity_rat_male.ismCarcinogenicityRatMale;
import insilico.core.exception.InitFailureException;
import insilico.core.model.InsilicoModel;
import insilico.core.model.iInsilicoModel;
import insilico.core.model.iInsilicoModelConsensus;
import insilico.endocrine_disruptors_irfmn.ismEndocrineDisruptorsIRFMN;
import insilico.fathead_epa.ismFatheadEPA;
import insilico.fish_lc50.ismFishLC50;
import insilico.km_arnot.ismKmArnot;
import insilico.ld50.ismLD50;
import insilico.logk.ismLogK;
import insilico.logp_alogp.ismLogPALogP;
import insilico.meylanlogp.ismLogPMeylan;
import insilico.persistence_quantative_water_irfmn.ismPersistenceWaterQuantitativeIrfmn;
import insilico.persistence_sediment_quantitative_irfmn.ismPersistenceSedimentQuantitativeIrfmn;
import insilico.persistence_soil_quantitative_irfmn.ismPersistenceSoilQuantitativeIrfmn;
import insilico.ppara_up.ismPPARAUp;
import insilico.pparg_up.ismPPARGup;
import insilico.ppb_coral.ismPPBCoral;
import insilico.pxr_up.ismPxrUp;
import insilico.skin_caesar.ismSkinCaesar;
import insilico.skin_cosmetics.ismSkinCosmetics;
import insilico.skin_sensitization_toxtree.ismSkinSensitizationToxTree;

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

        ep = new VegaEndpoint("Carcinogenicity", SECTION_HUMAN);
        ep.AddModel(new ismCarcinogenicityCaesar());
        ep.AddModel(new ismCarcinogenicityIsscanCgx());
        ep.AddModel(new ismCarcinogenicityRatMale());
        ep.AddModel(new ismCarcinogenicityRatFemale());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Acute Toxicity (LD50)", SECTION_HUMAN);
        ep.AddModel(new ismLD50());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Skin Sensitization", SECTION_HUMAN);
        ep.AddModel(new ismSkinCaesar());
        ep.AddModel(new ismSkinCosmetics());
        ep.AddModel(new ismSkinSensitizationToxTree());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Endocrine Disruptor activity", SECTION_HUMAN);
        ep.AddModel(new ismEndocrineDisruptorsIRFMN());
        Endpoints.add(ep);


        // Ecotox

        ep = new VegaEndpoint("Aquatic Acute Toxicity", SECTION_ECOTOX);
        ep.AddModel(new ismFishLC50());
        ep.AddModel(new ismFatheadEPA());
        Endpoints.add(ep);


        // Fate and Distribution

        ep = new VegaEndpoint("Persistence (sediment)", SECTION_FATE);
        ep.AddModel(new ismPersistenceSedimentQuantitativeIrfmn());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Persistence (soil)", SECTION_FATE);
        ep.AddModel(new ismPersistenceSoilQuantitativeIrfmn());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Persistence (water)", SECTION_FATE);
        ep.AddModel(new ismPersistenceWaterQuantitativeIrfmn());
        Endpoints.add(ep);


        // Physical Chemical properties

        ep = new VegaEndpoint("Octanol/Water partition coefficient", SECTION_PHYS);
        ep.AddModel(new ismLogPMeylan());
        ep.AddModel(new ismLogPALogP());
        Endpoints.add(ep);


        // Human PBPK

        ep = new VegaEndpoint("Plasma Protein Binding", SECTION_HUMAN_PBPK);
        ep.AddModel(new ismLogK());
        ep.AddModel(new ismPPBCoral());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Aromatase activity", SECTION_HUMAN_PBPK);
        ep.AddModel(new ismAromataseTox21());
        Endpoints.add(ep);

        ep = new VegaEndpoint("Hepatic Steatosis MIE", SECTION_HUMAN_PBPK);
        ep.AddModel(new ismPxrUp());
        ep.AddModel(new ismPPARGup());
        ep.AddModel(new ismPPARAUp());
//        ep.AddModel(new ismNRF2Up());
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
