import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class Arena {

  public enum Row {Front, Back};	//enum for specifying the front or back row
  public enum Team {A, B};		//enum for specifying team A or B

  private Player[][] teamA = null;	//two dimensional array representing the players of Team A
  private Player[][] teamB = null;	//two dimensional array representing the players of Team B
  private int numRowPlayers = 0;		//number of players in each row

  public static final int MAXROUNDS = 100;	//Max number of turn
  public static final int MAXEACHTYPE = 3;	//Max number of players of each type, in each team.
  private final Path logFile = Paths.get("battle_log.txt");

  private int numRounds = 0;	//keep track of the number of rounds so far
  public double hPofA = 0;
  public double hPofB = 0;
  public int RowNum=0;

  /**
   * Constructor.
   * @param _numRowPlayers is the number of players in each row.
   */
  public Arena(int _numRowPlayers)
  {
	//INSERT YOUR CODE HERE	
    this.teamA = new Player[2][_numRowPlayers];
    this.teamB = new Player[2][_numRowPlayers];
    numRowPlayers = _numRowPlayers;

    ////Keep this block of code. You need it for initialize the log file.
    ////(You will learn how to deal with files later)
    try {
      Files.deleteIfExists(logFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
    /////////////////////////////////////////

  }

  /**
   * Returns true if "player" is a member of "team", false otherwise.
   * Assumption: team can be either Team.A or Team.B
   * @param player
   * @param team
   * @return
   */
  public boolean isMemberOf(Player player, Team team)
  {
	//INSERT YOUR CODE HERE
			if(team == Team.A) {
				for(int a=0; a<2; a++) {
					
					for(int b=0; b<teamA[0].length;b++) {
						if(player.equals(teamA[a][b])) {
							return true;
						}
					}
				}
			}
			return false;
		}
  /**
   * This methods receives a player configuration (i.e., team, type, row, and position),
   * creates a new player instance, and places him at the specified position.
   * @param team is either Team.A or Team.B
   * @param pType is one of the Player.Type  {Healer, Tank, Samurai, BlackMage, Phoenix}
   * @param row	either Row.Front or Row.Back
   * @param position is the position of the player in the row. Note that position starts from 1, 2, 3....
   */
  public void addPlayer(Team team, Player.PlayerType pType, Row row, int position)
	{	
		//INSERT YOUR CODE HERE
		Player x = new Player(pType);
		if(row.equals(Row.Front)) {
			
			RowNum = 0;
		}
		else if(row.equals(Row.Back)) {
			
			RowNum = 1;
		}
		if(team.equals(Team.A)) {
		
			teamA[RowNum][position-1] = x;
			hPofA += x.getMaxHP();
		}
		else if(team.equals(Team.B)) {
			teamB[RowNum][position-1] = x;
			hPofB += x.getMaxHP();
		}
	}
  /**
   * Validate the players in both Team A and B. Returns true if all of the following conditions hold:
   *
   * 1. All the positions are filled. That is, there each team must have exactly numRow*numRowPlayers players.
   * 2. There can be at most MAXEACHTYPE players of each type in each team. For example, if MAXEACHTYPE = 3
   * then each team can have at most 3 Healers, 3 Tanks, 3 Samurais, 3 BlackMages, and 3 Phoenixes.
   *
   * Returns true if all the conditions above are satisfied, false otherwise.
   * @return
   */
  public boolean validatePlayers() 
  {
	  //INSERT YOUR CODE HERE
		int[] checkA = {0,0,0,0,0,0};
		int[] checkB = {0,0,0,0,0,0};
		
		for (int a = 0 ; a < this.numRowPlayers ; a++) {
		      for (int b = 0 ; b < 2 ; b++) {
		    	checkA[this.teamA[b][a].getType().ordinal()]++;
		    	checkB[this.teamB[b][a].getType().ordinal()]++;
		      }

		      for (int b = 0 ; b < 2 ; b++) {
		        if (this.teamA[b][a] == null || this.teamB[b][a] == null) {
		          return false;
		        }
		      }
		    }

		    for (int a = 0 ; a < 6 ; a++) {
		      if (checkA[a] > this.MAXEACHTYPE || checkB[a] > this.MAXEACHTYPE) {
		        return false;
		      }
		    }
		    return true;
		  }
  /**
   * Returns the sum of HP of all the players in the given "team"
   * @param team
   * @return
   */
  public static double getSumHP(Player[][] team)
  {
	//INSERT YOUR CODE HERE	
    double allHp = 0;

    for (int a = 0 ; a < team[0].length ; a++) 
    {
    	allHp+= team[0][a].getCurrentHP();
    	allHp+= team[1][a].getCurrentHP();
    }
    return allHp;
  }

  /**
   * Return the team (either teamA or teamB) whose number of alive players is higher than the other.
   *
   * If the two teams have an equal number of alive players, then the team whose sum of HP of all the
   * players is higher is returned.
   *
   * If the sums of HP of all the players of both teams are equal, return teamA.
   * @return
   */
  public Player[][] getWinningTeam()
  {
	//INSERT YOUR CODE HERE	
    if (aliveCount(this.teamA) == aliveCount(this.teamB)) 
    {
      if (getSumHP(this.teamA) > getSumHP(this.teamB)) 
      {
        return this.teamA;
      } 
      else 
      {
        return this.teamB;
      }
    } 
    else if (aliveCount(this.teamB) > aliveCount(this.teamA)) 
    {
    	return this.teamB;
    } 
    else {
    	return this.teamA;
    }
  }

  /**
   * This method simulates the battle between teamA and teamB. The method should have a loop that signifies
   * a round of the battle. In each round, each player in teamA invokes the method takeAction(). The players'
   * turns are ordered by its position in the team. Once all the players in teamA have invoked takeAction(),
   * not it is teamB's turn to do the same.
   *
   * The battle terminates if one of the following two conditions is met:
   *
   * 1. All the players in a team has been eliminated.
   * 2. The number of rounds exceeds MAXROUNDS
   *
   * After the battle terminates, report the winning team, which is determined by getWinningTeam().
   */
  public void startBattle()
  {
	//INSERT YOUR CODE HERE	
	  while (this.numRounds < 100) {
	      System.out.println("\n@ Round " + this.numRounds+ ":");
	      for (int a = 0 ; a < 2 ; a++) {
	        for (int b = 0 ; b < this.numRowPlayers ; b++) 
	        {
	          if (this.teamA[a][b].isAlive() && !this.teamA[a][b].isSleeping()) 
	          {
	            this.teamA[a][b].takeAction(this);
	            if (aliveCount(this.teamB) == 0) 
	            {
	              break;
	            }
	          }
	        }
	        if (aliveCount(this.teamB) == 0) 
	        {
	          break;
	        }
	      }
	      if (aliveCount(this.teamB) != 0) {
	        for (int a = 0 ; a < 2 ; a++) 
	        {
	          for (int b = 0 ; b < this.numRowPlayers ; b++) 
	          {
	            if (this.teamB[a][b].isAlive() && !this.teamB[a][b].isSleeping()) 
	            {
	              this.teamB[a][b].takeAction(this);
	              if(aliveCount(this.teamA) == 0) 
	              {
	                break;
	              }
	            }
	          }
	          if (aliveCount(this.teamA) == 0) 
	          {
	            break;
	          }
	        }
	        System.out.println("");
	        displayArea(this, true);
	      }
	      logAfterEachRound();
	      this.numRounds++;
	      if (aliveCount(this.teamA) == 0 || aliveCount(this.teamB) == 0) 
	      {
	        break;
	      }
	    }

	    System.out.println("");
	    displayArea(this, true);
	    logAfterEachRound();
	    if (aliveCount(this.teamA) == 0 || aliveCount(this.teamB) == 0)
	    {
	      if(aliveCount(this.teamA) == 0) 
	      {
	    	System.out.println("@@@ TeamB win");
	      }
	      else if(aliveCount(this.teamB) == 0) 
	      {
	    	System.out.println("@@@ TeamA win");
	      }
	    }
	  }
  // Other method created outside requirements
  public Player[][] getMyTeam(Player player) 
  {
    if (isMemberOf(player, Team.A)) 
    {
      return this.teamA;
    } else {
      return this.teamB;
    }
  }

  public Player[][] getOtherTeam(Player player) 
  {
    if (isMemberOf(player, Team.A)) 
    {
      return this.teamB;
    } else {
      return this.teamA;
    }
  }

  public int getRound() 
  {
    return this.numRounds;
  }

  public int aliveCount(Player[][] team)
  {
    int alive = 0;

    for (int i = 0 ; i < 2 ; i++) 
    {
      for(int j = 0 ; j < team[0].length ; j++) 
      {
        if (team[i][j].isAlive()) 
        {
          alive++;
        }
      }
    }

    return alive;
  }

  public Team getTeam(Player player) 
  {
    if (isMemberOf(player, Team.A)) 
    {
      return Team.A;
    } else {
      return Team.B;
    }
  }

  public int getCoordinateRow(Player player) 
  {
	//INSERT YOUR CODE HERE	
    if (isMemberOf(player, Team.A))
    {
      for (int i = 0 ; i < 2 ; i++) 
      {
        for (int j = 0 ; j < this.teamA[0].length ; j++) 
        {
          if (player.equals(this.teamA[i][j])) {
            return j + 1;
          }
        }
      }
    } else
    {
      for (int i=0 ; i < 2 ; i++) 
      {
    	  
        for (int j=0 ; j < this.teamB[0].length ; j++) 
        {
          if (player.equals(this.teamB[i][j])) 
          {
            return j + 1;
          }
        }
      }
    }

    return 0;
  }

  public Row getCoordinateCol(Player player) 
  {
    if (isMemberOf(player, Team.A)) 
    {
      for (int i = 0 ; i < 2 ; i++) 
      {
        for (int j = 0 ; j < this.teamA[0].length ; j++) 
        {
          if (player.equals(this.teamA[i][j])) 
          {
            if (i == 0) 
            {
              return Row.Front;
            } 
            else 
            {
              return Row.Back;
            }
          }
        }
      }
    } 
    else 
    {
      for (int i = 0 ; i < 2 ; i++)
      {
        for (int j = 0 ; j < this.teamB[0].length ; j++) 
        {
          if (player.equals(this.teamB[i][j])) 
          {
            if (i == 0)
            {
              return Row.Front;
            } 
            else 
            {
              return Row.Back;
            }
          }
        }
      }
    }

    return Row.Front;
  }


  /**
   * This method displays the current area state, and is already implemented for you.
   * In startBattle(), you should call this method once before the battle starts, and
   * after each round ends.
   *
   * @param arena
   * @param verbose
   */
  public static void displayArea(Arena arena, boolean verbose)
  {
    StringBuilder str = new StringBuilder();
    if(verbose)
    {
      str.append(String.format("%43s   %40s","Team A","")+"\t\t"+String.format("%-38s%-40s","","Team B")+"\n");
      str.append(String.format("%43s","BACK ROW")+String.format("%43s","FRONT ROW")+"  |  "+String.format("%-43s","FRONT ROW")+"\t"+String.format("%-43s","BACK ROW")+"\n");
      for(int i = 0; i < arena.numRowPlayers; i++)
      {
        str.append(String.format("%43s",arena.teamA[1][i])+String.format("%43s",arena.teamA[0][i])+"  |  "+String.format("%-43s",arena.teamB[0][i])+String.format("%-43s",arena.teamB[1][i])+"\n");
      }
    }

    str.append("@ Total HP of Team A = "+getSumHP(arena.teamA)+". @ Total HP of Team B = "+getSumHP(arena.teamB)+"\n\n");
    System.out.print(str.toString());


  }

  /**
   * This method writes a log (as round number, sum of HP of teamA, and sum of HP of teamB) into the log file.
   * You are not to modify this method, however, this method must be call by startBattle() after each round.
   *
   * The output file will be tested against the auto-grader, so make sure the output look something like:
   *
   * 1	47415.0	49923.0
   * 2	44977.0	46990.0
   * 3	42092.0	43525.0
   * 4	44408.0	43210.0
   *
   * Where the numbers of the first, second, and third columns specify round numbers, sum of HP of teamA, and sum of HP of teamB respectively.
   */
  private void logAfterEachRound()
  {
    try {
      Files.write(logFile, Arrays.asList(new String[]{numRounds+"\t"+getSumHP(teamA)+"\t"+getSumHP(teamB)}), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}