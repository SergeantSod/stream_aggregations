# stream_aggregations


A tool to process a stream of observations and calculate aggregate values within a rolling time window. 
The processing is done via streaming, so memory does not explode for long-running streams. 

This implementation merely reads from a file and prints to the console, though.

Everything works with vanilly sbt. Example:

```
sbt test
sbt "run /path/to/text_file.txt"
```

### Assumptions
- The timestamps are sorted in ascending order, i.e. observations arrive in-order.
- The granularity is the same for input and output. I.e. for each observation in the input we want to emit exactly one corresponding row in the output that has the aggregates up to this point in time

### Design decisions
- We operate on lazy streams so we don't consume excessive amounts of RAM. 
- The core of the API should be something that transforms a typed stream of inputs to a
  typed stream of outputs.
- We eschew the use of the `Stream` class from the stdlib, since it memoizes, which could make us run out of RAM if we aren't extremely careful. Instead, we focus on `Iterator`.
- We want to solve the hard part (streaming) in a way that allows plugging in more kinds of aggregations. Therefore, aggregations should be generic and composable.
- Aggregations and composition of aggregations are type-safe
- Extensibility is more important than performance, i.e. if we find a type-safe, easily
  extensible solution that is slightly less performant than an alternative that is not as type-safe or not as extensible,
  we still prefer the one that is both type-safe and extensible.
- We want to keep the input and output strictly separate from the domain logic
- We don't want to spend too much time on polishing the input or output side of things, since reading
  from a file and writing to sdtout is probably not how you would connect to the outside world in a realistic scenario
- We don't handle parse errors gracefully for the same reason. There are libraries for this.
- We don't obsess about character-perfect alignment of the output columns for the same reason and more importantly because it contradicts a streaming approach. Instead, we make a reasonable best-effort.
