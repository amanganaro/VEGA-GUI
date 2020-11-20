package insilico.vega.gui.models;

import insilico.carcinogenicity_antares.ismCarcinogenicityAntares;
import insilico.daphnia_demetra.ismDaphniaDemetra;
import insilico.fathead_knn.ismFatheadKnn;
import insilico.core.exception.InitFailureException;
import insilico.core.model.InsilicoModel;
import insilico.core.model.iInsilicoModel;
import insilico.core.model.iInsilicoModelConsensus;

import java.util.ArrayList;
import insilico.logp_alogp.ismLogPALogP;
import insilico.logp_mlogp.ismLogPMLogP;
import insilico.meylanlogp.ismLogPMeylan;
import insilico.watersolubility.ismWaterSolubilityIRFMN;


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

//        ep = new VegaEndpoint("Mutagenicity (Ames test)", SECTION_HUMAN);
//        ep.AddModel(new ismMutagenicityCaesar());
//        ep.AddModel(new ismMutagenicitySarpy());
//        ep.AddModel(new ismMutagenicityBB());
//        ep.AddModel(new ismMutagenicityKnn());
//        Endpoints.add(ep);
        
        ep = new VegaEndpoint("Carcinogenicity", SECTION_HUMAN);
        ep.AddModel(new ismCarcinogenicityAntares());
        Endpoints.add(ep);
        
        ep = new VegaEndpoint("Fish acute toxcity", SECTION_ECOTOX);
        ep.AddModel(new ismFatheadKnn());
        ep.AddModel(new ismDaphniaDemetra());
        Endpoints.add(ep);
        
//        ep = new VegaEndpoint("Ready Biodegradability", SECTION_FATE);
//        ep.AddModel(new ismReadyBioIRFMN());
//        Endpoints.add(ep);
        
//        ep = new VegaEndpoint("Persistence (sediment)", SECTION_FATE);
//        ep.AddModel(new ismPersistenceSedimentIrfmn());
//        ep.AddModel(new ismPersistenceSedimentQuantitativeIrfmn());
//        Endpoints.add(ep);
        
        ep = new VegaEndpoint("Octanol/Water partition", SECTION_PHYS);
        ep.AddModel(new ismLogPMeylan());
        ep.AddModel(new ismLogPMLogP());
        ep.AddModel(new ismLogPALogP());
        Endpoints.add(ep);
        
        ep = new VegaEndpoint("Water solubility", SECTION_PHYS);
        ep.AddModel(new ismWaterSolubilityIRFMN());
        Endpoints.add(ep);
        
//        ep = new VegaEndpoint("Skin permeation", SECTION_HUMAN_PBPK);
//        ep.AddModel(new ismSkinPermeationPotts());
//        ep.AddModel(new ismSkinPermeationTenBerge());
//        Endpoints.add(ep);
        
//        ep = new VegaEndpoint("BCF in fish", SECTION_ECO_PBPK);
//        ep.AddModel(new ismBCFCaesar());
//        ep.AddModel(new ismBCFMeylan());
//        ep.AddModel(new ismBCFArnotGobas());
//        Endpoints.add(ep);
        
        
        
        
//        Tox = new ModelsGroup();
//        Tox.AddModel(new ismMutagenicityCaesar());
//        Tox.AddModel(new ismMutagenicitySarpy());
//        Tox.AddModel(new ismMutagenicityBB());
//        Tox.AddModel(new ismMutagenicityKnn());
//        Tox.AddModel(new ismCarcinogenicityCaesar());
//        Tox.AddModel(new ismCarcinogenicityBB());
//        Tox.AddModel(new ismCarcinogenicityAntares());
//        Tox.AddModel(new ismCarcinogenicityIsscanCgx());
//        Tox.AddModel(new ismCarcinogenicitySFOClassification());
//        Tox.AddModel(new ismCarcinogenicitySFORegression());
//        Tox.AddModel(new ismCarcinogenicitySFIClassification());
//        Tox.AddModel(new ismCarcinogenicitySFIRegression());
//        Tox.AddModel(new ismDevtoxCaesar());
//        Tox.AddModel(new ismDevToxPG());
//        Tox.AddModel(new ismZebrafishCoral());
//        Tox.AddModel(new ismChromosomalAberrationCoral());
//        Tox.AddModel(new ismRbaIRFMN());
//        Tox.AddModel(new ismEstrogenBindingCerapp());
//        Tox.AddModel(new ismAndrogenBindingComparaIRFMN());
//        Tox.AddModel(new ismTRAlphaNRMEA());
//        Tox.AddModel(new ismTRBetaNRMEA());
//        Tox.AddModel(new ismAromataseIRFMN());
//        Tox.AddModel(new ismPgpNic());
//        Tox.AddModel(new ismSkinCaesar());
//        Tox.AddModel(new ismSkinIRFMN());
//        Tox.AddModel(new ismHepatotoxicityIrfmn());
//        Tox.AddModel(new ismTissueBloodIneris());
//        Tox.AddModel(new ismTotalHLQsarins());        
//        Tox.AddModel(new ismMicronucleusInVitro()); 
//        Tox.AddModel(new ismMicronucleusInVivo()); 
//        Tox.AddModel(new ismNoaelCoral());
//        Tox.AddModel(new ismCramerToxtree());
//
//        ConsTox = new ModelsConsensusGroup();
//        ConsTox.AddModel(new ismcMutagenicity());
//        
//        EcoTox = new ModelsGroup();
//        EcoTox.AddModel(new ismMoaEpa());
//        EcoTox.AddModel(new ismMoaIrfmn());
//        EcoTox.AddModel(new ismVerhaarToxtree());
//        EcoTox.AddModel(new ismFishIRFMN());
//        EcoTox.AddModel(new ismFishKnn());
//        EcoTox.AddModel(new ismFishNic());
//        EcoTox.AddModel(new ismFishLC50());        
//        EcoTox.AddModel(new ismFishCombase());        
//        EcoTox.AddModel(new ismFishNOEC());        
//        EcoTox.AddModel(new ismFatheadEPA());
//        EcoTox.AddModel(new ismFatheadKnn());
//        EcoTox.AddModel(new ismGuppyKnn());
//        EcoTox.AddModel(new ismDaphniaEPA());
//        EcoTox.AddModel(new ismDaphniaDemetra());
//        EcoTox.AddModel(new ismDaphniaEC50());
//        EcoTox.AddModel(new ismDaphniaCombase());
//        EcoTox.AddModel(new ismDaphniaNOEC());
//        EcoTox.AddModel(new ismAlgaeEC50());
//        EcoTox.AddModel(new ismAlgaeCombaseEC50());
//        EcoTox.AddModel(new ismAlgaeNOEC());
//        EcoTox.AddModel(new ismAlgaeCombaseClass());
//        EcoTox.AddModel(new ismBeeKnn());
//        EcoTox.AddModel(new ismSludgeCombaseClass());
//        EcoTox.AddModel(new ismSludgeCombaseEC50());
//        // EcoTox.AddModel(new ismLoelIrfmn()); // not for now!
//        
//        ConsEcoTox = new ModelsConsensusGroup();
//        
//        Environ = new ModelsGroup();
//        Environ.AddModel(new ismBCFCaesar());
//        Environ.AddModel(new ismBCFMeylan());
//        Environ.AddModel(new ismBCFKnn());
//        Environ.AddModel(new ismBCFArnotGobas());
//        Environ.AddModel(new ismKmArnot());
//        Environ.AddModel(new ismReadyBioIRFMN());
//        Environ.AddModel(new ismPersistenceSedimentIrfmn());
//        Environ.AddModel(new ismPersistenceSedimentQuantitativeIrfmn());
//        Environ.AddModel(new ismPersistenceSoilIrfmn());
//        Environ.AddModel(new ismPersistenceSoilQuantitativeIrfmn());
//        Environ.AddModel(new ismPersistenceWaterIrfmn());
//        Environ.AddModel(new ismPersistenceWaterQuantitativeIrfmn());
//        Environ.AddModel(new ismPersistenceAirCoral());
//
//        ConsEnviron = new ModelsConsensusGroup();
//        
//        Phys = new ModelsGroup();
//        Phys.AddModel(new ismLogPMeylan());
//        Phys.AddModel(new ismLogPMLogP());
//        Phys.AddModel(new ismLogPALogP());
//        Phys.AddModel(new ismWaterSolubilityIRFMN());
//        Phys.AddModel(new ismHydrolysisCoral());
//        Phys.AddModel(new ismHenrysLawOpera());
//        Phys.AddModel(new ismKocOpera());
//        Phys.AddModel(new ismKoaOpera());
//        Phys.AddModel(new ismSkinPermeationPotts());
//        Phys.AddModel(new ismSkinPermeationTenBerge());
//        
//        ConsPhys = new ModelsConsensusGroup();
        
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
