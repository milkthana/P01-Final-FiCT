
public class Player {

  public enum PlayerType {Healer, Tank, Samurai, BlackMage, Phoenix, Cherry};

  private PlayerType type; 	//Type of this player. Can be one of either Healer, Tank, Samurai, BlackMage, or Phoenix
  private double maxHP;		//Max HP of this player
  private double currentHP;	//Current HP of this player
  private double atk;			//Attack power of this player

  private boolean isSleeping;
  private boolean isCursed;
  private boolean isTaunting;
  private boolean isAlive;

  private int numSP;
  private int currentSP;

  /**
   * Constructor of class Player, which initializes this player's type, maxHP, atk, numSpecialTurns,
   * as specified in the given table. It also reset the internal turn count of this player.
   * @param _type
   */
  public Player(PlayerType _type)
  {
    this.type = _type;
    switch(_type) {
      case Healer:
        this.maxHP = 4790;
        this.atk = 238;
        this.numSP = 4;
        break;
      case Tank:
        this.maxHP = 5340;
        this.atk = 255;
        this.numSP = 4;
        break;
      case Samurai:
        this.maxHP = 4005;
        this.atk = 368;
        this.numSP = 3;
        break;
      case BlackMage:
        this.maxHP = 4175;
        this.atk = 303;
        this.numSP = 4;
        break;
      case Phoenix:
        this.maxHP = 4175;
        this.atk = 209;
        this.numSP = 8;
        break;
      case Cherry:
        this.maxHP = 3560;
        this.atk = 198;
        this.numSP = 4;
        break;
    }
    this.currentHP = this.maxHP;
    this.currentSP = this.numSP - 1;
    this.isAlive = true;
    this.isCursed = false;
    this.isSleeping = false;
    this.isTaunting = false;
  }

  /**
   * Returns the current HP of this player
   * @return
   */
  public double getCurrentHP()
  {
    return this.currentHP;
  }

  /**
   * Returns type of this player
   * @return
   */
  public Player.PlayerType getType()
  {
    return this.type;
  }

  /**
   * Returns max HP of this player.
   * @return
   */
  public double getMaxHP()
  {
    return this.maxHP;
  }

  /**
   * Returns whether this player is sleeping.
   * @return
   */
  public boolean isSleeping()
  {
    return this.isSleeping;
  }

  /**
   * Returns whether this player is being cursed.
   * @return
   */
  public boolean isCursed()
  {
    return this.isCursed;
  }

  /**
   * Returns whether this player is alive (i.e. current HP > 0).
   * @return
   */
  public boolean isAlive()
  {
    return this.isAlive;
  }

  /**
   * Returns whether this player is taunting the other team.
   * @return
   */
  public boolean isTaunting()
  {
    return this.isTaunting;
  }


  public void attack(Player target)
  {
    target.currentHP -= atk;

    if (target.currentHP <= 0) {
      target.currentHP = 0;
      target.currentSP = numSP - 1;
      target.isAlive = false;
      this.isCursed = false;
      this.isSleeping = false;
      this.isTaunting = false;
    }
  }

  public void useSpecialAbility(Player[][] myTeam, Player[][] theirTeam, int round, Arena arena)
  {
    switch(type) {
      case Healer:
        heal(findHealTarget(myTeam));
        break;
      case Tank:
        taunt();
        break;
      case Samurai:
        doubleSlash(findTarget(theirTeam));
        break;
      case BlackMage:
        curse(findTarget(theirTeam));
        break;
      case Phoenix:
        revive(findRevive(myTeam));
        break;
      case Cherry:
        fortuneCookie(theirTeam, arena);
        break;
    }
  }


  /**
   * This method is called by Arena when it is this player's turn to take an action.
   * By default, the player simply just "attack(target)". However, once this player has
   * fought for "numSpecialTurns" rounds, this player must perform "useSpecialAbility(myTeam, theirTeam)"
   * where each player type performs his own special move.
   * @param arena
   */
  public void takeAction(Arena arena)
  {
    if (currentSP == 0) {
      switch(type) {
        case BlackMage:
          System.out.println("# " + arena.getTeam(this) + "["+ arena.getCoordinateCol(this) +"]["+ arena.getCoordinateRow(this) + "]" + " {"+ this.type + "}" + " curses " + arena.getTeam(findTarget(arena.getOtherTeam(this))) +  "["+ arena.getCoordinateCol(findTarget(arena.getOtherTeam(this))) +"][" + arena.getCoordinateRow(findTarget(arena.getOtherTeam(this))) + "] {" + findTarget(arena.getOtherTeam(this)).getType() + "}");
          break;
        case Healer:
          System.out.println("# " + arena.getTeam(this) + "["+ arena.getCoordinateCol(this) +"]["+ arena.getCoordinateRow(this) + "]" + " {"+ this.type + "}" + " heals " + arena.getTeam(findHealTarget(arena.getMyTeam(this))) +  "["+ arena.getCoordinateCol(findHealTarget(arena.getMyTeam(this))) +"][" + arena.getCoordinateRow(findHealTarget(arena.getMyTeam(this))) + "] {" + findHealTarget(arena.getMyTeam(this)).getType() + "}");
          break;
        case Phoenix:
          if (findRevive(arena.getMyTeam(this)) != null) {
            System.out.println("# " + arena.getTeam(this) + "[" + arena.getCoordinateCol(this) + "][" + arena.getCoordinateRow(this) + "]" + " {" + this.type + "}" + " revives " + arena.getTeam(findRevive(arena.getMyTeam(this))) + "[" + arena.getCoordinateCol(findRevive(arena.getMyTeam(this))) + "][" + arena.getCoordinateRow(findRevive(arena.getMyTeam(this))) + "] {" + findRevive(arena.getMyTeam(this)).getType() + "}");
          } else {
            System.out.println("# " + arena.getTeam(this) + "["+ arena.getCoordinateCol(this) +"]["+ arena.getCoordinateRow(this) + "]" + " {"+ this.type + "}" + " revives No one");
          }
          break;
        case Samurai:
          System.out.println("# " + arena.getTeam(this) + "["+ arena.getCoordinateCol(this) +"]["+ arena.getCoordinateRow(this) + "]" + " {"+ this.type + "}" + " double-slashes " + arena.getTeam(findTarget(arena.getOtherTeam(this))) +  "["+ arena.getCoordinateCol(findTarget(arena.getOtherTeam(this))) +"][" + arena.getCoordinateRow(findTarget(arena.getOtherTeam(this))) + "] {" + findTarget(arena.getOtherTeam(this)).getType() + "} "+ " for " + this.atk*2 + " damage");
          break;
        case Tank:
          System.out.println("# " + arena.getTeam(this) + "["+ arena.getCoordinateCol(this) +"]["+ arena.getCoordinateRow(this) + "]" + " {"+ this.type + "}" + " is taunting");
          break;
      }

      useSpecialAbility(arena.getMyTeam(this), arena.getOtherTeam(this), arena.getRound(), arena);
      this.currentSP = this.numSP - 1;
    } else {
      if (this.currentSP == (this.numSP - 1)) {
        if (this.type == PlayerType.BlackMage) {
          fixCurse(arena.getOtherTeam(this));
        }
        if (this.type == PlayerType.Cherry && this.currentSP == (this.numSP - 1)) {
          fixCookie(arena.getOtherTeam(this));
        }
      }

      if (this.isTaunting) {
        this.isTaunting = false;
      }

      System.out.println("# " + arena.getTeam(this) + "["+ arena.getCoordinateCol(this) +"]["+ arena.getCoordinateRow(this) + "]" + " {"+ this.type + "}" + " attacks " + arena.getTeam(findTarget(arena.getOtherTeam(this))) +  "["+ arena.getCoordinateCol(findTarget(arena.getOtherTeam(this))) +"][" + arena.getCoordinateRow(findTarget(arena.getOtherTeam(this))) + "] {" + findTarget(arena.getOtherTeam(this)).getType() + "} "+ " for " + this.atk + " damage");

      attack(findTarget(arena.getOtherTeam(this)));
      this.currentSP--;
    }
  }

  // Player special ability

  public void heal(Player target) {
    if (target != null) {
      if (!target.isCursed) {
        target.currentHP += target.maxHP * 0.25;
      }

      if (target.currentHP > target.maxHP) {
        target.currentHP = target.maxHP;
      }
    }
  }

  public void taunt() {
    this.isTaunting = true;
  }

  public void doubleSlash(Player target) {
    attack(target);
    attack(target);
  }

  public void curse(Player target) {
    target.isCursed = true;
  }

  public void revive(Player target) {
    if (target != null) {
      target.isAlive = true;
      target.currentHP = target.maxHP * 0.3;
      target.currentSP = target.numSP - 1;
    }
  }

  public void fortuneCookie(Player[][] theirTeam, Arena arena) {
    for (int i = 0 ; i < 2 ; i++) {
      for (int j = 0 ; j < theirTeam[0].length ; j++) {
        if (theirTeam[i][j].isAlive) {
          theirTeam[i][j].isSleeping = true;
          System.out.println("# " + arena.getTeam(this) + "["+ arena.getCoordinateCol(this) +"]["+ arena.getCoordinateRow(this) + "]" + " {"+ this.type + "}" + " lures " + arena.getTeam(theirTeam[i][j]) + "["+ arena.getCoordinateCol(theirTeam[i][j]) +"][" + arena.getCoordinateRow(theirTeam[i][j]) + "] {" + theirTeam[i][j].getType() + "}");
        }
      }
    }
  }

  public void fixCurse(Player[][] theirTeam) {
    for (int i = 0 ; i < 2 ; i++) {
      for (int j = 0 ; j < theirTeam[0].length ; j++) {
        if (theirTeam[i][j].isCursed) {
          theirTeam[i][j].isCursed = false;
        }
      }
    }
  }

  public void fixCookie(Player[][] theirTeam) {
    for (int i = 0 ; i < 2 ; i++) {
      for (int j = 0 ; j < theirTeam[0].length ; j++) {
        if (theirTeam[i][j].isSleeping) {
          theirTeam[i][j].isSleeping = false;
        }
      }
    }
  }

  // Other method created outside requirements

  public boolean frontStillAlive(Player[][] targetTeam) {
    boolean isAlive = false;
    for (int i = 0 ; i < targetTeam[0].length ; i++) {
      if (targetTeam[0][i].isAlive) {
        isAlive = true;
        break;
      }
    }

    return isAlive;
  }

  public Player findTarget(Player[][] targetTeam) {
    // Find first taunting tank of opposite team
    Player min = null;

    for (int i = 0 ; i < 2 ; i++) {
      for (int j = 0 ; j < targetTeam[0].length ; j++) {
        if (targetTeam[i][j].isAlive && targetTeam[i][j].isTaunting) {
          if (min == null) {
            if (targetTeam[i][j].isAlive) {
              min = targetTeam[i][j];
            }
          } else {
            if (targetTeam[i][j].isAlive && targetTeam[i][j].currentHP < min.currentHP) {
              min = targetTeam[i][j];
            }
          }
        }
      }
    }

    if (min != null) {
      return min;
    } else {
      // Find lowest HP *from front to back* (Front will have higher priority)
      min = null;

      for (int i = 0 ; i < targetTeam[0].length ; i++) {
        if (min == null) {
          if (targetTeam[0][i].isAlive) {
            min = targetTeam[0][i];
          }
        } else {
          if (targetTeam[0][i].isAlive && targetTeam[0][i].currentHP < min.currentHP) {
            min = targetTeam[0][i];
          }
        }
      }

      if (min == null) {
        for (int i = 0 ; i < targetTeam[0].length ; i++) {
          if (min == null && targetTeam[1][i].isAlive) {
            min = targetTeam[1][i];
          } else if (targetTeam[1][i].isAlive) {
            if (min.currentHP > targetTeam[1][i].currentHP) {
              min = targetTeam[1][i];
            }
          }
        }
      } else {
        return min;
      }
      return min;
    }
  }

  public Player findHealTarget(Player[][] targetTeam) {
    //find the first taunting tank of the opposite team
    Player min = null;

    for (int i = 0 ; i < 2 ; i++) {
      for (int j = 0 ; j < targetTeam[0].length ; j++) {
        if (min == null) {
          if (targetTeam[i][j].isAlive && targetTeam[i][j].currentHP != targetTeam[i][j].maxHP) {
            min = targetTeam[i][j];
          }
        } else {
          if (targetTeam[i][j].isAlive && targetTeam[i][j].currentHP < min.currentHP && targetTeam[i][j].currentHP != targetTeam[i][j].maxHP) {
            min = targetTeam[i][j];
          }
        }
      }
    }

    if (min != null) {
      return min;
    } else {
      for (int i = 0 ; i < 2 ; i++) {
        for (int j = 0 ; j < targetTeam[0].length ; j++) {
          if (min == null) {
            if (targetTeam[i][j].isAlive) {
              min = targetTeam[i][j];
            }
          } else {
            if (targetTeam[i][j].isAlive && targetTeam[i][j].currentHP < min.currentHP) {
              min = targetTeam[i][j];
            }
          }
        }
      }
    }

    return min;
  }

  public Player findRevive(Player[][] team) {
    for (int i = 0 ; i < 2 ; i++) {
      for (int j = 0 ; j < team[0].length ; j++) {
        if (!team[i][j].isAlive) {
          return team[i][j];
        }
      }
    }

    return null;
  }

  /**
   * This method overrides the default Object's toString() and is already implemented for you.
   */
  @Override
  public String toString()
  {
    return "["+this.type.toString()+" HP:"+this.currentHP+"/"+this.maxHP+" ATK:"+this.atk+"]["
        +((this.isCursed())?"C":"")
        +((this.isTaunting())?"T":"")
        +((this.isSleeping())?"S":"")
        +"]";
  }
}