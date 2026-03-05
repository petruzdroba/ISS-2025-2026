# Multi-Pitch Assistant

## Use Case Specifications

| | |
|---|---|
| **UC-01** | Start Climbing Session |
| **UC-02** | End Climbing Session |
| **UC-03** | Monitor Altitude in Real-Time |
| **UC-04** | Detect Fall |
| **UC-05** | Detect Pitch Change |
| **UC-06** | Detect Rest or Retreat |
| **UC-07** | Add Manual Note |
| **UC-08** | View Session History |
| **UC-09** | View Session Details |
| **UC-10** | Edit Event Information |
| **UC-11** | Delete Event |
| **UC-12** | Add Event to Session |

---

## UC-01 – Start Climbing Session

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | The climber initiates a new climbing session, enabling the system to begin tracking altitude changes and recording events. |
| **Trigger** | The climber taps the 'Start Climbing' button on the home screen. |
| **Preconditions** | PRE-01.1 The app is installed and running on the device.<br>PRE-01.2 No other climbing session is currently active. |
| **Postconditions** | POST-01.1 GPS location is saved, if GPS permissions are granted.<br>POST-01.2 A new session record is created, with a predefined first event 'session-started', using the saved location.<br>POST-01.3 Session is marked as 'active', ready to begin reading altitude.<br>POST-01.4 Altitude monitoring is running. |
| **Normal Flow** | 1. The climber opens the app.<br>2. The system displays the home screen with the 'Start Climbing' button.<br>3. The climber taps 'Start Climbing'.<br>4. GPS permission is asked.<br>5. The system creates a new session record with a unique ID and start timestamp.<br>6. The system begins monitoring altitude via the device barometer.<br>7. The system logs a 'session-started' event.<br>8. The system transitions to the active session screen showing a live altitude reading and an empty event feed. |
| **Alternative Flows** | ALT-01.A GPS is unavailable:<br>A.1 The system detects that GPS is not available.<br>A.2 The system proceeds without location data; all other steps continue normally from step 5.<br><br>ALT-01.B Barometer is unavailable:<br>B.1 The system does not detect a barometer sensor.<br>B.2 Automatic event detection is disabled.<br>B.3 The climber may choose to proceed with manual note-taking only, or cancel. |
| **Exceptions** | EXC-01.A GPS request throws an unexpected error:<br>A.1 The system catches the error and proceeds without location data.<br>A.2 The session continues normally from step 4 as if GPS was unavailable. |

---

## UC-02 – End Climbing Session

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | The climber ends an active climbing session and the system persists the complete session record. |
| **Trigger** | The climber taps the 'End Climbing' button on the active session screen. |
| **Preconditions** | PRE-02.1 A climbing session is currently active. |
| **Postconditions** | POST-02.1 A 'session-ended' event has been logged with the current timestamp and current altitude.<br>POST-02.2 Session is marked as inactive.<br>POST-02.3 Altitude monitoring is stopped.<br>POST-02.4 The session is visible in the session history. |
| **Normal Flow** | 1. The climber taps 'End Climbing' on the active session screen.<br>2. The climber is prompted for confirm.<br>3. The system logs a 'session-ended' event with the current altitude and timestamp.<br>4. Session is marked as inactive.<br>5. Altitude recording is stopped.<br>6. The system reverts to the home screen.<br>7. Session history is available in the 'Log' tab. |
| **Alternative Flows** | ALT-02.A App is closed or crashes while a session is active:<br>A.1 The system detects on next launch that an unfinished session exists.<br>A.2 The system adds an 'error' event in the Session.<br>A.3 The system automatically ends the session and saves it with the last recorded timestamp and altitude. |
| **Exceptions** | EXC-02.A Session record fails to save:<br>A.1 The system notifies the climber that the session could not be saved.<br>A.2 The climber may retry saving or discard the session. |

---

## UC-03 – Monitor Altitude in Real-Time

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | While a session is active, the system continuously reads barometric pressure, converts it to altitude, and displays the current altitude to the climber. Altitude deltas are fed to the automatic event classifier. |
| **Trigger** | A climbing session becomes active (UC-01 completes successfully). |
| **Preconditions** | PRE-03.1 A climbing session is active.<br>PRE-03.2 The device barometer sensor is available and permission has been granted. |
| **Postconditions** | POST-03.1 The current altitude is shown in real-time on the active session screen.<br>POST-03.2 Each significant altitude delta has been forwarded to the event classifier. |
| **Normal Flow** | 1. The system starts a polling loop on the device barometer at a fixed interval.<br>2. The system reads the current pressure and converts it to an altitude value.<br>3. The system calculates the delta between the current and previous altitude reading.<br>4. The system checks whether the delta exceeds the noise-filter threshold.<br>5. If the delta exceeds the threshold, the system forwards it to the event classifier (see UC-04, UC-05, UC-06).<br>6. The system updates the displayed altitude on the active session screen.<br>7. Steps 1–6 repeat until the session ends. |
| **Alternative Flows** | ALT-03.A Delta is below the noise-filter threshold:<br>A.1 The system discards the reading.<br>A.2 The displayed altitude is not updated; the loop continues from step 1. |
| **Exceptions** | EXC-03.A A barometer reading returns an error:<br>A.1 The system logs an 'error' event.<br>A.2 The system continues the polling loop; monitoring is not interrupted. |

---

## UC-04 – Detect Fall

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | The system automatically detects a fall by analysing a sudden negative altitude delta and records it as a 'fall' event in the active session. |
| **Trigger** | The event classifier receives a negative altitude delta that meets or exceeds the fall-detection threshold. |
| **Preconditions** | PRE-04.1 A climbing session is active.<br>PRE-04.2 Altitude monitoring (UC-03) is running. |
| **Postconditions** | POST-04.1 A 'fall' event has been logged with timestamp and altitude.<br>POST-04.2 The fall event is visible in the active session event feed. |
| **Normal Flow** | 1. The event classifier receives a negative altitude delta from UC-03.<br>2. The system detects if there is upward movement (altitude gain) in the reading.<br>3. The system evaluates the delta against the fall-detection threshold.<br>4. The delta meets or exceeds the threshold.<br>5. The system classifies the movement as a fall.<br>6. The system logs a 'fall' event with the current timestamp and altitude.<br>7. The system displays the fall event in the active session event feed. |
| **Alternative Flows** | None |
| **Exceptions** | EXC-04.A The delta is ambiguous (between fall and retreat thresholds):<br>A.1 The system applies additional checks (e.g. rate of change, duration, controlled descend).<br>A.2 If the movement is classified as a retreat, UC-06 handles logging instead. |

---

## UC-05 – Detect Pitch Change

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | The system automatically detects when the climber completes a pitch by identifying a significant sustained upward altitude change, and records a 'pitch-changed' event. |
| **Trigger** | The event classifier receives a positive altitude delta pattern consistent with completing a pitch. |
| **Preconditions** | PRE-05.1 A climbing session is active.<br>PRE-05.2 Altitude monitoring (UC-03) is running. |
| **Postconditions** | POST-05.1 A 'pitch-changed' event has been logged with timestamp and altitude.<br>POST-05.2 The 'pitch-changed' event is shown on the active session screen. |
| **Normal Flow** | 1. The event classifier receives a positive altitude delta from UC-03.<br>2. The system evaluates the altitude gain pattern over a time window.<br>3. The pattern is consistent with a completed pitch.<br>4. The system increments the session's pitch count.<br>5. The system logs a 'pitch-changed' event with the current timestamp and altitude.<br>6. The system updates the pitch count display on the active session screen. |
| **Alternative Flows** | ALT-05.A Upward movement is brief and does not cross the pitch threshold:<br>A.1 The classifier does not fire; no event is logged.<br>A.2 Monitoring continues normally. |
| **Exceptions** | None |

---

## UC-06 – Detect Rest or Retreat

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | The system automatically classifies prolonged low-movement or a sustained downward altitude trend as a rest or retreat respectively, and logs the appropriate event. |
| **Trigger** | The event classifier receives altitude deltas that, over a time window, meet the criteria for rest or retreat. |
| **Preconditions** | PRE-06.1 A climbing session is active.<br>PRE-06.2 Altitude monitoring (UC-03) is running. |
| **Postconditions** | POST-06.1 A 'rest' or 'retreat' event has been logged with timestamp and altitude.<br>POST-06.2 The event is visible in the active session event feed. |
| **Normal Flow** | 1. The event classifier accumulates altitude deltas over a rolling time window.<br>2. The system evaluates the overall movement pattern.<br>3a. If the cumulative altitude change is within the rest threshold for the required duration, the system classifies the period as a rest.<br>3b. If a sustained downward trend is detected (above the fall threshold), the system classifies it as a retreat.<br>4. The system logs the appropriate event ('rest' or 'retreat') with timestamp and altitude.<br>5. The event appears in the active session event feed. |
| **Alternative Flows** | ALT-06.A Climber resumes upward movement before rest timeout elapses:<br>A.1 The system resets the rest timer.<br>A.2 No rest event is logged; monitoring continues normally. |
| **Exceptions** | None |

---

## UC-07 – Add Manual Note

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | During an active session the climber manually records a text note, which is saved as a timestamped event in the session. |
| **Trigger** | The climber taps the '+' (add note) button on the active session screen. |
| **Preconditions** | PRE-07.1 A climbing session is currently active. |
| **Postconditions** | POST-07.1 A 'manual-note' event has been logged with the climber's text, timestamp, and current altitude.<br>POST-07.2 The note is visible in the active session event feed. |
| **Normal Flow** | 1. The climber taps the '+' button on the active session screen.<br>2. The system displays a text input dialog.<br>3. The climber enters a note.<br>4. The climber confirms the note.<br>5. The system captures the current timestamp and altitude.<br>6. The system saves a 'manual-note' event containing the text, timestamp, and altitude.<br>7. The system closes the dialog and the note appears in the event feed. |
| **Alternative Flows** | ALT-07.A Climber cancels the dialog:<br>A.1 The system closes the dialog without saving.<br>A.2 The session continues normally with no event logged. |
| **Exceptions** | None |

---

## UC-08 – View Session History

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | The climber browses a chronological list of all completed climbing sessions stored on the device. |
| **Trigger** | The climber navigates to the 'Log' tab in the app's navigation. |
| **Preconditions** | PRE-08.1 The app is running.<br>PRE-08.2 At least one completed session exists in the local storage. |
| **Postconditions** | POST-08.1 A list of completed sessions is displayed, each showing its name, date, location, total duration. |
| **Normal Flow** | 1. The climber taps the 'Log' tab.<br>2. The system retrieves all completed sessions from local storage, ordered by start date descending.<br>3. The system displays a scrollable list of session summary cards.<br>4. Each card shows: session name, date, location, total duration.<br>5. The climber scrolls through the list. |
| **Alternative Flows** | ALT-08.A No completed sessions exist:<br>A.1 The system displays an empty-state message indicating no sessions have been recorded yet. |
| **Exceptions** | EXC-08.A Session data cannot be loaded (storage error):<br>A.1 The system displays an error message.<br>A.2 The climber may attempt to reload or return to the home screen. |

---

## UC-09 – View Session Details

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | The climber selects a session from the history list to view its full data and a complete chronological log of all recorded events. |
| **Trigger** | The climber taps a session summary card in the session history list (UC-08). |
| **Preconditions** | PRE-09.1 The climber is on the session history screen.<br>PRE-09.2 The selected session record exists in local storage. |
| **Postconditions** | POST-09.1 The session detail screen is displayed, showing all data and a full event log. |
| **Normal Flow** | 1. The climber taps a session card.<br>2. The system loads the full session record including all associated events.<br>3. The system displays the session detail screen with: name, start/end date-time, location.<br>4. Below the metadata, the system lists every event in chronological order.<br>5. Each event entry shows its type, timestamp, altitude (where available), and any notes.<br>6. The climber reviews the information.<br>7. The climber can edit data such as name. |
| **Alternative Flows** | None |
| **Exceptions** | EXC-09.A The session record fails to load:<br>A.1 The system displays an error message.<br>A.2 The climber is returned to the session history screen. |

---

## UC-10 – Edit Event Information

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | The climber edits the events of an existing event within a completed session. |
| **Trigger** | The climber swipes right and taps the edit button on the session detail screen (UC-09). |
| **Preconditions** | PRE-10.1 The climber is viewing the detail screen of a completed session.<br>PRE-10.2 The event is not a 'session-started' or 'session-ended' event. |
| **Postconditions** | POST-10.1 The selected event's type, altitude and notes field has been updated.<br>POST-10.2 The updated information is reflected on the session detail screen and in the history list. |
| **Normal Flow** | 1. The climber swipes right on an event in the event list.<br>2. The system presents an editable text field pre-populated with the event's existing type, altitude and notes.<br>3. The climber modifies the type, altitude, notes.<br>4. The climber confirms the edit.<br>5. The system saves the updated event notes. The system returns to the session detail screen showing the updated event. |
| **Alternative Flows** | ALT-10.A Climber cancels the edit:<br>A.1 The system discards the changes and returns to the session detail screen. |
| **Exceptions** | None |

---

## UC-11 – Delete Event

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | The climber removes an existing event from a session. |
| **Trigger** | The climber swipes left and taps the delete button on the session detail screen (UC-09). |
| **Preconditions** | PRE-11.1 The climber is viewing the detail screen of a completed session.<br>PRE-11.2 The event is not a 'session-started' or 'session-ended' event. |
| **Postconditions** | POST-11.1 The event has been permanently removed from the session record. |
| **Normal Flow** | 1. The climber swipes left and picks the delete option on an event.<br>2. The system removes the event from the session record.<br>3. The system returns to the session detail screen with the event removed. |
| **Alternative Flows** | None |
| **Exceptions** | None |

---

## UC-12 – Add Event to Session

| | |
|---|---|
| **Primary Actor** | Climber |
| **Secondary Actors** | None |
| **Description** | The climber manually adds a new event to an existing session from the session detail screen. |
| **Trigger** | The climber taps the add button at the bottom left of the screen on the session detail screen. |
| **Preconditions** | PRE-12.1 The climber is viewing the session detail screen (UC-09). |
| **Postconditions** | POST-12.1 A new event has been added to the session record. |
| **Normal Flow** | 1. The climber taps the add event button.<br>2. The system presents a form with a mandatory timestamp selector, mandatory event type selector, optional altitude and notes field.<br>3. The climber selects an event type, inputs the timestamp and optionally enters notes/altitude.<br>4. The climber confirms.<br>5. The system creates a new event with the selected type, current timestamp, and notes.<br>6. The system saves the event to the session record.<br>7. The session detail screen updates to show the new event in the event list. |
| **Alternative Flows** | ALT-12.A Climber cancels:<br>A.1 The system discards the action and returns to the session detail screen unchanged. |
| **Exceptions** | None |
