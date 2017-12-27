#ifndef PP_ATTRIBUTES_H
#define PP_ATTRIBUTES_H


#if defined(__GNUC__)
#  define PP_UNUSED   __attribute__ ((__unused__))
#  define PP_NORETURN __attribute__ ((__noreturn__))
#else
#  define PP_NORETURN
#  define PP_UNUSED
#endif


#endif /* PP_ATTRIBUTES_H */
