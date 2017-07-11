Assumptions:
- The timestamps are sorted in ascending order
- The granularity is the same for input and output. I.e. for each observation in the input we want to emit exactly one corresponding row in the output that has the aggregates up to this point in time
- At the end of the time series all current windows end normally and the aggregates over the window are emitted to the output

Design decisions:
- Everything should be evaluated as lazy as possible so we don't consume too much RAM. The core of the API should be something that transforms a typed stream of inputs to a
  typed stream of outputs.
- We eschew the use of the Stream class from the stdlib, since it memoizes, which could make us run out of RAM if we aren't extremely careful
- We want to solve the hard part (streaming) in a way that allows plugging in more kinds of aggregations
- We want type safety
- The combination of the two latter goals is more important than performance, i.e. if we find a type-safe, easily
  extensible solution that is slightly less performant than an alternative that is not as type-safe or not as extensible,
  we still prefer the one that is both type-safe and extensible.
- We want to keep the input and output strictly separate from the domain logic
- We don't want to spend any time on polishing the input or output side of things, since reading
  from a file and writing to sdtout is probably not how you would connect to the system in a realistic scenario
- We don't handle parse errors gracefully for the same reason
- We don't obsess about character-perfect alignment of the output columns for the same reason
