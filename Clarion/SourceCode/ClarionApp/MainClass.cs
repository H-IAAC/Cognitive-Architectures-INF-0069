
using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Threading;
using ClarionApp;
using ClarionApp.Model;
using ClarionApp.Exceptions;
using Gtk;

namespace ClarionApp
{
	class MainClass
	{
		#region properties
		private WSProxy ws = null;
        private ClarionAgentV2 agent2;
        String creatureId = String.Empty;
        String creatureName = String.Empty;
		#endregion

		#region constructor
		public MainClass(string[] args) {
			Application.Init();
			Console.WriteLine ("ClarionApp V0.8");
            bool GrowWorld = true;
            if (args.Length > 0){
                foreach (var command in args){
                    if (command == "withGrow"){
                        GrowWorld = true;
                    }
                    if (command == "noGrow"){
                        GrowWorld = false;
                    }
                }
            } 
			try
            {
                ws = new WSProxy("localhost", 4011);

                String message = ws.Connect();

                if (ws != null && ws.IsConnected)
                {
                    Console.Out.WriteLine ("[SUCCESS] " + message + "\n");
					ws.SendWorldReset();
                    ws.NewCreature(600, 300, 180, out creatureId, out creatureName);
					ws.SendCreateLeaflet();
                    ws.NewBrick(4, 747, 2, 800, 567);
                    ws.NewBrick(4, 50, -4, 747, 47);
                    ws.NewBrick(4, 49, 562, 796, 599);
                    ws.NewBrick(4, -2, 6, 50, 599);

                    if (!String.IsNullOrWhiteSpace(creatureId))
                    {
                        ws.SendStartCamera(creatureId);
                        ws.SendStartCreature(creatureId);
                    }

                    Console.Out.WriteLine("Creature created with name: " + creatureId + "\n");
                    agent2 = new ClarionAgentV2(ws,creatureId,creatureName, GrowWorld);
					

					Console.Out.WriteLine("Running Simulation ...\n");
                }
				else {
					Console.Out.WriteLine("The WorldServer3D engine was not found ! You must start WorldServer3D before running this application !");
					System.Environment.Exit(1);
				}
            }
            catch (WorldServerInvalidArgument invalidArtgument)
            {
                Console.Out.WriteLine(String.Format("[ERROR] Invalid Argument: {0}\n", invalidArtgument.Message));
            }
            catch (WorldServerConnectionError serverError)
            {
                Console.Out.WriteLine(String.Format("[ERROR] Is is not possible to connect to server: {0}\n", serverError.Message));
            }
            catch (Exception ex)
            {
                Console.Out.WriteLine(String.Format("[ERROR] Unknown Error: {0}\n", ex.Message));
            }
			Application.Run();
		}
		#endregion

		#region Methods
		public static void Main (string[] args)	{
			new MainClass(args);
		}
			
        #endregion
	}
	
	
}
