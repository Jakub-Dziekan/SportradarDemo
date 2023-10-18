package com.jd.sportradar.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreboardTest {

  private Scoreboard scoreboard;

  @BeforeEach
  void init() {
    scoreboard = new Scoreboard();
  }

  @Test
  public void should_ReturnEmptyScoreboard_When_NoMatchAdded_Test() {
    List<Match> scoreboardContent = scoreboard.getContent();
    assertNotNull(scoreboardContent, "The scoreboard was null");
    assertTrue(scoreboardContent.isEmpty(), "The scoreboard is not empty");
  }

  @Test
  public void should_ReturnEmptyScoreboard_When_FinishingAMatchAtEmptyScoreboard_Test() {
    List<Match> scoreboardContent = scoreboard.getContent();
    // theoretically there's access to Match creator method, but we're assuming it to be an advanced
    // tool,
    // Match is created on a scoreboard and then removed from a different, empty one
    Match match = scoreboard.startMatch(Team.ITALY_NATIONAL, Team.SPAIN_NATIONAL);
    assertNotNull(match, "Match has not been created");
    scoreboard.cleanUp();
    assertFalse(scoreboard.finishMatch(match));
    assertNotNull(scoreboardContent, "The scoreboard was null");
    assertTrue(scoreboardContent.isEmpty(), "The scoreboard is not empty");
  }

  @Test
  public void should_ReturnIdenticalScoreboard_When_AlreadyExistingMatchAdded_Test() {
    scoreboardProvider();
    List<Match> preScoreboardContent = scoreboard.getContent();
    Match match = scoreboard.startMatch(Team.SPAIN_NATIONAL, Team.BRAZIL_NATIONAL);
    assertNull(match, "Should not return an object. Duplicated match might have been created");
    List<Match> scoreboardContent = scoreboard.getContent();
    assertNotNull(scoreboardContent, "The scoreboard was null");
    assertTrue(scoreboardContent.isEmpty(), "The scoreboard is not empty");
    assertEquals(preScoreboardContent, scoreboardContent);
  }

  @Test
  public void should_ReturnIdenticalScoreboard_When_AlreadyFinishedMatchRemoved_Test() {
    scoreboardProvider();
    List<Match> preScoreboardContent = scoreboard.getContent();
    List<Match> extendedScoreboardContent;
    List<Match> scoreboardContent;

    // adding a new match
    Match match = scoreboard.startMatch(Team.POLAND_NATIONAL, Team.SLOVENIA_NATIONAL);
    assertNotNull(match, "Match has not been created");

    // saving the content including the new match
    extendedScoreboardContent = scoreboard.getContent();
    assertNotNull(extendedScoreboardContent, "The scoreboard was null");
    assertEquals(preScoreboardContent.size() + 1, extendedScoreboardContent.size());

    // finishing the added match, the scoreboard should be back to the original state
    assertTrue(scoreboard.finishMatch(match));
    scoreboardContent = scoreboard.getContent();
    assertNotNull(scoreboardContent, "The scoreboard was null");
    assertEquals(preScoreboardContent, scoreboardContent);

    // trying to finish the same match again, nothing should change
    assertFalse(scoreboard.finishMatch(match));
    scoreboardContent = scoreboard.getContent();
    assertNotNull(scoreboardContent, "The scoreboard was null");
    assertEquals(preScoreboardContent, scoreboardContent);

    // and to double check that the extended scoreboard is still not equal to the final one
    assertNotEquals(extendedScoreboardContent, scoreboardContent);
  }

  @ParameterizedTest(name = "{index} => homeTeam=''{0}'', awayTeam=''{1}''")
  @MethodSource("teamsProvider")
  public void should_ReturnANewMatch_When_AddingAMatchToEmptyScoreboard_Test(
      String homeTeam, String awayTeam) {
    Match match = scoreboard.startMatch(Team.valueOf(homeTeam), Team.valueOf(awayTeam));
    assertNotNull(match);
    assertEquals(0, match.getScore().getHomeScore());
    assertEquals(0, match.getScore().getAwayScore());
    assertEquals(homeTeam, match.getHomeTeam().toString());
    assertEquals(awayTeam, match.getAwayTeam().toString());
  }

  @ParameterizedTest(name = "{index} => homeTeam=''{0}'', awayTeam=''{1}''")
  @MethodSource("teamsProvider")
  public void should_ReturnAnUpdatedScoreboard_When_MatchIsAdded_Test(
      String homeTeam, String awayTeam) {
    scoreboardProvider();
    List<Match> preScoreboardContent = scoreboard.getContent();
    List<Match> extendedScoreboardContent;

    // adding a new match
    Match match = scoreboard.startMatch(Team.valueOf(homeTeam), Team.valueOf(awayTeam));
    assertNotNull(match, "Match has not been created");

    // saving the content including the new match
    extendedScoreboardContent = scoreboard.getContent();
    assertNotNull(extendedScoreboardContent, "The scoreboard was null");
    assertEquals(preScoreboardContent.size() + 1, extendedScoreboardContent.size());

    assertNotNull(match);
    assertEquals(0, match.getScore().getHomeScore());
    assertEquals(0, match.getScore().getAwayScore());
    assertEquals(homeTeam, match.getHomeTeam().toString());
    assertEquals(awayTeam, match.getAwayTeam().toString());

    // the newly added match will always be last (0 goals and latest), so we'll check the last
    // element
    // index equals the original size
    assertEquals(
        extendedScoreboardContent.indexOf(match),
        preScoreboardContent.size(),
        "The last element is not the inserted element");
  }

  @Test
  public void should_ReturnAnUpdatedScoreboard_When_MatchIsFinished_Test() {
    scoreboardProvider();
    List<Match> preScoreboardContent = scoreboard.getContent();
    List<Match> shrunkScoreboardContent;

    // finishing an existing match, it should be the MEXICO match
    assertTrue(scoreboard.finishMatch(scoreboard.getContent().get(0)));

    // saving the content including the new match
    shrunkScoreboardContent = scoreboard.getContent();
    assertNotNull(shrunkScoreboardContent, "The scoreboard was null");
    assertEquals(preScoreboardContent.size() - 1, shrunkScoreboardContent.size());
    assertEquals(
        0,
        shrunkScoreboardContent.stream()
            .filter(elem -> elem.getHomeTeam().equals(Team.MEXICO_NATIONAL))
            .count());
  }

  @Test
  public void should_ReturnAnUpdatedScoreboard_When_MatchIsUpdated_Test() {
    scoreboardProvider();
    List<Match> preScoreboardContent = scoreboard.getContent();
    List<Match> updatedScoreboardContent;

    // element of index 5 should be the Argentina-Australia match
    Match match = preScoreboardContent.get(4);
    assertEquals(match.getHomeTeam(), Team.ARGENTINA_NATIONAL);
    assertEquals(match.getAwayTeam(), Team.AUSTRALIA_NATIONAL);
    Match updatedMatch = scoreboard.updateMatch(match.updateScore(3, 1).orElse(mockMatchProvider()));
    assertNotNull(updatedMatch);
    assertEquals(match.getHomeTeam(), updatedMatch.getHomeTeam());
    assertEquals(match.getAwayTeam(), updatedMatch.getAwayTeam());
    assertEquals(0, match.getScore().getHomeScore());
    assertEquals(0, match.getScore().getAwayScore());
    assertEquals(3, updatedMatch.getScore().getHomeScore());
    assertEquals(1, updatedMatch.getScore().getAwayScore());

    // the update should move the position of the match in the scoreboard to the top (index 0)
    // without changing its size
    updatedScoreboardContent = scoreboard.getContent();
    assertNotNull(updatedScoreboardContent, "The scoreboard was null");
    assertEquals(preScoreboardContent.size(), updatedScoreboardContent.size());
    updatedMatch = updatedScoreboardContent.get(0);
    assertEquals(match.getHomeTeam(), updatedMatch.getHomeTeam());
    assertEquals(match.getAwayTeam(), updatedMatch.getAwayTeam());
    assertEquals(3, updatedMatch.getScore().getHomeScore());
    assertEquals(1, updatedMatch.getScore().getAwayScore());

    // and let's quickly check if the other elements didn't fall apart
    assertEquals(Team.MEXICO_NATIONAL, updatedScoreboardContent.get(1).getHomeTeam());
    assertEquals(Team.SPAIN_NATIONAL, updatedScoreboardContent.get(2).getHomeTeam());
    assertEquals(Team.GERMANY_NATIONAL, updatedScoreboardContent.get(3).getHomeTeam());
    assertEquals(Team.URUGUAY_NATIONAL, updatedScoreboardContent.get(4).getHomeTeam());
  }

  // a similar case to the above ones, but everything combined
  // (we've already proven above that it works separately, so we'll only check the result)
  @Test
  public void should_ReturnAnUpdatedScoreboard_When_MatchesAreAddedUpdatedAndFinished_Test() {
    scoreboardProvider();
    List<Match> preScoreboardContent = scoreboard.getContent();
    List<Match> updatedScoreboardContent;

    Match argentinaMatch = preScoreboardContent.get(4);
    Match mexicoMatch = preScoreboardContent.get(0);
    Match spainMatch = preScoreboardContent.get(1);
    Match germanyMatch = preScoreboardContent.get(2);
    Match uruguayMatch = preScoreboardContent.get(3);

    argentinaMatch = scoreboard.updateMatch(argentinaMatch.updateScore(3, 1).orElse(mockMatchProvider()));
    mexicoMatch = scoreboard.updateMatch(mexicoMatch.updateScore(0, 5).orElse(mockMatchProvider()));
    spainMatch = scoreboard.updateMatch(spainMatch.updateScore(10, 2).orElse(mockMatchProvider()));
    germanyMatch = scoreboard.updateMatch(germanyMatch.updateScore(2, 2).orElse(mockMatchProvider()));
    uruguayMatch = scoreboard.updateMatch(uruguayMatch.updateScore(6, 6).orElse(mockMatchProvider()));

    updatedScoreboardContent = scoreboard.getContent();
    assertNotNull(updatedScoreboardContent, "The scoreboard was null");
    assertEquals(preScoreboardContent.size(), updatedScoreboardContent.size());

    // let's quickly check if the elements are in order after assigning the scores
    assertEquals(Team.URUGUAY_NATIONAL, updatedScoreboardContent.get(0).getHomeTeam());
    assertEquals(6, updatedScoreboardContent.get(0).getScore().getAwayScore());

    assertEquals(Team.SPAIN_NATIONAL, updatedScoreboardContent.get(1).getHomeTeam());
    assertEquals(2, updatedScoreboardContent.get(1).getScore().getAwayScore());

    assertEquals(Team.MEXICO_NATIONAL, updatedScoreboardContent.get(2).getHomeTeam());
    assertEquals(5, updatedScoreboardContent.get(2).getScore().getAwayScore());

    assertEquals(Team.ARGENTINA_NATIONAL, updatedScoreboardContent.get(3).getHomeTeam());
    assertEquals(1, updatedScoreboardContent.get(3).getScore().getAwayScore());

    assertEquals(Team.GERMANY_NATIONAL, updatedScoreboardContent.get(4).getHomeTeam());
    assertEquals(2, updatedScoreboardContent.get(4).getScore().getAwayScore());

    // let's now finish two games and add two more, the USA-CMR one will have equal goals count as MEX-CAN
    assertTrue(scoreboard.finishMatch(argentinaMatch));
    assertTrue(scoreboard.finishMatch(spainMatch));
    Match newMatch = scoreboard.startMatch(Team.POLAND_NATIONAL, Team.SLOVENIA_NATIONAL);
    assertNotNull(newMatch);
    scoreboard.updateMatch(newMatch.updateScore(7, 6).orElse(mockMatchProvider()));
    newMatch = scoreboard.startMatch(Team.CAMEROON_NATIONAL, Team.USA_NATIONAL);
    assertNotNull(newMatch);
    scoreboard.updateMatch(newMatch.updateScore(3, 2).orElse(mockMatchProvider()));

    // since the new match is now the highest scoring game, it should be first
    // expected order: POL-SLO 7:6; URU-ITA 6:6; MEX-CAN 0:5; CMR-USA 3:2; GER-FRA 2:2;
    updatedScoreboardContent = scoreboard.getContent();
    assertEquals(5, updatedScoreboardContent.size());
    assertEquals(Team.POLAND_NATIONAL, updatedScoreboardContent.get(0).getHomeTeam());
    assertEquals(6, updatedScoreboardContent.get(0).getScore().getAwayScore());
    assertEquals(Team.CAMEROON_NATIONAL, updatedScoreboardContent.get(3).getHomeTeam());
    assertEquals(2, updatedScoreboardContent.get(3).getScore().getAwayScore());
  }

  // A couple of teams to repetitively test uniqueness assumptions (see documentation)
  static Stream<Arguments> teamsProvider() {
    return Stream.of(
        Arguments.of("ITALY_NATIONAL", "SPAIN_NATIONAL"),
        Arguments.of("SLOVENIA_NATIONAL", "POLAND_NATIONAL"),
        Arguments.of("CANADA_NATIONAL", "MEXICO_NATIONAL"));
  }

  public Match mockMatchProvider() {
    return null;
  }

  public void scoreboardProvider() {
    Team homeTeam = Team.MEXICO_NATIONAL;
    Team awayTeam = Team.CANADA_NATIONAL;
    scoreboard.startMatch(homeTeam, awayTeam);
    // adding the same on purpose to make sure it won't be duplicated
    homeTeam = Team.MEXICO_NATIONAL;
    awayTeam = Team.CANADA_NATIONAL;
    scoreboard.startMatch(homeTeam, awayTeam);
    homeTeam = Team.SPAIN_NATIONAL;
    awayTeam = Team.BRAZIL_NATIONAL;
    scoreboard.startMatch(homeTeam, awayTeam);
    homeTeam = Team.GERMANY_NATIONAL;
    awayTeam = Team.FRANCE_NATIONAL;
    scoreboard.startMatch(homeTeam, awayTeam);
    homeTeam = Team.URUGUAY_NATIONAL;
    awayTeam = Team.ITALY_NATIONAL;
    scoreboard.startMatch(homeTeam, awayTeam);
    homeTeam = Team.ARGENTINA_NATIONAL;
    awayTeam = Team.AUSTRALIA_NATIONAL;
    scoreboard.startMatch(homeTeam, awayTeam);
    assertEquals(5, scoreboard.getContent().size(), "The scoreboard has unexpected size");
  }
}
