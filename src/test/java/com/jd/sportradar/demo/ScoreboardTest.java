package com.jd.sportradar.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreboardTest {

  private Scoreboard scoreboard;

  Logger log = Logger.getLogger(ScoreboardTest.class.getName());

  @BeforeEach
  void init() {
    scoreboard = new ScoreboardImpl();
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
    // tool, match is created on a scoreboard and then removed from a different, empty one
    Match match = scoreboard.startMatch(Team.ITALY_NATIONAL, Team.SPAIN_NATIONAL);
    assertNotNull(match, "Match has not been created");
    scoreboard.cleanUp();
    assertFalse(scoreboard.finishMatch(match));
    assertNotNull(scoreboardContent, "The scoreboard was null");
    assertTrue(scoreboardContent.isEmpty(), "The scoreboard is not empty");
  }

  @Test
  public void should_ReturnIdenticalScoreboard_When_AlreadyExistingMatchAdded_Test() {
    List<Match> preScoreboardContent = scoreboardProvider();
    Match match = scoreboard.startMatch(Team.SPAIN_NATIONAL, Team.BRAZIL_NATIONAL);
    assertNull(match, "Should not return an object. Duplicated match might have been created");
    List<Match> scoreboardContent = scoreboard.getContent();
    assertNotNull(scoreboardContent, "The scoreboard was null");
    assertFalse(scoreboardContent.isEmpty(), "The scoreboard is not empty");
    assertEquals(preScoreboardContent, scoreboardContent);
  }

  @Test
  public void should_ReturnIdenticalScoreboard_When_NullMatchRemoved_Test() {
    List<Match> preScoreboardContent = scoreboardProvider();
    List<Match> scoreboardContent;

    assertFalse(scoreboard.finishMatch(null));
    scoreboardContent = scoreboard.getContent();
    assertEquals(preScoreboardContent, scoreboardContent);
  }

  @Test
  public void should_ReturnIdenticalScoreboard_When_NullMatchUpdated_Test() {
    List<Match> preScoreboardContent = scoreboardProvider();
    List<Match> scoreboardContent;

    assertNull(scoreboard.updateMatch(null));
    scoreboardContent = scoreboard.getContent();
    assertEquals(preScoreboardContent, scoreboardContent);
  }

  @Test
  public void should_ReturnIdenticalScoreboard_When_MatchWithNullTeamsStarted_Test() {
    List<Match> preScoreboardContent = scoreboardProvider();
    List<Match> scoreboardContent;

    assertNull(scoreboard.startMatch(null, Team.ARGENTINA_NATIONAL));
    assertNull(scoreboard.startMatch(null, null));
    assertNull(scoreboard.startMatch(Team.URUGUAY_NATIONAL, null));
    scoreboardContent = scoreboard.getContent();
    assertEquals(preScoreboardContent, scoreboardContent);
  }

  @Test
  public void should_ReturnIdenticalScoreboard_When_MatchWithNullTeamsUpdated_Test() {
    List<Match> preScoreboardContent = scoreboardProvider();
    List<Match> scoreboardContent;

    assertNull(scoreboard.updateMatch(Match.createMatch(null, Team.ARGENTINA_NATIONAL)));
    assertNull(scoreboard.updateMatch(Match.createMatch(null, null)));
    assertNull(scoreboard.updateMatch(Match.createMatch(Team.URUGUAY_NATIONAL, null)));
    assertNull(scoreboard.updateMatch(Match.createMatch(Team.URUGUAY_NATIONAL, null)));
    scoreboardContent = scoreboard.getContent();
    assertEquals(preScoreboardContent, scoreboardContent);
  }

  @Test
  public void should_ReturnIdenticalScoreboard_When_UpdatingNonExistingMatch_Test() {
    List<Match> preScoreboardContent = scoreboardProvider();
    List<Match> scoreboardContent;

    Match match = Match.createMatch(Team.CAMEROON_NATIONAL, Team.USA_NATIONAL);

    assertNull(scoreboard.updateMatch(match));
    scoreboardContent = scoreboard.getContent();
    assertEquals(preScoreboardContent, scoreboardContent);
  }

  @Test
  public void should_ReturnIdenticalScoreboard_When_AlreadyFinishedMatchRemoved_Test() {
    List<Match> preScoreboardContent = scoreboardProvider();
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
    List<Match> preScoreboardContent = scoreboardProvider();
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

    // the newly added match will always be first (0 goals and most recent), so we'll check the
    // first element
    assertEquals(
        extendedScoreboardContent.indexOf(match),
        0,
        "The last element is not the inserted element");
  }

  @Test
  public void should_ReturnAnUpdatedScoreboard_When_MatchIsFinished_Test() {
    List<Match> preScoreboardContent = scoreboardProvider();
    List<Match> shrunkScoreboardContent;

    // finishing an existing match, it should be the ARGENTINA match
    assertTrue(scoreboard.finishMatch(scoreboard.getContent().get(0)));

    // saving the content including the new match
    shrunkScoreboardContent = scoreboard.getContent();
    assertNotNull(shrunkScoreboardContent, "The scoreboard was null");
    assertEquals(preScoreboardContent.size() - 1, shrunkScoreboardContent.size());
    assertEquals(
        0,
        shrunkScoreboardContent.stream()
            .filter(elem -> elem.getHomeTeam().equals(Team.ARGENTINA_NATIONAL))
            .count());
  }

  @Test
  public void should_ReturnAnUpdatedScoreboard_When_MatchIsUpdated_Test() {
    List<Match> preScoreboardContent = scoreboardProvider();
    List<Match> updatedScoreboardContent;

    // element of index 4 should be the Mexico-Canada match, as it was added first and all scores
    // are 0:0
    Match match = preScoreboardContent.get(4);
    assertEquals(Team.MEXICO_NATIONAL, match.getHomeTeam());
    assertEquals(Team.CANADA_NATIONAL, match.getAwayTeam());
    Match updatedMatch =
        scoreboard.updateMatch(Optional.of(match.updateScore(3, 1)).orElse(mockMatchProvider()));
    assertNotNull(updatedMatch);
    assertEquals(match.getHomeTeam(), updatedMatch.getHomeTeam());
    assertEquals(match.getAwayTeam(), updatedMatch.getAwayTeam());
    assertEquals(0, match.getScore().getHomeScore());
    assertEquals(0, match.getScore().getAwayScore());
    assertEquals(3, updatedMatch.getScore().getHomeScore());
    assertEquals(1, updatedMatch.getScore().getAwayScore());

    // the update should move the position of the match in the scoreboard to the top (index 0)
    // without changing the size of the scoreboard
    updatedScoreboardContent = scoreboard.getContent();
    assertNotNull(updatedScoreboardContent, "The scoreboard was null");
    assertEquals(preScoreboardContent.size(), updatedScoreboardContent.size());
    updatedMatch = updatedScoreboardContent.get(0);
    assertEquals(match.getHomeTeam(), updatedMatch.getHomeTeam());
    assertEquals(match.getAwayTeam(), updatedMatch.getAwayTeam());
    assertEquals(3, updatedMatch.getScore().getHomeScore());
    assertEquals(1, updatedMatch.getScore().getAwayScore());

    // and let's quickly check if the other elements didn't fall apart
    assertEquals(Team.ARGENTINA_NATIONAL, updatedScoreboardContent.get(1).getHomeTeam());
    assertEquals(Team.URUGUAY_NATIONAL, updatedScoreboardContent.get(2).getHomeTeam());
    assertEquals(Team.GERMANY_NATIONAL, updatedScoreboardContent.get(3).getHomeTeam());
    assertEquals(Team.SPAIN_NATIONAL, updatedScoreboardContent.get(4).getHomeTeam());

    logScoreboard(getCallerMethodName());
  }

  // a similar case to the above UPDATE one, but more complex
  // (we've already proven above that it works separately, so we'll only check the result)
  @Test
  public void should_ReturnAnUpdatedScoreboard_When_MatchesAreUpdated_Test() {
    List<Match> preScoreboardContent = scoreboardProvider();
    List<Match> updatedScoreboardContent;

    // updating scores
    updatedScoreboardContent = scoreboardWithUpdatesProvider(preScoreboardContent);
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

    logScoreboard(getCallerMethodName());
  }

  // a similar case to the above ones, but everything combined
  // (we've already proven above that it works separately, so we'll only check the result)
  @Test
  public void should_ReturnAnUpdatedScoreboard_When_MatchesAreAddedUpdatedAndFinished_Test() {
    // adding matches and updating scores as above
    List<Match> updatedScoreboardContent = scoreboardWithUpdatesProvider(scoreboardProvider());

    // let's now finish two games and add two more, the USA-CMR one will have equal goals count as
    // MEX-CAN
    Match argentinaMatch = updatedScoreboardContent.get(3);
    Match spainMatch = updatedScoreboardContent.get(1);
    assertTrue(scoreboard.finishMatch(argentinaMatch));
    assertTrue(scoreboard.finishMatch(spainMatch));
    Match newMatch = scoreboard.startMatch(Team.POLAND_NATIONAL, Team.SLOVENIA_NATIONAL);
    assertNotNull(newMatch);
    scoreboard.updateMatch(Optional.of(newMatch.updateScore(7, 6)).orElse(mockMatchProvider()));
    newMatch = scoreboard.startMatch(Team.CAMEROON_NATIONAL, Team.USA_NATIONAL);
    assertNotNull(newMatch);
    scoreboard.updateMatch(Optional.of(newMatch.updateScore(3, 2)).orElse(mockMatchProvider()));

    // since the new match is now the highest scoring game, it should be first
    // expected order: POL-SLO 7:6; URU-ITA 6:6; CMR-USA 3:2; MEX-CAN 0:5; GER-FRA 2:2;
    updatedScoreboardContent = scoreboard.getContent();
    assertEquals(5, updatedScoreboardContent.size());
    assertEquals(Team.POLAND_NATIONAL, updatedScoreboardContent.get(0).getHomeTeam());
    assertEquals(6, updatedScoreboardContent.get(0).getScore().getAwayScore());
    assertEquals(Team.CAMEROON_NATIONAL, updatedScoreboardContent.get(2).getHomeTeam());
    assertEquals(2, updatedScoreboardContent.get(2).getScore().getAwayScore());

    logScoreboard(getCallerMethodName());
  }

  // A couple of teams to repetitively test uniqueness assumptions (see documentation)
  static Stream<Arguments> teamsProvider() {
    return Stream.of(
        Arguments.of("ITALY_NATIONAL", "SPAIN_NATIONAL"),
        Arguments.of("SLOVENIA_NATIONAL", "POLAND_NATIONAL"),
        Arguments.of("CANADA_NATIONAL", "MEXICO_NATIONAL"));
  }

  private Match mockMatchProvider() {
    return null;
  }

  private List<Match> scoreboardProvider() {
    scoreboard.startMatch(Team.MEXICO_NATIONAL, Team.CANADA_NATIONAL);
    // adding the same on purpose to make sure it won't be duplicated
    scoreboard.startMatch(Team.MEXICO_NATIONAL, Team.CANADA_NATIONAL);
    scoreboard.startMatch(Team.SPAIN_NATIONAL, Team.BRAZIL_NATIONAL);
    scoreboard.startMatch(Team.GERMANY_NATIONAL, Team.FRANCE_NATIONAL);
    scoreboard.startMatch(Team.URUGUAY_NATIONAL, Team.ITALY_NATIONAL);
    scoreboard.startMatch(Team.ARGENTINA_NATIONAL, Team.AUSTRALIA_NATIONAL);
    assertEquals(5, scoreboard.getContent().size(), "The scoreboard has unexpected size");
    return scoreboard.getContent();
  }

  private List<Match> scoreboardWithUpdatesProvider(List<Match> preScoreboardContent) {
    Match argentinaMatch =
        getSpecificMatch(Team.ARGENTINA_NATIONAL, Team.AUSTRALIA_NATIONAL, preScoreboardContent)
            .orElse(mockMatchProvider());
    Match uruguayMatch =
        getSpecificMatch(Team.URUGUAY_NATIONAL, Team.ITALY_NATIONAL, preScoreboardContent)
            .orElse(mockMatchProvider());
    Match germanyMatch =
        getSpecificMatch(Team.GERMANY_NATIONAL, Team.FRANCE_NATIONAL, preScoreboardContent)
            .orElse(mockMatchProvider());
    Match spainMatch =
        getSpecificMatch(Team.SPAIN_NATIONAL, Team.BRAZIL_NATIONAL, preScoreboardContent)
            .orElse(mockMatchProvider());
    Match mexicoMatch =
        getSpecificMatch(Team.MEXICO_NATIONAL, Team.CANADA_NATIONAL, preScoreboardContent)
            .orElse(mockMatchProvider());

    assertNotNull(argentinaMatch);
    assertNotNull(uruguayMatch);
    assertNotNull(germanyMatch);
    assertNotNull(spainMatch);
    assertNotNull(mexicoMatch);

    // updates will be shuffled a bit to test if time of update affects the result
    scoreboard.updateMatch(Optional.of(germanyMatch.updateScore(2, 2)).orElse(mockMatchProvider()));
    scoreboard.updateMatch(Optional.of(argentinaMatch.updateScore(3, 1)).orElse(mockMatchProvider()));
    scoreboard.updateMatch(Optional.of(mexicoMatch.updateScore(0, 5)).orElse(mockMatchProvider()));
    scoreboard.updateMatch(Optional.of(uruguayMatch.updateScore(6, 6)).orElse(mockMatchProvider()));
    scoreboard.updateMatch(Optional.of(spainMatch.updateScore(10, 2)).orElse(mockMatchProvider()));
    return scoreboard.getContent();
  }

  private Optional<Match> getSpecificMatch(
      Team homeTeam, Team awayTeam, List<Match> scoreboardContent) {
    return scoreboardContent.stream()
        .filter(m -> m.getHomeTeam().equals(homeTeam) && m.getAwayTeam().equals(awayTeam))
        .findFirst();
  }

  private void logScoreboard(String caller) {
    List<Match> data = scoreboard.getContent();
    log.info(String.format("=============================\n%s\n", caller));
    data.forEach(
        m ->
            log.info(
                String.format(
                    "Match: %s-%s (%d:%d) started at %s",
                    m.getHomeTeam(),
                    m.getAwayTeam(),
                    m.getScore().getHomeScore(),
                    m.getScore().getAwayScore(),
                    m.getArrivalIndex().toString())));
    log.info("=============================");
  }

  private String getCallerMethodName() {
    return StackWalker.getInstance()
        .walk(stream -> stream.skip(1).findFirst().get())
        .getMethodName();
  }
}
