import java.awt.* ;
import java.util.* ;
import java.io.*;
import org.math.plot.*;
import javax.swing.*;

public class Fantasy extends IO
{
    public static class PlayerIdent implements Comparable
    {
        public String name;
        public String team;
        
        public PlayerIdent(String n, String t)
        {
            name = n;
            team = t;
        }
        
        public boolean equals(Object o)
        {
            PlayerIdent PI = (PlayerIdent) o;
            return name.equals(PI.name) && team.equals(PI.team);
        }
        
        public int compareTo(Object o)
        {
            PlayerIdent PI = (PlayerIdent) o;
            if(name.compareTo(PI.name) != 0)
                return name.compareTo(PI.name);
            return team.compareTo(PI.team); 
        }
        
        public int hashCode()
        {
            return name.hashCode();
        }
    }
    
    public static class Player implements Comparable<Player>
    {
        int ID;
        int position;
        int totalPicks;
        int numPicks;
        String name;
        String team;
        double ADP;
        double points;
        double sumsquares;
        double stdev;
        double mean;
        double score;
        
        public PlayerIdent getPID()
        {
            return new PlayerIdent(name,team);
        }
        
        public String toString()
        {
            return "{ID = "+ID+",Position = " + position +",Name = " + name +",Team = " + 
                team +",ADP = " + ADP +",Points = " + points +"}";
        }
        
        public void calcInfo() 
        {
            if(numPicks > 0)
            {
                mean = totalPicks*1.0/numPicks;
                stdev = Math.sqrt((sumsquares+mean*mean*numPicks - 2*mean*totalPicks)*1.0/numPicks);
            }
        }
        
        public String adpInfo()
        {
            calcInfo();
            return "{Name = " + name + ", ADP = " + ADP + ", Total Picks = " + totalPicks + ", # Picks = " + numPicks + ", Mean = " + mean + ", Std Dev = " + stdev + "}";
        }
        
        public int compareTo(Player player) {
    		if(this.score > player.score)
    		      return -1;
    		else
    		      return 1;
     
    		//descending order
    		//return compareQuantity - this.quantity;
     
    	}
    }
    
    private static double[][] maxValues;
    private static String[][] maxNames;
    private static String[] bestNames;
    private static String[] positions;
    private static String[] illegalChars;
    private static HashMap<PlayerIdent,Integer> PlayerIDs;
    private static ArrayList<Player> players;
    private static PrintWriter outRemPlayers;
    private static PrintWriter outDoublePlayers;
    private static PrintWriter out;
    private static PrintWriter out2;
    private static double[] picks;
    private static double[] npicks;
    private static final double EPSILON = 1E-14;
    private static double[] X;
    private static double[] Y;
    private static double[][] bestValues;
    private static double[][] cumulative;
    private static int[] Y2;
    private static ArrayList<Player>[] availablePlayers;
    private static final int NUM_TEAMS = 12;
    private static final int TOTAL_PICKS = 14;
    private static String[] myPositions;
    private static Player[] myPicks;
    private static final int MY_PICK = 4;
    private static BufferedReader pickReader;
    private static Random rgen;
    private static double[][] playerValueWeights;
    
    private static void loadAvailablePlayers()
    {
        for(int i = 0; i < players.size(); i++)
        {
            Player p = players.get(i);
            int pos = p.position;
            ArrayList<Player> curr = availablePlayers[pos];
            int index = 0;
            while(index < curr.size() && p.points < curr.get(index).points)
            {
                index++;
            }
            curr.add(index,p);
        }
    }
    
    private static void loadCumulative() 
    {
        for(int i = 0; i < players.size(); i++)
        {
            Player p = players.get(i);
            if(p.ADP > 0)
            {
                double[] normal = getNormalCurve(p.ADP,p.stdev);
                cumulative[i][0] = 0;
                for(int j = 1; j < cumulative[0].length; j++)
                {
                    cumulative[i][j] = cumulative[i][j-1] + normal[j-1];
                }
            }
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        outRemPlayers = new PrintWriter(new BufferedWriter(new FileWriter("removedPlayers.txt")));
        outDoublePlayers = new PrintWriter(new BufferedWriter(new FileWriter("doublePlayers.txt")));
        out = new PrintWriter(new BufferedWriter(new FileWriter("output.txt")));
        out2 = new PrintWriter(new BufferedWriter(new FileWriter("output2.txt")));
        pickReader = new BufferedReader(new FileReader("picks.txt"));
        outRemPlayers.println("Removed Players");
        outDoublePlayers.println("Double Players (List should be empty)");
        players = new ArrayList<Player>();
        PlayerIDs = new HashMap<PlayerIdent,Integer>();
        String[] tempArr = {"QB","RB","WR","TE"};
        rgen = new Random();
        myPositions = tempArr;
        double[][] tempWeights = {{0.5,1.0},{0.6,0.8,0.95,1.0,1.0},{0.5,0.7,0.9,1.0,1.0},{0.6,1.0}};
        playerValueWeights = tempWeights;
        
        myPicks = new Player[TOTAL_PICKS];
        String[] myIllegalChars = {"\""};
        illegalChars = myIllegalChars;
        bestNames = new String[TOTAL_PICKS+1];
        positions = myPositions;      
        loadMainInfo();
        //System.exit(0);
        //analyzeMocks();
        availablePlayers = new ArrayList[myPositions.length];
        for(int i = 0; i < myPositions.length; i++)
        {
            availablePlayers[i] = new ArrayList<Player>();
        }
        cumulative = new double[players.size()][TOTAL_PICKS*NUM_TEAMS+1];
        loadAvailablePlayers();
        loadCumulative();
        bestValues = new double[myPositions.length][TOTAL_PICKS*NUM_TEAMS+1];
        calculateValues(1);
        
        /*
        double[] X = new double[171];
        for(int i = 0; i < 171; i++)
            X[i] = i;
        Plot2DPanel plot = new Plot2DPanel();
        plot.addLinePlot("my plot", X,bestValues[0]);
        plot.addLinePlot("my plot", X,bestValues[1]);
        plot.addLinePlot("my plot", X,bestValues[2]);
        plot.addLinePlot("my plot", X,bestValues[3]);
        JFrame frame = new JFrame("a plot panel");
        frame.setContentPane(plot);
        frame.setVisible(true);
        */
        
       
        //int[] numPositions = {1,3,3,1,0,0};
        //int[] pickOrder = {1,1,3,2,1,2,2,0};
        //System.out.println(evalPickOrder(pickOrder,1, 10));
        /*
        int[] numPositions = {0,3,3,1,1,1};
        calcBestDraft(numPositions,2);
        */
        //System.out.println(calcBest(numPositions,5,1));
        runDraft();
        out.close();
        out2.close();
        //System.exit(0);
    }
    
    private static void runDraft() throws Exception
    {
        int[] numPositions = {2,5,5,2,0,0};
        int myPick = MY_PICK;
        for(int pick = 1; pick < TOTAL_PICKS*NUM_TEAMS+1; pick++)
        {
            int round = (pick+NUM_TEAMS - 1)/NUM_TEAMS;
            int ID = 0;
            if(pickReader.ready())
                ID = Integer.parseInt(pickReader.readLine());
            else
            {
                clearValues();
                calculateValues(pick);
                if(pick > getPick(round,myPick))
                    calcBestDraft(numPositions,round+1);
                else
                    calcBestDraft(numPositions,round);
                showCurrentState(numPositions,pick);
                readInt();
            }
            boolean yourPick = pick == getPick(round,myPick);//readBoolean("Your Pick?");
            Player toRemove = players.get(ID);
            int pos = toRemove.position;
            if(yourPick)
            {
                myPicks[round-1] = toRemove;
                numPositions[pos]--;
            }
            availablePlayers[pos].remove(toRemove);
        }
    }
    
    private static double roundOff(double val)
    {
        return Math.round(val*100)/100.0;
    }
    
    private static void showCurrentState(int[] numPositions,int pick)
    {
        //int[] arr = {0,2,2,1,0,0};
        //System.out.println(calcBest(arr,4,MY_PICK));
        JFrame frame = new JFrame("Your Pick Summary");
    	String[] pickProjections = new String[TOTAL_PICKS];
    	String[] positionColors = {"F2A29D","B8CAEA","9DF2A7","F2E09D"};
    	for(int i = 0; i < TOTAL_PICKS; i++)
    	{
    	    int thisPick = getPick(i+1,MY_PICK);
    	    if(thisPick < pick)
    	        pickProjections[i] = "<h1>Your Pick</h1><table border=1><tr><th>Player</th><th>Position</th><th>Projected Points</th></tr><tr><td>"+myPicks[i].name+"</td><td>"+myPositions[myPicks[i].position]+"</td><td>"+myPicks[i].points + "</td></tr></table>"; 
    	    else
    	    {
    	        int recPos = Integer.parseInt(bestNames[i+1]);
    	        pickProjections[i] = "<center><h1>Pick " + thisPick + ", Recommended Position: " + myPositions[recPos] + "</h1>";
    	        Player[] mostLikelyWithRecomendedPositions = getMostLikely(pick,thisPick,5,recPos);
    	        String pickSummary = "";
	            String[] toTarget = {"Target", "Go for", "Try to get", "Your best bet is", "The player for you is", "Expect to land on", "Expect to be able to get", "Try to acquire","FBI High Profile Target:","Start warming up to","In all honesty, you're going to get","By my calculations, you shall acquire"};
	            String[] stretchPlayer = {"If he is available, get", "If you can, get","Cross your heart so you can get","Don't quit your day job, but try to get" ,"Hopefully you can find", "Maybe, by some stroke of luck, you can get", "You could strike","Fat chance you get", "Cross your fingers for"};
	            String[] endingPhrase = {"of being available", "of becoming engaged with your team","to become your all-star","to take you out to a 5 star victory","of hanging in there", "of staying alive", "of being fresh meat for you", "of being your tasty treat of the day", "of fulfilling your dream to have him on your team"};
	            pickSummary += toTarget[rgen.nextInt(toTarget.length)] + " " + mostLikelyWithRecomendedPositions[0].name + " (" + roundOff(mostLikelyWithRecomendedPositions[0].points) + " projected points).";
	            
	            Player highestPlayer = mostLikelyWithRecomendedPositions[0];
	            for(int k = 1; k < 5; k++) {
	                if(mostLikelyWithRecomendedPositions[k].points > highestPlayer.points) highestPlayer = mostLikelyWithRecomendedPositions[k];
	            }
	            pickSummary += " " + stretchPlayer[rgen.nextInt(stretchPlayer.length)] + " " + highestPlayer.name + " (" + roundOff(highestPlayer.points) + " projected points), but he only has a " + roundOff(100*(1 - cumulative[highestPlayer.ID][thisPick])/(1 - cumulative[highestPlayer.ID][pick])) + "% chance " + endingPhrase[rgen.nextInt(endingPhrase.length)] + ".";
	            
	            pickProjections[i] += "<p style='font-size: 14px;'>" + pickSummary + "</p>";
    	        for(int j=0; j<4; j++)
    	        {
    	            int TO_GRAB = 5;
    	            Player[] mostLikely = getMostLikely(pick,thisPick,TO_GRAB,j);
    	            pickProjections[i] += "<br/>";
    	            pickProjections[i] += "<div class='" + myPositions[j] + "' style=''";
    	            pickProjections[i] += "<h2>"+myPositions[j]+"</h2><br/>";
    	            pickProjections[i] += "<h2>Projected value: "+roundOff(bestValues[j][thisPick])+"</h2><br/>";
    	            if(j == recPos)
    	               pickProjections[i] += "<table border=1 style='font-weight:bold;";
    	            else
    	               pickProjections[i] += "<table border=1 style='";
    	            pickProjections[i] += "background-color:#"+positionColors[j]+"'>";
    	            pickProjections[i] += "<tr><th>Name</th><th>Team</th><th>Projected Points</th><th>Chance of Availability</th><th>Chance of best available</th></tr>";
    	            for(int k = 0; k < TO_GRAB; k++)
    	                 pickProjections[i] += "<tr><td>" + mostLikely[k].name + "</td><td>" + mostLikely[k].team + "</td><td>" + roundOff(mostLikely[k].points) + "</td><td>" + roundOff(100*(1 - cumulative[mostLikely[k].ID][thisPick])/(1 - cumulative[mostLikely[k].ID][pick]))+ "%</td><td>" + roundOff(100*mostLikely[k].score) + "%</td></tr>";
    	            pickProjections[i] += "</table>";
    	            pickProjections[i] += "</div>";
    	        }
    	        pickProjections[i] += "</center>";
    	    }
    	}
    	frame.getContentPane().add(new JTabbedPaneDemo(pickProjections),
    			BorderLayout.CENTER);
    	frame.setSize(1000, 1000);
    	frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
    	frame.setVisible(true);
    }
    
    private static void calculateValues(int myPick)
    {
        int pick = myPick;
        for(int i = 0; i < positions.length; i++)
        {
            int S = availablePlayers[i].size();
            for(int j = pick; j < TOTAL_PICKS*NUM_TEAMS+1; j++)
            {
                double prob = 1;
                for(int k = 0; k < S; k++)
                {
                    Player p = availablePlayers[i].get(k);
                    int ID = p.ID;
                    double pavail = 0;
                    if(1 - cumulative[ID][pick] >= EPSILON)
                        pavail = (1 - cumulative[ID][j])/(1 - cumulative[ID][pick]);
                    bestValues[i][j] += prob * pavail * p.points;
                    prob *= (1-pavail);
                }
            }
        }
    }
    
    private static Player[] getMostLikely(int currentPick, int myPick,int amount,int position)
    {
        PriorityQueue<Player> mostLikely = new PriorityQueue<Player>();
        int S = availablePlayers[position].size();
        double prob = 1;
        for(int k = 0; k < S; k++)
        {
            Player p = availablePlayers[position].get(k);
            int ID = p.ID;
            double pavail = 0;
            if(1 - cumulative[ID][myPick] >= EPSILON)
                pavail = (1 - cumulative[ID][myPick])/(1 - cumulative[ID][currentPick]);
            p.score = prob * pavail;
            mostLikely.add(p);
            prob *= (1-pavail);
        }
        Player[] best = new Player[amount];
        for(int i = 0; i < amount; i++)
            best[i] = mostLikely.poll();
        return best;
    }
    
    private static void clearValues()
    {
        for(int i = 0; i < positions.length; i++)
        {
            for(int j = 0; j < TOTAL_PICKS*NUM_TEAMS+1; j++)
            {
                bestValues[i][j] = 0;
            }
        }
    }
    
    private static void calcBestDraft(int[] myNumPositions,int myRound)
    {
        int[] numPositions = myNumPositions;
        int pick = MY_PICK;
        double best = calcBest(numPositions,myRound,pick);
        //System.out.println("yo" + pick + ": " + best);
        for(int m = 1; m < bestNames.length; m++)
        {
            int currPick = getPick(m,pick);
            int pos = 0;
            if(bestNames[m] != null)
                pos = Integer.parseInt(bestNames[m]);
            String player = availablePlayers[pos].get(0).name;
            double value = bestValues[pos][currPick];
            //System.out.println(currPick + " " + bestNames[m] + " " + value + " " + player);
        }
    }
    
    private static double calcBest(int[] numPositions, int round, int pick)
    {
        double optimal = 0;
        int currPick = getPick(round,pick);
        String[][] bestToReturn = new String[TOTAL_PICKS+1][numPositions.length];
        int optPos = 0;
        for(int i = 0; i < numPositions.length; i++)
        {
            int pos = i;
            if(numPositions[i] > 0)
            {
                double weight = playerValueWeights[pos][numPositions[i] - 1];
                numPositions[i]--;
                double total = weight*bestValues[pos][currPick] + calcBest(numPositions,round+1,pick);
                numPositions[i]++;
                if(total > optimal)
                {
                    bestToReturn[round][i] = pos + "";
                    for(int m = round + 1; m < bestToReturn.length; m++)
                    {
                        bestToReturn[m][i] = bestNames[m];
                    }
                    optimal = total;
                    optPos = i;
                }
            }
        }
        for(int m = 0; m < bestToReturn.length; m++)
        {
            //if(m == 1 && bestToReturn[1] != null)
                //System.out.println(bestToReturn[0]);
            bestNames[m] = bestToReturn[m][optPos];
        }
        return optimal;
    }
    
    private static int getPick(int round,int pick)
    {
        int basePick = (round - 1) * NUM_TEAMS;
        int more = pick;
        if(round % 2 == 0)
            more = NUM_TEAMS + 1 - pick;
        return basePick + more;
    }
    
    private static String convertToName(String orig)
    {
        StringTokenizer st = new StringTokenizer(orig);
        String result = "";
        int i = 0;
        while(st.hasMoreTokens())
        {
            String token = st.nextToken();
            if(i > 0)
                result += " ";
            result += token.substring(0,1).toUpperCase() + token.substring(1).toLowerCase();
            i++;
        }
        for(i = 0; i < illegalChars.length; i++)
        {
            result = result.replace(illegalChars[i],"");
        }
        return result;
    }

    
    public static void analyzeMocks() throws Exception {
        File folder = new File("Mock_Drafts");
        File[] files = folder.listFiles();
        for(int j = 0; j < files.length; j++)
        {
            ArrayList<String[] >  draftees = readFile(files[j].toString());
            int totalPicks = 0;
            int nPicks = 0;
            int mat = 0;
            for(int i = 0; i < draftees.size(); i++)
            {
                PlayerIdent p = new PlayerIdent(draftees.get(i)[0],draftees.get(i)[2]);
                if(PlayerIDs.containsKey(p))
                {
                    int ID = PlayerIDs.get(p);
                    if(draftees.get(i)[1].equals("QB") && nPicks < 4 && ID != 3)
                    {
                        totalPicks += (i+1);
                        nPicks++;
                    }
                    if(ID == 3)
                        mat = (i + 1);
                    Player player = players.get(ID);
                    player.totalPicks += (i + 1);
                    player.sumsquares += (i+1)*(i+1);
                    player.numPicks++;
                    //out2.println(id);
                }
                else
                {
                    out2.println("HEY " + p.name + " " + p.team);
                }
            }
        }
        for(int i = 0; i < players.size(); i++)
        {
            out2.println(players.get(i).adpInfo());
        }
    }
    
    public static ArrayList<String[] > readFile(String filename) throws Exception { //Name Pos Team
        ArrayList<String[] > players = new ArrayList<String[] >();
        BufferedReader fileReader = new BufferedReader(new FileReader(filename));
        while(fileReader.ready()) {
            ArrayList<String> words = new ArrayList<String>();
            String line = fileReader.readLine();
            StringTokenizer tk = new StringTokenizer(line);
            while(tk.hasMoreTokens()) {
                words.add(tk.nextToken());
            }
            String[] information = new String[3];
            String name = "";
            for(int i=0; i<words.size()-2; i++) {
                name += words.get(i);
                if(i < words.size() - 3)
                    name += " ";
            }
            if(words.size() >= 3)
            {
                information[0] = (name);
                information[0] = convertToName(information[0]);
                information[1] = (words.get(words.size()-2));
                information[2] = (words.get(words.size()-1));
                information[2] = information[2].substring(1,information[2].length() - 1);
                information[0] = transformName(information[0]);
                information[2] = transformTeam(information[2]);
                if(information[0].contains("Defense"))
                {
                    information[0] = information[0].replace("Defense","D/st");
                    information[2] = "NA";
                }
                players.add(information);
            }
        }
        return players;
    }
    
    private static String transformName(String orig)
    {
        String[] toReplace = {"Christopher Ivory","Stevie Johnson","Zachary Stacy","Ny Giants Defense","Ny Jets Defense"};
        String[] replacements = {"Chris Ivory","Steve Johnson","Zac Stacy","New York Giants Defense","New York Jets Defense"};
        
        for(int i = 0; i < toReplace.length; i++)
        {
            if(orig.equals(toReplace[i]))
                return replacements[i];
        }
        
        return orig;
    }
    
    private static String transformTeam(String orig)
    {
        String[] toReplace = {"WAS"};
        String[] replacements = {"WSH"};
        
        for(int i = 0; i < toReplace.length; i++)
        {
            if(orig.equals(toReplace[i]))
                return replacements[i];
        }
        
        return orig;
    }
    
    private static double evalPickOrder(int[] positionPicks, int startingRound, int pick)
    {
        double sum = 0;
        for(int i = 0; i < positionPicks.length; i++)
        {
            int thisPick = getPick(startingRound + i, pick);
            sum += bestValues[positionPicks[i]][thisPick];
        }
        return sum;
    }
    
    private static void loadMainInfo() throws Exception {
        BufferedReader f = new BufferedReader(new FileReader("projections.csv"));
        f.readLine(); //header row
        while(f.ready())
        {
            String[] row = f.readLine().split(",");
            String name = row[1];
            Player p = new Player();
            p.name = name;
            for(int i = 0; i < illegalChars.length; i++)
            {
                p.name = p.name.replace(illegalChars[i],"");
            }
            //p.name = convertToName(p.name);
            p.team = row[3];
            for(int i = 0; i < illegalChars.length; i++)
            {
                p.team = p.team.replace(illegalChars[i],"");
            }
            String ADPStr = row[TOTAL_PICKS+1]; 
            if(isDouble(ADPStr))
                p.ADP = Double.parseDouble(ADPStr);
            p.stdev = 0;
            for(int i = 0; i < myPositions.length; i++)
                if(myPositions[i].equals(row[2].replace("\"", "")))
                    p.position = i;
            int ID = players.size();
            p.ID = ID;
            p.points = Double.parseDouble(row[7]);
            players.add(p);
            PlayerIdent ident = p.getPID();
            if(PlayerIDs.containsKey(ident))
            {
                outDoublePlayers.println(p.name + " " + p.team);
            }
            PlayerIDs.put(ident,ID);
            out.println(p.ID + " " + p.name + " " + p.team);
        }
        outDoublePlayers.close();
        outRemPlayers.close();
        f = new BufferedReader(new FileReader("stdev.csv"));
        f.readLine();
        while(f.ready())
        {
            String[] row = f.readLine().split(",");
            String name = row[1];
            String team = row[3];
            PlayerIdent thisPlayer = new PlayerIdent(name,team);
            if(PlayerIDs.containsKey(thisPlayer))
            {
                int ID = PlayerIDs.get(thisPlayer);
                Player p = players.get(ID);
                if(p.stdev > EPSILON)
                {
                    outDoublePlayers.println(p.name + " " + p.team);
                }
                p.ADP = Double.parseDouble(row[4]);
                p.stdev = Double.parseDouble(row[5]);
            }
            else
            {
                //System.out.println("Player not found " + name + " : " + team); 
            }
        }
    }
    
    /*
    private static void loadMainInfo() throws Exception {
        for(int pos = 0; pos < positions.length; pos++)
        {
            String positionName = positions[pos];
            BufferedReader f = new BufferedReader(new FileReader(positionName+"-ADP.txt"));
            while(f.ready())
            {
                String s = f.readLine();
                String[] tokens = s.split(",");
                Player p = new Player();
                p.name = tokens[1].substring(1);
                for(int i = 0; i < illegalChars.length; i++)
                {
                    p.name = p.name.replace(illegalChars[i],"");
                }
                p.name = convertToName(p.name);
                p.team = tokens[2].substring(1,tokens[2].indexOf('"')).toUpperCase();
                p.ADP = Double.parseDouble(tokens[3]);
                p.position = pos;
                int ID = players.size();
                p.ID = ID;
                players.add(p);
                PlayerIdent ident = p.getPID();
                if(PlayerIDs.containsKey(ident))
                {
                    outDoublePlayers.println(p.name + " " + p.team);
                }
                PlayerIDs.put(ident,ID);
            }
            f = new BufferedReader(new FileReader(positionName+"-Points.txt"));
            while(f.ready())
            {
                String s = f.readLine();
                String[] tokens = s.split(",");
                int splitLoc = tokens[1].indexOf('(');
                String name = tokens[1].substring(0,splitLoc-1);
                name = convertToName(name);
                String team = tokens[1].substring(splitLoc+1,tokens[1].indexOf(')')).toUpperCase();
                double points = Double.parseDouble(tokens[2]);
                int position = pos;
                PlayerIdent ident = new PlayerIdent(name,team);
                if(PlayerIDs.containsKey(ident))
                {
                    int ID = PlayerIDs.get(ident);
                    Player p = players.get(ID);
                    if(p.points > EPSILON)
                    {
                        outDoublePlayers.println(p.name + " " + p.team);
                    }
                    p.points = points;
                }
                else
                {
                    Player p = new Player();
                    p.name = name;
                    p.team = team;
                    p.ADP = 0;
                    p.position = position;
                    p.points = points;
                    int ID = players.size();
                    p.ID = ID;
                    players.add(p);
                    PlayerIDs.put(p.getPID(),ID);
                }
            }
        }
        for(int i = 0; i < players.size(); i++)
        {
            Player p = players.get(i);
            if(p.points < EPSILON || p.ADP < EPSILON)
            {
                //players.remove(i);
                outRemPlayers.println(p.name + " " + p.team);
                //i--;
            }
        }
        for(int i = 0; i < players.size(); i++)
        {
            out.println(players.get(i).toString());
        }
        outDoublePlayers.close();
        outRemPlayers.close();
    }
    */
    
    private static void bestValue() {
        maxValues = new double[positions.length][TOTAL_PICKS*NUM_TEAMS+1];
        maxNames = new String[positions.length][TOTAL_PICKS*NUM_TEAMS+1];
        bestNames = new String[TOTAL_PICKS + 1];
        int[] counter = new int[positions.length];
        for(int i = 0; i < counter.length; i++)
        {
            counter[i] = TOTAL_PICKS*NUM_TEAMS;
        }
        for(int i = players.size() - 1; i >= 0; i--)
        {
            Player p = players.get(i);
            int position = p.position;
            int pick = (int) p.ADP;
            double points = p.points;
            while(counter[position] > pick)
            {
                counter[position]--;
                maxValues[position][counter[position]] = maxValues[position][counter[position]+1];
                maxNames[position][counter[position]] = maxNames[position][counter[position]+1];
            }
            if(points > maxValues[position][counter[position]])
            {
                maxValues[position][counter[position]] = points;
                maxNames[position][counter[position]] = p.name + " (" + p.team + ")";
            }
        }
        
        for(int i = 0; i < positions.length; i++)
        {
            for(int j = TOTAL_PICKS*NUM_TEAMS; j >= 1; j--)
            {
                if(maxValues[i][j] == 0)
                {
                    maxValues[i][j] = maxValues[i][j+1];
                    maxNames[i][j] = maxNames[i][j+1];
                }
            }
        }
        
        for(int i = 0; i < positions.length; i++)
        {
            for(int j = 0; j < TOTAL_PICKS*NUM_TEAMS+1; j++)
            {
                out.println(i + " " + j + " " + maxValues[i][j] + " " + maxNames[i][j]);
            }
        }
    }
    
    private static double getMean(int[] vals)
    {
        int S = 0;
        int L = vals.length;
        for(int i = 0; i < L; i++)
            S += vals[i];
        double mean = 0;
        for(int i = 0; i < L; i++)
        {
            mean += vals[i] * i * 1.0 / S;
        }
        return mean;
    }
    
    private static double getStdev(int[] vals)
    {
        int S = 0;
        int L = vals.length;
        for(int i = 0; i < L; i++)
            S += vals[i];
        double variance = 0;
        double mean = getMean(vals);
        for(int i = 0; i < L; i++)
        {
            variance += vals[i] * (i - mean) * (i - mean) / S;
        }
        double stdev = Math.sqrt(variance);
        return stdev;
    }
    
    private static double[] getNormalCurve(int[] vals)
    {
        int S = 0;
        int L = vals.length;
        for(int i = 0; i < L; i++)
            S += vals[i];
        double[] returnVals = new double[L];
        double mean = getMean(vals);
        double stdev = getStdev(vals);
        double total = 0;
        for(int i = 0; i < L; i++)
        {
            returnVals[i] = 1/(stdev * Math.sqrt(2*Math.PI));
            returnVals[i] *= Math.pow(Math.E,-(i-mean)*(i-mean)/(2*stdev*stdev));
            total += returnVals[i];
        }
        double multiplier = S / total;
        for(int i = 0; i < L; i++)
        {
            returnVals[i] *= multiplier;
        }
        return returnVals;
    }
    
    private static double[] getNormalCurve(double mean, double stdev)
    {
        int S = 1;
        if(stdev < EPSILON)
            stdev = 1;
        int L = TOTAL_PICKS*NUM_TEAMS+1;
        double[] returnVals = new double[L];
        double total = 0;
        for(int i = 0; i < L; i++)
        {
            returnVals[i] = 1/(stdev * Math.sqrt(2*Math.PI));
            returnVals[i] *= Math.pow(Math.E,-(i-mean)*(i-mean)/(2*stdev*stdev));
            total += returnVals[i];
        }
        double multiplier = S / total;
        for(int i = 0; i < L; i++)
        {
            returnVals[i] *= multiplier;
        }
        return returnVals;
    }
    
    private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}