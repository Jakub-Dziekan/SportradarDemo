package com.jd.sportradar.demo;

import java.util.*;

import static com.jd.sportradar.demo.Match.createMatch;

public final class Scoreboard {

  Set<Match> scoreboardContent;
  Comparator<Match> comparator =
      Comparator.comparing(Match::getHomeTeam).thenComparing(Match::getAwayTeam);

  public Scoreboard() {
    scoreboardContent = new TreeSet<>(comparator);
  }

  public Match updateMatch(Match match) {
    if (findAndRemove(match)) {
      Match newMatch = Match.createMatchFrom(match);
      scoreboardContent.add(newMatch);
      return newMatch;
    } else {
      return null;
    }
  }

  public Match startMatch(Team homeTeam, Team awayTeam) {
    if (homeTeam == null || awayTeam == null) return null;
    Match match = createMatch(homeTeam, awayTeam);
    return scoreboardContent.add(match) ? match : null;
  }

  private boolean findAndRemove(Match match) {
    if (null == match) return false;
    return scoreboardContent.remove(match);
  }

  public boolean finishMatch(Match match) {
    return findAndRemove(match);
  }

  public List<Match> getContent() {
    return scoreboardContent.stream()
        .sorted(
            Comparator.comparing(Match::getScoreSum)
                .thenComparing(Match::getArrivalIndex)
                .reversed()
                .thenComparing(Match::getHomeTeam)
                .thenComparing(Match::getAwayTeam))
        .toList();
  }

  public void cleanUp() {
    scoreboardContent = new TreeSet<>(comparator);
  }
}
