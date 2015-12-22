Core API design notes
=====================

This is a general document containing reasoning behind design choices made in
the creation of the Tox Core API. It also mentions some alternatives considered
and the reasons for not choosing them.

Design criteria
---------------

Our design focuses on the following aspects:

- Forward-thinking: the API should remain stable over an extended period of time.
- Stable ABI: we should give the user the ability to be long-term binary compatible.
- Minimise human errors: on the API designer's, its implementer's, and its user's side.
- Prefer self-consistency over minor readability improvements.
- Provide one and only one way to perform a certain task (or at least as few
  as possible).

Readability is an important criterion for API design, since clear client code
reduces possibility for error. However, given the choice between making a few
call sites more readable and keeping the API self-consistent, we prefer
consistency.


Types
-----

We try to use types in a semantically meaningful way.

- `uint8_t *` is used for byte arrays. Tox requires a platform where bytes are
  exactly 8 bits. Data in byte arrays is generally uninterpreted (except for
  keys), and is usually simply the payload for the various network functions.
  These byte arrays need an explicit length to be passed along with them, if
  they are of variable length, because they are not NUL-terminated.
- `char const *` contains NUL-terminated C strings that are read and interpreted
  by Core. These are used for host names.
- `bool` is used whenever the result of a function means success or failure.
  `true` always means success, `false` always means failure. In options, `true`
  means enabled, `false` means disabled.
- `size_t` is used for byte array lengths.
- `uint16_t` is used for port numbers. TCP/UDP port numbers are 16 bit, with
  port 0 being reserved. Port numbers are always expected in host byte order.
  Core takes care of proper conversion as required by networking functions.
- `uint32_t` is used for identifiers throughout Core. Examples of these are
  friend IDs and message receipts. It is also used for the nospam value, which
  is four 8-bit bytes long.

### `typedef`

We use the C `typedef` keyword as an abstraction. If the actual type is
irrelevant, we create an alias to abstract away that implementation detail. E.g.
all the error codes are `typedef`'d enums, but they might as well be defined as
`int`. A counter-example is `struct Tox_Options`, which is a struct per API. The
fact that it is a struct is part of the contract provided by the API, and is
therefore not abstracted away.

It would be possible to abstract away the fact that functions operate on Tox
pointers. We could do `typedef struct tox *Tox;`, which would make this fact
unknown. That would give us the freedom to later change it to something else,
e.g. an integer, breaking the ABI, but not the API.

We chose not to abstract away this fact, so that clients know they can treat the
result of `tox_new` as a pointer, meaning they can implicitly convert it from
and to `void *`, making it possible to pass a Tox instance as a user data
pointer in other APIs (or indeed the Tox Core API).


Error conditions
----------------

### Returning errors in the same value as success

C APIs often signal errors by shrinking the codomain of a function. E.g. a
function returning positive integers when successful will be typed `signed int`
and return negative integers in case of error. It is then possible to encode
various error codes into this negative integer. For functions returning
pointers, this is not possible, making error diagnosis on such functions rather
rudimentary. These functions can then take an out parameter for the error code.

An advantage of having negative integers for error codes is that, if there is
a global set of error codes for the entire API, the error codes can be
propagated through the library from the original failing code back to the public
API.

### Returning errors in a designated error code

For the Core API, we chose to use types in the most meaningful way, thus not
encoding failure in a range outside the codomain, but instead keeping the
codomain as it is, and explicitly returning the error status in an output
parameter `error`. Every function that can fail in different ways has such an
`error` parameter.

Error codes, if present, are always the last parameter of a function. This way,
the most interesting arguments come first.

### Out parameters vs. return codes

One alternative considered was to return the error code and passing the return
value of the function as an output parameter instead of passing the error code
as an output parameter. Our reasoning to choose the latter was that ignoring the
error code should be explicit, by passing NULL, instead of implicit, by ignoring
the return value. Ignoring the error code from a function can be an omission by
mistake, but explicitly passing NULL as the error code pointer is much less
likely to be unintentional. Ignoring the proper return value is also much less
likely than ignoring an error code, since the function was called with the
intention of getting its result.

#### Concerns

A concern was raised that the call site of a small number of functions could be
slightly improved in a few specific scenarios by returning the error code
instead of passing it as an out parameter. The functions in () are essentially
useless functions kept for legacy reasons (their value can also be read through
callbacks).

- `tox_bootstrap`
- `tox_self_set_name`
- `tox_self_set_status_message`
- `tox_friend_get_public_key`
- (`tox_friend_get_name`)
- (`tox_friend_get_status_message`)
- `tox_self_set_typing`
- `tox_friend_delete`
- `tox_file_control`
- `tox_friend_send_lossy_packet`
- `tox_friend_send_lossless_packet`

The scenario in which the call site of these functions would be improved is one
where the caller wants to know the specific error condition (not just the fact
that something went wrong, which is sufficient in most cases, and was usually
the only information available in the old API) *and* the error code is only used
once (e.g. in a `switch` statement).

Concretely, a common use of these functions would be:

    TOX_ERR_FRIEND_DELETE error;
    ...other code...
    tox_friend_delete(tox, ..., &error);
    switch (error) {
        // handle error
    }

Which would otherwise be written more concisely as:

    ...other code...
    switch (tox_friend_delete(tox, ..., &error)) {
        // handle error
    }

For cases where the error code is needed in a context other than a `switch`
condition, the improvements are negligible:

    TOX_ERR_FRIEND_DELETE error;
    ...other code...
    error = tox_friend_delete(tox, ...);
    if (error == CODE1 || error == CODE2) {
        // handle error 1 and 2
    }

The second improvement an error code as return value has is that the "OK" check
is slightly more explicit:

    if (tox_friend_delete(tox, ...) != TOX_ERR_LOAD_OK) {
        // handle error
    }

vs.

    if (!tox_friend_delete(tox, ..., NULL)) {
        // handle error
    }

The latter (using a NULL out parameter) is more concise and expresses the same
intent as the former, but the former can be seen as slightly more explicit.

However, these two readability improvements come at a rather high cost:

- Loss of self-consistency
- Increasing potential human errors

Having a self-consistent API is a tremendous advantage for anyone who starts to
learn the API. The Core API was designed with quick adaptation in mind. Client
code authors who read the first few function definitions should grasp the
general concepts used throughout the API, and will know what to expect from all
other functions. All callbacks behave the same, all error handling is the same,
naming conventions aid readers in their learning.

Changing the calling convention for 9 (11) of the 66 functions (at the time of
writing) will be surprising to users, giving more potential for making mistakes
and getting confused.

In addition, as mentioned before, calls to these 9 functions would no longer
explicitly express the intention of ignoring the specific error condition,
giving potential for forgetting the function's failure conditions. Given that
there are several functions without error code that cannot fail, could further
increase the impression that these 9 functions cannot fail.

For these reasons, we decided to sacrifice a small potential for slight
improvement of readability in order to preserve the API's consistency and
error-resistance properties.

### Responsibility of setting error codes

Functions always set their error code. If they succeed, they set it to their
respective OK value (always the first enumerator, therefore always equal to 0,
although no code should rely on this).This ensures that after returning, the
error code always represents the current execution state, and in particular is
always initialised. Client code can get less wrong if it's not their
responsibility to initialise it properly.

### Value of error codes

No client code should rely on the value or range of the error codes. The value
of the OK code is always zero, but even relying on that would be questionable
style. The only thing client code can rely on is that every value within an enum
type is different from every other value in that enum type. Any other value
relations are implementation details.

Error codes are often negative integers in C APIs, because they are returned in
the same (signed) integer return value as the valid positive "success" values.
Since the error set is disjunct and completely separate from the result set, the
actual value of error codes is irrelevant. They could be prime numbers,
fibonacci numbers (minus the first one, since it is equal to the second one),
or any other integer sequence in the range of the `int` type.

In the Core API, we use positive values for error codes, since this allows us to
keep the value of the enumerator implicitly defined by the C compiler,
eliminating one potential target for human error. If we use negative error
codes, that means we need to explicitly set the value for every enumerator,
giving way for the error of repeating a value.

In addition to reducing human error on the API designer's side, not having
explicit values written in the API makes it less likely for client programmers
to start assuming that these values can be used interchangably with the
enumerator names.


Program state machine
---------------------

The program state machine is the graph of all possible states the Tox object can
be in, and the transitions between those states. A general design principle we
try to follow is: avoid unreachable states in the program state machine.
Avoiding unreachable states means that it is always possible to, via a finite
number of transitions, reach every state from every other state. This means for
example that the program must always be able to return to the initial state by a
bounded sequence of function calls.


Naming conventions
------------------

A Tox instance contains several pieces of mutable information, such as nickname
and user status. Each of these has a set of functions named:
- `tox_set_${name}`: Setter function. If the information is a variable size byte
  array, this function will additionally have a length parameter.
- `tox_get_${name}`: Getter function. May require a variable size byte array.
- `tox_${name}_size`: Size function. Returns the required length of the variable
  size byte array to be passed to the `tox_get_${name}` function. If the size is
  known at compile time, this function does not exist.

Some information, such as Client ID and nickname, can be retrieved for both the
local client and for friends. Such functions are named:
- Local:
  - `tox_self_set_${name}`: Setter for local client.
  - `tox_self_get_${name}`: Getter for local client.
  - `tox_self_${name}_size`: Size function for local client.
- Friend:
  - `tox_friend_set_${name}`: Setter for friend info.
  - `tox_friend_get${name}`: Getter for friend info.
  - `tox_friend_${name}_size`: Size function for friend info.

Functions that get or set local information that does not exist for friends,
such as public/private keys and the nospam value, do not contain the word
"self".


`Tox_Options`
-------------

`Tox_Options` is the only struct in the core API. It is used as the parameter
to `tox_new` to set various startup and set-once runtime options. We considered
expanding the struct into parameters to `tox_new`, since that would make
bindings in some languages much easier (one would not need to create a struct
of which the layout is generally unknown). This thought was discarded, because

- In the future there may be many set-once startup options, and having an
  increasing number of parameters for `tox_new` would become unwieldy.
- Every time a new option is added, we would need to break the API.
- A struct allows us to easily get a default options object.

### Allocation/deallocation functions

We provide an allocation function to give clients the option to preserve
long-term binary (ABI) compatibility. If a client allocates a `Tox_Options`
object (either as an automatic, static, or dynamic variable), it will allocate
only as much memory as is required for `struct Tox_Options` of that particular
version of Core. A future version may have additional members for which the
client doesn't allocate memory. Using this too small allocation in a call to
`tox_new` will cause undefined behaviour.

Using an allocation function, the size of the allocation is updated along with
the library. Clients can still operate on their known prefix of the struct,
changing options they know about, without violating the ABI.

We provide a deallocation function for symmetry and long-term API stability.
