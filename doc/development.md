# Development process

Tox4j is a software project with very high security and stability requirements.
It will potentially be used by hundreds of millions of people around the world.
A security issue could have catastrophic consequences. Slightly less critical,
but still very important, is stability. If the library underlying our Android
app is unstable, it's annoying in the mildest case, and can be catastrophic to a
single person (imagine the code arbitrarily deleting chat histories).

Therefore, tox4j-team operates in a slightly different way from most open source
projects. We have strict design and code review processes that no contributor,
including the project owners, can violate. We have software managing our source
code repository that ensures the code review process is followed.

For any non-trivial development, we follow a rigorous design process. This
process ensures that every decision is founded in reason and that at least some
alternatives for the decisions have been considered. We split the design into
two stages with a document accompanying each stage. For reviews, we use a
collaborative editing environment that allows for suggestions and inline
comments (e.g. Google Docs).

## Goals, requirements, overview

The first stage is the design overview. This document contains the high level
overview of a design. It starts by answering the following questions:

*   What is the current state of the world? We describe a problem and outline a
    solution with the currently available technology. This establishes the
    background for our design.
*   What do we want the world to look like? This part describes at a high level
    what the new technology is intended to do, what problems it solves, and in
    particular, how the problem described in the previous section will be solved
    in a better way using this new technology.
*   What are the goals of this design?
*   Where does the scope of our design end? What is *not* a goal of this design?

Next comes a list of requirements. For example:

*   What kind of data sizes does our system operate on?
*   How reliable does it need to be?
*   How general or how specific does it need to be?
*   How extensible does it need to be?

The largest section of this document is the high level design overview. Here we
describe, without going into implementation details, what components the
solution will be comprised of, how they interact, and how they depend on each
other. For tooling, this may include usage examples. For code, this won't
include API definitions. This part may discuss storage choices, protocol
choices, and other high level design ideas.

Finally, the document includes a section discussing alternative approaches. The
discussion gives a short description of a technology that solves a similar
problem to ours, provides a link to the documentation of that technology, and
points out reasons for not choosing it.

This document forms the basis for discussion, and we require at least one
reviewer (if possible, two reviewers) to sign off on the idea. Ideally, we have
many more people commenting and suggesting on the document.

## Detailed technical design

After going through several iterations of discussion and improvement, the rough
design is expanded to become a full technical design. This is a separate
document, which may initially be a copy of the above document. It specifies the
APIs in detail, with message types, effects, and result types. These can be
language-specific or language-agnostic such as web or RPC APIs. The evolution of
this document usually goes hand in hand with the actual implementation.

## Writing code

In many open source projects, the people with write access to the source code
repository will submit code frequently and break things in the process. The
breakage is then hopefully resolved. In contrast, tox4j-team does not allow a
single line of code (or documentation) to enter the master branch of the main
repository without being reviewed by at least one member that did not write the
code. This means that even tox4j-team members cannot push arbitrary unreviewed
code to the repository. The exception to this rule are rollbacks of the most
recently accepted pull request, which may be performed without review (since it
will revert the state of the repository to an already reviewed point in time).

If you want to contribute code or documentation, you will go through the normal
Github process of forking the repository and opening a pull request. You will
then find your pull request on
[the code review site](https://upsource.slevermann.de/tox-j/reviews) with
reviewers from tox4j-team added to it, who will read and comment on your
submission. You will most likely receive actionable feedback, which you can
discuss on the review. You can then add more commits to the review by pushing
them to the pull request branch. If you feel that you have addressed a comment,
click "Resolve". The commit messages of your secondary commits (the ones that
were added to address comments) won't matter, as they will be automatically
collapsed into a single "Review progression" commit once the PR is approved.
When the reviewer or reviewers approved your change, you can close the code
review ("Close review" button), after which the PR will be merged.

A continuous integration system will build the code on an x86\_64 machine and
run an exhaustive testsuite to increase confidence in the changes. After the
code is accepted, a secondary CI system will build the code for the Android
platform.
