# Cazan Ștefan-Rareș 324CA

## The loading of the input values and system initialization:
- The actual loading of the user values is done via the `Database` class, which is implemented
  using the **Singleton Design Pattern**. The Singleton pattern was used to keep the Database
  consistent across the app for all data (users, tickets, milestones).

- The `App` class manages the loading process. It initializes the `Database` with users provided in
  the input files.
- The creation of `Ticket` entities is delegated to the `TicketFactory` class. This class
  implements the **Factory Design Pattern**. It receives data and determines the specific `Ticket`
  subclass (`Bug`, `FeatureRequest`, `UIFeedback`) to instantiate, ensuring that all attributes
  (severity, priority) are correctly populated.

- Once the database is populated, the main execution loop processes user commands. The workflow is
  based on the **Command Design Pattern**. The `CommandFactory` implements the **Factory Pattern**
  to detach the creation of command objects from their execution. It parses the input string and
  returns the specific `Command` implementation needed. The actual logic is then passed to the
  `executeLogic()` in each `BaseCommand` subclass.

- All commands extend the `BaseCommand` abstract class, which implements the **Template Method
  Design Pattern**. The `execute()` method defines the invariant of the operation (synchronizing
  time, validating the user, checking roles, and handling exceptions), while the specific logic is
  assigned to the abstract `executeLogic()` method implemented by subclasses. This design pattern
  ensures consistent execution because of the execute -> executeLogic() workflow.

- To manage timing and time-based interactions in the application, the `TimeManager` class is used.
  It is implemented as a **Singleton** to maintain a consistent internal clock. It also acts as the
  Subject in the **Observer Design Pattern**. Classes which need to react to time changes implement
  the `TimeObserver` interface. When `sync()` is called, it notifies all observers via the
  `onDayPassed()` method, ensuring deadlines and milestones are validated automatically.

- The `AssignTicket` command triggers a validation chain in the `AssignTicketValidator`. It checks
  if the user is not blocked and has the required expertise. If valid, the associations between
  `User` and `Ticket` are updated in the `Database`.

- The `ChangeStatus` command handles the lifecycle of a ticket. It verifies valid transitions based
  on the current status. If valid, the status is updated; otherwise, an error is generated.

- The `CreateMilestone` command allows `Manager` users to define key phases. When correct roles
  call it, it creates a `Milestone` and notifies the system.

- The `Search` command utilizes the **Strategy Design Pattern** to filter entities. The `Filter`
  interface defines a contract for different filtering strategies (`KeywordsFilter`,
  `StatusFilter`, `SeniorityFilter` etc.). The command chains these strategies based on input to
  refine the list of users or tickets, providing a flexible and extensible search mechanism. This
  makes the filtering of the search work flawlessly, in a modularised and extensible way.

- The reporting and analysis modules rely on the **Visitor Design Pattern**.
- The `GenerateTicketRiskReport`, `GenerateResolutionEfficiencyReport`, and
    `GenerateCustomerImpactReport` commands instantiate specific visitors: `RiskVisitor`,
    `EfficiencyVisitor`, and `ImpactVisitor`.
- The `TicketVisitor` interface enables the visitor classes to process different types of
    tickets.
- `RiskVisitor` calculates risk scores based on severity and status.
- `EfficiencyVisitor` computes developer resolution performance.
- `ImpactVisitor` assesses customer impact.
  This approach allows adding new reporting operations without modifying the `Ticket` classes.

## Output:
- The output generation is handled by the `OutputBuilder` class, which implements the **Builder
  Design Pattern**. This encapsulates the complexity of Jackson's `ObjectMapper` and ensures that
  all output follows the same JSON structure (command, username, timestamp, result/error).
- For report commands, the output logic parses the data collected throughout the BaseCommand
  subclass method calls and uses the **Builder** to format the final JSON report.

## Use of AI in the project:
The sections in which AI was used were:
##### understanding the Visitor, Strategy and Observer Pattern implementation details
##### optimizing the checkstyle configuration
##### fixing coding style errors, such as magic numbers and JavaDocs formatting
##### guidance with implementing design patterns and connecting
##### them (Factory with Command,Singleton with Observer etc.)

#
No code was generated with AI and copy-pasted inside the solution. No code was changed by AI when
fixing coding style errors. The markdown text is fully written by hand, with only some of the
formatting being generated by AI.
