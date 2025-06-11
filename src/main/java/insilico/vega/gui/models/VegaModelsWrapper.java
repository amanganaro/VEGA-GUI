package insilico.vega.gui.models;

import insilico.core.exception.GenericFailureException;
import insilico.core.exception.InitFailureException;
import insilico.core.model.*;
import insilico.core.model.runner.iInsilicoModelRunnerMessenger;
import insilico.core.tools.utils.GeneralUtilities;
import insilico.models.dispatcher.ModelDispatcher;
import insilico.models.exception.ModelNotFoundException;
import insilico.vega.gui.resources.VegaVersion;

import java.util.ArrayList;


/**
 * Class for gathering all available models together
 * 
 * @author amanganaro
 */
public class VegaModelsWrapper {
    
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

        ModelDispatcher MD = new ModelDispatcher();
        Endpoints = new ArrayList<>();

        for (ModelDispatcher.VegaEndpoint vegaEP : MD.GetOrganizedModels()) {
            VegaEndpoint ep = new VegaEndpoint(vegaEP.Name, vegaEP.Section);
            for (String modelTag : vegaEP.Models) {
                try {
                    InsilicoModel m = ModelDispatcher.GetModelFromTag(modelTag);
                    ep.AddModel(m);
                } catch (Exception e) {
                    throw new InitFailureException(e);
                }
            }
            for (String consTag : vegaEP.ModelsConsensus) {
                try {
                    InsilicoModelConsensus m = ModelDispatcher.GetConsensusFromTag(consTag);
                    ep.AddModelConsensus(m);
                } catch (Exception e) {
                    throw new InitFailureException(e);
                }
            }
            Endpoints.add(ep);
        }

        if (VegaVersion.PRINT_MODEL_LIST_TO_STDOUT) {
            System.out.println("------------------");
            System.out.println("No.\tSection\tEndpoint\tModel\tLabel\tType");
            int n = 1;
            for (VegaEndpoint e : Endpoints) {
                for (VegaModel m : e.Models)
                    System.out.println(n++ + "\t" + ModelDispatcher.SECTION_NAMES[e.Section] + "\t" + e.Name + "\t" + m.Model.getInfo().getName() +
                            "\t" + m.Model.getInfo().getKey() + "\t" + "Model");
                for (VegaModelConsensus m : e.ModelsConsensus)
                    System.out.println(n++ + "\t" + ModelDispatcher.SECTION_NAMES[e.Section] + "\t" + e.Name + "\t" + m.Model.getInfo().getName() +
                            "\t" + m.Model.getInfo().getKey() + "\t" + "Model");
            }
            System.out.println("------------------");
        }

    }

    public VegaModelsWrapper(iInsilicoModelRunnerMessenger messenger) throws InitFailureException {

        ModelDispatcher MD = new ModelDispatcher();
        Endpoints = new ArrayList<>();

        for (ModelDispatcher.VegaEndpoint vegaEP : MD.GetOrganizedModels()) {
            VegaEndpoint ep = new VegaEndpoint(vegaEP.Name, vegaEP.Section);
            for (String modelTag : vegaEP.Models) {
                try {
                    InsilicoModel m = ModelDispatcher.GetModelFromTag(modelTag, messenger, true);
                    if(VegaVersion.UNINSTALL_VEGA){
                        if(InsilicoModelPython.class.isAssignableFrom(m.getClass())){
                            ((InsilicoModelPython) m).removeCondaEnv();
                        }
                    }else{
                        ep.AddModel(m);
                    }
                } catch (Exception e) {
                    throw new InitFailureException(e);
                }
            }
            for (String consTag : vegaEP.ModelsConsensus) {
                try {
                    InsilicoModelConsensus m = ModelDispatcher.GetConsensusFromTag(consTag);
                    ep.AddModelConsensus(m);
                } catch (Exception e) {
                    throw new InitFailureException(e);
                }
            }
            Endpoints.add(ep);
        }



        if (VegaVersion.PRINT_MODEL_LIST_TO_STDOUT) {
            System.out.println("------------------");
            System.out.println("No.\tSection\tEndpoint\tModel\tLabel\tType");
            int n = 1;
            for (VegaEndpoint e : Endpoints) {
                for (VegaModel m : e.Models)
                    System.out.println(n++ + "\t" + ModelDispatcher.SECTION_NAMES[e.Section] + "\t" + e.Name + "\t" + m.Model.getInfo().getName() +
                            "\t" + m.Model.getInfo().getKey() + "\t" + "Model");
                for (VegaModelConsensus m : e.ModelsConsensus)
                    System.out.println(n++ + "\t" + ModelDispatcher.SECTION_NAMES[e.Section] + "\t" + e.Name + "\t" + m.Model.getInfo().getName() +
                            "\t" + m.Model.getInfo().getKey() + "\t" + "Model");
            }
            System.out.println("------------------");
        }

    }

    public ArrayList<InsilicoModel> GetAllInsilicoModels() {
        ArrayList<InsilicoModel> Res = new ArrayList<>();
        for (VegaEndpoint ep : Endpoints)
            for (VegaModel model : ep.Models)
                Res.add((InsilicoModel)model.Model);
        return Res;
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

    public void InitSingleModel(String modelTag, iInsilicoModelRunnerMessenger messenger) throws ModelNotFoundException, InitFailureException, GenericFailureException {
        InsilicoModel m = ModelDispatcher.GetModelFromTag(modelTag, messenger, false);
    }
}
