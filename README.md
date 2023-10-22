# SportradarDemo

## Assumptions
For lack of detailed requirements, a few general assumptions have been made.

### [A-1] Immutable structures
Most of the classes have been made Immutable, as it is expected, that they would be used in a multithreaded environment. For example, there's no way to edit the score of the match (and thus, brake it accidentally). We can only create a new match with a new score and insert it into the scoreboard. This assumption has caused some additional overhead but is seen as generally reasonable.

### [A-2] Match uniqueness
It is assumed that adding another match with identical home/away setup is an error and is ignored by the library. However, to simplify a little bit, it is not an error to add a new match with swapped home/away teams or a match with team already present in another match. It is assumed that such a functionality might be required to fix a bug in a set up.

### [A-3] Non multithreaded environment
Despite [A-1] since the solution was supposed to be simple, the concurrent consumer threads are excluded from it and apart from partial immutability it is not supported. There would be much more to test if we'd want to check all the possible race conditions.

### [A-4] Updating a match
It is also assumed, that updating a match doesn't mean that it is currently starting (the initial value of arrivalIndex is being copied). While a match is being copied when updated (by [A-1]), the arrivalIndex or "start time" is being transferred from the original one (see: updateMatch method)

### [A-5] Time of Arrival 
It is also assumed, that the time of start is a number. It could be a timestamp as well, but it seemed unnecessary, plus it would add a same-time arrival problem. Given that multithreading problem is excluded [A-3], the static sequence should be enough.

### [A-6] Data structures
It is also assumed, that the library would (theoretically) be used in a write-rarely read-often environment, so it is beneficial to sort the scoreboard directly after each change, as the data structure updates are not as time-constrained and would only happen occasionally. The go-to data structure would be a self-balancing tree - although since the size of the scoreboard wouldn't probably be substantial, any simple, ordered structure would be enough. Unfortunately this approach clashes a bit with the [A-2] assumption (ordering uses completely different fields than the uniqueness check), making the whole thing a bit convoluted, so while I had it working, in the end the data structure has been simplified for clarity.
This is why there's a tree (for uniqueness) and a list (for sorting). Two trees would probably be even more fun. One custom tree, overriding the Set methods and handling both even more than that, but we said that it should be kept simple.

### [A-7] Double sorting
<s>The output structure is being sorted twice in updateMatch - after Match removal and after adding the new one (Match is Immutable, so there's no real "update" of a MAtch). I have left it to keep the code easier to read. It could be removed though.</S> This has actually been adressed in one of the previous commits but stayed in the doc

### [A-8] Model assumptions
It didn't seem reasonable to implement the solution as an interface without the knowledge of how it would be used (the Scoreboard seems more of a furniture for the decorator pattern and as such should probably be a class). Despite that I have provided a version with an interface contract. At least it makes the solutions more readable.

### [A-9] Documentation
Methods documentation has been mostly ommited for time constraints (and since it's just a fake library - there's no added value in investing time in it), but it is generally regarded as necessary in real projects.

## The Contract

<b> arrivalIndex - "time of arrival" abstraction </b>

### Match updateMatch(Match match);
Updates a Match with a new version. First we're looking for an existing instance of a match (an instance is an object with identical home and away teams), we remove it and then the new one is added. In very specific case it could be exactly the same as the removed one (reference-wise), but it's an unlikely use case. Any update of a Match creates a new instance, so it would be an update of nothing.
<b>NOTE: The update keeps the original arrivalIndex of the object </b>

This operation sorts the output structure.

### Match startMatch(Team homeTeam, Team awayTeam);
Starts a match, creating it in the process and assigning the arrivalIndex. The score is set to 0:0.

This operation sorts the output structure.

### boolean finishMatch(Match match);
Finishes a match and removes it from the scoreboard.
<b> NOTE: again, any search against the scoreboard is evaluating home and away teams. To finish a match we don't need an exact reference. </b>

This operation sorts the output structure.

### List<Match> getContent();
Returns a sorted output structure (a list of sorted matches).

### void cleanUp();
Clears the internal structure making the Scoreboard empty. It DOES NOT reset the arrivalIndex.
