# stream

This works like RxJava or Java stream.
Based on `observe` pattern, we make linked list of observables to
perform a stream for a chain of process.

## Usage

```java
DkObservable
	.fromExecution(() -> {
		return true;
	})
	.map(ok -> {
		return "done";
	})
	.subscribe();
```
