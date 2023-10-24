
/** ***************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *    Klaus Raizer, Andre Paraense, Ricardo Ribeiro Gudwin
 **************************************************************************** */

import java.util.Random;
import java.util.logging.Level;
import support.ResourcesGenerator;
import ws3dproxy.CommandExecException;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Creature;
import ws3dproxy.model.World;
import ws3dproxy.util.Constants;
import ws3dproxy.util.Logger;

/**
 *
 * @author rgudwin
 */
public final class Environment {

    public String host = "localhost";
    public int port = 4011;
    public String robotID = "r0";
    public Creature c = null;

    public Environment() {
        WS3DProxy proxy = new WS3DProxy();
        try {
            World w = World.getInstance();
            w.reset();
            //World.createFood(0, 350, 75);
            //World.createFood(0, 100, 220);
            //World.createFood(0, 250, 210);
            c = proxy.createCreature(1, 1, 0, 0);
            generateMaze(proxy.getWorld());
            c.start();
            w.grow(1);
            //grow(w,7);
        } catch (CommandExecException e) {

        }
        System.out.println("Robot " + c.getName() + " is ready to go.");
    }

    public synchronized void grow(World w, int time) {
        try {
            if (time <= 0) {
                time = Constants.TIMEFRAME;
            }
            w.getDimensionAndDeliverySpot();
            ResourcesGenerator rg = new ResourcesGenerator(time, w.getEnvironmentWidth(), w.getEnvironmentHeight(), w.getDeliverySpot().getX(), w.getDeliverySpot().getY());
            rg.start();
        } catch (CommandExecException ex) {
            Logger.logException(World.class.getName(), ex);
        }
    }

    //Gera um labirinto por divisão recursiva
    private void generateMaze(World world) {

        int worldH = world.getEnvironmentHeight();
        int worldW = world.getEnvironmentWidth();

        int ratio = 40;

        int h = worldH / ratio;
        int w = worldW / ratio;
        //Mapa em grid do ambiente indicando locais com parede (célula ocupada)
        boolean[][] walls = new boolean[h][w];

        walls = divideChambers(walls, 0, 0, h, w);

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                System.out.print(walls[i][j] ? "x" : " ");
            }
            System.out.println("");
        }
        for (int i = 1; i < h - 1; i++) {
            for (int j = 1; j < w - 1; j++) {
                if (walls[i][j]) {
//                    insertWall(world, i, j, i, j+1, ratio);                    
                    //Up
                    if (walls[i][j + 1]) {
                        insertWall(world, i, j, i, j + 1, ratio);
                    }
//                    //Down
//                    if (walls[i][j-1]){
//                        insertWall(world, i, j, i, j-1, ratio);
//                    }
//                    //Left
//                    if (walls[i-1][j]){
//                        insertWall(world, i-1, j, i, j, ratio);
//                    }
                    //Rigth
                    if (walls[i + 1][j]) {
                        insertWall(world, i + 1, j, i, j, ratio);
                    }

                }
            }
        }

    }

    private boolean[][] divideChambers(boolean[][] walls, int startH, int startW, int endH, int endW) {
        Random rnd = new Random();

        if ((endH - startH) >= 3 && (endW - startW) >= 3) {

            //Intersecção das duas linhas que dividem o ambiente em 4 câmaras
            //Garantindo que nenhuma parede estrá na borda do ambiente
            int divH = rnd.nextInt(endH - startH - 2) + startH + 1;
            int divW = rnd.nextInt(endW - startW - 2) + startW + 1;

            //Criação das paredes definidas pelas duas linhas
            for (int i = startH; i < endH; i++) {
                walls[i][divW] = true;
            }
            for (int i = startW; i < endW; i++) {
                walls[divH][i] = true;
            }

            //Cria uma abertura em 3 das quatro paredes formadas
            int noOpenning = rnd.nextInt(4);
            int i1 = rnd.nextInt(divH - startH) + startH;
            int j1 = rnd.nextInt(divW - startW) + startW;
            int i2 = rnd.nextInt(endH - divH - 1) + divH + 1;
            int j2 = rnd.nextInt(endW - divW - 1) + divW + 1;
            if (noOpenning != 0) {
                walls[i1][divW] = false;
            }
            if (noOpenning != 1) {
                walls[divH][j1] = false;
            }
            if (noOpenning != 2) {
                walls[i2][divW] = false;
            }
            if (noOpenning != 3) {
                walls[divH][j2] = false;
            }

            //Subdivisão das 4 câmeras geradas
            walls = divideChambers(walls, startH, startW, divH, divW);
            walls = divideChambers(walls, divH, divW, endH, endW);
            walls = divideChambers(walls, startH, divW, divH, endW);
            walls = divideChambers(walls, divH, startW, endH, divW);

            //Reabertura
            if (noOpenning != 0) {
                walls[i1][divW + 1] = false;
                walls[i1][divW - 1] = false;
            }
            if (noOpenning != 1) {
                walls[divH + 1][j1] = false;
                walls[divH - 1][j1] = false;
            }
            if (noOpenning != 2) {
                walls[i2][divW + 1] = false;
                walls[i2][divW - 1] = false;
            }
            if (noOpenning != 3) {
                walls[divH + 1][j2] = false;
                walls[divH - 1][j2] = false;
            }

        }
        return walls;
    }

    private void insertWall(World world, int y1, int x1, int y2, int x2, int ratio) {
        int w = 5;
        try {
            world.createBrick(5, x1 * ratio - w, y1 * ratio - w, x1 * ratio + w, y1 * ratio + w);
            world.createBrick(5, x2 * ratio - w, y2 * ratio - w, x2 * ratio + w, y2 * ratio + w);
            world.createBrick(5, x1 * ratio - w, y1 * ratio - w, x2 * ratio + w, y2 * ratio + w);

        } catch (CommandExecException ex) {
            java.util.logging.Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
