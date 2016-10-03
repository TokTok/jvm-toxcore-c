#include "ToxCore.h"

using namespace core;

#include "generated/impls.h"

void
reference_symbols_core ()
{
  int tox_finalize; // For Java only.

#define JAVA_METHOD_REF(NAME)  unused (JAVA_METHOD_NAME (NAME));
#define CXX_FUNCTION_REF(NAME) unused (NAME);
#include "generated/natives.h"
#undef CXX_FUNCTION_REF
#undef JAVA_METHOD_REF
}

#define TOX_MAX_HOSTNAME_LENGTH 255
#define TOX_DEFAULT_PROXY_PORT  8080
#define TOX_DEFAULT_TCP_PORT    0
#define TOX_DEFAULT_START_PORT  33445
#define TOX_DEFAULT_END_PORT    (TOX_DEFAULT_START_PORT + 100)
#include "generated/constants.h"

ToxInstances<tox::core_ptr, std::unique_ptr<Events>> core::instances;

template<> extern char const *const module_name<Tox> = "core";
template<> extern char const *const exn_prefix<Tox> = "";
