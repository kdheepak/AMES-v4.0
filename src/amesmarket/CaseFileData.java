/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * TODO: License
 */
package amesmarket;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import amesmarket.filereaders.IZoneIndexProvider;
import amesmarket.filereaders.IZoneIndexProvider.NamedIndexProvider;

/**
 * Configuration parameters.
 *
 * Parameters are public to make adapting the existing code quicker.
 *
 * Adapted from {@link AMESGUIFrame.AMESFrame}
 * @author Sean L. Mooney
 */
public class CaseFileData {

    /**
     * Where the LSE data comes from. Either comes from the load case,
     * or directly specified in the TestCase file.
     * One of {@link #LSE_DEMAND_TEST_CASE} or {@link #LSE_DEMAND_LOAD_CASE}.
     */
    private int lseDemandSource;

    public Object[][] nodeData;
    public Object[][] branchData;
    public GenData[] genData;
    public Object[][] lse3SecData;  // 3-sectional 8-hour LSE data
    public Object[][] lseData;      // Combine 3-sectional to 24-hour LSE data
    public Object[][] lseSec1Data;  // First 8-hour LSE data
    public Object[][] lseSec2Data;  // Second 8-hour LSE data
    public Object[][] lseSec3Data;  // Third 8-hour LSE data
    public Object[][][] lsePriceSensitiveDemand;
    public Object[][] lseHybridDemand;

    public int iNodeData;
    public int iBranchData;
    public int iGenData;
    public int iLSEData;
    public boolean bPriceSensitiveDemand;
    public boolean bHybridDemand;
    public boolean isPU;
    public double baseS;
    public double baseV;
    public double reserveRequirements;

    // Learning and action domain parameters
    public double Default_Cooling;
    public double Default_Experimentation;
    public double Default_InitPropensity;
    public int Default_M1;
    public int Default_M2;
    public int Default_M3;
    public double Default_RI_MAX_Lower;
    public double Default_RI_MAX_Upper;
    public double Default_RI_MIN_C;
    public double Default_Recency;
    public double Default_SlopeStart;
    public int Default_iRewardSelection;
    public double Cooling;
    public double Experimentation;
    public double InitPropensity;
    public int M1;
    public int M2;
    public int M3;
    public double RI_MAX_Lower;
    public double RI_MAX_Upper;
    public double RI_MIN_C;
    public double Recency;
    public double SlopeStart;
    public int iRewardSelection;
    public double[][] genLearningData;
    public boolean bGenLearningDataSet = false;

    public long RandomSeed;
    public int iMaxDay;
    public double dThresholdProbability;
    public double dDailyNetEarningThreshold;
    public double dGenPriceCap;
    public double dLSEPriceCap;
    public int iStartDay;
    public int iCheckDayLength;
    public double dActionProbability;
    public int iLearningCheckStartDay;
    public int iLearningCheckDayLength;
    public double dLearningCheckDifference;
    public int iDailyNetEarningStartDay;
    public int iDailyNetEarningDayLength;
    public boolean bMaximumDay = true;
    public boolean bThreshold = true;
    public boolean bDailyNetEarningThreshold = false;
    public boolean bActionProbabilityCheck = false;
    public boolean bLearningCheck = false;
    private boolean hasGenLearningData = false;

    /**
     * Name of the file that describes the load scenarios.
     * Must be specified by the TestCase file.
     */
    public String loadCaseControlFile;
    /**
     * The capacity reserve margin. Used to scale 'real' input data
     * to generation capacity of the grid.
     * Stored a decimal [-1, 1] not as  a percentage, [-100, 100].
     * See {@link DefaultLPCProvider#scaleLoadProfileCollections(double, double)}.
     * Defaults to 10, unless set in the TestCase file.
     */
    public double capacityMargin;
    private ArrayList<String> canaryGenCos;

    private final Map<String, SCUCInputData> scucData;
    

    /**
     * Store an association between zone names and the array index
     * used to model that zone in the bulk of the program.
     */
    private final NamedIndexProvider zoneIndexMap;

    private final Map<String, Double> noLoadCosts;
    private final Map<String, Double> coldStartUpCosts;
    private final Map<String, Double> hotStartUpCosts;
    private final Map<String, Double> shutDownCosts;

    private final Map<String, String> fuelType;

    /**
     * Type of SCUC (Deterministic or Stocastic).
     */
    private int scucType = SCUC.SCUC_DETERM;

    public CaseFileData() {
        RandomSeed = 695672061;
        iMaxDay = 50;
        dThresholdProbability = 0.999;
        dDailyNetEarningThreshold = 10.0;
        dGenPriceCap = 1000.0;
        dLSEPriceCap = 0.0;
        iStartDay = 1;
        iCheckDayLength = 5;
        dActionProbability = 0.001;
        iLearningCheckStartDay = 1;
        iLearningCheckDayLength = 5;
        dLearningCheckDifference = 0.001;
        iDailyNetEarningStartDay = 1;
        iDailyNetEarningDayLength = 5;
        reserveRequirements = 1000;

        lseDemandSource = LSE_DEMAND_TEST_CASE;
        loadCaseControlFile="DATA/ControlFile.dat";

        zoneIndexMap = new NamedIndexProvider();

        capacityMargin = .10;
        canaryGenCos = new ArrayList<String>();

        scucData = new HashMap<String, CaseFileData.SCUCInputData>();
        noLoadCosts = new HashMap<String, Double>();
        shutDownCosts = new HashMap<String, Double>();
        coldStartUpCosts = new HashMap<String, Double>();
        hotStartUpCosts = new HashMap<String, Double>();

        fuelType = new HashMap<String, String>();
}

    /**
     * @param should be one of the LSE_DEMAND_* constants.
     */
    public void setLSEDemandSource(int lseDemandSource) {
        this.lseDemandSource = lseDemandSource;
    }

    /**
     * @return one of {@link #LSE_DEMAND_TEST_CASE} or {@link #LSE_DEMAND_LOAD_CASE}
     */
    public int getLSEDemandSource() {
        return lseDemandSource;
    }

    public void setCanaryGenCo(ArrayList<String> alertGenCos) {
        this.canaryGenCos.addAll(alertGenCos);
    }
    
    /**
     * FIXME Valid values
     * @param type
     */
    public void setSCUCType(int type) {
        this.scucType = type;
    }
    
    public int getSCUCType() {
        return scucType;
    }

    public void markCanaryGenCos() {
        if(canaryGenCos.size() == 0) return;

        //quick-to-implement algorithm. Iterate over the entire
        //list until we find the genco with the same name.
        //If this turns into a bottle neck, sort the genco list
        //by name into a new list and search for the names.
        //Should probably leave the original list in order.
        //Not sure if there is an implied order between the
        //the list of genco's and how the rest of the network
        //gets connected up.
        for(String genName : canaryGenCos) {
            boolean foundCanary = false;
            for(int g = 0; g < genData.length && !foundCanary; g++) {
                if(genData[g].name.equals(genName)) {
                    genData[g].isCanary = true;
                    foundCanary = true;
                }
            }
            if (!foundCanary) {
                System.err.println(String.format(
                        "[WARNING] AlertGen %s Not Found", genName));
            }
        }
    }

    /**
     * Check to see if any of the gencos is missing SCUC data. If so,
     * generate a default set of parameters.
     * @param testConf
     */
    public void ensureSCUCData() {
        if(genData == null) return;
        for(GenData gd : genData) {
            if(scucData.get(gd.name) == null) {
                scucData.put(gd.name, new SCUCInputData(gd.capU, 1, 0, 0, 0, 0, 0, 0, 1, 1));
            }
        }
    }

    /**
     * Check to see if the hybrid demand sources match the DemandSourceFlag. If
     * the demand source is set to {@link #LSE_DEMAND_LOAD_CASE}, force all the
     * flags to 1.
     *
     * From design notes: The value of the LSEDataSource flag will also
     * determine what type of data is expected is the TestCase file. If the
     * value is TestCase, then the test case file will need to include
     * everything that was in an AMES-2.05 TestCase. If the value is LoadCase,
     * LSEDataFixedDemand and LSE- DataPriceSensitiveDemand and
     * LSEDataHybridDemand sections may be omitted. Even if these sections are
     * included, AMES will ignore the data specified in favor of the LoadCase.
     * Finally, the demand type flags, (LSEDataHybridDemand). In case 1 the
     * flags come from the TestCase file and in case 2 all of the flags will be
     * programmatically set to 1, ensuring DCOPFJ, if used, behaves correctly.
     */
    public void checkLSEHybridDemandSources() {
        if(getLSEDemandSource() == LSE_DEMAND_LOAD_CASE) {
            //make sure the flags are all one
            for(int i = 0; i<lseHybridDemand.length; i++) {
                //Start at one. Index 0 is the LSE name.
                for(int k = 3; k < lseHybridDemand[i].length; k++) {
                    int flag = (Integer)lseHybridDemand[i][k];
                    int exFlag = 1;

                    if(flag != exFlag) {
                        System.err.println(
                                "Warning: LSEDemandSource is LoadCase. " +
                                "Expected " + lseHybridDemand[i][0] + " hour " + (k + 1) +
                                " demand flag to be " + exFlag + ". Found " + flag + "."
                                );
                        System.err.println("Setting flag to " + exFlag);
                        lseHybridDemand[i][k] = 1;
                    }
                }
            }
        }
    }

    /**
     * Helper method to prevent NPEs where the TestCase uses
     * a LoadCase for the demand source and there is no fixed
     * demand in the TestCase file.
     */
    public void ensureLSEData() {
        //don't modify the data if the LSE demand is supposed
        //to be in the TestCase.
        if (lseDemandSource == LSE_DEMAND_TEST_CASE || lseData != null) {
            return;
        }


        iLSEData = zoneIndexMap.getNumZones();
        lseSec1Data = new Object[iLSEData][11];
        lseSec2Data = new Object[iLSEData][11];
        lseSec3Data = new Object[iLSEData][11];
        lseData = new Object[iLSEData][27];

        for(int z = 0; z < lseData.length; z++) {
            lseData[z][0] = "LSE" + (z+1); //NAME
            lseData[z][1] = (z+1);         //ID
            lseData[z][2] = (z+1);         //At bus
            //hourly demand
            for(int h = 3; h < lseData[z].length; h++){
                lseData[z][h] = 0; //dummy no demand data
            }
        }

    }

    /**
     * Generate some dummy price sensitive demand date, if not in
     * the test case. Make to call after {@link #ensureLSEData()}, or
     * may crash.
     */
    public void ensureLSEPriceSenstiveDemandData() {
        //don't modify the data if the LSE demand is supposed
        //to be in the TestCase.
        if (lseDemandSource == LSE_DEMAND_TEST_CASE
                || lsePriceSensitiveDemand != null) {
            return;
        }

        final int numHours = 24;
        final int numCols = 7;

        iLSEData = zoneIndexMap.getNumZones();
        lsePriceSensitiveDemand = new Object[iLSEData][numHours][numCols];

        for(int l = 0; l < iLSEData; l++){
            for(int h = 0; h < 24; h++) {
                lsePriceSensitiveDemand[l][h][0] = lseData[l][0]; //NAME
                lsePriceSensitiveDemand[l][h][1] = lseData[l][1]; //ID
                lsePriceSensitiveDemand[l][h][2] = lseData[l][2]; //BUS
                lsePriceSensitiveDemand[l][h][3] = h;             //HOUR
                lsePriceSensitiveDemand[l][h][4] = 0;             //c
                lsePriceSensitiveDemand[l][h][5] = 0;             //d
                lsePriceSensitiveDemand[l][h][6] = 0;             //SLMax
            }
        }
    }

    ///////////////////////CONSTANTS////////////////////////////////////////////
    /**
     * Use the LSE demand data in the TestCase file.
     */
    public static final int LSE_DEMAND_TEST_CASE = 1;
    /**
     * Use the LoadCase as the source of LSE demand data.
     */
    public static final int LSE_DEMAND_LOAD_CASE = 2;
    ///////////////////////END CONSTANTS///////////////////////////////////////

    /**
     * Model the GenData section of the input file.
     * @author Sean L. Mooney
     *
     */
    public static class GenData {

        /**
         * @param name
         * @param id
         * @param atBus
         * @param sCost
         * @param a
         * @param b
         * @param capL
         * @param capU
         * @param initMoney
         */
        public GenData(String name, int id, int atBus, double sCost, double a,
                       double b, double capL, double capU, double initMoney) {
            this.name = name;
            this.id = id;
            this.atBus = atBus;
            this.sCost = sCost;
            this.a = a;
            this.b = b;
            this.capL = capL;
            this.capU = capU;
            this.initMoney = initMoney;
            //Assume all GenCos are 'standard' generators
            //until one is marked explicitly as a canary.
            this.isCanary = false;
        }

        /**
         * Creates a copy of the parameters to a double array.
         *
         * Utility method to make loading data into the AMESMarket easy.
         * @return
         */
        public Object[] asArray() {
            return new Object[] {
                       name, id, atBus, sCost, a, b, capL, capU, initMoney, isCanary
                   };
        }

        public final String name;
        public final int id;
        public final int atBus;
        public final double sCost;
        public final double a;
        public final double b;
        public final double capL;
        public final double capU;
        public final double initMoney;
        /**
         * Whether or not the generator is a 'canary' generator.
         * Like the canary in a coal mine, canary generators
         * indicate a problem with the grid. Canary generators provide
         * a means to ensure a load profile can be met, since AMES does
         * not yet handle reserves/reserve markets.
         */
        //not final because the list of 'canaries' get 'matched up'
        //with generators until after the entire file is read.
        public boolean isCanary;
    }

    /**
     * Adjusts the control file path as relative to the
     * TestCase file, if needed.
     * 
     * The path is adjusted if these conditions hold:
     * <ol>
     * <li>{@link #testCaseFile} is not null</li>
     * <li>The path for the control file is not null</li>
     * </ol>
     * @param root root file location. Does nothing if the root is null.
     */
    public void adjustLoadControlFilePath(File root){
        if(root == null) return; //ignore a null root.

        File loadCaseControl = new File(loadCaseControlFile);
        if(!loadCaseControl.isAbsolute()){
            File rootDir = root.getParentFile();
            if(rootDir != null) {
                loadCaseControlFile = new File(rootDir, loadCaseControlFile).getPath();
            }
        }
    }

        //TODO: add individual setters if needed.
    /**
     * Put the scuc data for a single genco.
     * @param gencoName
     * @param powerT0
     * @param unitOnT0
     * @param minUpTime
     * @param minDownTime
     * @param NominalRampUp
     * @param nominalRampDown
     * @param StartupRampLim
     * @param ShutdownRampLim
     * @param schedule1
     * @param schedule2
     */
    public void putScucData(String gencoName, double powerT0, int unitOnT0,
            int minUpTime, int minDownTime, double nominalRampUp, double nominalRampDown,
            double startupRampLim, double shutdownRampLim, int schedule1, int schedule2) {
        SCUCInputData sid = new SCUCInputData(powerT0, unitOnT0, minUpTime,
                minDownTime, nominalRampUp, nominalRampDown, startupRampLim,
                shutdownRampLim, schedule1, schedule2);

        scucData.put(gencoName, sid);
    }

    /**
     * Get reference to the mapping from ZoneNames to array indexes.
     * If anything is changed here, it will be reflected everywhere in
     * the system.
     *
     * Use {@link #addZoneNameMapping(String, int)} to add new mappings.
     * @return map from zone names to indexes.
     */
    public IZoneIndexProvider getZoneNames() {
        return zoneIndexMap;
    }

    public void addFuelType(String genCo, String fuel) {
        fuelType.put(genCo,fuel);
    }
    
    public void addNoLoadCost(String genCo, double cost){
        noLoadCosts.put(genCo, cost);
    }
    public void addColdStartUpCost(String genCo, double cost){
        coldStartUpCosts.put(genCo, cost);
    }
    public void addHotStartUpCost(String genCo, double cost){
        hotStartUpCosts.put(genCo, cost);
    }
    public void addShutDownCost(String genCo, double cost){
        shutDownCosts.put(genCo, cost);
    }

    public boolean hasFuelType(String genCo) {
        return fuelType.containsKey(genCo);
    }
    
    public boolean hasNoLoadCost(String genCo) {
        return noLoadCosts.containsKey(genCo);
    }

    public boolean hasColdStartUpCost(String genCo) {
        return coldStartUpCosts.containsKey(genCo);
    }
    public boolean hasHotStartUpCost(String genCo) {
        return hotStartUpCosts.containsKey(genCo);
    }

    public boolean hasShutDownCost(String genCo) {
        return shutDownCosts.containsKey(genCo);
    }

    /**
     *
     * @param genCo
     * @return
     * @throws IllegalArgumentException if the key is not known.
     */
    public double getNoLoadCostForGen(String genCo) {
        Double d = noLoadCosts.get(genCo);
        if(d == null){
            throw new IllegalArgumentException("Unknown key " + genCo);
        } else {
            return d.doubleValue();
        }
    }
    public double getShutDownCostForGen(String genCo) {
        Double d = shutDownCosts.get(genCo);
        if(d == null){
            throw new IllegalArgumentException("Unknown key " + genCo);
        } else {
            return d.doubleValue();
        }
    }
    public double getColdStartUpCostForGen(String genCo) {
        Double d = coldStartUpCosts.get(genCo);
        if(d == null){
            throw new IllegalArgumentException("Unknown key " + genCo);
        } else {
            return d.doubleValue();
        }
    }
    public double getHotStartUpCostForGen(String genCo) {
        Double d = hotStartUpCosts.get(genCo);
        if(d == null){
            throw new IllegalArgumentException("Unknown key " + genCo);
        } else {
            return d.doubleValue();
        }
    }

    public String getFuelTypeForGen(String genCo) {
        String d = fuelType.get(genCo);
        if(d == null){
            throw new IllegalArgumentException("Unknown key " + genCo);
        } else {
            return d;
        }
    }

    
    /**
     * @return and empty string, or a list of names we didn't know about.
     */
    public String checkNoLoadNames() {
        //THIS APPROCH IS WRONG. Basis, but needs fixed.
        //Actually we need a map of check gencos, not zones. names period.
//        StringBuilder sb = new StringBuilder();
//        boolean unknownName = false;
//        for(String n : noLoadCosts.keySet()) {
//            if(!zoneIndexMap.hasIndexForName(n)){
//                unknownName = true;
//                sb.append(n); sb.append("\n");
//            }
//        }
//
//        if(unknownName)
//            return sb.toString();
//        else
            return "";
    }

    /**
     * Map zoneName to idx.
     * @param zoneName name of the zone. Not null.
     * @param idx
     * @throws IllegalArgumentException if zoneName is null.
     */
    public void addZoneNameMapping(String zoneName, int idx) {
        if( zoneName == null)
            throw new IllegalArgumentException();
        zoneIndexMap.put(zoneName, idx);
    }

    /**
     * Get a reference to the map storing the input data.
     *
     * This is useful to move the entire set of parameters
     * around at once.
     * @return
     */
    public Map<String, SCUCInputData> getSCUCInputData() {
        return scucData;
    }

    public double getReserveRequirements() {
        return reserveRequirements;
    }
    ///////////////////////////////SCUC ACCESSORS//////////////////////////////
    /*
     * Accessor for pieces of scuc data.
     *
     * Added for test purposes.
     */
    /**
    *
    * @param genCo
    * @return -1 if genco not found, or the value.
    */
   public double getPowGenT0(String genCo) {
       SCUCInputData inputData = scucData.get(genCo);
       if (inputData != null) {
           return inputData.powerT0;
       } else {
           return -1;
       }
   }

   /**
    *
    * @param genCo
    * @return -1 if genco not found, or the value.
    */
   public int unitOnT0(String genCo) {
       SCUCInputData inputData = scucData.get(genCo);
       if (inputData != null) {
           return inputData.unitOnT0;
       } else {
           return -1;
       }
   }

   /**
    *
    * @param genCo
    * @return -1 if genco not found, or the value.
    */
   public int minUpTime(String genCo) {
       SCUCInputData inputData = scucData.get(genCo);
       if (inputData != null) {
           return inputData.minUpTime;
       } else {
           return -1;
       }
   }

   /**
    *
    * @param genCo Name of the genco
    * @return -1 if genco not found, or the value.
    */
   public double minDownTime(String genCo) {
       SCUCInputData inputData = scucData.get(genCo);
       if (inputData != null) {
           return inputData.minDownTime;
       } else {
           return -1;
       }
   }

   /**
    *
    * @param genCo
    * @return -1 if genco not found, or the value.
    */
   public double nominalRampUp(String genCo) {
       SCUCInputData inputData = scucData.get(genCo);
       if (inputData != null) {
           return inputData.nominalRampUp;
       } else {
           return -1;
       }
   }

   /**
    *
    * @param genCo
    * @return -1 if genco not found, or the value.
    */
   public double nominalRampDown(String genCo) {
       SCUCInputData inputData = scucData.get(genCo);
       if (inputData != null) {
           return inputData.nominalRampDown;
       } else {
           return -1;
       }
   }

   /**
    *
    * @param genCo
    * @return -1 if genco not found, or the value.
    */
   public int scucSchedule2(String genCo) {
       SCUCInputData inputData = scucData.get(genCo);
       if (inputData != null) {
           return inputData.schedule2;
       } else {
           return -1;
       }
   }

   /**
    *
    * @param genCo
    * @return -1 if genco not found, or the value.
    */
   public int scucSchedule1(String genCo) {
       SCUCInputData inputData = scucData.get(genCo);
       if (inputData != null) {
           return inputData.schedule1;
       } else {
           return -1;
       }
   }

   /**
    *
    * @param genCo
    * @return -1 if genco not found, or the value.
    */
   public int reserveReq(String genCo) {
       SCUCInputData inputData = scucData.get(genCo);
       if (inputData != null) {
           return 0; //FIXME: Value for reserve req.
       } else {
           return -1;
       }
   }

   /**
    *
    * @param genCo
    * @return -1 if genco not found, or the value.
    */
   public double startupRampLim(String genCo) {
       SCUCInputData inputData = scucData.get(genCo);
       if (inputData != null) {
           return inputData.startupRampLim;
       } else {
           return -1;
       }
   }

   /**
    *
    * @param genCo
    * @return -1 if genco not found, or the value.
    */
   public double shutdownRampLim(String genCo) {
       SCUCInputData inputData = scucData.get(genCo);
       if (inputData != null) {
           return inputData.shutdownRampLim;
       } else {
           return -1;
       }
   }
   /////////////////////////////END SCUC ACCESSORS////////////////////////////

   /**
    * Mark if the TestCase included learning data for the gencos.
    * @param b
    */
   public void setHasGenLearningData(boolean b) {
       this.hasGenLearningData = b;
   }

   /**
    * {@link #hasGenLearningData()}.
    */
   public boolean hasGenLearningData(){
       return hasGenLearningData;
   }

    /**
     * Struct to model the scuc input data for a single GenCo.
     *
     *
     * @author Sean L. Mooney
     *
     */
    public static class SCUCInputData {
        private double powerT0;
        private int unitOnT0;
        private int minUpTime;
        private int minDownTime;
        private double nominalRampUp;
        private double nominalRampDown;
        private double startupRampLim;
        private double shutdownRampLim;
        private int schedule1;
        private int schedule2;

        /**
         * @param powerT0
         * @param unitOnT0
         * @param minUpTime
         * @param minDownTime
         * @param nominalRampUp
         * @param nominalRampDown
         * @param startupRampLim
         * @param shutdownRampLim
         * @param schedule1
         * @param schedule2
         */
        public SCUCInputData(double powerT0, int unitOnT0, int minUpTime,
                int minDownTime, double nominalRampUp, double nominalRampDown,
                double startupRampLim, double shutdownRampLim, int schedule1,
                int schedule2) {
            this.powerT0 = powerT0;
            this.unitOnT0 = unitOnT0;
            this.minUpTime = minUpTime;
            this.minDownTime = minDownTime;
            this.nominalRampUp = nominalRampUp;
            this.nominalRampDown = nominalRampDown;
            this.startupRampLim = startupRampLim;
            this.shutdownRampLim = shutdownRampLim;
            this.schedule1 = schedule1;
            this.schedule2 = schedule2;
        }

        /**
         * @return the powerT0
         */
        public double getPowerT0() {
            return powerT0;
        }

        /**
         * @param powerT0 the powerT0 to set
         */
        public void setPowerT0(double powerT0) {
            this.powerT0 = powerT0;
        }

        /**
         * @return the unitOnT0
         */
        public int getUnitOnT0() {
            return unitOnT0;
        }

        /**
         * @param unitOnT0 the unitOnT0 to set
         */
        public void setUnitOnT0(int unitOnT0) {
            this.unitOnT0 = unitOnT0;
        }

        /**
         * @return the minUpTime
         */
        public int getMinUpTime() {
            return minUpTime;
        }

        /**
         * @param minUpTime the minUpTime to set
         */
        public void setMinUpTime(int minUpTime) {
            this.minUpTime = minUpTime;
        }

        /**
         * @return the minDownTime
         */
        public int getMinDownTime() {
            return minDownTime;
        }

        /**
         * @param minDownTime the minDownTime to set
         */
        public void setMinDownTime(int minDownTime) {
            this.minDownTime = minDownTime;
        }

        /**
         * @return the nominalRampUp
         */
        public double getNominalRampUp() {
            return nominalRampUp;
        }

        /**
         * @param nominalRampUp the nominalRampUp to set
         */
        public void setNominalRampUp(double nominalRampUp) {
            this.nominalRampUp = nominalRampUp;
        }

        /**
         * @return the nominalRampDown
         */
        public double getNominalRampDown() {
            return nominalRampDown;
        }

        /**
         * @param nominalRampDown the nominalRampDown to set
         */
        public void setNominalRampDown(double nominalRampDown) {
            this.nominalRampDown = nominalRampDown;
        }

        /**
         * @return the startupRampLim
         */
        public double getStartupRampLim() {
            return startupRampLim;
        }

        /**
         * @param startupRampLim the startupRampLim to set
         */
        public void setStartupRampLim(double startupRampLim) {
            this.startupRampLim = startupRampLim;
        }

        /**
         * @return the shutdownRampLim
         */
        public double getShutdownRampLim() {
            return shutdownRampLim;
        }

        /**
         * @param shutdownRampLim the shutdownRampLim to set
         */
        public void setShutdownRampLim(double shutdownRampLim) {
            this.shutdownRampLim = shutdownRampLim;
        }

        /**
         * @return the schedule1
         */
        public int getSchedule1() {
            return schedule1;
        }

        /**
         * @param schedule1 the schedule1 to set
         */
        public void setSchedule1(int schedule1) {
            this.schedule1 = schedule1;
        }

        /**
         * @return the schedule2
         */
        public int getSchedule2() {
            return schedule2;
        }

        /**
         * @param schedule2 the schedule2 to set
         */
        public void setSchedule2(int schedule2) {
            this.schedule2 = schedule2;
        }

        public String verify() {
            return null; //TODO-XX Don't know how to verify a SCUC Data object
        }
    }
}
