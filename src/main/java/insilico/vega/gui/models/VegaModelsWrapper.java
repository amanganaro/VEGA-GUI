package insilico.vega.gui.models;

import insilico.core.exception.GenericFailureException;
import insilico.core.exception.InitFailureException;
import insilico.core.exception.InitFailurePythonException;
import insilico.core.exception.PythonModelResourceNotFoundException;
import insilico.core.model.*;
import insilico.core.model.runner.iInsilicoModelRunnerMessenger;
import insilico.core.tools.utils.GeneralUtilities;
import insilico.models.dispatcher.ModelDispatcher;
import insilico.models.exception.ModelNotFoundException;
import insilico.vega.gui.resources.VegaVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


/**
 * Class for gathering all available models together
 * 
 * @author amanganaro
 */
public class VegaModelsWrapper {
    
    public ArrayList<VegaEndpoint> Endpoints;
    private static final Logger LOGGER = LogManager.getLogger();
   
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
                    if(InsilicoModelPython.class.isAssignableFrom(m.getClass()) && VegaVersion.UNINSTALL_VEGA) {
                        InsilicoModel toRemove = InitSingleModelWithoutEnv(m.getInfo().getKey(), null);
                        ((InsilicoModelPython) toRemove).removeCondaEnv();
                    } else {
                        ep.AddModel(m);
                    }
                }
                catch(PythonModelResourceNotFoundException | InitFailurePythonException ex){
                    LOGGER.error(ex.getMessage());
                    VegaVersion.USE_PYTHON_MODELS = false;
                    throw new InitFailureException(ex);
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage());
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

    public InsilicoModel InitSingleModel(String modelTag, iInsilicoModelRunnerMessenger messenger) throws ModelNotFoundException, InitFailureException, GenericFailureException {

        try {

            return ModelDispatcher.GetModelFromTag(modelTag, messenger, false);

        }catch(PythonModelResourceNotFoundException | InitFailurePythonException ex){
            LOGGER.error(ex.getMessage());
            VegaVersion.USE_PYTHON_MODELS = false;
            throw new InitFailureException(ex);
        }
    }

    public InsilicoModel InitSingleModelWithoutEnv(String modelTag, iInsilicoModelRunnerMessenger messenger) throws ModelNotFoundException, InitFailureException, GenericFailureException {
        try {
            InsilicoModel modelInited = ModelDispatcher.GetModelFromTag(modelTag, messenger, true);

            for (int i = 0; i < Endpoints.size(); i++) {
                VegaEndpoint ep = Endpoints.get(i);
                for (int j = 0; j < ep.Models.size(); j++) {
                    VegaModel model = ep.Models.get(j);
                    if (model.Model.getInfo().getKey().equals(modelTag)) {
                        model.Model = modelInited;
                    }
                }
            }
            return modelInited;
        }catch(PythonModelResourceNotFoundException | InitFailurePythonException ex){
            LOGGER.error(ex.getMessage());
            VegaVersion.USE_PYTHON_MODELS = false;
            throw new InitFailureException(ex);
        }
    }
}
