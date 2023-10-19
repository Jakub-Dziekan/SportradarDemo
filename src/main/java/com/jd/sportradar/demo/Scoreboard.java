package com.jd.sportradar.demo;

import java.util.List;

import static com.jd.sportradar.demo.Match.createMatch;

public interface Scoreboard {

    public Match updateMatch(Match match);

    public Match startMatch(Team homeTeam, Team awayTeam);

    public boolean finishMatch(Match match);

    public List<Match> getContent();

    public void cleanUp();
}
