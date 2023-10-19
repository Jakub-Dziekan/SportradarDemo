package com.jd.sportradar.demo;

import java.util.*;
import java.util.function.Function;

import static com.jd.sportradar.demo.Match.createMatch;

public final class ScoreboardImpl implements Scoreboard {

  private Set<Match> scoreboardContent;
  private List<Match> sortedOutput;
  private final Comparator<Match> uniquenessComparator =
      Comparator.comparing(Match::getHomeTeam).thenComparing(Match::getAwayTeam);

  private final Comparator<Match> outputComparator =
      Comparator.comparing(Match::getScoreSum)
          .thenComparing(Match::getArrivalIndex)
          .reversed()
          .thenComparing(Match::getHomeTeam)
          .thenComparing(Match::getAwayTeam);

  public ScoreboardImpl() {
    scoreboardContent = new TreeSet<>(uniquenessComparator);
    sortedOutput = new ArrayList<>();
  }

  // We're going to look for any instance of the match (could be a different one with the same
  // teams!) remove it and then add the passed instance.
  // It is technically possible (even though unlikely) that the new instance will be exactly the
  // same as the original one
  @Override
  public Match updateMatch(Match match) {
    if (findAndRemove(match, false)) {
      return updateScoreboard(match, true, m -> scoreboardContent.add(m)) ? match : null;
    } else {
      return null;
    }
  }

  @Override
  public Match startMatch(Team homeTeam, Team awayTeam) {
    if (homeTeam == null || awayTeam == null) return null;
    Match match = createMatch(homeTeam, awayTeam);
    return updateScoreboard(match, true, m -> scoreboardContent.add(m)) ? match : null;
  }

  private boolean findAndRemove(Match match, Boolean shouldUpdateOutput) {
    if (null == match) return false;
    return updateScoreboard(match, shouldUpdateOutput, m -> scoreboardContent.remove(m));
  }

  @Override
  public boolean finishMatch(Match match) {
    return findAndRemove(match, true);
  }

  @Override
  public List<Match> getContent() {
    return sortedOutput;
  }

  @Override
  public void cleanUp() {
    scoreboardContent = new TreeSet<>(uniquenessComparator);
    sortedOutput = new ArrayList<>();
  }

  private void updateOutput() {
    sortedOutput = scoreboardContent.stream().sorted(outputComparator).toList();
  }

  private boolean updateScoreboard(
      Match match, Boolean shouldUpdateOutput, Function<Match, Boolean> operation) {
    if (operation.apply(match)) {
      if (shouldUpdateOutput) updateOutput();
      return true;
    } else {
      return false;
    }
  }
}
