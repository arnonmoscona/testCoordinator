This is a very simple clean-room implementation of the TestCoordinator class from JConch [https://code.google.com/p/jconch/].

Many people used JConch only for this useful utility, which helps with testing asynchronous code.

The problem is that the JConch project does not seem to have been maintained in a while, and so as old code gets 
refactored, moved, upgraded, etc. one ends up with this uncomfortable dependency that did not keep up with current 
practices.

I am in the same boat as many users, and while with Java8 it may be that there are better ways to do this, I find it 
useful to have this drop-in replacement for places that I don't want to spend much time refactoring tests.