using System;
using System.Collections.Generic;
using System.Linq;
using System.Globalization;
using Clarion;
using Clarion.Framework;
using Clarion.Framework.Core;
using Clarion.Framework.Templates;
using Clarion.Framework.Extensions;
using ClarionApp.Model;
using ClarionApp;
using System.Threading;
using Gtk;

namespace ClarionApp
{

    public class ClarionAgentV2
    {
        #region Constants

        Random rnd;
        bool WithGrowWorld;
        /// <summary>
        /// Constant that represents the Visual Sensor
        /// </summary>
        private String SENSOR_VISUAL_DIMENSION = "VisualSensor";

        /// <summary>
        /// Constant that represents the first Leaflet
        /// </summary>
        private String LEAFLET_1 = "Leaflet1";


        /// <summary>
        /// Constant that represents the second Leaflet
        /// </summary>
        private String LEAFLET_2 = "Leaflet2";


        /// <summary>
        /// Constant that represents the third Leaflet
        /// </summary>
        private String LEAFLET_3 = "Leaflet3";






        /// <summary>
        /// Constant that represents that there is at least one wall ahead
        /// </summary>
        private String DIMENSION_WALL_AHEAD = "WallAhead";

        /// <summary>
        /// Constant that represents that there is at least one wall ahead
        /// </summary>
        private String DIMENSION_FOOD_AHEAD = "FoodAhead";

        /// <summary>
        /// Constant that represents that there is at least one wall ahead
        /// </summary>
        private String DIMENSION_RED_JEWEL_AHEAD = "RedJewelAhead";

        /// <summary>
        /// Constant that represents that there is at least one wall ahead
        /// </summary>
        private String DIMENSION_GREEN_JEWEL_AHEAD = "GreenJewelAhead";

        /// <summary>
        /// Constant that represents that there is at least one wall ahead
        /// </summary>
        private String DIMENSION_BLUE_JEWEL_AHEAD = "BlueJewelAhead";

        /// <summary>
        /// Constant that represents that there is at least one wall ahead
        /// </summary>
        private String DIMENSION_YELLOW_JEWEL_AHEAD = "YellowJewelAhead";

        /// <summary>
        /// Constant that represents that there is at least one wall ahead
        /// </summary>
        private String DIMENSION_MAGENTA_JEWEL_AHEAD = "MagentaJewelAhead";

        /// <summary>
        /// Constant that represents that there is at least one wall ahead
        /// </summary>
        private String DIMENSION_WHITE_JEWEL_AHEAD = "WhiteJewelAhead";


        /// <summary>
        /// Constant that represents that there is at least one wall ahead
        /// </summary>
        private String DIMENSION_FOOD_CLOSE = "FoodClose";

        /// <summary>
        /// Constant that represents that there is at least one wall ahead
        /// </summary>
        private String DIMENSION_JEWEL_CLOSE = "JewelClose";

        /// <summary>
        /// Constant that represents that a Red Jewel is needed for a leaflet
        /// </summary>
        private String DIMENSION_JEWEL_RED = "Red";

        /// <summary>
        /// Constant that represents that a Green Jewel is needed for a leaflet
        /// </summary>
        private String DIMENSION_JEWEL_GREEN = "Green";

        /// <summary>
        /// Constant that represents that a Blue Jewel is needed for a leaflet
        /// </summary>
        private String DIMENSION_JEWEL_BLUE = "Blue";

        /// <summary>
        /// Constant that represents that a Yellow Jewel is needed for a leaflet
        /// </summary>
        private String DIMENSION_JEWEL_YELLOW = "Yellow";

        /// <summary>
        /// Constant that represents that a Magenta Jewel is needed for a leaflet
        /// </summary>
        private String DIMENSION_JEWEL_MAGENTA = "Magenta";

        /// <summary>
        /// Constant that represents that a White Jewel is needed for a leaflet
        /// </summary>
        private String DIMENSION_JEWEL_WHITE = "White";

        /// <summary>
        /// 
        /// </summary>
        private String DIMENSION_LEAFLET_SCORE = "Score";

		double prad = 0;
        double creatureX, creatureY;

        String foodId, jewelId;

        long completedLeafletId = 0;
        List<long> completedLeaflets = new List<long>();
        #endregion

        #region Properties
		public MindViewer mind;
		String creatureId = String.Empty;
		String creatureName = String.Empty;
        #region Simulation
        /// <summary>
        /// If this value is greater than zero, the agent will have a finite number of cognitive cycle. Otherwise, it will have infinite cycles.
        /// </summary>
        public double MaxNumberOfCognitiveCycles = -1;
        /// <summary>
        /// Current cognitive cycle number
        /// </summary>
        private double CurrentCognitiveCycle = 0;
        /// <summary>
        /// Time between cognitive cycle in miliseconds
        /// </summary>
        public Int32 TimeBetweenCognitiveCycles = 0;
        /// <summary>
        /// A thread Class that will handle the simulation process
        /// </summary>
        private Thread runThread;
        #endregion

        #region Agent
		private WSProxy worldServer;
        /// <summary>
        /// The agent 
        /// </summary>
        private Clarion.Framework.Agent CurrentAgent;
        #endregion

        #region Perception Input
        /// <summary>
        /// Perception input to indicates a wall ahead
        /// </summary>
		private DimensionValuePair inputWallAhead;


        /// <summary>
        /// Perception input to indicates a wall ahead
        /// </summary>
		private DimensionValuePair inputFoodAhead;

        /// <summary>
        /// Perception input to indicates a wall ahead
        /// </summary>
		private Dictionary<string,DimensionValuePair> inputJewelAhead = new Dictionary<string, DimensionValuePair>();

        /// <summary>
        /// Perception input to indicates a wall ahead
        /// </summary>
		private DimensionValuePair inputFoodClose;

        /// <summary>
        /// Perception input to indicates a wall ahead
        /// </summary>
		private DimensionValuePair inputJewelClose;

        /// <summary>
        /// 
        /// </summary>
		private Dictionary<string, DimensionValuePair> leaflet1Inputs = new Dictionary<string, DimensionValuePair>();

        /// <summary>
        /// 
        /// </summary>
		private Dictionary<string, DimensionValuePair> leaflet2Inputs = new Dictionary<string, DimensionValuePair>();

        /// <summary>
        /// 
        /// </summary>
		private Dictionary<string, DimensionValuePair> leaflet3Inputs = new Dictionary<string, DimensionValuePair>();
        #endregion



        #region Action Output
        /// <summary>
        /// Output action that makes the agent to rotate clockwise
        /// </summary>
		private ExternalActionChunk outputRotateClockwise;
        /// <summary>
        /// Output action that makes the agent go ahead
        /// </summary>
		private ExternalActionChunk outputGoAhead;


        /// <summary>
        /// Output action that makes the agent eat a food
        /// </summary>
		private ExternalActionChunk outputEatFood;
        /// <summary>
        /// Output action that makes the agent grab a jewel
        /// </summary>
		private ExternalActionChunk outputGetJewel;
        /// <summary>
        /// Output action
        /// </summary>
		private ExternalActionChunk outputDeliver;
        #endregion

        #region Goals Chunk
        private GoalChunk feed;
        private GoalChunk collectLeaflet;
        #endregion

        #endregion

        #region Constructor
		public ClarionAgentV2(WSProxy nws, String creature_ID, String creature_Name, bool wgw)
        {
			worldServer = nws;
            worldServer.NewDelivery(60, 60);
			// Initialize the agent
            CurrentAgent = World.NewAgent("Current Agent");
			mind = new MindViewer();
			mind.Show ();
			creatureId = creature_ID;
			creatureName = creature_Name;

            rnd = new Random();

            WithGrowWorld = wgw;

            // Initialize Sensor's Input Information
            inputWallAhead = World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_WALL_AHEAD);
            inputFoodAhead = World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_FOOD_AHEAD);
            inputFoodClose = World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_FOOD_CLOSE);
            inputJewelClose = World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_JEWEL_CLOSE);


            inputJewelAhead.Add("Red", World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_RED_JEWEL_AHEAD));
            inputJewelAhead.Add("Blue", World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_BLUE_JEWEL_AHEAD));
            inputJewelAhead.Add("Green", World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_GREEN_JEWEL_AHEAD));
            inputJewelAhead.Add("Yellow", World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_YELLOW_JEWEL_AHEAD));
            inputJewelAhead.Add("Magenta", World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_MAGENTA_JEWEL_AHEAD));
            inputJewelAhead.Add("White", World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_WHITE_JEWEL_AHEAD));

            //Initialize Leaflets Input Information
            //  Leaflet 1
            leaflet1Inputs.Add("Red", World.NewDimensionValuePair(LEAFLET_1, DIMENSION_JEWEL_RED));
            leaflet1Inputs.Add("Blue", World.NewDimensionValuePair(LEAFLET_1, DIMENSION_JEWEL_BLUE));
            leaflet1Inputs.Add("Green", World.NewDimensionValuePair(LEAFLET_1, DIMENSION_JEWEL_GREEN));
            leaflet1Inputs.Add("Yellow", World.NewDimensionValuePair(LEAFLET_1, DIMENSION_JEWEL_YELLOW));
            leaflet1Inputs.Add("Magenta", World.NewDimensionValuePair(LEAFLET_1, DIMENSION_JEWEL_MAGENTA));
            leaflet1Inputs.Add("White", World.NewDimensionValuePair(LEAFLET_1, DIMENSION_JEWEL_WHITE));
            leaflet1Inputs.Add("Score", World.NewDimensionValuePair(LEAFLET_1, DIMENSION_LEAFLET_SCORE));
            //  Leaflet 2
            leaflet2Inputs.Add("Red", World.NewDimensionValuePair(LEAFLET_2, DIMENSION_JEWEL_RED));
            leaflet2Inputs.Add("Blue", World.NewDimensionValuePair(LEAFLET_2, DIMENSION_JEWEL_BLUE));
            leaflet2Inputs.Add("Green", World.NewDimensionValuePair(LEAFLET_2, DIMENSION_JEWEL_GREEN));
            leaflet2Inputs.Add("Yellow", World.NewDimensionValuePair(LEAFLET_2, DIMENSION_JEWEL_YELLOW));
            leaflet2Inputs.Add("Magenta", World.NewDimensionValuePair(LEAFLET_2, DIMENSION_JEWEL_MAGENTA));
            leaflet2Inputs.Add("White", World.NewDimensionValuePair(LEAFLET_2, DIMENSION_JEWEL_WHITE));
            leaflet2Inputs.Add("Score", World.NewDimensionValuePair(LEAFLET_2, DIMENSION_LEAFLET_SCORE));
            //  Leaflet 3
            leaflet3Inputs.Add("Red", World.NewDimensionValuePair(LEAFLET_3, DIMENSION_JEWEL_RED));
            leaflet3Inputs.Add("Blue", World.NewDimensionValuePair(LEAFLET_3, DIMENSION_JEWEL_BLUE));
            leaflet3Inputs.Add("Green", World.NewDimensionValuePair(LEAFLET_3, DIMENSION_JEWEL_GREEN));
            leaflet3Inputs.Add("Yellow", World.NewDimensionValuePair(LEAFLET_3, DIMENSION_JEWEL_YELLOW));
            leaflet3Inputs.Add("Magenta", World.NewDimensionValuePair(LEAFLET_3, DIMENSION_JEWEL_MAGENTA));
            leaflet3Inputs.Add("White", World.NewDimensionValuePair(LEAFLET_3, DIMENSION_JEWEL_WHITE));
            leaflet3Inputs.Add("Score", World.NewDimensionValuePair(LEAFLET_3, DIMENSION_LEAFLET_SCORE));


            // Initialize Output actions
            outputRotateClockwise = World.NewExternalActionChunk(CreatureActions.ROTATE_CLOCKWISE.ToString());
            outputGoAhead = World.NewExternalActionChunk(CreatureActions.GO_AHEAD.ToString());
            outputEatFood = World.NewExternalActionChunk(CreatureActions.GET_FOOD.ToString());
            outputGetJewel = World.NewExternalActionChunk(CreatureActions.GET_JEWEL.ToString());
            outputDeliver = World.NewExternalActionChunk(CreatureActions.DELIVER_LEAFLET.ToString());

            // Initialize Goals
            feed = World.NewGoalChunk();
            collectLeaflet = World.NewGoalChunk();

            //Create thread to simulation
            runThread = new Thread(CognitiveCycle);
			Console.WriteLine("Agent started");
        }
        #endregion

        #region Public Methods
        /// <summary>
        /// Run the Simulation in World Server 3d Environment
        /// </summary>
        public void Run()
        {                
			Console.WriteLine ("Running ...");
            // Setup Agent to run
            if (runThread != null && !runThread.IsAlive)
            {
                SetupAgentInfraStructure();
				// Start Simulation Thread                
                runThread.Start(null);
            }
        }

        /// <summary>
        /// Abort the current Simulation
        /// </summary>
        /// <param name="deleteAgent">If true beyond abort the current simulation it will die the agent.</param>
        public void Abort(Boolean deleteAgent)
        {   Console.WriteLine ("Aborting ...");
            if (runThread != null && runThread.IsAlive)
            {
                runThread.Abort();
            }

            if (CurrentAgent != null && deleteAgent)
            {
                CurrentAgent.Die();
            }
        }

		IList<Thing> processSensoryInformation()
		{
			IList<Thing> response = null;

			if (worldServer != null && worldServer.IsConnected)
			{
				response = worldServer.SendGetCreatureState(creatureName);
				prad = (Math.PI / 180) * response.First().Pitch;
				while (prad > Math.PI) prad -= 2 * Math.PI;
				while (prad < - Math.PI) prad += 2 * Math.PI;
                creatureX = response.First().X1;
                creatureY = response.First().Y1;
				Sack s = worldServer.SendGetSack("0");
				mind.setBag(s);

                if (response.Where(item => ((item.CategoryId == Thing.CATEGORY_PFOOD || item.CategoryId == Thing.CATEGORY_NPFOOD) && item.DistanceToCreature <= 40 )).Any())
                    foodId = response.Where(item => ((item.CategoryId == Thing.CATEGORY_PFOOD || item.CategoryId == Thing.CATEGORY_NPFOOD) && item.DistanceToCreature <= 40 )).First().Name;
                if (response.Where(item => (item.CategoryId == Thing.CATEGORY_JEWEL && item.DistanceToCreature <= 40)).Any())
                    jewelId = response.Where(item => (item.CategoryId == Thing.CATEGORY_JEWEL && item.DistanceToCreature <= 40)).First().Name;

                Creature c = (Creature) response.First();
                foreach (Leaflet l in c.getLeaflets()){
                    if (CheckIfCompleted(l, s)){
                        completedLeafletId = l.leafletID;
                    }
                }

			}

			return response;
		}

		void processSelectedAction(CreatureActions externalAction)
		{   Thread.CurrentThread.CurrentCulture = new CultureInfo("en-US");
			if (worldServer != null && worldServer.IsConnected)
			{
				switch (externalAction)
				{
				case CreatureActions.DO_NOTHING:
					// Do nothing as the own value says
					worldServer.SendSetAngle(creatureId, 0, 0, prad);
					break;
				case CreatureActions.ROTATE_CLOCKWISE:
					worldServer.SendSetAngle(creatureId, 0.4, -0.4, 0.4);
					break;
				case CreatureActions.GO_AHEAD:
					worldServer.SendSetAngle(creatureId, 1, 1, prad);
					break;
				case CreatureActions.GET_FOOD:
					worldServer.SendEatIt(creatureId, foodId);
					break;
				case CreatureActions.GET_JEWEL:
					worldServer.SendSackIt(creatureId, jewelId);
					break;
				case CreatureActions.DELIVER_LEAFLET:
					if (Math.Sqrt(creatureX*creatureX + creatureY*creatureY) > 180)
                        worldServer.SendSetGoTo(creatureId, 1, 1, 60, 60);
                    else
                        if (completedLeafletId != 0){
                            worldServer.SendDeliverLeaflet(creatureId, completedLeafletId);
                            completedLeaflets.Add(completedLeafletId);
                        }

					break;
				default:
					break;
				}
			}
		}

        #endregion

        #region Setup Agent Methods
        /// <summary>
        /// Setup agent infra structure (ACS, NACS, MS and MCS)
        /// </summary>
        private void SetupAgentInfraStructure()
        {
            // Setup the ACS Subsystem
            SetupACS();      

            SetupMS();              
        }

        private void SetupMS()
        {            
            // Food Drive
            FoodDrive foodDrive = AgentInitializer.InitializeDrive(CurrentAgent, FoodDrive.Factory, 0.5);
            DriveEquation foodEq = AgentInitializer.InitializeDriveComponent(foodDrive, DriveEquation.Factory);
            foodDrive.Commit(foodEq);
            CurrentAgent.Commit(foodDrive);

            // Leaflets Drive
            RecognitionAchievementDrive getLeafletDrive = AgentInitializer.InitializeDrive(CurrentAgent, RecognitionAchievementDrive.Factory, 0.5);
            DriveEquation leafletEq = AgentInitializer.InitializeDriveComponent(getLeafletDrive, DriveEquation.Factory);
            getLeafletDrive.Commit(leafletEq);
            CurrentAgent.Commit(getLeafletDrive);

            
            GoalSelectionModule gsm = AgentInitializer.InitializeMetaCognitiveModule(CurrentAgent, GoalSelectionModule.Factory);
            GoalSelectionEquation gse = AgentInitializer.InitializeMetaCognitiveDecisionNetwork(gsm, GoalSelectionEquation.Factory);
            
            gse.Input.Add(foodDrive.GetDriveStrength());
            gse.Input.Add(getLeafletDrive.GetDriveStrength());

            GoalStructureUpdateActionChunk gActFood = World.NewGoalStructureUpdateActionChunk();
            gActFood.Add(GoalStructure.RecognizedActions.SET_RESET, feed);

            GoalStructureUpdateActionChunk gActLeaflet = World.NewGoalStructureUpdateActionChunk();
            gActLeaflet.Add(GoalStructure.RecognizedActions.SET_RESET, collectLeaflet);
            
            gse.Output.Add(gActFood);
            gse.Output.Add(gActLeaflet);

            gsm.SetRelevance(gActFood, foodDrive, 1);
            gsm.SetRelevance(gActLeaflet, getLeafletDrive, 1);

            gsm.Commit(gse);
            CurrentAgent.Commit(gsm);
            
            CurrentAgent.MS.Parameters.CURRENT_GOAL_ACTIVATION_OPTION = MotivationalSubsystem.CurrentGoalActivationOptions.FULL;

        }

        /// <summary>
        /// Setup the ACS subsystem
        /// </summary>
        private void SetupACS()
        {
            // Create Rule to avoid collision with wall
            SupportCalculator avoidCollisionWallSupportCalculator = FixedRuleToRotate;
            FixedRule ruleAvoidCollisionWall = AgentInitializer.InitializeActionRule(CurrentAgent, FixedRule.Factory, outputRotateClockwise, avoidCollisionWallSupportCalculator);

            // Commit this rule to Agent (in the ACS)
            CurrentAgent.Commit(ruleAvoidCollisionWall);

            // Create Colission To Go Ahead
            SupportCalculator goAheadSupportCalculator = FixedRuleToGoAhead;
            FixedRule ruleGoAhead = AgentInitializer.InitializeActionRule(CurrentAgent, FixedRule.Factory, outputGoAhead, goAheadSupportCalculator);
            
            // Commit this rule to Agent (in the ACS)
            CurrentAgent.Commit(ruleGoAhead);


            // Create Colission To eat food
            SupportCalculator eatFoodSupportCalculator = FixedRuleEatFood;
            FixedRule ruleEatFood = AgentInitializer.InitializeActionRule(CurrentAgent, FixedRule.Factory, outputEatFood, eatFoodSupportCalculator);
            
            // Commit this rule to Agent (in the ACS)
            CurrentAgent.Commit(ruleEatFood);

            // Create Colission To eat food
            SupportCalculator getJewelSupportCalculator = FixedRuleGrabJewel;
            FixedRule ruleGetJewel = AgentInitializer.InitializeActionRule(CurrentAgent, FixedRule.Factory, outputGetJewel, getJewelSupportCalculator);
            
            // Commit this rule to Agent (in the ACS)
            CurrentAgent.Commit(ruleGetJewel);

            // Create 
            SupportCalculator deliverLeafletSupportCalculator = FixedRuleToDelivery;
            FixedRule ruleDeliverLeaflet = AgentInitializer.InitializeActionRule(CurrentAgent, FixedRule.Factory, outputDeliver, deliverLeafletSupportCalculator);
            
            // Commit this rule to Agent (in the ACS)
            CurrentAgent.Commit(ruleDeliverLeaflet);

            // Disable Rule Refinement
            CurrentAgent.ACS.Parameters.PERFORM_RER_REFINEMENT = false;

            // The selection type will be probabilistic
            CurrentAgent.ACS.Parameters.LEVEL_SELECTION_METHOD = ActionCenteredSubsystem.LevelSelectionMethods.STOCHASTIC;

            // The action selection will be fixed (not variable) i.e. only the statement defined above.
            CurrentAgent.ACS.Parameters.LEVEL_SELECTION_OPTION = ActionCenteredSubsystem.LevelSelectionOptions.FIXED;

            // Define Probabilistic values
            CurrentAgent.ACS.Parameters.FIXED_FR_LEVEL_SELECTION_MEASURE = 1;
            CurrentAgent.ACS.Parameters.FIXED_IRL_LEVEL_SELECTION_MEASURE = 0;
            CurrentAgent.ACS.Parameters.FIXED_BL_LEVEL_SELECTION_MEASURE = 0;
            CurrentAgent.ACS.Parameters.FIXED_RER_LEVEL_SELECTION_MEASURE = 0;
        }

        /// <summary>
        /// Make the agent perception. In other words, translate the information that came from sensors to a new type that the agent can understand
        /// </summary>
        /// <param name="sensorialInformation">The information that came from server</param>
        /// <returns>The perceived information</returns>
		private SensoryInformation prepareSensoryInformation(IList<Thing> listOfThings)
        {
            // New sensory information
            SensoryInformation si = World.NewSensoryInformation(CurrentAgent);

            // Detect if we have a wall ahead
            Boolean wallAhead = listOfThings.Where(item => ((item.CategoryId == Thing.CATEGORY_BRICK || (item.CategoryId == Thing.CATEGORY_CREATURE && item.Name != creatureName)) && item.DistanceToCreature <= 61)).Any();
            double wallAheadActivationValue = wallAhead ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
            si.Add(inputWallAhead, wallAheadActivationValue);



            Boolean foodAhead = listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_PFOOD || item.CategoryId == Thing.CATEGORY_NPFOOD)).Any();
            double foodAheadActivationValue = foodAhead ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
            si.Add(inputFoodAhead, foodAheadActivationValue);

            Boolean foodClose = listOfThings.Where(item => ((item.CategoryId == Thing.CATEGORY_PFOOD || item.CategoryId == Thing.CATEGORY_NPFOOD) && item.DistanceToCreature <= 45 )).Any();
            double foodCloseActivationValue = foodClose ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
            si.Add(inputFoodClose, foodCloseActivationValue);

            foreach (KeyValuePair<string, DimensionValuePair> kvp in inputJewelAhead){
                Boolean jewelAhead = listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_JEWEL && item.Material.Color == kvp.Key && isInCenterVision(item))).Any();
                double jewelAheadActivationValue = jewelAhead ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
                si.Add(kvp.Value, jewelAheadActivationValue);
            }

            Boolean jewelClose = listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_JEWEL && item.DistanceToCreature <= 45)).Any();
            double jewelCloseActivationValue = jewelClose ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
            si.Add(inputJewelClose, jewelCloseActivationValue);

            Creature c = (Creature) listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_CREATURE)).First();

			//Console.WriteLine(sensorialInformation);
			
			int n = 0;
            int maxScore = 0;
			foreach(Leaflet l in c.getLeaflets()) {
                maxScore = l.payment > maxScore ? l.payment : maxScore;
            }

			foreach(Leaflet l in c.getLeaflets()) {
				mind.updateLeaflet(n,l);

                if(n == 0){
                    si = GetLeafletSensorInput(l, leaflet1Inputs, si, maxScore);
                }
                if(n == 1){
                    si = GetLeafletSensorInput(l, leaflet2Inputs, si, maxScore);
                }
                if(n == 2){
                    si = GetLeafletSensorInput(l, leaflet3Inputs, si, maxScore);
                }
				n++;
			} 


            // Drives
            //double hunger =  (1 - (c.Fuel / 1000)) * CurrentAgent.Parameters.MAX_ACTIVATION;
            si[FoodDrive.MetaInfoReservations.STIMULUS, "FoodDrive"] = 1 - TanHFuelStimulus(c.Fuel);
            //hunger > 0.7 ? 1 : 0;

            si[RecognitionAchievementDrive.MetaInfoReservations.STIMULUS, "RecognitionAchievementDrive"] = TanHFuelStimulus(c.Fuel);
            //hunger < 0.7 ? 1 : 0;
            //completedLeaflets.Count < 3 ? (c.Fuel / 1000) * CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;

            return si;
        }
        #endregion

        #region Fixed Rules
        private double FixedRuleToRotate(ActivationCollection currentInput, Rule target)
        {
            // See partial match threshold to verify what are the rules available for action selection
            return HasLeafletsToCompleted(currentInput) &&
                    (
                        (currentInput.Contains(inputWallAhead, CurrentAgent.Parameters.MAX_ACTIVATION)) || 
                        !(ComputeIfMoveAhead(currentInput) || IsOneLeafletCompleted(currentInput)) 
                    ) ? 1.0 : 0.0;
        }

        private double FixedRuleToGoAhead(ActivationCollection currentInput, Rule target)
        {
            // Here we will make the logic to go ahead
            
            return HasLeafletsToCompleted(currentInput) && ComputeIfMoveAhead(currentInput) ? 1.0 : 0.0;
        }

        private double FixedRuleToDelivery(ActivationCollection currentInput, Rule target)
        {
            // Here we will make the logic to deliver leaflet
            
            return IsOneLeafletCompleted(currentInput) ? 1.0 : 0.0;
        }

        private bool ComputeIfMoveAhead(ActivationCollection currentInput){
            var leafletJewelAhead = false;
            foreach (var d in currentInput){
                DimensionValuePair dv = d.WORLD_OBJECT.AsDimensionValuePair;
                if (inputJewelAhead.Values.Contains(dv) && d.ACTIVATION == CurrentAgent.Parameters.MAX_ACTIVATION){
                    string color = inputJewelAhead.FirstOrDefault(item => item.Value == dv).Key;
                    if ((currentInput.Contains(leaflet1Inputs[color], CurrentAgent.Parameters.MAX_ACTIVATION) &&
                         !currentInput.Contains(leaflet1Inputs["Score"], CurrentAgent.Parameters.MIN_ACTIVATION)) ||
                        (currentInput.Contains(leaflet2Inputs[color], CurrentAgent.Parameters.MAX_ACTIVATION) &&
                         !currentInput.Contains(leaflet2Inputs["Score"], CurrentAgent.Parameters.MIN_ACTIVATION)) ||
                        (currentInput.Contains(leaflet3Inputs[color], CurrentAgent.Parameters.MAX_ACTIVATION) &&
                         !currentInput.Contains(leaflet3Inputs["Score"], CurrentAgent.Parameters.MIN_ACTIVATION))){
                            leafletJewelAhead = true;
                    }
                }

            }
            //Console.WriteLine($"Feed {currentInput.Contains(feed.AsDimensionValuePair, CurrentAgent.Parameters.MAX_ACTIVATION)} Collect {currentInput.Contains(collectLeaflet.AsDimensionValuePair, CurrentAgent.Parameters.MAX_ACTIVATION)}");
            //Console.WriteLine($"Leaf1 {currentInput.Contains(collectLeaflet1.AsDimensionValuePair, CurrentAgent.Parameters.MAX_ACTIVATION)} Leaf2 {currentInput.Contains(collectLeaflet2.AsDimensionValuePair, CurrentAgent.Parameters.MAX_ACTIVATION)} Leaf3 {currentInput.Contains(collectLeaflet3.AsDimensionValuePair, CurrentAgent.Parameters.MAX_ACTIVATION)}");

            return ((currentInput.Contains(inputWallAhead, CurrentAgent.Parameters.MIN_ACTIVATION))&& 
                        (
                            (currentInput.Contains(inputFoodAhead, CurrentAgent.Parameters.MAX_ACTIVATION) &&
                                currentInput.Contains(feed.AsDimensionValuePair, CurrentAgent.Parameters.MAX_ACTIVATION)) || 
                            (leafletJewelAhead)
                        ));
        }


        private bool IsOneLeafletCompleted(ActivationCollection currentInput){
            bool l1Completed = true, l2Completed = true, l3Completed = true;
            foreach (var d in currentInput){
                DimensionValuePair dv = d.WORLD_OBJECT.AsDimensionValuePair;
                if (leaflet1Inputs.ContainsValue(dv) && 
                        (dv.Value.ToString() != DIMENSION_LEAFLET_SCORE && d.ACTIVATION == 1 ||
                         dv.Value.ToString() == DIMENSION_LEAFLET_SCORE && d.ACTIVATION == 0)
                    )
                        l1Completed = false;
                if (leaflet2Inputs.ContainsValue(dv) && 
                        (dv.Value.ToString() != DIMENSION_LEAFLET_SCORE && d.ACTIVATION == 1 ||
                         dv.Value.ToString() == DIMENSION_LEAFLET_SCORE && d.ACTIVATION == 0)
                    )
                        l2Completed = false;
                if (leaflet3Inputs.ContainsValue(dv) && 
                        (dv.Value.ToString() != DIMENSION_LEAFLET_SCORE && d.ACTIVATION == 1 ||
                         dv.Value.ToString() == DIMENSION_LEAFLET_SCORE && d.ACTIVATION == 0)
                    )
                        l3Completed = false;

            }
            return l1Completed || l2Completed || l3Completed;
        }

        private bool HasLeafletsToCompleted(ActivationCollection currentInput){
            foreach (var d in currentInput){
                DimensionValuePair dv = d.WORLD_OBJECT.AsDimensionValuePair;
                if (dv.Value.ToString() == DIMENSION_LEAFLET_SCORE && d.ACTIVATION != 0)
                    return true;
            }
            return false;
        }
        


        private double FixedRuleEatFood(ActivationCollection currentInput, Rule target)
        {
            // Here we will make the logic to eat food
            return ((currentInput.Contains(inputFoodClose, CurrentAgent.Parameters.MAX_ACTIVATION))) ? 1.0 : 0.0;
        }

        private double FixedRuleGrabJewel(ActivationCollection currentInput, Rule target)
        {
            // Here we will make the logic to go ahead
            return ((currentInput.Contains(inputJewelClose, CurrentAgent.Parameters.MAX_ACTIVATION))) ? 1.0 : 0.0;
        }
        #endregion

        #region Run Thread Method
        private void CognitiveCycle(object obj)
        {

			Console.WriteLine("Starting Cognitive Cycle ... press CTRL-C to finish !");
            Thread.Sleep(10000);
            // Cognitive Cycle starts here getting sensorial information
            while (CurrentCognitiveCycle != MaxNumberOfCognitiveCycles)
            {   
				// Get current sensory information                    
				IList<Thing> currentSceneInWS3D = processSensoryInformation();

                // Make the perception
                SensoryInformation si = prepareSensoryInformation(currentSceneInWS3D);

                //Perceive the sensory information
                CurrentAgent.Perceive(si);

                //Choose an action
                ExternalActionChunk chosen = CurrentAgent.GetChosenExternalAction(si);
                Console.WriteLine(si);

                // Get the selected action
                String actionLabel = chosen.LabelAsIComparable.ToString();
                //Console.WriteLine(actionLabel);
                CreatureActions actionType = (CreatureActions)Enum.Parse(typeof(CreatureActions), actionLabel, true);

                // Call the output event handler
				processSelectedAction(actionType);

                if (WithGrowWorld)
                    GrowWorld(CurrentCognitiveCycle);
                // Increment the number of cognitive cycles
                CurrentCognitiveCycle++;

                //Wait to the agent accomplish his job
                if (TimeBetweenCognitiveCycles > 0)
                {
                    Thread.Sleep(TimeBetweenCognitiveCycles);
                }
			}
        }
        #endregion

        #region Auxiliaxy methods
        private bool isInCenterVision(Thing item){
            double px = item.comX - creatureX;
            double py = item.comY - creatureY;

            double visionAngle = Math.Atan2(py, px) - prad;
            //Console.WriteLine($"Angle {px} - {py}");

            return (Math.Abs(visionAngle) < 0.25);
        }

        private void GrowWorld(double count){
            if (count % 2000 == 0){
                for (var i=0; i<5; i++){
                    int x = rnd.Next(100, 700);
                    int y = rnd.Next(100, 500);
                    double px = x - creatureX;
                    double py = y - creatureY;
                    if (Math.Sqrt(px*px + py*py) > 45){
                        switch (rnd.Next(0, 8) + rnd.Next(0, 3)){
                            case 0:
                                worldServer.NewFood(0, x, y);
                                break;
                            case 1:
                                worldServer.NewFood(1, x, y);
                                break;
                            case 2:
                                worldServer.NewJewel(0, x, y);
                                break;
                            case 3:
                                worldServer.NewJewel(1, x, y);
                                break;
                            case 4:
                                worldServer.NewJewel(2, x, y);
                                break;
                            case 5:
                                worldServer.NewJewel(3, x, y);
                                break;
                            case 6:
                                worldServer.NewJewel(4, x, y);
                                break;
                            case 7:
                                worldServer.NewJewel(5, x, y);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

        private double TanHFuelStimulus(double fuel){
            return Math.Tanh(fuel/50-8)/2+0.5;
        }

        private bool CheckIfCompleted(Leaflet l, Sack s){
            double totalMissing = 0;
            totalMissing += Math.Max(l.getRequired("Red") - s.red_crystal, 0);
            totalMissing += Math.Max(l.getRequired("Green") - s.green_crystal, 0);
            totalMissing += Math.Max(l.getRequired("Blue") - s.blue_crystal, 0);
            totalMissing += Math.Max(l.getRequired("Yellow") - s.yellow_crystal, 0);
            totalMissing += Math.Max(l.getRequired("Magenta") - s.magenta_crystal, 0);
            totalMissing += Math.Max(l.getRequired("White") - s.white_crystal, 0);
            if (totalMissing > 0)
                return false;
            return true;
        }

        private SensoryInformation GetLeafletSensorInput(Leaflet l, Dictionary<string, DimensionValuePair> inputs, SensoryInformation si, int maxScore){
            Sack s = worldServer.SendGetSack(creatureId);
            if (l.getRequired("Red") > s.red_crystal)
                si.Add(inputs["Red"], CurrentAgent.Parameters.MAX_ACTIVATION);
            else
                si.Add(inputs["Red"], CurrentAgent.Parameters.MIN_ACTIVATION);
            
            if (l.getRequired("Green") > s.green_crystal)
                si.Add(inputs["Green"], CurrentAgent.Parameters.MAX_ACTIVATION);
            else
                si.Add(inputs["Green"], CurrentAgent.Parameters.MIN_ACTIVATION);
            
            if (l.getRequired("Blue") > s.blue_crystal)
                si.Add(inputs["Blue"], CurrentAgent.Parameters.MAX_ACTIVATION);
            else
                si.Add(inputs["Blue"], CurrentAgent.Parameters.MIN_ACTIVATION);
            
            if (l.getRequired("Yellow") > s.yellow_crystal)
                si.Add(inputs["Yellow"], CurrentAgent.Parameters.MAX_ACTIVATION);
            else
                si.Add(inputs["Yellow"], CurrentAgent.Parameters.MIN_ACTIVATION);
            
            if (l.getRequired("Magenta") > s.magenta_crystal)
                si.Add(inputs["Magenta"], CurrentAgent.Parameters.MAX_ACTIVATION);
            else
                si.Add(inputs["Magenta"], CurrentAgent.Parameters.MIN_ACTIVATION);
            
            if (l.getRequired("White") > s.white_crystal)
                si.Add(inputs["White"], CurrentAgent.Parameters.MAX_ACTIVATION);
            else
                si.Add(inputs["White"], CurrentAgent.Parameters.MIN_ACTIVATION);
            
            if (!completedLeaflets.Contains(l.leafletID))
                si.Add(inputs["Score"], CurrentAgent.Parameters.MAX_ACTIVATION * l.payment / maxScore);
            else
                si.Add(inputs["Score"], CurrentAgent.Parameters.MIN_ACTIVATION * l.payment / maxScore);

            return si;
        }
        #endregion

    }
}
