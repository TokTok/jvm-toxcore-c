#pragma once

/*****************************************************************************
 *
 * Identity and unused-value function.
 *
 *****************************************************************************/

static auto const identity = [](auto v) { return v; };

template<typename ...T> static inline void unused (T const &...) { }
