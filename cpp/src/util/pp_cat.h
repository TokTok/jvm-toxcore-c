#pragma once

/**
 * Preprocessor utility to concatenate two tokens. If one of the tokens is a
 * macro, it is evaluated before concatenation.
 */
#define PP_CAT(a, b) PP_CAT_(a, b)
#define PP_CAT_(a, b) a##b
