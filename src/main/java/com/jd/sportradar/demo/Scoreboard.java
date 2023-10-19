package com.jd.sportradar.demo;

import java.util.List;

import static com.jd.sportradar.demo.Match.createMatch;

public interface Scoreboard {

  Match updateMatch(Match match);

  Match startMatch(Team homeTeam, Team awayTeam);

  boolean finishMatch(Match match);

  List<Match> getContent();

  void cleanUp();
}
