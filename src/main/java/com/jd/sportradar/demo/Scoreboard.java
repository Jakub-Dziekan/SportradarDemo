package com.jd.sportradar.demo;

import java.util.*;

public final class Scoreboard {

  Set<Match> scoreboardContent;
  Comparator<Match> comparator = Comparator.comparingInt(Match::hashCode);

  public Scoreboard() {
    scoreboardContent = new TreeSet<>(comparator);
  }

  public Match updateMatch(Match match) {
    if (null == match) return null;
    return null;
  }

  public Match startMatch(Team homeTeam, Team awayTeam) {
    return null;
  }

  public boolean finishMatch(Match match) {
    return false;
  }

  public ArrayList<Match> getContent() {
    return new ArrayList<>();
  }

  public void cleanUp() {
    scoreboardContent = new TreeSet<>(comparator);
  }
}
